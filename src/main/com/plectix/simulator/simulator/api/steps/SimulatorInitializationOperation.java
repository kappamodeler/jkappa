package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;

public class SimulatorInitializationOperation extends AbstractOperation<Simulator> {
	private final SimulatorInputData inputData;
	
	public SimulatorInitializationOperation(SimulatorInputData inputData) {
		super(null, OperationType.SIMULATOR_INITIALIZATION);
		this.inputData = inputData;
	}
	
	protected Simulator performDry() {
		Simulator simulator = new Simulator();
		simulator.loadSimulationArguments(inputData);
		return simulator;
	}

}
