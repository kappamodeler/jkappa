package com.plectix.simulator.simulator.api.steps;

import java.util.List;

import com.plectix.simulator.controller.SimulatorResultsData;
import com.plectix.simulator.controller.SimulatorStatusInterface;
import com.plectix.simulator.io.ConsoleOutputManager;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulationclasses.perturbations.ComplexPerturbation;
import com.plectix.simulator.simulationclasses.perturbations.TimeCondition;
import com.plectix.simulator.simulationclasses.solution.OperationMode;
import com.plectix.simulator.simulator.ObservablesLiveDataSource;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationClock;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationState;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorMessage;
import com.plectix.simulator.simulator.UpdatesPerformer;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.RuleApplicator;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;
import com.plectix.simulator.streaming.LiveDataSourceInterface;
import com.plectix.simulator.streaming.LiveDataStreamer;
import com.plectix.simulator.util.Info.InfoType;
import com.plectix.simulator.util.io.PlxLogger;

public class SimulationOperation extends AbstractOperation<Object> {
	private final Simulator simulator;
	private final double time;
	private final long events;
	
	/**
	 * Be careful using this constructor, you should strictly control the type of the second parameter
	 * @param simulator
	 * @param time
	 */
	public SimulationOperation(Simulator simulator) {
		super(simulator.getSimulationData(), OperationType.SIMULATION);
		this.simulator = simulator;
		this.correctSimulationArguments();
		
		SimulationData simulationData = simulator.getSimulationData();
		SimulationArguments arguments = simulationData.getSimulationArguments();
		
		if (simulationData.getSimulationArguments().isTime()) {
			this.time = arguments.getTimeLimit();
			this.events = -1;
		} else {
			this.time = -1;
			this.events = arguments.getMaxNumberOfEvents();
			simulationData.getConsoleOutputManager().println("*Warning* No time limit.");
		}
	}
	
	private void correctSimulationArguments() {
		simulator.getSimulationData().getSimulationArguments().setSimulationType(SimulationType.SIM);
	}
	
	protected Object performDry() throws Exception {
		if (time < 0) {
			this.performEventsSimulation();
		} else {
			this.performTimeSimulation();
		}
		simulator.getSimulationData().getKappaSystem().getState().refreshSimulationType(SimulationType.SIM);
		return null;
	}
	
	private void performEventsSimulation() throws Exception {
		SimulationClock clock = new SimulationClock(simulator.getSimulationData());
		clock.setEvent(events);
		this.runSimulation(clock);
	}

	private void performTimeSimulation() throws Exception {
		SimulationClock clock = new SimulationClock(simulator.getSimulationData());
		clock.setTimeLimit(time);
		this.runSimulation(clock);
	}

	private void runSimulation(SimulationClock clock) throws StoryStorageException {
		Object statusLock = simulator.getStatusLock();
		
		final SimulationState state = simulator.initializeSimulationState();
		
		final SimulationData simulationData = simulator.getSimulationData();
		final SimulatorStatusInterface simulatorStatus = simulator.getStatus();
		final SimulatorResultsData simulatorResultsData = simulator.getSimulatorResultsData();
		final PlxLogger logger = simulator.getLogger();
		
		final RuleApplicator ruleApplicator = new RuleApplicator(simulationData);
		final LiveDataStreamer liveDataStreamer = simulator.getLiveDataStreamer();
		
		clock.setClockStamp(System.currentTimeMillis());

		ConsoleOutputManager consoleOutputManager = simulationData.getConsoleOutputManager();
		consoleOutputManager.addAdditionalInfo(InfoType.INFO, "-Simulation...");

		int seed = simulationData.getSimulationArguments().getSeed();
		consoleOutputManager.addAdditionalInfo(InfoType.INFO,
				"--Seeding random number generator with given seed "
						+ Integer.valueOf(seed).toString());

		synchronized (statusLock) {
			state.setEventsToZero();
		}

		long clashesNumber = 0;
		long maxClashes = 0;
		boolean isEndRules = false;
		boolean isCalculateObs = false;
		LiveDataSourceInterface liveDataSource = new ObservablesLiveDataSource(
				simulationData.getKappaSystem().getObservables());
		liveDataStreamer.reset(simulationData.getSimulationArguments()
				.getLiveDataInterval(), simulationData.getSimulationArguments()
				.getLiveDataPoints(), simulationData.getSimulationArguments()
				.getLiveDataConsumerClassname(), liveDataSource);
		simulatorStatus.setStatusMessage(SimulatorMessage.STATUS_RUNNING);

		simulationData.getKappaSystem().getObservables().addInitialState();
		while (!clock.isEndSimulation(state)
				&& maxClashes <= simulationData.getSimulationArguments()
						.getMaxClashes()) {
			if (Thread.interrupted()) {
				// TODO: Do any necessary clean-up and collect data we can
				// return
				consoleOutputManager
						.println("Simulation is interrupted because the thread is cancelled");
				simulatorResultsData.setCancelled(true);
				simulatorStatus.setProgress(1.0);
				break;
			}

			while (simulationData.checkSnapshots(state.getCurrentTime())) {
				simulationData.createSnapshots(state.getCurrentTime());
			}

			simulationData.getKappaSystem().checkPerturbation(state.getCurrentTime());
			simulationData.getKappaSystem().updateRuleActivities();
			Rule rule = simulationData.getKappaSystem().getRandomRule();

			if (rule == null) {
				List<ComplexPerturbation<?, ?>> perturbations = simulationData
						.getKappaSystem().getPerturbations();
				double tmpTime = simulationData.getSimulationArguments()
						.getTimeLimit();
				ComplexPerturbation<TimeCondition, ?> tmpPerturbation = null;

				for (ComplexPerturbation<?, ?> perturbation : perturbations) {
					if (perturbation.getCondition() instanceof TimeCondition) {
						// TODO awful type cast
						ComplexPerturbation<TimeCondition, ?> castedPerturbation = (ComplexPerturbation<TimeCondition, ?>) perturbation;
						double conditionTimeLimit = castedPerturbation
								.getCondition().getTimeLimit();
						if (conditionTimeLimit > state.getCurrentTime()
								&& conditionTimeLimit < tmpTime) {
							tmpTime = conditionTimeLimit;
							tmpPerturbation = castedPerturbation;
						}
					}
				}
				// TODO is it right way: to add timeSampleMin to currentTime for
				// applying the perturbation?
				if (tmpPerturbation != null) {
					state.setCurrentTime(tmpPerturbation.getCondition().getTimeLimit()
							+ simulationData.getKappaSystem().getObservables()
									.getTimeSampleMin());
					simulationData.getKappaSystem().checkPerturbation(
							state.getCurrentTime());
					rule = simulationData.getKappaSystem().getRandomRule();
				} else {
					isEndRules = true;
					clock.setTimeLimit(state.getCurrentTime());
					consoleOutputManager.println("#");
					break;
				}
			}

			if (!rule.hasInfiniteRate()) {
				synchronized (statusLock) {
					state.setCurrentTime(state.getCurrentTime() + simulationData.getKappaSystem()
							.getTimeValue());
				}
				liveDataStreamer.addNewDataPoint(state.getCurrentEventNumber(),
						state.getCurrentTime());
			}
			
			if (clock.isEndSimulation(state)) {
				if (simulationData.getSimulationArguments().isTime()) {
					simulationData.checkOutputFinalState(simulationData
							.getSimulationArguments().getTimeLimit());
				} else {
					simulationData.checkOutputFinalState(state.getCurrentTime());
				}

			}
			if (isCalculateObs 
					&& simulationData.getSimulationArguments().getReportExactSampleTime()
					&& simulationData.getSimulationArguments().isTime()) {
				
				simulationData.getKappaSystem().getObservables().calculateExactSampleObs(
								state.getCurrentTime(),
								state.getCurrentEventNumber(),
								simulationData.getSimulationArguments().isTime());
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Rule: " + rule.getName());
			}

			List<Injection> injectionsList = simulationData.getKappaSystem()
					.chooseInjectionsForRuleApplication(rule);
			isCalculateObs = false;
			if (injectionsList != null) {
				// negative update
				maxClashes = 0;
				if (logger.isDebugEnabled())
					logger.debug("negative update");

				synchronized (statusLock) {
					state.incCurrentEventNumber();
				}

				List<Injection> newInjections = ruleApplicator.applyRule(rule,
						injectionsList, simulationData);
				if (newInjections != null) {

					UpdatesPerformer.doNegativeUpdate(newInjections);

					// positive update
					if (logger.isDebugEnabled()) {
						logger.debug("positive update");
					}

					// on the 4th mode we should set only super injections, so
					// we do it manually
					// directly after the rule application
					if (simulationData.getSimulationArguments()
							.getOperationMode() != OperationMode.FOURTH) {
						simulationData.getKappaSystem().doPositiveUpdate(rule,
								newInjections);
					}

					simulationData.getKappaSystem().getSolution()
							.flushPoolContent(rule.getPool());
					isCalculateObs = true;

					// simulationData.getKappaSystem().getObservables().calculateObs(currentTime,
					// currentEventNumber,
					// simulationData.getSimulationArguments().isTime());
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Rule rejected");
					}
				}
			} else {
				consoleOutputManager.addAdditionalInfo(InfoType.INTERNAL,
						"Application of rule exp is clashing");
				if (logger.isDebugEnabled()) {
					logger.debug("Clash");
				}
				clashesNumber++;
				maxClashes++;
			}
			
			if (simulationData.getSimulationArguments().getLiveDataInterval() != -1) {
				simulationData.getKappaSystem().getObservables().updateLastValues();
			}
			
			if (!clock.isEndSimulation(state)
					&& maxClashes > simulationData.getSimulationArguments()
							.getMaxClashes()) {
				simulationData.checkOutputFinalState(state.getCurrentTime());

			}

			if (isCalculateObs
					&& (!simulationData.getSimulationArguments()
							.getReportExactSampleTime() || !simulationData
							.getSimulationArguments().isTime())) {
				simulationData.getKappaSystem().getObservables().calculateObs(
						state.getCurrentTime(), state.getCurrentEventNumber(),
						simulationData.getSimulationArguments().isTime());
			}

		}

		liveDataStreamer.stop();
		simulatorStatus.setStatusMessage(SimulatorMessage.STATUS_WRAPPING);

		// simulationData.getKappaSystem().getObservables().calculateObsLast(
		// currentTime, currentEventNumber);
		if (simulationData.getSimulationArguments().isTime()
				&& state.getCurrentTime() > simulationData.getSimulationArguments()
						.getTimeLimit()) {
			clock.setTimeLimit(simulationData.getSimulationArguments()
					.getTimeLimit());
			clock.setEvent(state.getCurrentEventNumber() - 1);
		} else {
			clock.setTimeLimit(state.getCurrentTime());
			clock.setEvent(state.getCurrentEventNumber());
		}
		endSimulation(simulationData.getSimulationArguments()
				.getOutputTypeForAdditionalInfo(), isEndRules, logger);
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

	@Override
	protected boolean noNeedToPerform() {
		return false;
	}

	@Override
	protected Object retrievePreparedResult() {
		return null;
	}
}
