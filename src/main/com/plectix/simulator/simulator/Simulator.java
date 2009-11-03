package com.plectix.simulator.simulator;

import java.util.List;

import com.plectix.simulator.BuildConstants;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.controller.SimulatorInterface;
import com.plectix.simulator.controller.SimulatorResultsData;
import com.plectix.simulator.controller.SimulatorStatusInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulationclasses.perturbations.Perturbation;
import com.plectix.simulator.simulationclasses.perturbations.PerturbationType;
import com.plectix.simulator.simulationclasses.solution.OperationMode;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.RuleApplicator;
import com.plectix.simulator.staticanalysis.stories.Stories;
import com.plectix.simulator.staticanalysis.stories.storage.EventBuilder;
import com.plectix.simulator.streaming.LiveData;
import com.plectix.simulator.streaming.LiveDataSourceInterface;
import com.plectix.simulator.streaming.LiveDataStreamer;
import com.plectix.simulator.util.MemoryUtil;
import com.plectix.simulator.util.PlxLogger;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.RunningMetric;
import com.plectix.simulator.util.Info.InfoType;
import com.plectix.simulator.util.MemoryUtil.PeakMemoryUsage;

public final class Simulator implements SimulatorInterface {

	private static final String NAME = "Java Simulator JSIM";
	private static final String INTRO_MESSAGE = "JSIM: Build on " + BuildConstants.BUILD_DATE 
											  + " from Revision " + BuildConstants.BUILD_SVN_REVISION
											  + ", JRE: " + System.getProperty("java.vendor") + " " + System.getProperty("java.version");
	private static final String STATUS_INITIALIZING = "Initializing";
	private static final String STATUS_RUNNING = "Running";
	private static final String STATUS_WRAPPING = "Wrapping the simulation results";
	private static final String STATUS_IDLE = "Idle";
	private static final String STATUS_EXCEPTION = "Exception thrown: ";

	private static final PlxLogger LOGGER = ThreadLocalData.getLogger(Simulator.class);

	/** Use synchronized (statusLock) when changing the value of this variable */
	private double currentTime = 0.0;
	/** Use synchronized (statusLock) when changing the value of this variable */
	private long currentEventNumber = 0;
	/** Use synchronized (statusLock) when changing the value of this variable */
	private int currentIterationNumber = 0;
	private boolean CSiteration = false;
	private int timeStepCounter = 0;
	
	private final SimulationData simulationData = new SimulationData();
	private final SimulatorStatus simulatorStatus = new SimulatorStatus();
	private final SimulatorResultsData simulatorResultsData = new SimulatorResultsData();
	private final LiveDataStreamer liveDataStreamer = new LiveDataStreamer();
	
	/** Object to lock when we are reading variables to compute the current status */
	private final Object statusLock = new Object();
	private final RuleApplicator ruleApplicator = new RuleApplicator(simulationData);
	
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
				progress = simulatorStatus.getCurrentEventNumber() * 1.0 / simulationArguments.getMaxNumberOfEvents();
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
	

	/**
	 * Returns the live data
	 * 
	 * @param liveData
	 * @return
	 */
	public final LiveData getLiveData() {
		return liveDataStreamer.getLiveData();
	}
	
	@Override
	public final void cleanUpAfterException(Exception exception) {
		simulatorStatus.setStatusMessage(STATUS_EXCEPTION + exception.getClass().getName());
		simulatorStatus.setProgress(1.0);
	}

	private final void addIteration(int iteration) {
		// TODO: This method should be rewritten!!!
		List<List<RunningMetric>> runningMetrics = simulationData.getRunningMetrics();
		int number_of_observables = simulationData.getKappaSystem().getObservables().getUniqueComponentList().size();

		if (iteration == 0) {
			simulationData.getTimeStamps().add(currentTime);

			for (int observable_num = 0; observable_num < number_of_observables; observable_num++) {
				runningMetrics.get(observable_num).add(new RunningMetric());
			}
		}

		for (int observable_num = 0; observable_num < number_of_observables; observable_num++) {
			// x is the value for the observable_num at the current time
			double x = simulationData.getKappaSystem().getObservables()
					.getUniqueComponentList().get(observable_num)
					.getCurrentState(simulationData.getKappaSystem().getObservables());
			if (timeStepCounter >= runningMetrics.get(observable_num).size()) {
				runningMetrics.get(observable_num).add(new RunningMetric());
			}
			runningMetrics.get(observable_num).get(timeStepCounter).add(x);
		}

		timeStepCounter++;
	}

	private final void endMerge(PlxTimer timer){
		simulationData.stopTimer(InfoType.OUTPUT,timer, "-Merge stories:");
	}


	private final void endSimulation(InfoType outputType, boolean noRulesLeft, PlxTimer timer) {
		outputType = simulationData.getSimulationArguments().getOutputTypeForAdditionalInfo();
		simulationData.stopTimer(outputType,timer, "-Simulation:");

		switch (outputType) {
		case OUTPUT:
			if (noRulesLeft) {
				LOGGER.info("end of simulation: there are no active rules");
			} else {
				LOGGER.info("end of simulation: time");
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
		simulatorStatus.setStatusMessage(STATUS_INITIALIZING);
	
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
			if (simulationData.getSimulationArguments().isGenereteMap() || 
					simulationData.getSimulationArguments().getSimulationType() == SimulationType.CONTACT_MAP){
					//simulationData.getSimulationArguments().isContactMap() ) {
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
			simulationData.addInfo(InfoType.OUTPUT, InfoType.INFO, "-Peak Memory Usage (in bytes): " + peakMemoryUsage 
					+ " [period= " + simulationData.getSimulationArguments().getMonitorPeakMemory() + " milliseconds]");
		}
		
		// Output XML data:
		simulationData.outputXMLData();
		
		simulatorStatus.setStatusMessage(STATUS_IDLE);
	}

	public final void run(int iteration_num) throws Exception {
		simulationData.addInfo(InfoType.OUTPUT, InfoType.INFO, "-Simulation...");
		
		PlxTimer timer = new PlxTimer();
		timer.startTimer();
		
		int seed = simulationData.getSimulationArguments().getSeed();
		simulationData.addInfo(InfoType.OUTPUT, InfoType.INFO,
				"--Seeding random number generator with given seed "
						+ Integer.valueOf(seed).toString());
		
		synchronized (statusLock) {
			currentEventNumber = 0;
		}
		
		long clash = 0;
		long max_clash = 0;
		boolean isEndRules = false;
		LiveDataSourceInterface liveDataSource = new ObservablesLiveDataSource(simulationData.getKappaSystem().getObservables());
		liveDataStreamer.reset(simulationData.getSimulationArguments().getLiveDataInterval(), simulationData.getSimulationArguments().getLiveDataPoints(), 
				simulationData.getSimulationArguments().getLiveDataConsumerClassname(), liveDataSource);
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
			Rule rule = simulationData.getKappaSystem().getRandomRule();
			if (rule == null) {
				List<Perturbation> perturbations = simulationData
						.getKappaSystem().getPerturbations();
				double tmpTime = simulationData.getSimulationArguments()
						.getTimeLength();
				Perturbation tmpPerturbation = null;
				for (Perturbation perturbation : perturbations) {
					if (perturbation.getType() == PerturbationType.TIME
							&& perturbation.getTimeCondition() > currentTime
							&& perturbation.getTimeCondition() < tmpTime) {
						tmpTime = perturbation.getTimeCondition();
						tmpPerturbation = perturbation;
					}
				}
				// TODO is it right way: to add timeSampleMin to currentTime for
				// applying the perturbation?
				if (tmpPerturbation != null){
					currentTime = tmpPerturbation.getTimeCondition()
							+ simulationData.getKappaSystem().getObservables()
									.getTimeSampleMin();
					simulationData.getKappaSystem().checkPerturbation(currentTime);
					rule = simulationData.getKappaSystem().getRandomRule();
				} else {
					isEndRules = true;
					simulationData.setTimeLength(currentTime);
					simulationData.println("#");
					break;
				}
			}
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Rule: " + rule.getName());
			}
	
			List<Injection> injectionsList = 
				simulationData.getKappaSystem().chooseInjectionsForRuleApplication(rule);
	
			if (injectionsList != null) {
				// negative update
				max_clash = 0;
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("negative update");
	
				synchronized (statusLock) {
					currentEventNumber++;
				}

				List<Injection> newInjections = ruleApplicator.applyRule(rule, injectionsList, simulationData);
				if (newInjections != null) {
	
					UpdatesPerformer.doNegativeUpdate(newInjections);
				
					// positive update
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("positive update");
					}
	
					// on the 4th mode we should set only super injections, so we do it manually 
					// directly after the rule application
					if (simulationData.getSimulationArguments().getOperationMode() != OperationMode.FOURTH) {
					simulationData.getKappaSystem().doPositiveUpdate(rule, newInjections);
					}
	
					simulationData.getKappaSystem().getSolution().flushPoolContent(rule.getPool());
				
					simulationData.getKappaSystem().getObservables().calculateObs(currentTime, currentEventNumber, simulationData.getSimulationArguments().isTime());
				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Rule rejected");
					}
				}
			} else {
				simulationData.addInfo(InfoType.DO_NOT_OUTPUT,InfoType.INTERNAL, "Application of rule exp is clashing");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Clash");
				}
				clash++;
				max_clash++;
			}
	
			if (!rule.hasInfiniteRate()) {
				synchronized (statusLock) {
					currentTime += simulationData.getKappaSystem().getTimeValue();
				}
				liveDataStreamer.addNewDataPoint(currentEventNumber, currentTime);
			}
			
			if (CSiteration) {
				addIteration(iteration_num);
			}
		}
	
		liveDataStreamer.stop();
		simulatorStatus.setStatusMessage(STATUS_WRAPPING);
		
		simulationData.checkOutputFinalState(currentTime);
		simulationData.getKappaSystem().getObservables().calculateObsLast(currentTime, currentEventNumber);
		simulationData.setTimeLength(currentTime);
		simulationData.setEvent(currentEventNumber);
		
		endSimulation(InfoType.OUTPUT, isEndRules, timer);
	}

	public final void runIterations() throws Exception {
		CSiteration = true;
		int seed = simulationData.getSimulationArguments().getSeed();
		simulationData.initIterations();
		
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
		Stories stories = simulationData.getKappaSystem().getStories();
		
		synchronized (statusLock) {
			currentEventNumber = 0;
		}
		
		InfoType additionalInfoOutputType = simulationData.getSimulationArguments().getOutputTypeForAdditionalInfo();
		simulationData.addInfo(additionalInfoOutputType, InfoType.INFO, "-Simulation...");
		
		
		simulationData.resetBar();
		PlxTimer timerAllStories = new PlxTimer();
		timerAllStories.startTimer();

		simulatorStatus.setStatusMessage(STATUS_RUNNING);
		synchronized (statusLock) {
			currentIterationNumber = 0;
		}
		
		int seed = simulationData.getSimulationArguments().getSeed();
		simulationData.addInfo(additionalInfoOutputType, InfoType.INFO,
					"--Seeding random number generator with given seed "
							+ Integer.valueOf(seed).toString());
		
		EventBuilder eventBuilder = new EventBuilder();
		while (currentIterationNumber < simulationData.getSimulationArguments().getIterations()) {
			PlxTimer timer = null;
			if (additionalInfoOutputType != InfoType.DO_NOT_OUTPUT){
				simulationData.addInfo(additionalInfoOutputType,InfoType.INFO, "-Simulation...");
				timer = new PlxTimer();
				timer.startTimer();
			}
			
			boolean isEndRules = false;
			long clash = 0;
			long max_clash = 0;
			simulationData.getStoriesAgentTypesStorage().setIteration(currentIterationNumber);
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
				Rule rule = simulationData.getKappaSystem().getRandomRule();

				if (rule == null) {
					simulationData.setTimeLength(currentTime);
					simulationData.printlnBar();
					break;
				}

				List<Injection> injectionsList 
					= simulationData.getKappaSystem().chooseInjectionsForRuleApplication(rule);
				if (!rule.hasInfiniteRate()) {
					synchronized (statusLock) {
						currentTime += simulationData.getKappaSystem().getTimeValue();
					}
				}
				if (injectionsList!=null) {
					eventBuilder.setNewEvent(currentEventNumber,rule.getRuleId());
					max_clash = 0;
					//what is this??
					if (stories.checkRule(rule.getRuleId(), currentIterationNumber)) {
						rule.applyRuleForStories(injectionsList, eventBuilder, simulationData, true);
						stories.addLastEventToStoryStorifyRule(currentIterationNumber, eventBuilder.getEvent(), currentTime);
						synchronized (statusLock) {
							currentEventNumber++;
						}
						isEndRules = true;
						simulationData.printlnBar();
						break;
					}
					
					rule.applyRuleForStories(injectionsList, eventBuilder, simulationData, false);
					if (!rule.doesNothing()) {
						stories.addEventToStory(currentIterationNumber, eventBuilder.getEvent());
					}
					synchronized (statusLock) {
						currentEventNumber++;
					}

					UpdatesPerformer.doNegativeUpdate(injectionsList);
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
			
			stories.cleaningStory(currentIterationNumber);
			endSimulation(additionalInfoOutputType,isEndRules, timer);
			
			if (currentIterationNumber < simulationData.getSimulationArguments().getIterations() - 1) {
				resetSimulation(InfoType.DO_NOT_OUTPUT);
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
		
		if(additionalInfoOutputType != InfoType.DO_NOT_OUTPUT){
			simulationData.println("#");
			endSimulation(InfoType.OUTPUT, false, timerAllStories);
		}
		
		PlxTimer mergeTimer= new PlxTimer();
		mergeTimer.startTimer();
		endMerge(mergeTimer);
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
