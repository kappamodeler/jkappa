package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.localviews.LocalViewsMain;

public class LocalViewsComputationOperation extends AbstractOperation {

	public LocalViewsComputationOperation() {
		super(OperationType.LOCAL_VIEWS);
	}
	
	public LocalViewsMain perform(SimulationData simulationData) {
		// see this method, it's very nice
		return simulationData.getKappaSystem().getLocalViews();
	}

}
