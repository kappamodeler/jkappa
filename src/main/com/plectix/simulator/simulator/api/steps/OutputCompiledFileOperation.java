package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.io.ConsoleOutputManager;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.api.OperationType;

public class OutputCompiledFileOperation extends AbstractOperation<Object> {
	private final ConsoleOutputManager consoleManager;
	
	protected OutputCompiledFileOperation(SimulationData simulationData) {
		super(simulationData, OperationType.OUTPUT_COMPILED_DATA);
		consoleManager = simulationData.getConsoleOutputManager();
	}

	@Override
	protected boolean noNeedToPerform() {
		return false;
	}

	@Override
	protected Object performDry() throws Exception {
		consoleManager.outputData();
		return null;
	}

	@Override
	protected Object retrievePreparedResult() throws Exception {
		return null;
	}

}
