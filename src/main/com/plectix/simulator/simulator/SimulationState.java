package com.plectix.simulator.simulator;

public class SimulationState {
	/** Use synchronized (statusLock) when changing the value of this variable */
	private double currentTime = 0.0;
	/** Use synchronized (statusLock) when changing the value of this variable */
	private long currentEventNumber = 0;
	/** Use synchronized (statusLock) when changing the value of this variable */
	private int currentIterationNumber = 0;
	
	public double getCurrentTime() {
		return currentTime;
	}
	
	public void setCurrentTime(double currentTime) {
		this.currentTime = currentTime;
	}
	
	public long getCurrentEventNumber() {
		return currentEventNumber;
	}
	
	public void setEventsToZero() {
		this.currentEventNumber = 0;
	}
	
	public int getCurrentIterationNumber() {
		return currentIterationNumber;
	}
	
	public void setIterationsToZero() {
		this.currentIterationNumber = 0;
	}
	
	public void incCurrentEventNumber() {
		this.currentEventNumber++;
	}
	
	public void incCurrentIterationNumber() {
		this.currentIterationNumber++;
	}
}
