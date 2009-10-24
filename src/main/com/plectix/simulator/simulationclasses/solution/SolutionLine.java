package com.plectix.simulator.simulationclasses.solution;

/**
 * This class we use for solution content output when "--compile" is set.
 * It contains of lines, were given us by parser, such as '%init: 10 * A(x)' and so on. 
 */
//TODO rename
public final class SolutionLine {
	private final String line;
	private long number;

	public SolutionLine(String line, long count) {
		this.line = line;
		this.number = count;
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
	public final long getNumber() {
		return number;
	}

	/**
	 * Sets quantity parameter of the SolutionLine 
	 * @param count new value
	 */
	public final void setNumber(long count) {
		this.number = count;
	}
}
