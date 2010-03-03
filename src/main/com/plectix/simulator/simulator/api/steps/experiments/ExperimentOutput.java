package com.plectix.simulator.simulator.api.steps.experiments;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;

public class ExperimentOutput {
	private final KappaSystem kappaSystem;
	
	public ExperimentOutput(SimulationData simulationData) {
		this.kappaSystem = simulationData.getKappaSystem();
	}

//	public long getObservableCount(IConnectedComponent component) {
//		
//	}
	
	public double getObservableFinalState(String name) {
		return this.kappaSystem.getObservables().getFinalComponentState(name);
	}
	
	public double getObservableFinalState(Pattern<?> pattern) {
		return this.kappaSystem.getObservables().getFinalComponentState(pattern);
	}
}
