package com.plectix.simulator.simulator;

import com.plectix.simulator.controller.SimulatorStatusInterface;

/**
 * This object holds data to report the status of the Simulator for periodical updates.
 * <br>
 * 
 * It is not thread-safe because we assume that only one thread which is separate than the
 * simulation thread calls its get methods and requests its update from the Simulator (which
 * in turn calls the set methods). Therefore, the same thread calls all the method below. 
 * Make sure that simulation thread doesn't use this object.
 * 
 * @author ecemis
 */
public class SimulatorStatus implements SimulatorStatusInterface {

	private long currentEventNumber = 0;

	private int currentIterationNumber = 0;
	
	private double currentTime = Double.NaN;

	private double progress = Double.NaN;
	private String statusMessage = null;

	protected SimulatorStatus() {
		super();
	}
	
	public final double getCurrentEventNumber() {
		return currentEventNumber;
	}

	public final int getCurrentIterationNumber() {
		return currentIterationNumber;
	}
	
	public final double getCurrentTime() {
		return currentTime;
	}

	public final int getNumberOfObservables() {
		throw new RuntimeException("This method is not implemented yet!");
	}

	public final int getObservableCount(int i) {
		throw new RuntimeException("This method is not implemented yet!");
	}
	
	public final String getObservableName(int i) {
		throw new RuntimeException("This method is not implemented yet!");
	}

	public final double getProgress() {
		return progress ;
	}
	
	public final String getStatusMessage() {
		return statusMessage;
	}

	protected final void setCurrentEventNumber(long currentEventNumber) {
		this.currentEventNumber = currentEventNumber;
	}

	protected final void setCurrentIterationNumber(int currentIterationNumber) {
		this.currentIterationNumber = currentIterationNumber;
	}

	protected final void setCurrentTime(double currentTime) {
		this.currentTime = currentTime;
	}

	protected final void setProgress(double progress) {
		this.progress = progress;
	}

	protected final void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
}
