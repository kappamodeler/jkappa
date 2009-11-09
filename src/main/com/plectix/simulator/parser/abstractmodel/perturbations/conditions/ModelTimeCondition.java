package com.plectix.simulator.parser.abstractmodel.perturbations.conditions;

public final class ModelTimeCondition implements PerturbationCondition {
	private final double timeBoundParameter;
	
	public ModelTimeCondition(double timeBound) {
		this.timeBoundParameter = timeBound;
	}
	
	public final double getBounds() {
		return this.timeBoundParameter;
	}
	
	@Override
	public final ConditionType getType() {
		return ConditionType.TIME;
	}
	
	@Override
	public final String toString() {
		return "$T > " + timeBoundParameter;
	}
}
