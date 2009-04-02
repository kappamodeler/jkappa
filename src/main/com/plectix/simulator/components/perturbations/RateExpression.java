package com.plectix.simulator.components.perturbations;

import java.io.Serializable;

import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.interfaces.*;

public final class RateExpression implements IPerturbationExpression, Serializable {
	private final CRule rule;
	private double value;

	public RateExpression(CRule rule, double value) {
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

	public final double getMultiplication(CObservables obs) {
		if (this.rule == null)
			return this.value;
		return this.rule.getRate() * this.value;
	}

	public final double getValue() {
		return value;
	}

	public final void setValue(double value) {
		this.value = value;
	}
}
