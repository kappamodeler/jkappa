package com.plectix.simulator.parser.abstractmodel.perturbations.conditions;

import com.plectix.simulator.parser.abstractmodel.perturbations.LinearExpressionMonome;
import com.plectix.simulator.parser.abstractmodel.perturbations.ModelLinearExpression;
import com.plectix.simulator.util.InequalitySign;

public final class ModelSpeciesCondition implements PerturbationCondition {
	private final ModelLinearExpression expression;
	private final String argument;
	private final InequalitySign inequalitySign; 
	
	public ModelSpeciesCondition(String argument, ModelLinearExpression expression, 
			InequalitySign inequalitySign) {
		this.expression = expression;
		this.argument = argument;
		this.inequalitySign = inequalitySign;
	}
	
	public final ModelLinearExpression getExpression() {
		return expression;
	}
	
	public final InequalitySign inequalitySign() {
		return inequalitySign;
	}
	
	public final String getPickedObservableName() {
		return argument;
	}
	
	@Override
	public final ConditionType getType() {
		return ConditionType.SPECIES;
	}
	
	@Override
	public final String toString() {
		return "['" + argument + "'] " + inequalitySign + " " + expressionToString(expression); 
	}
	
	private final String expressionToString(ModelLinearExpression expression) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (LinearExpressionMonome monome : expression.getPolynome()) {
			if (first) {
				first = false;
			} else {
				if (monome.getMultiplier() >= 0) {
					sb.append(" + ");
				}
			}
			sb.append(monome.getMultiplier() + " * ['" + monome.getEntityName() + "']");
		}
		return sb.toString();
	}
}
