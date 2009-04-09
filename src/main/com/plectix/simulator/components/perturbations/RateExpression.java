package com.plectix.simulator.components.perturbations;

import java.io.Serializable;

import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.interfaces.*;

/**
 * This class implements rate parameters for right handSide in "perturbation expression".
 * @author avokhmin
 * @see CPerturbation
 */
@SuppressWarnings("serial")
public final class RateExpression implements IPerturbationExpression, Serializable {
	private final CRule rule;
	private double value;

	/**
	 * Constructor of RateExpression with given <b>rule</b> and <b>value</b> - correction factor.
	 * @param rule given rule
	 * @param value given correction factor
	 */
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
		return value + "";
	}

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
