package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.localviews.LocalViewsMain;

public class LocalViewsComputationOperation extends AbstractOperation<LocalViewsMain> {
	private final SimulationData simulationData;
	
	public LocalViewsComputationOperation(SimulationData simulationData) {
		super(simulationData, OperationType.LOCAL_VIEWS);
		this.simulationData = simulationData;
	}
	
	protected LocalViewsMain performDry() {
		// see this method, it's very nice
		return simulationData.getKappaSystem().getLocalViews();
	}

}
