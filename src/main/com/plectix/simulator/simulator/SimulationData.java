package com.plectix.simulator.simulator;

import java.util.List;

import com.plectix.simulator.interfaces.IObservables;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISolution;


public class SimulationData {
	private List<IRule> rules;

	private List<IObservables> observables;

	private double intialTime;
	
	private double timeLength;
	
	private long numPoints;
	
	private ISolution solution; // soup of initial components

	public void initLifts() {
		//creates lifts for all rules
	}

	public void initInjections() {
		//creates injections for all rules
	}

	public double getTimeLength() {
		// TODO Auto-generated method stub
		return timeLength;
	}

	public ISolution getSolution() {
		return solution;
	}

}
