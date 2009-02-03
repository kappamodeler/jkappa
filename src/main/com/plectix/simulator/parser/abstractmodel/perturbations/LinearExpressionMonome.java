package com.plectix.simulator.parser.abstractmodel.perturbations;

public class LinearExpressionMonome {
	// this String can be null, if we've got free coefficient
	private final String myRuleName;
	private final double myMultiplier;
	
	public LinearExpressionMonome(String ruleName, double coef) {
		myMultiplier = coef;
		myRuleName = ruleName;
	}
	
	public String getRuleName() {
		return myRuleName;
	}
	
	public double getMultiplier() {
		return myMultiplier;
	}
	
	public String toString() {
		if (myRuleName == null) {
			return myMultiplier + "";
		}
		return myMultiplier + " * '" + myRuleName + "'";
	}
	                          
}
