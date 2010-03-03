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

	private String latestCompiledKappaFileName = null;
	private char[] latestCompiledKappaInputArray = null;

	private boolean kappaModelCreated = false;

	private String latestLoadedKappaFileName = null;

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

	public void setKappaFileCompiled() {
		this.kappaFileCompiled = true;
		this.latestCompiledKappaFileName = simulationData.getSimulationArguments().getInputFileName();
		this.latestCompiledKappaInputArray = simulationData.getSimulationArguments().getInputCharArray();
	}

	public boolean isKappaFileCompiled() {
		if (this.latestCompiledKappaFileName != null) {
			String currentFileName = simulationData.getSimulationArguments().getInputFileName();
			return kappaFileCompiled && this.latestCompiledKappaFileName.equals(currentFileName);	
		} else if (this.latestCompiledKappaInputArray != null) {
			char[] currentInputArray = simulationData.getSimulationArguments().getInputCharArray();
			return kappaFileCompiled && this.latestCompiledKappaInputArray.equals(currentInputArray);	
		}
		return false;
	}

	public void setKappaModelCreationStatus(boolean b) {
		kappaModelCreated = b;
	}
	
	public boolean isKappaModelCreated() {
		return this.kappaModelCreated;
	}

	public String getLatestLoadedFileName() {
		return this.latestLoadedKappaFileName ;
	}
	
	public void setLatestLoadedFileName(String latestLoadedFileName) {
		this.latestLoadedKappaFileName = latestLoadedFileName;
	}
}
