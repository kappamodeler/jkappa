package com.plectix.simulator.util;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Timer;
import java.util.TimerTask;

public class MemoryUtil {
	private static PeakMemoryUsage peakMemoryUsage = null;
	private static Timer peakMemoryTimer = null;
	
	private static final TimerTask PEAK_MEMORY_TIMER_TASK = new TimerTask() {
		@Override
		public void run() {
			peakMemoryUsage.update();
		} 
	};
	
	public static final PeakMemoryUsage getPeakMemoryUsage() {
		peakMemoryTimer.cancel();
		peakMemoryTimer = null;
		peakMemoryUsage.update();
		return peakMemoryUsage;
	}
	
	/**
	 * Turns on monitoring of peak memory usage.
	 * 
	 * @param period time in milliseconds between successive memory monitoring operations
	 * @return <true> if monitoring is turned on with this call, <false> if it was already turned on
	 */
	public static final boolean monitorPeakMemoryUsage(long period) {
		if (peakMemoryUsage != null) {
			// monitoring already turned on! 
			return false;
		}
		
		peakMemoryUsage = new PeakMemoryUsage();
		peakMemoryTimer = new Timer();
		peakMemoryTimer.scheduleAtFixedRate(PEAK_MEMORY_TIMER_TASK, 0, period);
		return true;
	}
	
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
	
	public static class PeakMemoryUsage {
		private long heap = 0;
		private long nonHeap = 0;
		private long total = 0;
		
		protected PeakMemoryUsage() {
			super();
		}
		
		public void update() {
			MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
			long currentHeap = mbean.getHeapMemoryUsage().getUsed();
			long currentNonHeap = mbean.getNonHeapMemoryUsage().getUsed();
			synchronized (this) {
				heap = Math.max(heap, currentHeap);
				nonHeap = Math.max(nonHeap, currentNonHeap);
				total = Math.max(total, currentHeap + currentNonHeap);
			}
		}
		
		@Override
		public String toString() {
			return "Heap= " + heap + " NonHeap= " + nonHeap + " Total= " + total;
		}

		public final long getHeap() {
			return heap;
		}

		public final long getNonHeap() {
			return nonHeap;
		}

		public final long getTotal() {
			return total;
		}
	}

}
