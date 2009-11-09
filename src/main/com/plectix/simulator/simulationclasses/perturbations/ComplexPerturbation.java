package com.plectix.simulator.simulationclasses.perturbations;

public class ComplexPerturbation<C extends ConditionInterface, M extends AbstractModification> {
	private final C condition;
	private final M modification;
	
	public ComplexPerturbation(C condition, M modification) {
		this.condition = condition;
		this.modification = modification;
	}
	
	public final C getCondition() {
		return condition;
	}

	public final M getModification() {
		return modification;
	}
	
	@Override
	public final String toString() {
		StringBuffer expression = new StringBuffer();
		expression.append(condition);
		expression.append(" do ");
		expression.append(modification);
		expression.append("\n");
		return expression.toString();
	}
}
