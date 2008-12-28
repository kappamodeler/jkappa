package com.plectix.simulator.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Timer;
import java.util.TimerTask;

public class MemoryUtil {

	public static final void dumpUsedMemoryInfoPeriodically(long period) {
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				dumpUsedMemory();
			} 
		};

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(timerTask, 0, period);
	}

	public static final void dumpUsedMemory() {
	       MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
	       System.err.println("Memory: "
	    		   + mbean.getNonHeapMemoryUsage().getUsed() + " "
	    		   + mbean.getHeapMemoryUsage().getUsed()
	       );
	}
}
