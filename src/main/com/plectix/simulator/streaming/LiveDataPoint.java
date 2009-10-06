package com.plectix.simulator.streaming;

public final class LiveDataPoint {

	private final long eventNumber;
	private final double eventTime;
	private final double[] plotValues;

	public LiveDataPoint(long eventNumber, double eventTime, double[] plotValues) {
		super();
		this.eventNumber = eventNumber;
		this.eventTime = eventTime;
		this.plotValues = plotValues;
	}

	public final long getEventNumber() {
		return eventNumber;
	}

	public final double getEventTime() {
		return eventTime;
	}

	public final double[] getPlotValues() {
		return plotValues;
	}
	
	@Override
	public final String toString() {
		return "(" + eventNumber + ", " + eventTime + ", " + plotValues[0] + ")";
	}

}
