package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;

public class ReturnErrorOperation extends AbstractOperation<Object> {
	private final String message;
		
	public ReturnErrorOperation(SimulationData simulationData, String message) {
		super(simulationData, OperationType.DO_NOTHING);
		this.message = message;
	}

	protected final Object performDry() throws RuntimeException {
		throw new RuntimeException(message);
	}
}
