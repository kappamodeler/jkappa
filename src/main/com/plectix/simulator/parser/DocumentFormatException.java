package com.plectix.simulator.parser;

public class DocumentFormatException extends SimulationDataFormatException {

	private static final long serialVersionUID = -7652416240591595915L;

	DocumentFormatException(ParseErrorMessage message) {
		super(message);
	}

	public DocumentFormatException(String message) {
		super(message);
	}

	public DocumentFormatException(ParseErrorMessage message, String line) {
		super(message, line);
	}
}
