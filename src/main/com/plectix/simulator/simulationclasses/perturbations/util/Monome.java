package com.plectix.simulator.simulationclasses.perturbations.util;

class Monome<T extends Vector> {
	private final T vector;
	private double value;

	/**
	 * Constructor of RateExpression with given <b>rule</b> and <b>value</b> - correction factor.
	 * @param ruleVector given rule
	 * @param value given correction factor
	 */
	public Monome(T ruleVector, double value) {
		this.vector = ruleVector;
		this.value = value;
	}

	public final String getModifyingEntityName() {
		if (vector != null)
			return vector.getName();
		return null;
	}
	
	public final double getMultiplication() {
		if (this.vector == null)
			return this.value;
		return this.vector.getValue() * this.value;
	}

	public final double getCoefficient() {
		return value;
	}

	public final void setCoefficient(double value) {
		this.value = value;
	}
	
	@Override
	public final String toString() {
		if (vector == null) {
			return value + "";
		} else {
			return value + " * " + vector.getName();
		}
	}
}
