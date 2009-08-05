package com.plectix.simulator.components.solution;

/**
 * This class we use for solution content output when "--compile" is set.
 * It contains of lines, were given us by parser, such as '%init: 10 * A(x)' and so on. 
 */
public final class SolutionLine {
	private final String line;
	private long count;

	public SolutionLine(String line, long count) {
		this.line = line;
		this.count = count;
	}

	/**
	 * @return String representation of substances, described by this line
	 */
	public final String getLine() {
		return line;
	}

	/**
	 * @return quantity parameter of the SolutionLine
	 */
	public final long getCount() {
		return count;
	}

	/**
	 * Sets quantity parameter of the SolutionLine 
	 * @param count new value
	 */
	public final void setCount(long count) {
		this.count = count;
	}

}
