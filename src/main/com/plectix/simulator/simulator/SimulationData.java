package com.plectix.simulator.simulator;

import java.util.List;

import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.interfaces.IObservables;
import com.plectix.simulator.interfaces.ISolution;


public class SimulationData {
	private List<CRule> rules;
	private IObservables observables = new CObservables();
	private double intialTime;
	private double timeLength;
	private long numPoints;
	private ISolution solution=new CSolution(); // soup of initial components
	
	public IObservables getObservables() {
		return observables;
	}
	
	
	public final void initLifts() {
		//creates lifts for all rules
	}

	public final void initInjections() {
		//creates injections for all rules
	}

	public final double getTimeLength() {
		// TODO Auto-generated method stub
		return timeLength;
	}

	public final ISolution getSolution() {
		return solution;
	}


	public final void setRules(List<CRule> rules) {
		this.rules = rules;
	}

	public final List<CRule> getRules() {
		return rules;
	}
}
