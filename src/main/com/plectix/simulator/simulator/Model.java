package com.plectix.simulator.simulator;

import com.plectix.simulator.interfaces.IActivationMap;

public class Model{

	public Model(SimulationData simData) {
		this.simulationData = simData;
	}

	private IActivationMap activationMap;
	
	private SimulationData simulationData;
	
	private double commonActivity=0.;

	public final void init() {
		simulationData.initLifts();
		simulationData.initInjections();
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
