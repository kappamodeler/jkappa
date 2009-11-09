package com.plectix.simulator.simulationclasses.perturbations.util;

import com.plectix.simulator.staticanalysis.Rule;

public class VectorRule implements Vector {
	private final Rule rule;
	
	public VectorRule(Rule rule) {
		this.rule = rule;
	}
	
	@Override
	public double getValue() {
		return rule.getRate();
	}

	@Override
	public String getName() {
		return "'" + rule.getName() + "'";
	}

}
