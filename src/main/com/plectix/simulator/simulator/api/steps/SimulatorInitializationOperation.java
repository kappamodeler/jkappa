package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;

public class SimulatorInitializationOperation extends AbstractOperation {
	public SimulatorInitializationOperation() {
		super(OperationType.SIMULATOR_INITIALIZATION);
	}
	
	public Simulator perform(SimulatorInputData inputData) {
		Simulator simulator = new Simulator();
		simulator.loadSimulationArguments(inputData);
		return simulator;
	}

}
