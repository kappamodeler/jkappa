package com.plectix.simulator.simulator;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import com.plectix.simulator.BuildConstants;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.stories.CNetworkNotation;
import com.plectix.simulator.components.stories.CStories;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.controller.SimulatorInterface;
import com.plectix.simulator.controller.SimulatorResultsData;
import com.plectix.simulator.controller.SimulatorStatusInterface;
import com.plectix.simulator.probability.CProbabilityCalculation;
import com.plectix.simulator.util.MemoryUtil;
import com.plectix.simulator.util.PlxLogger;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.RunningMetric;
import com.plectix.simulator.util.Info.InfoType;
import com.plectix.simulator.util.MemoryUtil.PeakMemoryUsage;

public class Simulator implements SimulatorInterface {

	private static final String NAME = "Java Simulator JSIM";
	
	private static final String INTRO_MESSAGE = "JSIM: Build on " + BuildConstants.BUILD_DATE 
											  + " from SVN Revision " + BuildConstants.BUILD_SVN_REVISION
											  + ", JRE: " + System.getProperty("java.vendor") + " " + System.getProperty("java.version");
	
	private static final String STATUS_RUNNING = "Running";
	
	private static final String STATUS_WRAPPING = "Wrapping the simulation results";
	
	private static final String STATUS_IDLE = "Idle";
	
	private static final PlxLogger LOGGER = ThreadLocalData.getLogger(Simulator.class);

	/** Use synchronized (statusLock) when changing the value of this variable */
	private double currentTime = 0.0;
	
	/** Use synchronized (statusLock) when changing the value of this variable */
	private long currentEventNumber = 0;
	
	/** Use synchronized (statusLock) when changing the value of this variable */
	private int currentIterationNumber = 0;

	private boolean CSiteration = false;

	private int timeStepCounter = 0;
	
	private SimulationData simulationData = new SimulationData();
	
	private SimulatorStatus simulatorStatus = new SimulatorStatus();

	private SimulatorResultsData simulatorResultsData = new SimulatorResultsData();
	
	/** Object to lock when we are reading variables to compute the current status */
	private Object statusLock = new Object();
	
	public Simulator() {
		super();
		simulatorStatus.setStatusMessage(STATUS_IDLE);
	}

	/**
	 * We assume that this method is called from a separate thread than the simulation thread.
	 * We also assume that there can be only one thread calling this method at a time.
	 * 
	 */
	public final SimulatorStatusInterface getStatus() {
		synchronized (statusLock) {
			// save the current state variables in the status object and use them below
			simulatorStatus.setCurrentTime(currentTime);
			simulatorStatus.setCurrentEventNumber(currentEventNumber);
			simulatorStatus.setCurrentIterationNumber(currentIterationNumber);
		}

		// let's compute our progress:
		double progress = simulatorStatus.getProgress();
		if (Double.isNaN(progress) || progress < 1.0) {
			SimulationArguments simulationArguments = simulationData.getSimulationArguments();
			if (simulationArguments.isTime()) {
				progress = simulatorStatus.getCurrentTime() / simulationArguments.getTimeLength();
			} else {
				progress = simulatorStatus.getCurrentEventNumber() * 1.0 / simulationArguments.getEvent();
			}

			if (simulationArguments.isStorify()) {
				progress = (progress + simulatorStatus.getCurrentIterationNumber()) / simulationArguments.getIterations();
				if (progress > 1.0) {
					progress = 1.0;
				}
			}
			simulatorStatus.setProgress(progress);
		}

		return simulatorStatus;
	}
	
	private final void addIteration(int iteration_num) {
		// TODO: This method should be rewritten!!!
		List<List<RunningMetric>> runningMetrics = simulationData.getRunningMetrics();
		int number_of_observables = simulationData.getKappaSystem().getObservables().getComponentListForXMLOutput().size();

		if (iteration_num == 0) {
			simulationData.getTimeStamps().add(currentTime);

			for (int observable_num = 0; observable_num < number_of_observables; observable_num++) {
				runningMetrics.get(observable_num).add(new RunningMetric());
			}
		}

		for (int observable_num = 0; observable_num < number_of_observables; observable_num++) {
			// x is the value for the observable_num at the current time
			double x = simulationData.getKappaSystem().getObservables()
					.getComponentListForXMLOutput().get(observable_num)
					.getCurrentState(simulationData.getKappaSystem().getObservables());
			if (timeStepCounter >= runningMetrics.get(observable_num).size()) {
				runningMetrics.get(observable_num).add(new RunningMetric());
			}
			runningMetrics.get(observable_num).get(timeStepCounter).add(x);
		}

		timeStepCounter++;
	}

	private final Source addCompleteSource() throws TransformerException, ParserConfigurationException {
		Source source = simulationData.createDOMModel();
		simulatorResultsData.addResultSource(source);
		return source;
	}
	
	private final void endOfMerge(PlxTimer timer){
		simulationData.stopTimer(InfoType.OUTPUT,timer, "-Merge stories:");
	}


	private final void endOfSimulation(InfoType outputType,boolean isEndRules, PlxTimer timer) {
		if(!simulationData.getSimulationArguments().isShortConsoleOutput())
			outputType = InfoType.OUTPUT;
		simulationData.stopTimer(outputType,timer, "-Simulation:");

		switch (outputType) {
		case OUTPUT:
			if (!isEndRules) {
				LOGGER.info("end of simulation: time");
			} else {
				LOGGER.info("end of simulation: there are no active rules");
			}
			break;
		}
	}

	public final void resetSimulation(InfoType outputType) {
		synchronized (statusLock) {
			currentTime = 0.0;
		}
		simulationData.resetSimulation(outputType);
	}

	public final void run(SimulatorInputData simulatorInputData) throws Exception {
		// add info about JSIM:
		simulationData.addInfo(InfoType.OUTPUT, InfoType.INFO, INTRO_MESSAGE);
	
		PlxTimer timer = new PlxTimer();
		timer.startTimer();

		simulationData.setPrintStream(simulatorInputData.getPrintStream());
		simulationData.setSimulationArguments(InfoType.OUTPUT,simulatorInputData.getSimulationArguments());
		simulationData.readSimulatonFile(InfoType.OUTPUT);
		simulationData.getKappaSystem().initialize(InfoType.OUTPUT);

		simulationData.stopTimer(InfoType.OUTPUT, timer, "-Initialization:");
		simulationData.setClockStamp(System.currentTimeMillis());
		
		if (simulationData.getSimulationArguments().isCompile()) {
			simulationData.outputData();
			return;
		}
		
		if (!simulationData.getSimulationArguments().isDebugInit()) {
			if (simulationData.getSimulationArguments().isGenereteMap() || simulationData.getSimulationArguments().isContactMap() ) {
				// nothing to do in this case... outputData is called below...
			} else if (simulationData.getSimulationArguments().isNumberOfRuns()) {
				// this mode needs to be re-implemented
				// runIterations();
				// throw an Exception for now:
				throw new RuntimeException("Iterations mode is not supported at this point!");
			} else if (simulationData.getSimulationArguments().isStorify()) {
				runStories();
			} else {
				run(0);
			}
		}
		
		// Let's see if we monitor peak memory usage
		PeakMemoryUsage peakMemoryUsage = MemoryUtil.getPeakMemoryUsage();
		if (peakMemoryUsage != null) {
			peakMemoryUsage.update();
			simulationData.addInfo(InfoType.OUTPUT, InfoType.INFO, "-Peak Memory Usage (in bytes): " + peakMemoryUsage);
		}
		
		// Output XML data:
		Source source = addCompleteSource();
		simulationData.outputData(source, currentEventNumber);
		
		simulatorStatus.setStatusMessage(STATUS_IDLE);
		
		// simulationData.println("-------" + simulatorResultsData.getResultSource());
	}

	public final void run(int iteration_num) throws Exception {
		simulationData.addInfo(InfoType.OUTPUT, InfoType.INFO, "-Simulation...");
		
		PlxTimer timer = new PlxTimer();
		timer.startTimer();
		
		CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(InfoType.OUTPUT,simulationData);
	
		synchronized (statusLock) {
			currentEventNumber = 0;
		}
		long clash = 0;
		long max_clash = 0;
		boolean isEndRules = false;
		simulatorStatus.setStatusMessage(STATUS_RUNNING);
		
		while (!simulationData.isEndSimulation(currentTime, currentEventNumber)
				&& max_clash <= simulationData.getSimulationArguments().getMaxClashes()) {
			if (Thread.interrupted())  {
				// TODO: Do any necessary clean-up and collect data we can return
				simulationData.println("Simulation is interrupted because the thread is cancelled");
				simulatorResultsData.setCancelled(true);
				simulatorStatus.setProgress(1.0);
				break;
			}
			
			while (simulationData.checkSnapshots(currentTime)) {
				simulationData.createSnapshots(currentTime);				
			}
			
			simulationData.getKappaSystem().checkPerturbation(currentTime);
			CRule rule = ruleProbabilityCalculation.getRandomRule();
	
			if (rule == null) {
				isEndRules = true;
				simulationData.setTimeLength(currentTime);
				simulationData.println("#");
				break;
			}
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Rule: " + rule.getName());
			}
	
			List<CInjection> injectionsList = ruleProbabilityCalculation.getSomeInjectionList(rule);
			if (!rule.isInfiniteRated()) {
				synchronized (statusLock) {
					currentTime += ruleProbabilityCalculation.getTimeValue();
				}
			}
	
			if (!rule.isClash(injectionsList)) {
				// negative update
				max_clash = 0;
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("negative update");
	
				synchronized (statusLock) {
					currentEventNumber++;
				}
				rule.applyRule(injectionsList, simulationData);
	
				SimulationUtils.doNegativeUpdate(injectionsList);
				
				// positive update
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("positive update");
				}
	
				simulationData.getKappaSystem().doPositiveUpdate(rule, injectionsList);
	
				simulationData.getKappaSystem().getObservables().calculateObs(currentTime, currentEventNumber, simulationData.getSimulationArguments().isTime());
			} else {
				simulationData.addInfo(InfoType.NOT_OUTPUT,InfoType.INTERNAL, "Application of rule exp is clashing");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Clash");
				}
				clash++;
				max_clash++;
			}
	
			if (CSiteration) {
				addIteration(iteration_num);
			}
		}
	
		simulatorStatus.setStatusMessage(STATUS_WRAPPING);
		
		simulationData.checkOutputFinalState(currentTime);
		simulationData.getKappaSystem().getObservables().calculateObsLast(currentTime);
		simulationData.setTimeLength(currentTime);
		simulationData.setEvent(currentEventNumber);
		
		endOfSimulation(InfoType.OUTPUT, isEndRules, timer);
	}

	public final void runIterations() throws Exception {
		CSiteration = true;
		int seed = simulationData.getSimulationArguments().getSeed();
		List<Double> timeStamps = new ArrayList<Double>();
		List<List<RunningMetric>> runningMetrics = new ArrayList<List<RunningMetric>>();
		simulationData.initIterations(timeStamps, runningMetrics);
		
		for (int iteration_num = 0; iteration_num < simulationData.getSimulationArguments().getIterations(); iteration_num++) {
			// Initialize the Random Number Generator with seed = initialSeed +
			// i
			// We also need a new command line argument to feed the initialSeed.
			// See --seed argument of simplx.
			simulationData.getSimulationArguments().setSeed(seed + iteration_num);

			// run the simulator
			timeStepCounter = 0;
			// while (true) {
			// run the simulation until the next time to dump the results
			run(iteration_num);
			// }

			// if the simulator's initial state is cached, reload it for next
			// run
			if (iteration_num < simulationData.getSimulationArguments().getIterations() - 1) {
				resetSimulation(InfoType.OUTPUT);
			}

		}

		// we are done. report the results
		simulationData.createTMPReport();
		// Source source = addCompleteSource();
		// outputData(source, 0);
	}

	public final void runStories() throws Exception {
		CStories stories = simulationData.getKappaSystem().getStories();
		
		synchronized (statusLock) {
			currentEventNumber = 0;
		}
		
		if(simulationData.getSimulationArguments().isShortConsoleOutput()) {
			simulationData.addInfo(InfoType.OUTPUT,InfoType.INFO, "-Simulation...");
		}
		
		simulationData.resetBar();
		PlxTimer timerAllStories = new PlxTimer();
		timerAllStories.startTimer();

		simulatorStatus.setStatusMessage(STATUS_RUNNING);
		synchronized (statusLock) {
			currentIterationNumber = 0;
		}
		
		while (currentIterationNumber < simulationData.getSimulationArguments().getIterations()) {
			PlxTimer timer=null;
			if(!simulationData.getSimulationArguments().isShortConsoleOutput()){
				simulationData.addInfo(InfoType.OUTPUT,InfoType.INFO, "-Simulation...");
				timer = new PlxTimer();
				timer.startTimer();
			}
			
			boolean isEndRules = false;
			long clash = 0;
			long max_clash = 0;
			CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(InfoType.NOT_OUTPUT,simulationData);
		    
		    while (!simulationData.isEndSimulation(currentTime, currentEventNumber)
					&& max_clash <= simulationData.getSimulationArguments().getMaxClashes()) {
				if (Thread.interrupted())  {
					// TODO: Do any necessary clean-up and collect data we can return
					simulationData.println("Simulation is interrupted because the thread is cancelled");
					simulatorResultsData.setCancelled(true);
					simulatorStatus.setProgress(1.0);
					break;
				}
				
				simulationData.getKappaSystem().checkPerturbation(currentTime);
				CRule rule = ruleProbabilityCalculation.getRandomRule();

				if (rule == null) {
					simulationData.setTimeLength(currentTime);
					simulationData.printlnBar();
					break;
				}

				List<CInjection> injectionsList = ruleProbabilityCalculation.getSomeInjectionList(rule);
				
				if (!rule.isInfiniteRated()) {
					synchronized (statusLock) {
						currentTime += ruleProbabilityCalculation.getTimeValue();
					}
				}
				if (!rule.isClash(injectionsList)) {
					// TODO: Make sure that CNetworkNotation works with long event number, not integer
					CNetworkNotation netNotation = new CNetworkNotation(this, (int)currentEventNumber, rule, injectionsList, simulationData);
					max_clash = 0;
					if (stories.checkRule(rule.getRuleID(), currentIterationNumber)) {
						rule.applyLastRuleForStories(injectionsList,netNotation);
						rule.applyRuleForStories(injectionsList, netNotation, simulationData, true);
						stories.addToNetworkNotationStoryStorifyRule(currentIterationNumber, netNotation, currentTime);
						synchronized (statusLock) {
							currentEventNumber++;
						}
						isEndRules = true;
						simulationData.printlnBar();
						break;
					}
					
					rule.applyRuleForStories(injectionsList, netNotation, simulationData, false);
					netNotation.fillAddedAgentsID(simulationData);
					if (!rule.isRHSEqualsLHS()) {
						stories.addToNetworkNotationStory(currentIterationNumber, netNotation);
					}
					synchronized (statusLock) {
						currentEventNumber++;
					}

					SimulationUtils.doNegativeUpdate(injectionsList);
					simulationData.getKappaSystem().doPositiveUpdate(rule, injectionsList);
				} else {
					clash++;
					max_clash++;
				}
				
			} // end of simulation here...
		    
			simulationData.checkStoriesBar(currentIterationNumber);
			synchronized (statusLock) {
				currentEventNumber = 0;
			}
			stories.handling(currentIterationNumber);
			
			if(!simulationData.getSimulationArguments().isShortConsoleOutput()) {
				endOfSimulation(InfoType.OUTPUT,isEndRules, timer);
			}
			
			if (currentIterationNumber < simulationData.getSimulationArguments().getIterations() - 1) {
				resetSimulation(InfoType.NOT_OUTPUT);
			}
			
			// check whether the thread is interrupted above or since then...
			if (simulatorResultsData.isCancelled() || Thread.interrupted())  {
				// TODO: Do any necessary clean-up and collect data we can return
				simulationData.println("Simulation is interrupted because the thread is cancelled");
				simulatorResultsData.setCancelled(true);
				simulatorStatus.setProgress(1.0);
				break;
			}
			
			synchronized (statusLock) {
				currentIterationNumber++;
			}
		} // end of iteration here...

		simulatorStatus.setStatusMessage(STATUS_WRAPPING);
		
		if(simulationData.getSimulationArguments().isShortConsoleOutput()){
			simulationData.println("#");
			endOfSimulation(InfoType.OUTPUT, false, timerAllStories);
		}
		
		PlxTimer mergeTimer= new PlxTimer();
		mergeTimer.startTimer();
		stories.merge();
		endOfMerge(mergeTimer);
	}
	
	//////////////////////////////////////////////////////////////////////////
	//
	//                    GETTERS AND SETTERS
	//
	//

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
