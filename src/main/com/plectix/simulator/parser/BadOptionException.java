package com.plectix.simulator.parser;

public class BadOptionException extends SimulationDataFormatException {
	private static final long serialVersionUID = 1133293352997486233L;

	public BadOptionException (KappaFileLine line, ParseErrorMessage message) {
		super(line, message);
	}
}
