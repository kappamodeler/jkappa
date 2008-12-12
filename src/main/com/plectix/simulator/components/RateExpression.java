package com.plectix.simulator.components;

import java.io.Serializable;

import com.plectix.simulator.interfaces.*;

public final class RateExpression implements IPerturbationExpression, Serializable {
	private final IRule rule;
	private double value;

	public RateExpression(IRule rule, double value) {
		this.rule = rule;
		this.value = value;
	}

	public final String getName() {
		if (rule != null)
			return rule.getName();
		return null;
	}

	public final String getValueToString() {
		//return Double.valueOf(value).toString();
		return value + "";
	}

	// public double getMultiplication() {
	// if (this.rule == null)
	// return this.value;
	// return this.rule.getRuleRate() * this.value;
	// }

	public final double getMultiplication(IObservables obs) {
		if (this.rule == null)
			return this.value;
		return this.rule.getRuleRate() * this.value;
	}

	public final double getValue() {
		return value;
	}

	public final void setValue(double value) {
		this.value = value;
	}
}
