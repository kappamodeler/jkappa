package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;

public class ReturnErrorOperation extends AbstractOperation {
	private final String message;
		
	public ReturnErrorOperation(String message) {
		super(OperationType.DO_NOTHING);
		this.message = message;
	}

	public final void perform(Simulator simulator) throws RuntimeException {
		throw new RuntimeException(message);
	}
}
