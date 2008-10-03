package com.plectix.simulator.components;

public class SolutionLines {
	private String line;

	private long count;

	public SolutionLines(String line, long count) {
		this.line = line;
		this.count = count;
	}

	public String getLine() {
		return line;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

}
