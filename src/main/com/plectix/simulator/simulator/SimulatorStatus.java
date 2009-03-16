package com.plectix.simulator.simulator;

import com.plectix.simulator.controller.SimulatorStatusInterface;

public class SimulatorStatus implements SimulatorStatusInterface {

	private double currentTime = Double.NaN;
	private long currentEventNumber = 0;

	private String statusMessage = null;

	protected final void setCurrentTime(double currentTime) {
		this.currentTime = currentTime;
	}

	protected final void setCurrentEventNumber(long currentEventNumber) {
		this.currentEventNumber = currentEventNumber;
	}

	protected final void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	public final double getCurrentTime() {
		return currentTime;
	}

	public double getCurrentEventNumber() {
		return currentEventNumber;
	}
	
	public final String getStatusMessage() {
		return statusMessage;
	}


	public final int getIterationNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	public final int getNumberOfObservables() {
		// TODO Auto-generated method stub
		return 0;
	}

	public final int getObservableCount(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	public final String getObservableName(int i) {
		// TODO Auto-generated method stub
		return null;
	}


}
