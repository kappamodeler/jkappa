package com.plectix.simulator.util;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Timer;
import java.util.TimerTask;

public class MemoryUtil {

	public static final void dumpUsedMemoryInfoPeriodically(final PrintStream printStream, long period) {
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				dumpUsedMemory(printStream);
			} 
		};

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(timerTask, 0, period);
	}

	public static final void dumpUsedMemory(PrintStream printStream) {
	       printStream.println(getUsedMemory());
	}

	public static final String getUsedMemory() {
	       MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
	       return new String("Memory: "
	    		   + mbean.getNonHeapMemoryUsage().getUsed() + " "
	    		   + mbean.getHeapMemoryUsage().getUsed()
	       );
	}
}
