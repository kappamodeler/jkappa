package com.plectix.simulator.simulator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

import com.plectix.simulator.components.CInjection;
import com.plectix.simulator.components.CNetworkNotation;
import com.plectix.simulator.components.CPerturbation;
import com.plectix.simulator.components.CProbabilityCalculation;
import com.plectix.simulator.components.CSnapshot;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.components.CStories;
import com.plectix.simulator.components.NameDictionary;
import com.plectix.simulator.components.ObservablesConnectedComponent;
import com.plectix.simulator.components.SolutionLines;
import com.plectix.simulator.components.actions.CActionType;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.controller.SimulatorInterface;
import com.plectix.simulator.controller.SimulatorResultsData;
import com.plectix.simulator.interfaces.IAction;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.ILiftElement;
import com.plectix.simulator.interfaces.IObservablesConnectedComponent;
import com.plectix.simulator.interfaces.IPerturbationExpression;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.util.Info;
import com.plectix.simulator.util.RunningMetric;
import com.plectix.simulator.util.PlxTimer;

public class Simulator implements SimulatorInterface {

	private static final String NAME = "Java Simulator JSIM";
	
	private static final Logger LOGGER = Logger.getLogger(Simulator.class);

	private static NameDictionary nameDictionary = new NameDictionary();

	private static ThreadLocal<PrintStream> printStream = new ThreadLocal<PrintStream>();

	public static final NameDictionary getNameDictionary() {
		return nameDictionary;
	}

	public static PrintStream getErrorStream() {
		return printStream.get();
	}

	public static void println(String text) {
		printStream.get().println(text);
	}

	public static void print(String text) {
		printStream.get().print(text);
	}

	private int agentIdGenerator = 0;

	private double currentTime = 0.;

	private boolean isIteration = false;

	private int timeStepCounter = 0;
	
	private SimulationData simulationData;

	private SimulatorResultsData simulatorResultsData;
	

	public Simulator() {
		simulationData = new SimulationData();
		simulatorResultsData = new SimulatorResultsData();
		printStream.set(System.out);
	}

	@Override
	public SimulatorInterface clone() {
		return new Simulator();
	}
	
	private final void addIteration(int iteration_num) {

		List<List<RunningMetric>> runningMetrics = simulationData.getRunningMetrics();
		int number_of_observables = getSimulationData().getObservables().getComponentListForXMLOutput().size();

		if (iteration_num == 0) {
			simulationData.getTimeStamps().add(currentTime);

			for (int observable_num = 0; observable_num < number_of_observables; observable_num++) {
				runningMetrics.get(observable_num).add(new RunningMetric());
			}
		}

		for (int observable_num = 0; observable_num < number_of_observables; observable_num++) {
			// x is the value for the observable_num at the current time
			double x = getSimulationData().getObservables()
					.getComponentListForXMLOutput().get(observable_num)
					.getSize(getSimulationData().getObservables());
			if (timeStepCounter >= runningMetrics.get(observable_num).size()) {
				runningMetrics.get(observable_num).add(new RunningMetric());
			}
			runningMetrics.get(observable_num).get(timeStepCounter).add(x);
		}

		timeStepCounter++;
	}

	public final void doNegativeUpdate(List<IInjection> injectionsList) {
		for (IInjection injection : injectionsList) {
			if (injection != CInjection.EMPTY_INJECTION) {
				for (ISite site : injection.getChangedSites()) {
					site.getAgentLink().getEmptySite()
							.removeInjectionsFromCCToSite(injection);
					site.getAgentLink().getEmptySite().clearLiftList();
					site.removeInjectionsFromCCToSite(injection);
					site.clearLiftList();
				}
				if (injection.getChangedSites().size() != 0) {
					for (ISite site : injection.getSiteList()) {
						if (!injection
								.checkSiteExistanceAmongChangedSites(site)) {
							site.removeInjectionFromLift(injection);
						}
					}
					injection.getConnectedComponent()
							.removeInjection(injection);
				}
			}
		}
	}

	public final List<IAgent> doNegativeUpdateForDeletedAgents(IRule rule,
			List<IInjection> injectionsList) {
		List<IAgent> freeAgents = new ArrayList<IAgent>();
		for (IInjection injection : injectionsList) {
			for (ISite checkedSite : rule.getSitesConnectedWithDeleted()) {
				if (!injection.checkSiteExistanceAmongChangedSites(checkedSite)) {

					IAgent checkedAgent = checkedSite.getAgentLink();
					SimulationUtils.addToAgentList(freeAgents, checkedAgent);
					for (ILiftElement lift : checkedAgent.getEmptySite()
							.getLift()) {
						lift.getConnectedComponent().removeInjection(
								lift.getInjection());
					}
					checkedAgent.getEmptySite().clearLiftList();
					for (ILiftElement lift : checkedSite.getLift()) {

						for (ISite site : lift.getInjection().getSiteList()) {
							if (site != checkedSite)
								site.removeInjectionFromLift(lift
										.getInjection());
						}

						lift.getConnectedComponent().removeInjection(
								lift.getInjection());
					}
					checkedSite.clearLiftList();
				}
			}
		}
		for (ISite checkedSite : rule.getSitesConnectedWithBroken()) {
			IAgent checkedAgent = checkedSite.getAgentLink();
			SimulationUtils.addToAgentList(freeAgents, checkedAgent);
		}
		return freeAgents;
	}

	public final void doPositiveUpdate(IRule rule,
			List<IInjection> myCurrentInjectionsList) {
		if (getSimulationData().isActivationMap()) {
			positiveUpdate(rule.getActivatedRule(), rule.getActivatedObservable(), rule);
		} else {
			positiveUpdate(getSimulationData().getRules(), getSimulationData().getObservables().getConnectedComponentList(), rule);
		}

		doPositiveUpdateForDeletedAgents(doNegativeUpdateForDeletedAgents(rule, myCurrentInjectionsList));
	}

	public final void doPositiveUpdateForDeletedAgents(List<IAgent> agentsList) {
		for (IAgent agent : agentsList) {
			for (IRule rule : getSimulationData().getRules()) {
				for (IConnectedComponent cc : rule.getLeftHandSide()) {
					IInjection inj = cc.getInjection(agent);
					if (inj != null) {
						if (!agent.isAgentHaveLinkToConnectedComponent(cc, inj))
							cc.setInjection(inj);
					}
				}
			}
			for (IObservablesConnectedComponent obsCC : getSimulationData()
					.getObservables().getConnectedComponentList()) {
				IInjection inj = obsCC.getInjection(agent);
				if (inj != null) {
					if (!agent.isAgentHaveLinkToConnectedComponent(obsCC, inj))
						obsCC.setInjection(inj);
				}
			}
		}
	}

	public final long generateNextAgentId() {
		return agentIdGenerator++;
	}

	public final void outputData() {
		outputRules();
		outputPertubation();
		outputSolution();
	}

	private Source addCompleteSource() throws TransformerException,
			ParserConfigurationException {
		Source source = simulationData.createDOMModel();
		simulatorResultsData.addResultSource(source);
		return source;
	}

	public final void outputData(Source source, long count) {
		try {
			PlxTimer timerOutput = new PlxTimer();
			timerOutput.startTimer();
			getSimulationData().writeToXML(source, timerOutput);
		} catch (ParserConfigurationException e) {
			e.printStackTrace(Simulator.getErrorStream());
		} catch (TransformerException e) {
			e.printStackTrace(Simulator.getErrorStream());
		}
	}

	private final void outputPertubation() {

		Simulator.println("PERTURBATIONS:");

		for (CPerturbation perturbation : simulationData.getPerturbations()) {
			Simulator.println(perturbationToString(perturbation));
		}

	}

	private final void outputRules() {
		for (IRule rule : getRules()) {
			// int countAgentsInLHS = rule.getCountAgentsLHS();
			// int indexNewAgent = countAgentsInLHS;

			for (IAction action : rule.getActionList()) {
				switch (CActionType.getById(action.getTypeId())) {
				case BREAK: {
					ISite siteTo = ((ISite) action.getSiteFrom().getLinkState()
							.getSite());
					if (action.getSiteFrom().getAgentLink().getIdInRuleSide() < siteTo
							.getAgentLink().getIdInRuleSide()) {
						// BRK (#0,a) (#1,x)
						Simulator.print("BRK (#");
						Simulator.print(""
								+ (action.getSiteFrom().getAgentLink()
										.getIdInRuleSide() - 1));
						Simulator.print(",");
						Simulator.print(action.getSiteFrom().getName());
						Simulator.print(") ");
						Simulator.print("(#");
						Simulator
								.print(""
										+ (siteTo.getAgentLink()
												.getIdInRuleSide() - 1));
						Simulator.print(",");
						Simulator.print(siteTo.getName());
						Simulator.print(") ");
						Simulator.println();
					}
					break;
				}
				case DELETE: {
					// DEL #0
					Simulator.print("DEL #");
					Simulator.println(""
							+ (action.getAgentFrom().getIdInRuleSide() - 1));
					break;
				}
				case ADD: {
					// ADD a#0(x)
					Simulator.print("ADD " + action.getAgentTo().getName()
							+ "#");

					Simulator.print(""
							+ (action.getAgentTo().getIdInRuleSide() - 1));
					Simulator.print("(");
					int i = 1;
					for (ISite site : action.getAgentTo().getSites()) {
						Simulator.print(site.getName());
						if ((site.getInternalState() != null)
								&& (site.getInternalState().getNameId() >= 0))
							Simulator.print("~"
									+ site.getInternalState().getName());
						if (action.getAgentTo().getSites().size() > i++)
							Simulator.print(",");
					}
					Simulator.println(") ");

					break;
				}
				case BOUND: {
					// BND (#1,x) (#0,a)
					ISite siteTo = ((ISite) action.getSiteFrom().getLinkState()
							.getSite());
					if (action.getSiteFrom().getAgentLink().getIdInRuleSide() > siteTo
							.getAgentLink().getIdInRuleSide()) {
						Simulator.print("BND (#");
						Simulator.print(""
								+ (action.getSiteFrom().getAgentLink()
										.getIdInRuleSide() - 1));
						Simulator.print(",");
						Simulator.print(action.getSiteFrom().getName());
						Simulator.print(") ");
						Simulator.print("(#");
						Simulator.print(""
								+ (action.getSiteTo().getAgentLink()
										.getIdInRuleSide() - 1));
						Simulator.print(",");
						Simulator.print(siteTo.getName());
						Simulator.print(") ");
						Simulator.println();
					}
					break;
				}
				case MODIFY: {
					// MOD (#1,x) with p
					Simulator.print("MOD (#");
					Simulator.print(""
							+ (action.getSiteFrom().getAgentLink()
									.getIdInRuleSide() - 1));
					Simulator.print(",");
					Simulator.print(action.getSiteFrom().getName());
					Simulator.print(") with ");
					Simulator.print(action.getSiteTo().getInternalState()
							.getName());
					Simulator.println();
					break;
				}
				}

			}

			String line = SimulationUtils.printPartRule(rule.getLeftHandSide(), simulationData
					.isOcamlStyleObsName());
			line = line + "->";
			line = line
					+ SimulationUtils.printPartRule(rule.getRightHandSide(), simulationData
							.isOcamlStyleObsName());
			String ch = new String();
			for (int j = 0; j < line.length(); j++)
				ch = ch + "-";

			Simulator.println(ch);
			if (rule.getName() != null) {
				Simulator.print(rule.getName());
				Simulator.print(": ");
			}
			Simulator.print(line);
			Simulator.println();
			Simulator.println(ch);
			Simulator.println();
			Simulator.println();
		}
	}

	public static final void println() {
		printStream.get().println();
	}

	private final void outputSolution() {
		Simulator.println("INITIAL SOLUTION:");
		for (SolutionLines sl : ((CSolution) simulationData.getSolution())
				.getSolutionLines()) {
			Simulator.print("-");
			Simulator.print("" + sl.getCount());
			Simulator.print("*[");
			Simulator.print(sl.getLine());
			Simulator.println("]");
		}
	}

	private final void outToLogger(boolean isEndRules, PlxTimer timer) {
		simulationData.stopTimer(timer, "-Simulation:");

		if (!isEndRules)
			LOGGER.info("end of simulation: time");
		else
			LOGGER.info("end of simulation: there are no active rules");
	}

	private final String perturbationParametersToString(
			List<IPerturbationExpression> sumParameters) {
		String st = new String();

		int index = 1;
		for (IPerturbationExpression parameters : sumParameters) {
			st += parameters.getValueToString();
			if (parameters.getName() != null) {
				st += "*[";
				st += parameters.getName();
				st += "]";
			}
			if (index < sumParameters.size())
				st += " + ";
			index++;
		}

		return st;
	}

	private final String perturbationToString(CPerturbation perturbation) {
		String st = "-";
		String greater;
		if (perturbation.getGreater())
			greater = "> ";
		else
			greater = "< ";

		switch (perturbation.getType()) {
		case CPerturbation.TYPE_TIME: {
			st += "Whenever current time ";
			st += greater;
			st += perturbation.getTimeCondition();
			break;
		}
		case CPerturbation.TYPE_NUMBER: {
			st += "Whenever [";
			st += simulationData.getObservables().getComponentList().get(
					perturbation.getObsNameID()).getName();
			st += "] ";
			st += greater;
			st += perturbationParametersToString(perturbation
					.getLHSParametersList());
			break;
		}
		}
		st += " do kin(";
		st += perturbation.getPerturbationRule().getName();
		st += "):=";
		st += perturbationParametersToString(perturbation
				.getRHSParametersList());

		return st;
	}

	private final void positiveUpdate(List<IRule> rulesList,
			List<IObservablesConnectedComponent> list, IRule rule) {
		for (IRule rules : rulesList) {
			for (IConnectedComponent cc : rules.getLeftHandSide()) {
				cc.doPositiveUpdate(rule.getRightHandSide());
			}
		}
		for (IObservablesConnectedComponent oCC : list) {
			if (oCC.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX)
				oCC.doPositiveUpdate(rule.getRightHandSide());
		}
	}

	public final void resetSimulation() {
		getSimulationData().addInfo(new Info(Info.TYPE_INFO, "-Reset simulation data."));
		getSimulationData().addInfo(new Info(Info.TYPE_INFO, "-Initialization..."));
		
		PlxTimer timer = new PlxTimer();
		timer.startTimer();
		
		simulationData.clearRules();
		simulationData.getObservables().resetLists();
		simulationData.getSolution().clearAgents();
		simulationData.getSolution().clearSolutionLines();
		
		if (simulationData.getPerturbations() != null) {
			simulationData.clearPerturbations();
		}

		currentTime = 0.0;

		if (getSimulationData().getSerializationMode() != SimulationData.MODE_READ) {
			getSimulationData().readSimulatonFile(this);
		}
		
		simulationData.initialize();
		
		simulationData.stopTimer(timer, "-Initialization:");
		simulationData.setClockStamp(System.currentTimeMillis());
	}

	public final void run(int iteration_num) throws Exception {
		getSimulationData().addInfo(new Info(Info.TYPE_INFO, "-Simulation..."));
		
		PlxTimer timer = new PlxTimer();
		timer.startTimer();
		
		CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(getSimulationData());

		long clash = 0;
		long count = 0;
		long max_clash = 0;
		boolean isEndRules = false;
		
		while (!getSimulationData().isEndSimulation(currentTime, count)
				&& max_clash <= getSimulationData().getMaxClashes()) {
			while (getSimulationData().checkSnapshots(currentTime)) {
				createSnapshots();				
			}
			simulationData.checkPerturbation(currentTime);
			IRule rule = ruleProbabilityCalculation.getRandomRule();

			if (rule == null) {
				isEndRules = true;
				getSimulationData().setTimeLength(currentTime);
				Simulator.println("#");
				break;
			}
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Rule: " + rule.getName());

			List<IInjection> injectionsList = ruleProbabilityCalculation
					.getSomeInjectionList(rule);
			if (!rule.isInfinityRate())
				currentTime += ruleProbabilityCalculation.getTimeValue();

			if (!rule.isClash(injectionsList)) {
				// negative update
				max_clash = 0;
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("negative update");

				count++;
				rule.applyRule(injectionsList, this);

				doNegativeUpdate(injectionsList);
				// positive update
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("positive update");

				doPositiveUpdate(rule, injectionsList);

				getSimulationData().getObservables().calculateObs(currentTime,
						count, getSimulationData().isTime());
			} else {
				getSimulationData().addInfo(
						new Info(Info.TYPE_INTERNAL,
								"Application of rule exp is clashing"));
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Clash");
				clash++;
				max_clash++;
			}

			if (isIteration)
				addIteration(iteration_num);
		}
		checkOutputFinalState();
		getSimulationData().getObservables().calculateObsLast(currentTime);
		getSimulationData().setTimeLength(currentTime);
		getSimulationData().setEvent(count);
		
		outToLogger(isEndRules, timer);
		Source source = addCompleteSource();
		if (!isIteration)
			outputData(source, count);
	}
	
	private void checkOutputFinalState(){
		if(getSimulationData().isOutputFinalState())
			createSnapshots();
	}
	
	private void createSnapshots(){
		getSimulationData().addSnapshot(
				new CSnapshot(getSimulationData(),currentTime));
//		getSimulationData().setSnapshotTime(currentTime);
	}

	public final void run(SimulatorInputData simulatorInputData) throws Exception {
		String[] args = simulatorInputData.getArgs();
		printStream.set(simulatorInputData.getPrintStream());
		SimulationData simulationData = getSimulationData();
		simulationData.setCommandLine(args);
		Simulator.println("Java " + simulationData.getCommandLine());

		PlxTimer timer = new PlxTimer();
		timer.startTimer();
		
		simulationData.parseArguments(args);
		
		simulationData.readSimulatonFile(this);
		
		simulationData.initialize();
		getSimulationData().stopTimer(timer, "-Initialization:");
		getSimulationData().setClockStamp(System.currentTimeMillis());
		
		if (simulationData.isCompile()) {
			outputData();
			return;
		}
		
		if (!simulationData.isDebugInitOption()) {

			if (simulationData.isGenereteMapOption() || simulationData.isContactMapOption() ) {
				Source source = addCompleteSource();
				outputData(source, 0);
			} else if (simulationData.isNumberOfRunsOption()) {
				runIterations();
			} else if (simulationData.isStorifyOption()) {
				runStories();
			} else {
				run(0);
			}

			//simulatorResultsData.setResultSource(simulationData.createDOMModel
			// ());
		}
		
		System.out.println("-------" + simulatorResultsData.getResultSource());
	}

	public final void runIterations() throws Exception {
		isIteration = true;
		int seed = getSimulationData().getSeed();
		List<Double> timeStamps = new ArrayList<Double>();
		List<List<RunningMetric>> runningMetrics = new ArrayList<List<RunningMetric>>();
		getSimulationData().initIterations(timeStamps, runningMetrics);
		for (int iteration_num = 0; iteration_num < getSimulationData()
				.getIterations(); iteration_num++) {
			// Initialize the Random Number Generator with seed = initialSeed +
			// i
			// We also need a new command line argument to feed the initialSeed.
			// See --seed argument of simplx.
			getSimulationData().setSeed(seed + iteration_num);

			// run the simulator
			timeStepCounter = 0;
			// while (true) {
			// run the simulation until the next time to dump the results
			run(iteration_num);
			// }

			// if the simulator's initial state is cached, reload it for next
			// run
			if (iteration_num < getSimulationData().getIterations() - 1)
				resetSimulation();

		}

		// we are done. report the results
		getSimulationData().createTMPReport();
		// Source source = addCompleteSource();
		// outputData(source, 0);
	}

	public final void runStories() throws Exception {
		CStories stories = getSimulationData().getStories();
		int count = 0;
		for (int i = 0; i < simulationData.getIterations(); i++) {
		    getSimulationData().addInfo(new Info(Info.TYPE_INFO, "-Simulation..."));
		    
			PlxTimer timer = new PlxTimer();
			timer.startTimer();
			
			boolean isEndRules = false;
			long clash = 0;
			IRule rule;
			long max_clash = 0;
			CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(
					getSimulationData());
		    getSimulationData().resetBar();
			while (!getSimulationData().isEndSimulation(currentTime, count)
					&& max_clash <= getSimulationData().getMaxClashes()) {
				
				simulationData.checkPerturbation(currentTime);
				rule = ruleProbabilityCalculation.getRandomRule();

				if (rule == null) {
					getSimulationData().setTimeLength(currentTime);
					Simulator.println("#");
					break;
				}

				List<IInjection> injectionsList = ruleProbabilityCalculation
						.getSomeInjectionList(rule);
				currentTime += ruleProbabilityCalculation.getTimeValue();
				if (!rule.isClash(injectionsList)) {
					CNetworkNotation netNotation = new CNetworkNotation(count,
							rule, injectionsList, getSimulationData());
					max_clash = 0;
					if (stories.checkRule(rule.getRuleID(), i)) {
						rule.applyLastRuleForStories(injectionsList,
								netNotation);
						rule.applyRuleForStories(injectionsList, netNotation,
								this,true);
						stories.addToNetworkNotationStoryStorifyRule(i,
								netNotation, currentTime);
						// stories.addToNetworkNotationStory(i, netNotation);
						count++;
						isEndRules = true;
						Simulator.println("#");
						break;
					}
					rule.applyRuleForStories(injectionsList, netNotation, this,false);
					if (!rule.isRHSEqualsLHS())
						stories.addToNetworkNotationStory(i, netNotation);
					count++;

					doNegativeUpdate(injectionsList);
					doPositiveUpdate(rule, injectionsList);
				} else {
					clash++;
					max_clash++;
				}
			}
			count = 0;
			outToLogger(isEndRules, timer);
			stories.handling(i);
			if (i < simulationData.getIterations() - 1)
				resetSimulation();
		}
		stories.merge();
		Source source = addCompleteSource();
		outputData(source, count);
	}

	public final void setRules(List<IRule> rules) {
		getSimulationData().setRules(rules);
	}

	
	//////////////////////////////////////////////////////////////////////////
	//
	//                    GETTERS AND SETTERS
	//
	//
	
	public final int getAgentIdGenerator() {
		return agentIdGenerator;
	}

	public final double getCurrentTime() {
		return currentTime;
	}

	public final String getName() {
		return NAME;
	}

	public final List<IRule> getRules() {
		return simulationData.getRules();
	}

	public final SimulationData getSimulationData() {
		return simulationData;
	}

	public final SimulatorResultsData getSimulatorResultsData() {
		return simulatorResultsData;
	}

}
