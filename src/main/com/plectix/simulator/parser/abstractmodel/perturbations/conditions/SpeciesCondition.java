package com.plectix.simulator.parser.abstractmodel.perturbations.conditions;

import com.plectix.simulator.parser.abstractmodel.perturbations.LinearExpression;
import com.plectix.simulator.parser.abstractmodel.perturbations.LinearExpressionMonome;
import com.plectix.simulator.util.InequalitySign;

public final class SpeciesCondition implements PerturbationCondition {
	private final LinearExpression expression;
	private final String argument;
	private final InequalitySign inequalitySign; 
	
	public SpeciesCondition(String argument, LinearExpression expression, 
			InequalitySign inequalitySign) {
		this.expression = expression;
		this.argument = argument;
		this.inequalitySign = inequalitySign;
	}
	
	public final LinearExpression getExpression() {
		return expression;
	}
	
	public final InequalitySign inequalitySign() {
		return inequalitySign;
	}
	
	public final String getArgument() {
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
	
	private final String expressionToString(LinearExpression expression) {
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
			sb.append(monome.getMultiplier() + " * ['" + monome.getObsName() + "']");
		}
		return sb.toString();
	}
}
