package com.plectix.simulator.simulator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

import com.plectix.simulator.components.CNetworkNotation;
import com.plectix.simulator.components.CProbabilityCalculation;
import com.plectix.simulator.components.CSnapshot;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.components.CStories;
import com.plectix.simulator.components.NameDictionary;
import com.plectix.simulator.components.SolutionLines;
import com.plectix.simulator.components.actions.CActionType;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.controller.SimulatorInterface;
import com.plectix.simulator.controller.SimulatorResultsData;
import com.plectix.simulator.interfaces.IAction;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.util.Info;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.RunningMetric;

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
		int number_of_observables = simulationData.getObservables().getComponentListForXMLOutput().size();

		if (iteration_num == 0) {
			simulationData.getTimeStamps().add(currentTime);

			for (int observable_num = 0; observable_num < number_of_observables; observable_num++) {
				runningMetrics.get(observable_num).add(new RunningMetric());
			}
		}

		for (int observable_num = 0; observable_num < number_of_observables; observable_num++) {
			// x is the value for the observable_num at the current time
			double x = simulationData.getObservables()
					.getComponentListForXMLOutput().get(observable_num)
					.getSize(simulationData.getObservables());
			if (timeStepCounter >= runningMetrics.get(observable_num).size()) {
				runningMetrics.get(observable_num).add(new RunningMetric());
			}
			runningMetrics.get(observable_num).get(timeStepCounter).add(x);
		}

		timeStepCounter++;
	}

	public final long generateNextAgentId() {
		return agentIdGenerator++;
	}

	public final void outputData() {
		outputRules();
		simulationData.outputPertubation();
		outputSolution();
	}

	private Source addCompleteSource() throws TransformerException, ParserConfigurationException {
		Source source = simulationData.createDOMModel();
		simulatorResultsData.addResultSource(source);
		return source;
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

	public final void resetSimulation() {
		simulationData.addInfo(new Info(Info.TYPE_INFO, "-Reset simulation data."));
		simulationData.addInfo(new Info(Info.TYPE_INFO, "-Initialization..."));
		
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

		if (simulationData.getSerializationMode() != SimulationData.MODE_READ) {
			simulationData.readSimulatonFile(this);
		}
		
		simulationData.initialize();
		
		simulationData.stopTimer(timer, "-Initialization:");
		simulationData.setClockStamp(System.currentTimeMillis());
	}

	public final void run(int iteration_num) throws Exception {
		simulationData.addInfo(new Info(Info.TYPE_INFO, "-Simulation..."));
		
		PlxTimer timer = new PlxTimer();
		timer.startTimer();
		
		CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(simulationData);

		long clash = 0;
		long count = 0;
		long max_clash = 0;
		boolean isEndRules = false;
		
		while (!simulationData.isEndSimulation(currentTime, count)
				&& max_clash <= simulationData.getMaxClashes()) {
			while (simulationData.checkSnapshots(currentTime)) {
				createSnapshots();				
			}
			simulationData.checkPerturbation(currentTime);
			IRule rule = ruleProbabilityCalculation.getRandomRule();

			if (rule == null) {
				isEndRules = true;
				simulationData.setTimeLength(currentTime);
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

				SimulationUtils.doNegativeUpdate(injectionsList);
				// positive update
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("positive update");

				simulationData.doPositiveUpdate(rule, injectionsList);

				simulationData.getObservables().calculateObs(currentTime, count, simulationData.isTime());
			} else {
				simulationData.addInfo(
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
		simulationData.getObservables().calculateObsLast(currentTime);
		simulationData.setTimeLength(currentTime);
		simulationData.setEvent(count);
		
		outToLogger(isEndRules, timer);
		Source source = addCompleteSource();
		
		if (!isIteration) {
			simulationData.outputData(source, count);
		}
	}
	
	private void checkOutputFinalState(){
		if(simulationData.isOutputFinalState())
			createSnapshots();
	}
	
	private void createSnapshots(){
		simulationData.addSnapshot(
				new CSnapshot(simulationData,currentTime));
//		simulationData.setSnapshotTime(currentTime);
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
				simulationData.outputData(source, 0);
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
		int seed = simulationData.getSeed();
		List<Double> timeStamps = new ArrayList<Double>();
		List<List<RunningMetric>> runningMetrics = new ArrayList<List<RunningMetric>>();
		simulationData.initIterations(timeStamps, runningMetrics);
		for (int iteration_num = 0; iteration_num < simulationData
				.getIterations(); iteration_num++) {
			// Initialize the Random Number Generator with seed = initialSeed +
			// i
			// We also need a new command line argument to feed the initialSeed.
			// See --seed argument of simplx.
			simulationData.setSeed(seed + iteration_num);

			// run the simulator
			timeStepCounter = 0;
			// while (true) {
			// run the simulation until the next time to dump the results
			run(iteration_num);
			// }

			// if the simulator's initial state is cached, reload it for next
			// run
			if (iteration_num < simulationData.getIterations() - 1)
				resetSimulation();

		}

		// we are done. report the results
		simulationData.createTMPReport();
		// Source source = addCompleteSource();
		// outputData(source, 0);
	}

	public final void runStories() throws Exception {
		CStories stories = simulationData.getStories();
		int count = 0;
		for (int i = 0; i < simulationData.getIterations(); i++) {
		    simulationData.addInfo(new Info(Info.TYPE_INFO, "-Simulation..."));
		    
			PlxTimer timer = new PlxTimer();
			timer.startTimer();
			
			boolean isEndRules = false;
			long clash = 0;
			IRule rule;
			long max_clash = 0;
			CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(
					simulationData);
		    simulationData.resetBar();
			while (!simulationData.isEndSimulation(currentTime, count)
					&& max_clash <= simulationData.getMaxClashes()) {
				
				simulationData.checkPerturbation(currentTime);
				rule = ruleProbabilityCalculation.getRandomRule();

				if (rule == null) {
					simulationData.setTimeLength(currentTime);
					Simulator.println("#");
					break;
				}

				List<IInjection> injectionsList = ruleProbabilityCalculation
						.getSomeInjectionList(rule);
				currentTime += ruleProbabilityCalculation.getTimeValue();
				if (!rule.isClash(injectionsList)) {
					CNetworkNotation netNotation = new CNetworkNotation(count,
							rule, injectionsList, simulationData);
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

					SimulationUtils.doNegativeUpdate(injectionsList);
					simulationData.doPositiveUpdate(rule, injectionsList);
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
		simulationData.outputData(source, count);
	}

	public final void setRules(List<IRule> rules) {
		simulationData.setRules(rules);
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
