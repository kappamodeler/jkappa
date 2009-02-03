package com.plectix.simulator.parser.abstractmodel.perturbations;

public class AbstractSpeciesCondition implements AbstractCondition {
	private final LinearExpression myExpression;
	private final String myArgument;
	private final boolean myIsGreater; 
	
	public AbstractSpeciesCondition(String argument, LinearExpression expression, boolean greater) {
		myExpression = expression;
		myArgument = argument;
		myIsGreater = greater;
	}
	
	public LinearExpression getExpression() {
		return myExpression;
	}
	
	public boolean isGreater() {
		return myIsGreater;
	}
	
	public String getArgument() {
		return myArgument;
	}
	
	
	//--------------------TO STRINGS----------------------------
	
	public String toString() {
		String greater;
		if (myIsGreater) {
			greater = ">";
		} else {
			greater = "<";
		}
		
		return "['" + myArgument + "'] " + greater + " " + expressionToString(myExpression); 
	}
	   
	private String expressionToString(LinearExpression expr) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (LinearExpressionMonome monome : expr.getPolynome()) {
			if (first) {
				first = false;
			} else {
				if (monome.getMultiplier() >= 0) {
					sb.append(" + ");
				}
			}
			sb.append(monome.getMultiplier() + " * ['" + monome.getRuleName() + "']");
		}
		return sb.toString();
	}
}
