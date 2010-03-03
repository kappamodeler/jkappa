package com.plectix.simulator.staticanalysis.observables;

public class MaxStateFinder implements ObservablesStatesHandler {
	private double maximum = Double.MIN_VALUE;
	
	@Override
	public void visit(double item) {
		maximum = Math.max(maximum, item);
	}

	@Override
	public double getResult() {
		return maximum;
	}

}
