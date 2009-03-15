package com.plectix.simulator.controller;

public interface SimulatorStatusInterface {

	public String getStatusMessage();
	
	public int getIterationNumber();
	
	public double getCurrentTime();
	
	public double getCurrentEventNumber();
	
	public int getNumberOfObservables();
	
	public String getObservableName(int i);
	
	public int getObservableCount(int i);
	
}
