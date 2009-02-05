package com.plectix.simulator.parser.abstractmodel.perturbations.modifications;

import com.plectix.simulator.parser.abstractmodel.perturbations.LinearExpression;

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
		return "'" + myArgument + "'" + " := " + myExpression;
	}

	@Override
	public ModificationType getType() {
		return ModificationType.RATE;
	}
}
