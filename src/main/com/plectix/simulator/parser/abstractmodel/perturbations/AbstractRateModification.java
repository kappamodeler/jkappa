package com.plectix.simulator.parser.abstractmodel.perturbations;

public class AbstractRateModification implements AbstractModification {
	private final LinearExpression myExpression;
	private final String myArgument;
	
	public AbstractRateModification(String argument, LinearExpression expression) {
		myExpression = expression;
		myArgument = argument;
	}
	
	public LinearExpression getExpression() {
		return myExpression;
	}
	
	public String getArgument() {
		return myArgument;
	}
	
	public String toString() {
		return "do " + "'" + myArgument + "'" + " := " + myExpression;
	}
}
