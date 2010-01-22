package com.plectix.simulator.simulator.api;

import java.io.IOException;

import org.apache.commons.cli.ParseException;

import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.simulator.Simulator;

public abstract class AbstractOperation {
	private final OperationType type;
	
	protected AbstractOperation(OperationType type) {
		this.type = type;
	}
	                          
//	private void performPreviousSteps(Simulator simulator) throws ParseException {
//		OperationType latestOperationDone = simulator.getLatestOperation();
//		OperationType currentOperation = type;
//		
//		while (latestOperationDone != currentOperation) { 
//			currentOperation.createDefaultOperation().perform(simulator);
//			
//		}
//	}
}
