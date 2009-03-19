package com.plectix.simulator.simulator;

import com.plectix.simulator.controller.SimulatorStatusInterface;

public class SimulatorStatus implements SimulatorStatusInterface {

	private long currentEventNumber = 0;

	private double currentTime = Double.NaN;
	private double progress = Double.NaN;

	private String statusMessage = null;

	public final double getCurrentEventNumber() {
		return currentEventNumber;
	}
	
	public final double getCurrentTime() {
		return currentTime;
	}

	public final int getIterationNumber() {
		throw new RuntimeException("This method is not implemented yet!");
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
