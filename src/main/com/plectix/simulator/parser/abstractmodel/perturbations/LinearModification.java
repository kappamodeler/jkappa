package com.plectix.simulator.parser.abstractmodel.perturbations;

public abstract class LinearModification {
	private ModelLinearExpression expression = new ModelLinearExpression();
	private final String leftHandSideObservableName;

	LinearModification(String leftHandSideVariable) {
		leftHandSideObservableName = leftHandSideVariable;
	}

	public final void addMonome(String ruleName, double coefficient) {
		this.expression.addMonome(new LinearExpressionMonome(ruleName, coefficient));
	}

	public final void addMonome(LinearExpressionMonome monome) {
		this.expression.addMonome(monome);
	}
	
	public final void setExpression(ModelLinearExpression expression) {
		this.expression = expression;
	}

	public final String getModifiableRule() {
		return leftHandSideObservableName;
	}

	public final ModelLinearExpression getRightHandSideExpression() {
		return expression;
	}

	@Override
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("'" + leftHandSideObservableName + "' := " + expression);
		return sb.toString();
	}
}
