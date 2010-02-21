package com.plectix.simulator.simulator.api.steps;

import java.util.List;

import com.plectix.simulator.controller.SimulatorResultsData;
import com.plectix.simulator.controller.SimulatorStatusInterface;
import com.plectix.simulator.io.ConsoleOutputManager;
import com.plectix.simulator.io.SimulationDataReader;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationClock;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationState;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorMessage;
import com.plectix.simulator.simulator.SimulatorStatus;
import com.plectix.simulator.simulator.UpdatesPerformer;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;
import com.plectix.simulator.simulator.SimulationArguments.StoryCompressionMode;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.stories.Stories;
import com.plectix.simulator.staticanalysis.stories.storage.EventBuilder;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.Info.InfoType;
import com.plectix.simulator.util.io.PlxLogger;

public class StoriesComputationOperation extends AbstractOperation<Object> {
	private final Simulator simulator; 
	private final long events;
	private final double time; 
	private final StoryCompressionMode mode; 
	private final int iterationsNumber;
	
	public StoriesComputationOperation(Simulator simulator) {
		super(simulator.getSimulationData(), OperationType.STORIES);
		
		this.simulator = simulator; 
		
		SimulationData simulationData = simulator.getSimulationData();
		SimulationArguments arguments = simulationData.getSimulationArguments();
		this.mode = arguments.getStorifyMode(); 
		this.iterationsNumber = arguments.getIterations();
		this.correctSimulationArguments();
		
		if (simulationData.getSimulationArguments().isTime()) {
			this.events = -1;
			this.time = arguments.getTimeLimit(); 
		} else {
			this.events = arguments.getMaxNumberOfEvents();
			this.time = -1; 
			simulationData.getConsoleOutputManager().println("*Warning* No time limit.");
		}
	}

	private void correctSimulationArguments() {
		SimulationArguments simulationArguments = simulator.getSimulationData().getSimulationArguments();
		simulationArguments.setSimulationType(SimulationType.STORIFY);
		simulationArguments.setStorifyFlag(true);
		simulationArguments.setIterations(iterationsNumber);
		simulationArguments.setStorifyMode(mode);
	}
	
	protected Object performDry() throws Exception {
		if (time < 0) {
			this.computeEventStories();
		} else {
			this.computeTimeStories();
		}
		simulator.getSimulationData().getKappaSystem().getState().refreshSimulationType(SimulationType.STORIFY);
		return null;
	}
	
	private void computeTimeStories() throws Exception {
		SimulationClock clock = new SimulationClock(simulator.getSimulationData());
		clock.setTimeLimit(time);
		this.runStories(clock);
	}
	
	private void computeEventStories() throws Exception {
		SimulationClock clock = new SimulationClock(simulator.getSimulationData());
		clock.setEvent(events);
		this.runStories(clock);
	}

	private final void runStories(SimulationClock clock) throws Exception {
		Object statusLock = simulator.getStatusLock();
		
		SimulationState state = simulator.initializeSimulationState();
		
		final SimulationData simulationData = simulator.getSimulationData();
		final SimulatorStatusInterface simulatorStatus = simulator.getStatus();
		final SimulatorResultsData simulatorResultsData = simulator.getSimulatorResultsData();
		final PlxLogger logger = simulator.getLogger();
		
		if (simulationData.getKappaSystem().getStories() == null) {
			throw new RuntimeException("Kappa input contains no story annotations"); 
		}
		clock.setClockStamp(System.currentTimeMillis());
		
		Stories stories = simulationData.getKappaSystem().getStories();
		ConsoleOutputManager consoleOutputManager = simulationData.getConsoleOutputManager();
		
		synchronized (statusLock) {
			state.setEventsToZero();
		}

		InfoType additionalInfoOutputType = simulationData
				.getSimulationArguments().getOutputTypeForAdditionalInfo();
		consoleOutputManager.addAdditionalInfo(InfoType.INFO, "-Simulation...");

		clock.resetBar();
//		PlxTimer timerAllStories = new PlxTimer();
//		timerAllStories.startTimer();

		simulatorStatus.setStatusMessage(SimulatorMessage.STATUS_RUNNING);
		synchronized (statusLock) {
			state.setIterationsToZero();
		}

		int seed = simulationData.getSimulationArguments().getSeed();
		consoleOutputManager.addAdditionalInfo(InfoType.INFO,
				"--Seeding random number generator with given seed "
						+ Integer.valueOf(seed).toString());

		EventBuilder eventBuilder = new EventBuilder();
		while (state.getCurrentIterationNumber() < simulationData.getSimulationArguments()
				.getIterations()) {
//			PlxTimer timer = null;
//			if (additionalInfoOutputType != InfoType.DO_NOT_OUTPUT) {
//				consoleOutputManager.addAdditionalInfo(InfoType.INFO,
//						"-Simulation...");
//				timer = new PlxTimer();
//				timer.startTimer();
//			}

			boolean isEndRules = false;
			long clash = 0;
			long currentNumberOfClashes = 0;
			simulationData.getStoriesAgentTypesStorage().setIteration(
					state.getCurrentIterationNumber());
			while (!clock.isEndSimulation(state, currentNumberOfClashes)) {
				if (Thread.interrupted()) {
					// TODO: Do any necessary clean-up and collect data we can
					// return
					consoleOutputManager
							.println("Simulation is interrupted because the thread is cancelled");
					simulatorResultsData.setCancelled(true);
					simulatorStatus.setProgress(1.0);
					break;
				}

				simulationData.getKappaSystem().checkPerturbation(state.getCurrentTime());
				simulationData.getKappaSystem().updateRuleActivities();
				Rule rule = simulationData.getKappaSystem().getRandomRule();

				if (rule == null) {
					clock.setTimeLimit(state.getCurrentTime());
					consoleOutputManager.println("#");
					break;
				}

				List<Injection> injectionsList = simulationData
						.getKappaSystem().chooseInjectionsForRuleApplication(
								rule);
				if (!rule.hasInfiniteRate()) {
					synchronized (statusLock) {
						state.setCurrentTime(state.getCurrentTime() + simulationData.getKappaSystem()
								.getTimeValue());
					}
				}
				if (injectionsList != null) {
					eventBuilder.setNewEvent(state.getCurrentEventNumber(), rule
							.getRuleId());
					currentNumberOfClashes = 0;
					// what is this??
					if (stories.checkRule(rule.getRuleId(),
							state.getCurrentIterationNumber())) {
						rule.applyRuleForStories(injectionsList, eventBuilder,
								simulationData, true);
						stories.addLastEventToStoryStorifyRule(
								state.getCurrentIterationNumber(),
								eventBuilder.getEvent(), state.getCurrentTime());
						synchronized (statusLock) {
							state.incCurrentEventNumber();
						}
						isEndRules = true;
						consoleOutputManager.println("#");
						break;
					}

					rule.applyRuleForStories(injectionsList, eventBuilder,
							simulationData, false);
					if (!rule.doesNothing()) {
						stories.addEventToStory(state.getCurrentIterationNumber(),
								eventBuilder.getEvent());
					}
					synchronized (statusLock) {
						state.incCurrentEventNumber();
					}

					UpdatesPerformer.doNegativeUpdate(injectionsList);
					simulationData.getKappaSystem().doPositiveUpdate(rule,
							injectionsList);
				} else {
					clash++;
					currentNumberOfClashes++;
				}

			} // end of simulation here...

			clock.checkStoriesBar(state.getCurrentIterationNumber());
			synchronized (statusLock) {
				state.setEventsToZero();
			}

			stories.cleaningStory(state.getCurrentIterationNumber());
			this.endSimulation(additionalInfoOutputType, isEndRules, logger);

			if (state.getCurrentIterationNumber() < simulationData
					.getSimulationArguments().getIterations() - 1) {
				this.resetSimulationData();
			}

			// check whether the thread is interrupted above or since then...
			if (simulatorResultsData.isCancelled() || Thread.interrupted()) {
				// TODO: Do any necessary clean-up and collect data we can
				// return
				consoleOutputManager
						.println("Simulation is interrupted because the thread is cancelled");
				simulatorResultsData.setCancelled(true);
				simulatorStatus.setProgress(1.0);
				break;
			}

			synchronized (statusLock) {
				state.incCurrentIterationNumber();
			}
		} // end of iteration here...

		simulatorStatus.setStatusMessage(SimulatorMessage.STATUS_WRAPPING);

		if (additionalInfoOutputType != InfoType.DO_NOT_OUTPUT) {
			consoleOutputManager.println("#");
			this.endSimulation(InfoType.OUTPUT, false, logger);
		}

		PlxTimer mergeTimer = new PlxTimer();
		mergeTimer.startTimer();
		endMerge(mergeTimer);
	}
	
	private final void resetSimulationData() throws Exception {
		SimulationData simulationData = simulator.getSimulationData();
		
		synchronized (simulator.getStatusLock()) {
			simulator.getState().setCurrentTime(0);
		}

		ConsoleOutputManager consoleOutputManager = simulationData.getConsoleOutputManager();
		consoleOutputManager.addAdditionalInfo(InfoType.INFO,
				"-Reset simulation data.");
		consoleOutputManager.addAdditionalInfo(InfoType.INFO,
				"-Initialization...");

		simulationData.reset();
		this.reCompileKappaInput();
		simulator.initializeKappaSystem();
	}
	
	private final void reCompileKappaInput() throws Exception {
		SimulationData simulationData = simulator.getSimulationData();
		SimulatorStatus status = simulator.getLatestFreezedStatus();
		
		status.setStatusMessage(SimulatorMessage.STATUS_READING_KAPPA);
		
		new SimulationDataReader(simulationData).compileKappaFile(
				simulationData.getKappaInput(), InfoType.INFO);
	}
	
	private final void endSimulation(InfoType outputType, boolean noRulesLeft, PlxLogger logger) {
		switch (outputType) {
		case OUTPUT:
			if (noRulesLeft) {
				logger.info("end of simulation: there are no active rules");
			} else {
				logger.info("end of simulation: time");
			}
			break;
		}
	}
	
	private final void endMerge(PlxTimer timer) {
		SimulationData simulationData = simulator.getSimulationData();
		SimulationClock.stopTimer(simulationData, InfoType.OUTPUT, timer,
				"-Merge stories:");
	}
	
	@Override
	protected boolean noNeedToPerform() {
		return false;
	}

	@Override
	protected Object retrievePreparedResult() {
		return null;
	}
}
