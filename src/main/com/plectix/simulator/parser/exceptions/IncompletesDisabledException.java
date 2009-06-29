package com.plectix.simulator.parser.exceptions;

public class IncompletesDisabledException extends DocumentFormatException {

	private static final long serialVersionUID = -7652416240591595915L;

	public IncompletesDisabledException(ParseErrorMessage message) {
		super(message);
	}

	public IncompletesDisabledException(String message) {
		super(message);
	}

	public IncompletesDisabledException(ParseErrorMessage message, String line) {
		super(message, line);
	}
}
