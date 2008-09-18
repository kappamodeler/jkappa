package com.plectix.simulator.simulator;

import java.util.List;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.interfaces.IObservables;
import com.plectix.simulator.interfaces.ISolution;


public class SimulationData {
	private List<CRule> rules;
	private List<IObservables> observables;
	private double intialTime;
	private double timeLength;
	private long numPoints;
	private ISolution solution=new CSolution(); // soup of initial components
	
	public List<IObservables> getObservables() {
		return observables;
	}
	
	
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


	public void setRules(List<CRule> rules) {
		this.rules = rules;
	}

	public List<CRule> getRules() {
		return rules;
	}
}
