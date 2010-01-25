package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationClock;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;
import com.plectix.simulator.simulator.SimulationArguments.StoryCompressionMode;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;

public class StoriesComputationOperation extends AbstractOperation<Object> {
	private final Simulator simulator; 
	private final int events;
	private final double time; 
	private final StoryCompressionMode mode; 
	private final int iterationsNumber;
	
	public StoriesComputationOperation(Simulator simulator, double time, StoryCompressionMode mode,
			int iterationsNumber) {
		super(simulator.getSimulationData(), OperationType.STORIES);
		this.simulator = simulator; 
		this.events = -1;
		this.time = time; 
		this.mode = mode; 
		this.iterationsNumber = iterationsNumber;
	}
	
	public StoriesComputationOperation(Simulator simulator, int events, StoryCompressionMode mode,
			int iterationsNumber) {
		super(simulator.getSimulationData(), OperationType.STORIES);
		this.simulator = simulator; 
		this.events = events;
		this.time = -1; 
		this.mode = mode; 
		this.iterationsNumber = iterationsNumber;
	}
	
	protected Object performDry() throws Exception {
		if (time < 0) {
			this.computeEventStories();
		} else {
			this.computeTimeStories();
		}
		return null;
	}
	
	private void computeTimeStories() throws Exception {
		SimulationArguments simulationArguments = simulator.getSimulationData().getSimulationArguments();
		simulationArguments.setIterations(iterationsNumber);
		simulationArguments.setStorifyMode(mode);
		simulationArguments.setSimulationType(SimulationType.STORIFY);
		SimulationClock clock = new SimulationClock(simulator.getSimulationData());
		clock.setTimeLimit(time);
		simulator.runStories(clock);
	}
	
	private void computeEventStories() throws Exception {
		SimulationArguments simulationArguments = simulator.getSimulationData().getSimulationArguments();
		simulationArguments.setIterations(iterationsNumber);
		simulationArguments.setStorifyMode(mode);
		simulationArguments.setSimulationType(SimulationType.STORIFY);
		SimulationClock clock = new SimulationClock(simulator.getSimulationData());
		clock.setEvent(events);
		simulator.runStories(clock);
	}
}
