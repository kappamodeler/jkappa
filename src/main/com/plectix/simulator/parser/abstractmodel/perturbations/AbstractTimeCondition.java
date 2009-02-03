package com.plectix.simulator.parser.abstractmodel.perturbations;

public class AbstractTimeCondition implements AbstractCondition {
	private final double myTimeBounds;
	
	public AbstractTimeCondition(double bounds) {
		myTimeBounds = bounds;
	}
	
	public double getBounds() {
		return myTimeBounds;
	}
	
	public String toString() {
		return "$T > " + myTimeBounds;
	}
}
