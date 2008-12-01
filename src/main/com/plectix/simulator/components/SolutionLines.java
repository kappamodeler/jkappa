package com.plectix.simulator.components;

public final class SolutionLines {
	private final String line;
	private long count;

	public SolutionLines(String line, long count) {
		this.line = line;
		this.count = count;
	}

	public final String getLine() {
		return line;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

}
