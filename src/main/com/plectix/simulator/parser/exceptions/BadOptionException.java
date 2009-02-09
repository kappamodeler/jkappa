package com.plectix.simulator.parser.exceptions;

import com.plectix.simulator.parser.KappaFileLine;

public class BadOptionException extends SimulationDataFormatException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1133293352997486233L;

	public BadOptionException (KappaFileLine line, ParseErrorMessage message) {
		super(line, message);
	}
}
