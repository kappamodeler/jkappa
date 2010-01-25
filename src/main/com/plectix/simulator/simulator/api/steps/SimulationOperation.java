package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.simulator.SimulationClock;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;

public class SimulationOperation extends AbstractOperation<Object> {
	private final Simulator simulator;
	private final double time;
	private final int events;
	
	/**
	 * Be careful using this constructor, you should strictly control the type of the second parameter
	 * @param simulator
	 * @param time
	 */
	public SimulationOperation(Simulator simulator, double time) {
		super(simulator.getSimulationData(), OperationType.SIMULATION);
		this.simulator = simulator;
		this.time = time;
		this.events = -1;
	}
	
	/**
	 * Be careful using this constructor, you should strictly control the type of the second parameter
	 * @param simulator
	 * @param time
	 */
	public SimulationOperation(Simulator simulator, int events) {
		super(simulator.getSimulationData(), OperationType.SIMULATION);
		this.simulator = simulator;
		this.time = -1;
		this.events = events;
	}

	protected Object performDry() throws Exception {
		if (time < 0) {
			this.performEventsSimulation();
		} else {
			this.performTimeSimulation();
		}
		return null;
	}
	
	private void performEventsSimulation() throws Exception {
		simulator.getSimulationData().getSimulationArguments().setSimulationType(SimulationType.SIM);
		SimulationClock clock = new SimulationClock(simulator.getSimulationData());
		clock.setEvent(events);
		simulator.runSimulation(clock);
	}

	private void performTimeSimulation() throws Exception {
		simulator.getSimulationData().getSimulationArguments().setSimulationType(SimulationType.SIM);
		SimulationClock clock = new SimulationClock(simulator.getSimulationData());
		clock.setTimeLimit(time);
		simulator.runSimulation(clock);
	}
}
