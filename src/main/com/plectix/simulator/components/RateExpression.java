package com.plectix.simulator.components;

import com.plectix.simulator.interfaces.IPerturbationExpression;

public class RateExpression implements IPerturbationExpression {
	private CRule rule;
	double value;

	public RateExpression(CRule rule, double value) {
		this.rule = rule;
		this.value = value;
	}

	@Override
	public String getName() {
		if (rule != null)
			return rule.getName();
		return null;
	}

	@Override
	public String getValueToString() {
		return Double.valueOf(value).toString();
	}

	// public double getMultiplication() {
	// if (this.rule == null)
	// return this.value;
	// return this.rule.getRuleRate() * this.value;
	// }

	@Override
	public double getMultiplication() {
		if (this.rule == null)
			return this.value;
		return this.rule.getRuleRate() * this.value;
	}

	@Override
	public double getValue() {
		return value;
	}

	@Override
	public void setValue(double value) {
		this.value = value;
	}
}
