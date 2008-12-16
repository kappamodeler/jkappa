package com.plectix.simulator.util;

import java.lang.management.ManagementFactory;

public class TimerSimulation {
	private long timeStart;
	private double wallClockTimeInSeconds = -1;

	private long timeStartThread;
	private double threadTimeInSeconds = -1;


	public TimerSimulation() {
		super();
	}
	
	public TimerSimulation(boolean isStart) {
		if (isStart) {
			startTimer();
		}
	}

	public final void startTimer() {
		timeStart = System.currentTimeMillis();
		timeStartThread = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
	}

	public final String getTimerMessage() {
		wallClockTimeInSeconds = 1.0E-3 * (System.currentTimeMillis() - timeStart);
		threadTimeInSeconds = 1.0E-9 * (ManagementFactory.getThreadMXBean()
				.getCurrentThreadCpuTime() - timeStartThread);
		return "WallClock=" + wallClockTimeInSeconds + " ThreadTime="+ threadTimeInSeconds;
	}

	public double getWallClockTimeInSeconds() {
		return wallClockTimeInSeconds;
	}

	public double getThreadTimeInSeconds() {
		return threadTimeInSeconds;
	}
}
