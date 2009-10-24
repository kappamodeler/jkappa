package com.plectix.simulator.util;

public class ObservableState {
	private static byte NO_VALUE = -1;
	
	private final double time;
	private final long event;
	
	public ObservableState(double time) {
		this.time = time;
		this.event = NO_VALUE;
	}
	
	public ObservableState(double time, long event) {
		this.event = event;
		this.time = time;
	}

	public double getTime() {
		return time;
	}

	public long getEvent() {
		return event;
	}

}
