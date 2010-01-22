package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;

public class SolutionInitializationOperation extends AbstractOperation {

	public SolutionInitializationOperation() {
		super(OperationType.SOLUTION_INITIALIZATION);
	}
	
	public void perform(Simulator simulator) {
		simulator.initializeKappaSystem();
	}

}
