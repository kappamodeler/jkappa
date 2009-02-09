package com.plectix.simulator.parser.exceptions;

import com.plectix.simulator.parser.KappaFileLine;

public class ParseErrorException extends SimulationDataFormatException {

	private static final long serialVersionUID = 2958048616898350657L;
	
	public ParseErrorException(KappaFileLine line, ParseErrorMessage message, String sourceLine) {
		super(line, message, sourceLine);
	}
	
	public ParseErrorException(KappaFileLine line, ParseErrorMessage message) {
		super(line, message);
	}
	
	public ParseErrorException(ParseErrorMessage message, String sourceLine) {
		super(message);
	}
	
	public ParseErrorException(KappaFileLine kappaFileLine, String message) {
		super(kappaFileLine, message);
	}

	public ParseErrorException(ParseErrorMessage message) {
		super(message);
	}
}
