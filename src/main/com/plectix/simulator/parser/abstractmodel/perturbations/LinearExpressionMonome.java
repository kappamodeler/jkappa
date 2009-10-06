package com.plectix.simulator.parser.abstractmodel.perturbations;

public final class LinearExpressionMonome {
	// this String can be null, if we've got free coefficient
	private final String observableName;
	private final double coefficient;
	
	public LinearExpressionMonome(String ruleName, double coefficient) {
		this.coefficient = coefficient;
		this.observableName = ruleName;
	}
	
	public final String getObsName() {
		return observableName;
	}
	
	public final double getMultiplier() {
		return coefficient;
	}
	
	@Override
	public final String toString() {
		if (observableName == null) {
			return coefficient + "";
		}
		return coefficient + " * '" + observableName + "'";
	}
	                          
}
