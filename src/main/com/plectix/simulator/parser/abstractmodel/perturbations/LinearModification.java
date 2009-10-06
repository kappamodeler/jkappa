package com.plectix.simulator.parser.abstractmodel.perturbations;

public abstract class LinearModification {
	private LinearExpression expression = new LinearExpression();
	private final String leftHandSideObservableName;

	public LinearModification(String leftHandSideVariable) {
		leftHandSideObservableName = leftHandSideVariable;
	}

	public final void addMonome(String ruleName, double coefficient) {
		this.expression.addMonome(new LinearExpressionMonome(ruleName, coefficient));
	}

	public final void addMonome(LinearExpressionMonome monome) {
		this.expression.addMonome(monome);
	}
	
	public final void setExpression(LinearExpression expression) {
		this.expression = expression;
	}

	public final String getModifiableRule() {
		return leftHandSideObservableName;
	}

	public final LinearExpression getRightHandSideExpression() {
		return expression;
	}

	@Override
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("'" + leftHandSideObservableName + "' := " + expression);
		return sb.toString();
	}
}
