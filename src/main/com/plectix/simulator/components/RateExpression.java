package com.plectix.simulator.components;


public class RateExpression {
	private CRule rule;
	double value;

	public RateExpression(CRule rule, double value) {
		this.rule = rule;
		this.value = value;
	}

	public double getMultiplication() {
		if (this.rule == null)
			return this.value;
		return this.rule.getRuleRate() * this.value;
	}
}
