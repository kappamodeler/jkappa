package com.plectix.simulator.staticanalysis.observables;

public interface ObservablesStatesHandler {
	public void visit(double item);
	
	public double getResult();
}
