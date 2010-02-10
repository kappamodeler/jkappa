package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.api.OperationType;

public class DumpHelpOperation extends AbstractOperation<Object> {
	protected DumpHelpOperation(SimulationData simulationData) {
		super(simulationData, OperationType.DUMP_HELP);
	}

	@Override
	protected Object performDry() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object retrievePreparedResult() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean noNeedToPerform() {
		return false;
	}
}
