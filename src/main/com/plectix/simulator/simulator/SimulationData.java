package com.plectix.simulator.simulator;

import java.util.List;

import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.interfaces.ISolution;


public class SimulationData {
	private List<CRule> rules;
	private CObservables observables = new CObservables();
	private double intialTime;
	private double timeLength=10;
	private long numPoints;
	private ISolution solution=new CSolution(); // soup of initial components
	private boolean compile = false;
	
	public CObservables getObservables() {
		return observables;
	}
	
	
	public final void initializeLifts() {
		//creates lifts for all rules
	}

	public final void initializeInjections() {
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
	
	public boolean isCompile() {
		return compile;
	}
	
	public void setCompile(boolean compile) {
		this.compile = compile;
	}
}
