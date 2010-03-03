package com.plectix.simulator.simulator;

import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.controller.SimulatorInterface;
import com.plectix.simulator.controller.SimulatorResultsData;
import com.plectix.simulator.controller.SimulatorStatusInterface;
import com.plectix.simulator.simulator.api.steps.CommandLineDefinedWorkflow;
import com.plectix.simulator.simulator.api.steps.OperationManager;
import com.plectix.simulator.simulator.api.steps.SimulationOperation;
import com.plectix.simulator.simulator.api.steps.SolutionInitializationOperation;
import com.plectix.simulator.simulator.api.steps.StoriesComputationOperation;
import com.plectix.simulator.streaming.LiveData;
import com.plectix.simulator.streaming.LiveDataStreamer;
import com.plectix.simulator.util.io.PlxLogger;

public final class Simulator implements SimulatorInterface {
	private static final PlxLogger logger = ThreadLocalData.getLogger(Simulator.class);

	private final SimulationData simulationData = new SimulationData();
	private final SimulatorStatus simulatorStatus = new SimulatorStatus();
	private final SimulatorResultsData simulatorResultsData = new SimulatorResultsData();
	private final LiveDataStreamer liveDataStreamer = new LiveDataStreamer();

	/**
	 * Object to lock when we are reading variables to compute the current
	 * status
	 */
	private final Object statusLock = new Object();

	// TODO maybe we can just leave only SimulatorStatus
	private SimulationState state = new SimulationState();

	public Simulator() {
		super();
		simulatorStatus.setStatusMessage(SimulatorMessage.STATUS_IDLE);
	}

	/**
	 * We assume that this method is called from a separate thread than the
	 * simulation thread. We also assume that there can be only one thread
	 * calling this method at a time.
	 * 
	 */
	public final SimulatorStatusInterface getStatus() {
		synchronized (statusLock) {
			// save the current state variables in the status object and use
			// them below
			simulatorStatus.setCurrentTime(state.getCurrentTime());
			simulatorStatus.setCurrentEventNumber(state.getCurrentEventNumber());
			simulatorStatus.setCurrentIterationNumber(state.getCurrentIterationNumber());
		}

		// let's compute our progress:
		double progress = simulatorStatus.getProgress();
		if (Double.isNaN(progress) || progress < 1.0) {
			SimulationArguments simulationArguments = simulationData
					.getSimulationArguments();
			if (simulationArguments.isTime()) {
				progress = simulatorStatus.getCurrentTime()
						/ simulationArguments.getTimeLimit();
			} else {
				progress = simulatorStatus.getCurrentEventNumber() * 1.0
						/ simulationArguments.getMaxNumberOfEvents();
			}

			if (simulationArguments.needToStorify()) {
				progress = (progress + simulatorStatus
						.getCurrentIterationNumber())
						/ simulationArguments.getIterations();
				if (progress > 1.0) {
					progress = 1.0;
				}
			}
			simulatorStatus.setProgress(progress);
		}

		return simulatorStatus;
	}

	@Override
	public final void cleanUpAfterException(Exception exception) {
		simulatorStatus.setStatusMessage(SimulatorMessage.STATUS_EXCEPTION
				+ exception.getClass().getName());
		simulatorStatus.setProgress(1.0);
	}
	

	public final void run(SimulatorInputData simulatorInputData)
			throws Exception {
		OperationManager manager = simulationData.getKappaSystem().getOperationManager();
		manager.perform(new CommandLineDefinedWorkflow(this, simulatorInputData));
	}

	public final void runSimulation() throws Exception {
		OperationManager manager = simulationData.getKappaSystem().getOperationManager();
		manager.perform(new SimulationOperation(this));
	}

	public final void runStories() throws Exception {
		OperationManager manager = simulationData.getKappaSystem().getOperationManager();
		manager.perform(new StoriesComputationOperation(this));
	}
	
	/**
	 * This method only reads Kappa File and builds the KappaSystem object We
	 * should be able to run it independently
	 * @throws Exception 
	 */
	
	public final void initializeKappaSystem() throws Exception {
		simulatorStatus.setStatusMessage(SimulatorMessage.STATUS_INITIALIZING);
		simulationData.getKappaSystem().getOperationManager()
				.perform(new SolutionInitializationOperation(simulationData)); 
	}
	

	// ////////////////////////////////////////////////////////////////////////
	//
	// GETTERS AND SETTERS
	//
	//

	public final LiveData getLiveData() {
		return liveDataStreamer.getLiveData();
	}

	public final String getName() {
		return SimulatorMessage.NAME;
	}

	public final SimulationData getSimulationData() {
		return simulationData;
	}

	public final SimulatorResultsData getSimulatorResultsData() {
		return simulatorResultsData;
	}

	public PlxLogger getLogger() {
		return logger;
	}

	public LiveDataStreamer getLiveDataStreamer() {
		return liveDataStreamer;
	}

	public Object getStatusLock() {
		return statusLock;
	}

	public SimulationState initializeSimulationState() {
		state = new SimulationState();
		return state;
	}
	
	public SimulatorStatus getLatestFreezedStatus() {
		return simulatorStatus;
	}

	public SimulationState getState() {
		return state;
	}
}
