package com.plectix.simulator.simulationclasses.perturbations;

import java.io.Serializable;

import com.plectix.simulator.interfaces.PerturbationExpressionInterface;
import com.plectix.simulator.staticanalysis.Observables;
import com.plectix.simulator.staticanalysis.Rule;

/**
 * This class implements rate parameters for right handSide in "perturbation expression".
 * @author avokhmin
 * @see Perturbation
 */
@SuppressWarnings("serial")
public final class RateExpression implements PerturbationExpressionInterface, Serializable {
	private final Rule rule;
	private double value;

	/**
	 * Constructor of RateExpression with given <b>rule</b> and <b>value</b> - correction factor.
	 * @param rule given rule
	 * @param value given correction factor
	 */
	public RateExpression(Rule rule, double value) {
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

	@Override
	public final double getMultiplication(Observables observables) {
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
