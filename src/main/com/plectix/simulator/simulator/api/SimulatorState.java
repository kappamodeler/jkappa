package com.plectix.simulator.simulator.api;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;

public class SimulatorState {
	private final SimulationData simulationData;
	
	/*----------STATIC PROPERTIES-------------*/
	private boolean subViewsComputed = false;
	private boolean injectionsSet = false;
	private boolean kappaFileCompiled = false;
	private SimulationType latestSimulationType = SimulationType.NONE;

	public SimulatorState(SimulationData simulationData) {
		this.simulationData = simulationData;
	}

	public void reset() {
		subViewsComputed = false;
		injectionsSet = false;
		latestSimulationType = SimulationType.NONE;
		this.getKappaSystem().markAsNotInitialized();
		kappaFileCompiled = false;
	}
	
	public final boolean subviewsComputed() {
		return this.subViewsComputed;
	}
	
	public final void setSubviewsStatus(boolean status) {
		this.subViewsComputed = status;
	}
	
	public final boolean injectionsAreSet() {
		return this.injectionsSet;
	}
	
	public final void setInjectionsStatus(boolean status) {
		this.injectionsSet = status;
	}
	
	public final SimulationType getLatestSimulationType() {
		return latestSimulationType;
	}
	
	public final void refreshSimulationType(SimulationType type) {
		if (latestSimulationType != type) {
			if (latestSimulationType != SimulationType.NONE) {
				this.getKappaSystem().markAsNotInitialized();
			}
			latestSimulationType = type;
		}
	}
	
	public final boolean kappaSystemIsInitialized() {
		return this.getKappaSystem().isInitialized();
	}
	
	private final KappaSystem getKappaSystem() {
		return simulationData.getKappaSystem();
	}

	public void setKappaFileCompilationStatus(boolean kappaFileCompiled) {
		this.kappaFileCompiled = kappaFileCompiled;
	}

	public boolean isKappaFileCompiled() {
		return kappaFileCompiled;
	}
}
