package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.BuildConstants;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.api.OperationType;

public class DumpVersionOperation extends AbstractOperation<Object> {
	private final SimulationData simulationData;
	
	protected DumpVersionOperation(SimulationData simulationData) {
		super(simulationData, OperationType.DUMP_VERSION);
		this.simulationData = simulationData;
	}

	@Override
	protected Object performDry() throws Exception {
		simulationData.getConsoleOutputManager().println("Java Simulator Revision: "
				+ BuildConstants.BUILD_SVN_REVISION);
		return null;
	}

	@Override
	protected Object retrievePreparedResult() throws Exception {
		return null;
	}

	@Override
	protected boolean noNeedToPerform() {
		return false;
	}
}
