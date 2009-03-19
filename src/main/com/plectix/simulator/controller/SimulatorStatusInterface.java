package com.plectix.simulator.controller;

public interface SimulatorStatusInterface {

	public String getStatusMessage();
	
	/** 
	 * Returns the progress of the simulator, which is a number between 0 and 1.
	 * 
	 * @return the progress of the simulator
	 */
	public double getProgress();
	
	public int getIterationNumber();

	/**
	 * Returns the current simulation time 
	 * 
	 * @return the current time
	 */
	public double getCurrentTime();
	
	public double getCurrentEventNumber();
	
	public int getNumberOfObservables();
	
	public String getObservableName(int i);
	
	public int getObservableCount(int i);
	
}
