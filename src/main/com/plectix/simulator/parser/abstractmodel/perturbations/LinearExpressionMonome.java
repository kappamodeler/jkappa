package com.plectix.simulator.parser.abstractmodel.perturbations;

public class LinearExpressionMonome {
	// this String can be null, if we've got free coefficient
	private final String myObsName;
	private final double myMultiplier;
	
	public LinearExpressionMonome(String ruleName, double coef) {
		myMultiplier = coef;
		myObsName = ruleName;
	}
	
	public String getObsName() {
		return myObsName;
	}
	
	public double getMultiplier() {
		return myMultiplier;
	}
	
	public String toString() {
		if (myObsName == null) {
			return myMultiplier + "";
		}
		return myMultiplier + " * '" + myObsName + "'";
	}
	                          
}
