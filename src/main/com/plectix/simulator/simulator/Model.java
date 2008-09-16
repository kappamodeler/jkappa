package com.plectix.simulator.simulator;

import com.plectix.simulator.interfaces.IActivationMap;

public class Model{

	public Model(SimulationData simData) {
		this.simData = simData;
	}

	private IActivationMap activationMap;
	
	private SimulationData simData;
	
	private double commonActivity=0.;

	public void init() {
		simData.initLifts();
		simData.initInjections();
		createActivationMap();
		
	}

	private void createActivationMap() {
		// TODO Auto-generated method stub
		
	}

	public IActivationMap getActivationMap() {
		return activationMap;
	}
	
	public SimulationData getSimData() {
		return simData;
	}

	public double getCommonActivity() {
		return commonActivity;
	}

	
	
}
