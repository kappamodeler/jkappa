package com.plectix.simulator.controller;

public interface SimulatorStatusInterface {

	public String getStatus();
	
	public int getIterationNumber();
	
	public double getCurrentTime();
	
	public int getNumberOfObservables();
	
	public String getObservableName(int i);
	
	public int getObservableCount(int i);
	
}
