package com.plectix.simulator.util;

import java.lang.management.ManagementFactory;

/**
 * A simple Timer class to measure wall clock and thread CPU times. It is not thread safe
 * 
 * @author ecemis
 */
public class PlxTimer {
	private long timeStart;
	private double wallClockTimeInSeconds = -1;

	private long timeStartThread;
	private double threadTimeInSeconds = -1;

	private boolean running = false;

	public PlxTimer() {
		super();
	}

	public final void startTimer() {
		timeStart = System.currentTimeMillis();
		timeStartThread = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		running = true;
	}
	
	public final void stopTimer() {
		updateTimes();
		running = false;
	}

	public final String getTimeMessage() {
		if (running) {
			updateTimes();
		}
		return "WallClock=" + wallClockTimeInSeconds + " ThreadTime="+ threadTimeInSeconds;
	}

	public final double getWallClockTimeInSeconds() {
		if (running) {
			throw new RuntimeException("Stop the timer before calling this method!");
		}
		return wallClockTimeInSeconds;
	}

	public final double getThreadTimeInSeconds() {
		if (running) {
			throw new RuntimeException("Stop the timer before calling this method!");
		}
		return threadTimeInSeconds;
	}
	
	private final void updateTimes() {
		wallClockTimeInSeconds = 1.0E-3 * (System.currentTimeMillis() - timeStart);
		threadTimeInSeconds = 1.0E-9 * (ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime() - timeStartThread);
	}
}
