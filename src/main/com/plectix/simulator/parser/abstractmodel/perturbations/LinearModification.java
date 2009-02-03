package com.plectix.simulator.parser.abstractmodel.perturbations;

public class LinearModification {
	private LinearExpression myRightHandSitePolynome = new LinearExpression();
	private final String myLeftHandsideVariable;

	public LinearModification(String leftVariable) {
		myLeftHandsideVariable = leftVariable;
	}

	public void addMonome(String ruleName, double coef) {
		myRightHandSitePolynome.addMonome(new LinearExpressionMonome(ruleName, coef));
	}

	public void addMonome(LinearExpressionMonome monome) {
		myRightHandSitePolynome.addMonome(monome);
	}
	
	public void setExpression(LinearExpression expression) {
		myRightHandSitePolynome = expression;
	}

	public String getModifiableRule() {
		return myLeftHandsideVariable;
	}

	public LinearExpression getRightHandSideExpression() {
		return myRightHandSitePolynome;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("'" + myLeftHandsideVariable + "' := " + myRightHandSitePolynome);
		return sb.toString();
	}
}
