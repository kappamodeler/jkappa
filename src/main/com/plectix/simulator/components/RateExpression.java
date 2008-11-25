package com.plectix.simulator.components;

import com.plectix.simulator.interfaces.*;

public class RateExpression implements IPerturbationExpression {
	private IRule rule;
	double value;

	public RateExpression(IRule rule, double value) {
		this.rule = rule;
		this.value = value;
	}

	public String getName() {
		if (rule != null)
			return rule.getName();
		return null;
	}

	public String getValueToString() {
		return Double.valueOf(value).toString();
	}

	// public double getMultiplication() {
	// if (this.rule == null)
	// return this.value;
	// return this.rule.getRuleRate() * this.value;
	// }

	public double getMultiplication() {
		if (this.rule == null)
			return this.value;
		return this.rule.getRuleRate() * this.value;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
