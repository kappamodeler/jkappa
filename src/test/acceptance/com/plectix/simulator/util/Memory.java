package com.plectix.simulator.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

public class Memory {

	private MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();

	private long currentHeapMemory;
	private long currentNonHeapMemory;
	private long currentTotalMemory;

	// constructor
	public Memory() {
		reset();
	}

	public long getHeapMemoryUsage() {
		return mbean.getHeapMemoryUsage().getUsed();
	}

	public long getNonHeapMemoryUsage() {
		return mbean.getNonHeapMemoryUsage().getUsed();
	}

	public long getTotalMemoryUsage() {
		return getHeapMemoryUsage() + getNonHeapMemoryUsage();
	}

	// reset
	public void reset() {

		currentHeapMemory = getHeapMemoryUsage();
		currentNonHeapMemory = getNonHeapMemoryUsage();
		currentTotalMemory = getTotalMemoryUsage();

	}

	public long usedHeap() {
		return getHeapMemoryUsage() - currentHeapMemory;
	}

	public long usedNonHeap() {
		return getNonHeapMemoryUsage() - currentNonHeapMemory;
	}

	public long usedTotal() {
		return getTotalMemoryUsage() - currentTotalMemory;
	}

	public String getPrintToString(String s) {
		return s + " : " + toString(usedHeap(), usedNonHeap(), usedTotal());
	}

	public void print(String s) {
		System.out.println(getPrintToString(s));
	}

	private final String toString(long heap, long nonHeap, long total) {
		return "Heap = " + heap + " Byte | NonHeap = " + nonHeap
				+ " Byte | Total = " + total + " Byte";
	}

}
