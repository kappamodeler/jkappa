package com.plectix.simulator.util;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Timer;
import java.util.TimerTask;

public final class MemoryUtil {
	private static PeakMemoryUsage peakMemoryUsage = null;
	
	/**
	 * Turns on monitoring of peak memory usage. This method is not thread-safe and it is
	 * designed to be called when the application is just launched. The information
	 * must be retrieved with {@link #getPeakMemoryUsage()} method.
	 * 
	 * @param period time in milliseconds between successive memory monitoring operations
	 * @return <true> if monitoring is turned on with this call, <false> if it was already turned on
	 * @see #getPeakMemoryUsage()
	 */
	public static final boolean monitorPeakMemoryUsage(long period) {
		if (peakMemoryUsage != null) {
			// monitoring already turned on! 
			return false;
		}
		
		peakMemoryUsage = new PeakMemoryUsage(period);
		return true;
	}

	/**
	 * Turns off monitoring of peak memory usage and returns the usage information.
	 * Monitoring must be turned on before calling this method
	 * using {@link #monitorPeakMemoryUsage(long)}.
	 * 
	 * This method is not thread-safe and it is designed to be called just before 
	 * the application quits (as opposed to monitoring the usage continuously).
	 * 
	 * @return the peak memory usage since {@link #monitorPeakMemoryUsage(long)} is called,
	 * or <code>null</code> if the monitoring is not already turned on
	 * @see #monitorPeakMemoryUsage(long)
	 */
	public static final PeakMemoryUsage getPeakMemoryUsage() {
		if (peakMemoryUsage == null) {
			// monitoring is off! 
			return null;
		}
		
		final PeakMemoryUsage ret = peakMemoryUsage;
		peakMemoryUsage.stopTimer();
		peakMemoryUsage = null;
		return ret;
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

	private static void dumpUsedMemory(PrintStream printStream) {
	       printStream.println(getUsedMemory());
	}

	private static String getUsedMemory() {
	       MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
	       return "Memory: "
	    		   + mbean.getNonHeapMemoryUsage().getUsed() + " "
	    		   + mbean.getHeapMemoryUsage().getUsed();
	}
	
	public static final class PeakMemoryUsage {
		private long heap = 0;
		private long nonHeap = 0;
		private long total = 0;
		
		private MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
		private Timer peakMemoryTimer = new Timer();
		
		PeakMemoryUsage(long period) {
			super();
			peakMemoryTimer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					if (peakMemoryUsage != null) {
						peakMemoryUsage.update();
					}
				} 
			}, 0, period);
		}
		
		final boolean stopTimer() {
			if (peakMemoryTimer == null) {
				return false;
			}
			peakMemoryTimer.cancel();
			peakMemoryTimer = null;
			update();
			return true;
		}
		
		public final void update() {
			long currentHeap = mbean.getHeapMemoryUsage().getUsed();
			long currentNonHeap = mbean.getNonHeapMemoryUsage().getUsed();
			synchronized (this) {
				heap = Math.max(heap, currentHeap);
				nonHeap = Math.max(nonHeap, currentNonHeap);
				total = Math.max(total, currentHeap + currentNonHeap);
			}
		}
		
		@Override
		public final String toString() {
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
