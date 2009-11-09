package com.plectix.simulator.parser.abstractmodel.perturbations.modifications;

import com.plectix.simulator.parser.abstractmodel.perturbations.ModelLinearExpression;

public final class ModelRateModification implements PerturbationModification {
	private final ModelLinearExpression expression;
	private final String argument;
	
	public ModelRateModification(String argument, ModelLinearExpression expression) {
		this.expression = expression;
		this.argument = argument;
	}
	
	public final ModelLinearExpression getExpression() {
		return expression;
	}
	
	public final String getArgument() {
		return argument;
	}
	
	@Override
	public final ModificationType getType() {
		return ModificationType.RATE;
	}
	
	@Override
	public String toString() {
		return "'" + argument + "'" + " := " + expression;
	}
}
