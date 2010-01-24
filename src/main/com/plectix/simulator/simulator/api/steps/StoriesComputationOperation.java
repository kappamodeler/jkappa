package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationClock;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;
import com.plectix.simulator.simulator.SimulationArguments.StoryCompressionMode;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;

class StoriesComputationOperation extends AbstractOperation {

	public StoriesComputationOperation() {
		super(OperationType.STORIES);
	}
	
	public void computeTimeStories(Simulator simulator, double time, StoryCompressionMode mode,
				int iterationsNumber) throws Exception {
		SimulationArguments simulationArguments = simulator.getSimulationData().getSimulationArguments();
		simulationArguments.setIterations(iterationsNumber);
		simulationArguments.setStorifyMode(mode);
		simulationArguments.setSimulationType(SimulationType.STORIFY);
		SimulationClock clock = new SimulationClock(simulator.getSimulationData());
		clock.setTimeLimit(time);
		simulator.runStories(clock);
	}
	
	public void computeEventStories(Simulator simulator, int events, StoryCompressionMode mode,
				int iterationsNumber) throws Exception {
		SimulationArguments simulationArguments = simulator.getSimulationData().getSimulationArguments();
		simulationArguments.setIterations(iterationsNumber);
		simulationArguments.setStorifyMode(mode);
		simulationArguments.setSimulationType(SimulationType.STORIFY);
		SimulationClock clock = new SimulationClock(simulator.getSimulationData());
		clock.setEvent(events);
		simulator.runStories(clock);
	}
}
