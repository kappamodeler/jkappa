package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.simulator.SimulationClock;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;

public class SimulationOperation extends AbstractOperation {
	public SimulationOperation() {
		super(OperationType.SIMULATION);
	}
	
	public void performEventsSimulation(Simulator simulator, int events) throws Exception {
		simulator.getSimulationData().getSimulationArguments().setSimulationType(SimulationType.SIM);
		SimulationClock clock = new SimulationClock(simulator.getSimulationData());
		clock.setEvent(events);
		simulator.runSimulation(clock);
	}

	public void performTimeSimulation(Simulator simulator, double time) throws Exception {
		simulator.getSimulationData().getSimulationArguments().setSimulationType(SimulationType.SIM);
		SimulationClock clock = new SimulationClock(simulator.getSimulationData());
		clock.setTimeLimit(time);
		simulator.runSimulation(clock);
	}
}
