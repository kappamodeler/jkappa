package com.plectix.simulator.simulator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import com.plectix.simulator.*;
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
import com.plectix.simulator.interfaces.IActivationMap;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.ILiftElement;
import com.plectix.simulator.interfaces.IObservablesConnectedComponent;
import com.plectix.simulator.interfaces.IPerturbationExpression;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.options.SimulatorOptions;
import com.plectix.simulator.options.SimulatorArguments;
import com.plectix.simulator.util.Info;
import com.plectix.simulator.util.RunningMetric;
import com.plectix.simulator.util.TimerSimulation;

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

	private IActivationMap activationMap;

	private int agentIdGenerator = 0;

	private SimulatorArguments myArguments;

	private double currentTime = 0.;

	private boolean isIteration = false;
	
	private boolean storyMode = false;

	private TimerSimulation timer;

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

	public void init(SimulatorArguments arguments) {
		this.myArguments = arguments;
		simulationData.initialize();
		getSimulationData().stopTimer(timer, "-Initialization:");
		getSimulationData().setClockStamp(System.currentTimeMillis());
	}
	
	private final void addIteration(int iteration_num) {

		List<List<RunningMetric>> runningMetrics = simulationData
				.getRunningMetrics();
		int number_of_observables = getSimulationData().getObservables()
				.getComponentListForXMLOutput().size();

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

	private final void addToAgentList(List<IAgent> list, IAgent agent) {
		for (IAgent agentL : list)
			if (agentL == agent)
				return;
		list.add(agent);
	}

	private final void checkPerturbation() {
		if (getSimulationData().getPerturbations().size() != 0) {
			for (CPerturbation pb : getSimulationData().getPerturbations()) {
				switch (pb.getType()) {
				case CPerturbation.TYPE_TIME: {
					if (!pb.isDo())
						pb.checkCondition(currentTime);
					break;
				}
				case CPerturbation.TYPE_NUMBER: {
					pb.checkCondition(getSimulationData().getObservables());
					break;
				}
				case CPerturbation.TYPE_ONCE: {
					if (!pb.isDo())
						pb.checkConditionOnce(currentTime);
					break;
				}
				}

			}

		}
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
					addToAgentList(freeAgents, checkedAgent);
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
			addToAgentList(freeAgents, checkedAgent);
		}
		return freeAgents;
	}

	public final void doPositiveUpdate(IRule rule,
			List<IInjection> myCurrentInjectionsList) {
		if (getSimulationData().isActivationMap()) {
			positiveUpdate(rule.getActivatedRule(), rule
					.getActivatedObservable(), rule);
		} else {
			positiveUpdate(getSimulationData().getRules(), getSimulationData()
					.getObservables().getConnectedComponentList(), rule);
		}

		doPositiveUpdateForDeletedAgents(doNegativeUpdateForDeletedAgents(rule,
				myCurrentInjectionsList));

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


	public final void startTimer() {
		timer = new TimerSimulation();
		timer.startTimer();
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
		TimerSimulation timerOutput = new TimerSimulation();
		timerOutput.startTimer();

		try {
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
			int countAgentsInLHS = rule.getCountAgentsLHS();
			int indexNewAgent = countAgentsInLHS;

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

	public static void println() {
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

	private final void outToLogger(boolean isEndRules, TimerSimulation timer) {
		getSimulationData().stopTimer(timer, "-Simulation:");
		// Simulator.println("-Simulation: " + timer.getTimer() + " sec. CPU");
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
		getSimulationData().addInfo(
				new Info(Info.TYPE_INFO, "-Reset simulation data."));
		getSimulationData().addInfo(
				new Info(Info.TYPE_INFO, "-Initialization..."));
		startTimer();
		getSimulationData().clearRules();
		getSimulationData().getObservables().resetLists();
		getSimulationData().getSolution().clearAgents();
		getSimulationData().getSolution().clearSolutionLines();
		if (getSimulationData().getPerturbations() != null)
			getSimulationData().clearPerturbations();

		currentTime = 0;

		if (getSimulationData().getSerializationMode() != getSimulationData().MODE_READ) {
			SimulationMain.readSimulatonFile(this, myArguments);
		}
		init(myArguments);
	}

	public void run(int iteration_num) throws Exception {
		getSimulationData().addInfo(new Info(Info.TYPE_INFO, "-Simulation..."));
		TimerSimulation timer = new TimerSimulation(true);
		long clash = 0;
		IRule rule;
		CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(
				getSimulationData());

		boolean isEndRules = false;

		boolean hasSnapshot = false;
		if (getSimulationData().getSnapshotTime() >= 0.0)
			hasSnapshot = true;

		long count = 0;

		long max_clash = 0;
		while (!getSimulationData().isEndSimulation(currentTime, count)
				&& max_clash <= getSimulationData().getMaxClashes()) {
			if (hasSnapshot
					&& getSimulationData().getSnapshotTime() <= currentTime) {
				hasSnapshot = false;
				getSimulationData().setSnapshot(
						new CSnapshot(getSimulationData()));
				getSimulationData().setSnapshotTime(currentTime);
			}
			checkPerturbation();
			rule = ruleProbabilityCalculation.getRandomRule();

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
		getSimulationData().getObservables().calculateObsLast(currentTime);
		getSimulationData().setTimeLength(currentTime);
		getSimulationData().setEvent(count);
		outToLogger(isEndRules, timer);
		Source source = addCompleteSource();
		if (!isIteration)
			outputData(source, count);
	}

	public void run(SimulatorInputData simulatorInputData) throws Exception {
		String[] args = simulatorInputData.getArgs();
		printStream.set(simulatorInputData.getPrintStream());
		SimulationData simulationData = getSimulationData();
		simulationData.setCommandLine(args);
		Simulator.println("Java " + simulationData.getCommandLine());
		startTimer();
		myArguments = SimulationMain.parseArguments(getSimulationData(),
				SimulationUtils.changeArgs(args));
		SimulationMain.readSimulatonFile(this, myArguments);
		init(myArguments);
		if (myArguments.hasOption(SimulatorOptions.COMPILE)) {
			outputData();
			return;
		}
		if (!myArguments.hasOption(SimulatorOptions.DEBUG_INIT)) {

			if (myArguments.hasOption(SimulatorOptions.GENERATE_MAP)
					|| myArguments.hasOption(SimulatorOptions.CONTACT_MAP)) {
				Source source = addCompleteSource();
				outputData(source, 0);
			} else if (myArguments.hasOption(SimulatorOptions.NUMBER_OF_RUNS))
				runIterations();
			else if (myArguments.hasOption(SimulatorOptions.STORIFY))
				runStories();
			else
				run(0);

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
			getSimulationData().addInfo(
					new Info(Info.TYPE_INFO, "-Simulation..."));
			startTimer();
			TimerSimulation timer = new TimerSimulation(true);
			boolean isEndRules = false;
			long clash = 0;
			IRule rule;
			CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(
					getSimulationData());
			long max_clash = 0;
			getSimulationData().resetBar();
			while (!getSimulationData().isEndSimulation(currentTime, count)
					&& max_clash <= getSimulationData().getMaxClashes()) {
				checkPerturbation();
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

	public final IActivationMap getActivationMap() {
		return activationMap;
	}

	public int getAgentIdGenerator() {
		return agentIdGenerator;
	}

	public double getCurrentTime() {
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

	public SimulatorResultsData getSimulatorResultsData() {
		return simulatorResultsData;
	}

	public boolean isStoryMode() {
		return storyMode;
	}

}
