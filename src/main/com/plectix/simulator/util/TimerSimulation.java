package com.plectix.simulator.util;

import java.lang.management.ManagementFactory;

public class TimerSimulation {
	private long timeStart;
	private double wallClockTimeInSeconds = -1;

	private long timeStartThread;
	private double threadTimeInSeconds = -1;

	public final void startTimer() {
		timeStart = System.currentTimeMillis();
		timeStartThread = ManagementFactory.getThreadMXBean()
				.getCurrentThreadCpuTime();
	}

	public double getWallClockTimeInSeconds() {
		return wallClockTimeInSeconds;
	}

	public double getThreadTimeInSeconds() {
		return threadTimeInSeconds;
	}

	public final String getTimerMess() {
		wallClockTimeInSeconds = 1.0E-3 * (System.currentTimeMillis() - timeStart);
		threadTimeInSeconds = 1.0E-9 * (ManagementFactory.getThreadMXBean()
				.getCurrentThreadCpuTime() - timeStartThread);
		return "WallClock=" + wallClockTimeInSeconds + " ThreadTime="
				+ threadTimeInSeconds;
	}

}
