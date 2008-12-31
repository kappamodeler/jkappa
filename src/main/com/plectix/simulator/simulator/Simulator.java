package com.plectix.simulator.simulator;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

import com.plectix.simulator.components.CNetworkNotation;
import com.plectix.simulator.components.CProbabilityCalculation;
import com.plectix.simulator.components.CStories;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.controller.SimulatorInterface;
import com.plectix.simulator.controller.SimulatorResultsData;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.util.Info;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.RunningMetric;

public class Simulator implements SimulatorInterface {

	private static final String NAME = "Java Simulator JSIM";
	
	private static final Logger LOGGER = Logger.getLogger(Simulator.class);

	private double currentTime = 0.0;

	private boolean isIteration = false;

	private int timeStepCounter = 0;
	
	private SimulationData simulationData = new SimulationData();

	private SimulatorResultsData simulatorResultsData = new SimulatorResultsData();
	

	public Simulator() {
		super();
	}

	private final void addIteration(int iteration_num) {
		// TODO: This method should be rewritten!!!
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


	public final void outputData() {
		simulationData.outputRules();
		simulationData.outputPertubation();
		simulationData.outputSolution();
	}

	private final Source addCompleteSource() throws TransformerException, ParserConfigurationException {
		Source source = simulationData.createDOMModel();
		simulatorResultsData.addResultSource(source);
		return source;
	}


	private final void outToLogger(boolean isEndRules, PlxTimer timer) {
		simulationData.stopTimer(timer, "-Simulation:");

		if (!isEndRules) {
			LOGGER.info("end of simulation: time");
		} else {
			LOGGER.info("end of simulation: there are no active rules");
		}
	}

	public final void resetSimulation() {
		currentTime = 0.0;
		simulationData.resetSimulation();
	}

	public final void run(int iteration_num) throws Exception {
		simulationData.addInfo(Info.TYPE_INFO, "-Simulation...");
		
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
				simulationData.createSnapshots(currentTime);				
			}
			
			simulationData.checkPerturbation(currentTime);
			IRule rule = ruleProbabilityCalculation.getRandomRule();

			if (rule == null) {
				isEndRules = true;
				simulationData.setTimeLength(currentTime);
				simulationData.println("#");
				break;
			}
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Rule: " + rule.getName());
			}

			List<IInjection> injectionsList = ruleProbabilityCalculation.getSomeInjectionList(rule);
			if (!rule.isInfinityRate()) {
				currentTime += ruleProbabilityCalculation.getTimeValue();
			}

			if (!rule.isClash(injectionsList)) {
				// negative update
				max_clash = 0;
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("negative update");

				count++;
				rule.applyRule(injectionsList, simulationData);

				SimulationUtils.doNegativeUpdate(injectionsList);
				
				// positive update
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("positive update");
				}

				simulationData.doPositiveUpdate(rule, injectionsList);

				simulationData.getObservables().calculateObs(currentTime, count, simulationData.isTime());
			} else {
				simulationData.addInfo(Info.TYPE_INTERNAL, "Application of rule exp is clashing");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Clash");
				}
				clash++;
				max_clash++;
			}

			if (isIteration) {
				addIteration(iteration_num);
			}
		}
		
		simulationData.checkOutputFinalState(currentTime);
		simulationData.getObservables().calculateObsLast(currentTime);
		simulationData.setTimeLength(currentTime);
		simulationData.setEvent(count);
		
		outToLogger(isEndRules, timer);
		Source source = addCompleteSource();
		
		if (!isIteration) {
			simulationData.outputData(source, count);
		}
	}
	

	public final void run(SimulatorInputData simulatorInputData) throws Exception {
		String[] args = simulatorInputData.getArguments();

		simulationData.setCommandLine(args);
		simulationData.setPrintStream(simulatorInputData.getPrintStream());
		
		simulationData.println("Java " + simulationData.getCommandLine());

		PlxTimer timer = new PlxTimer();
		timer.startTimer();
		
		simulationData.parseArguments(args);
		simulationData.readSimulatonFile();
		simulationData.initialize();
		
		simulationData.stopTimer(timer, "-Initialization:");
		simulationData.setClockStamp(System.currentTimeMillis());
		
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
		}
		
		System.out.println("-------" + simulatorResultsData.getResultSource());
	}

	public final void runIterations() throws Exception {
		isIteration = true;
		int seed = simulationData.getSeed();
		List<Double> timeStamps = new ArrayList<Double>();
		List<List<RunningMetric>> runningMetrics = new ArrayList<List<RunningMetric>>();
		simulationData.initIterations(timeStamps, runningMetrics);
		
		for (int iteration_num = 0; iteration_num < simulationData.getIterations(); iteration_num++) {
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
			if (iteration_num < simulationData.getIterations() - 1) {
				resetSimulation();
			}

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
		    simulationData.addInfo(Info.TYPE_INFO, "-Simulation...");
		    
			PlxTimer timer = new PlxTimer();
			timer.startTimer();
			
			boolean isEndRules = false;
			long clash = 0;
			long max_clash = 0;
			CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(simulationData);
		    simulationData.resetBar();
		    
		    while (!simulationData.isEndSimulation(currentTime, count)
					&& max_clash <= simulationData.getMaxClashes()) {
				
				simulationData.checkPerturbation(currentTime);
				IRule rule = ruleProbabilityCalculation.getRandomRule();

				if (rule == null) {
					simulationData.setTimeLength(currentTime);
					simulationData.println("#");
					break;
				}

				List<IInjection> injectionsList = ruleProbabilityCalculation.getSomeInjectionList(rule);
				
				if (!rule.isInfinityRate()) {
					currentTime += ruleProbabilityCalculation.getTimeValue();
				}
				if (!rule.isClash(injectionsList)) {
					CNetworkNotation netNotation = new CNetworkNotation(count, rule, injectionsList, simulationData);
					max_clash = 0;
					if (stories.checkRule(rule.getRuleID(), i)) {
						rule.applyLastRuleForStories(injectionsList,netNotation);
						rule.applyRuleForStories(injectionsList, netNotation, simulationData, true);
						stories.addToNetworkNotationStoryStorifyRule(i, netNotation, currentTime);
						// stories.addToNetworkNotationStory(i, netNotation);
						count++;
						isEndRules = true;
						simulationData.println("#");
						break;
					}
					
					rule.applyRuleForStories(injectionsList, netNotation, simulationData, false);
					if (!rule.isRHSEqualsLHS()) {
						stories.addToNetworkNotationStory(i, netNotation);
					}
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
			
			if (i < simulationData.getIterations() - 1) {
				resetSimulation();
			}
		}
		
		stories.merge();
		Source source = addCompleteSource();
		simulationData.outputData(source, count);
	}
	
	//////////////////////////////////////////////////////////////////////////
	//
	//                    GETTERS AND SETTERS
	//
	//

	public final double getCurrentTime() {
		return currentTime;
	}

	public final String getName() {
		return NAME;
	}

	public final SimulationData getSimulationData() {
		return simulationData;
	}

	public final SimulatorResultsData getSimulatorResultsData() {
		return simulatorResultsData;
	}

}
