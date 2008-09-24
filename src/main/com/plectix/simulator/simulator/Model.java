package com.plectix.simulator.simulator;

import com.plectix.simulator.interfaces.IActivationMap;

public class Model{

	private double commonActivity = 0.0; 
	
	private IActivationMap activationMap;
	
	private SimulationData simulationData;
	

	public Model(SimulationData simData) {
		this.simulationData = simData;
	}

	public final void initialize() {
		simulationData.initializeLifts();
		simulationData.initializeInjections();
		createActivationMap();
	}

	private final void createActivationMap() {
		// TODO Auto-generated method stub
	}

	public final IActivationMap getActivationMap() {
		return activationMap;
	}
	
	public final SimulationData getSimulationData() {
		return simulationData;
	}

	public final double getCommonActivity() {
		return commonActivity;
	}

	
	
}
