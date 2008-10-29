package com.plectix.simulator.parser;

public class ParseErrorException extends Exception {

	private static final long serialVersionUID = 2958048616898350657L;

	public ParseErrorException(String message) {
		super(message);
	}
	
	public ParseErrorException() {
		super();
	}
}
