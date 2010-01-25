package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;

public class SolutionInitializationOperation extends AbstractOperation<Object> {
	private final Simulator simulator;
	
	public SolutionInitializationOperation(Simulator simulator) {
		super(simulator.getSimulationData(), OperationType.INITIALIZATION);
		this.simulator = simulator;
	}
	
	protected Object performDry() throws Exception {
		simulator.initializeKappaSystem();
		return null;
	}

}
