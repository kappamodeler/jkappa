package com.plectix.simulator.parser.abstractmodel.perturbations.conditions;

public class AbstractTimeCondition implements AbstractCondition {
	private final double myTimeBounds;
	
	public AbstractTimeCondition(double bounds) {
		myTimeBounds = bounds;
	}
	
	public double getBounds() {
		return myTimeBounds;
	}
	
	public ConditionType getType() {
		return ConditionType.TIME;
	}
	
	public String toString() {
		return "$T > " + myTimeBounds;
	}
}
