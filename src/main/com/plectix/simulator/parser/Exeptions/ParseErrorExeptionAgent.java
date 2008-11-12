package com.plectix.simulator.parser.Exeptions;

public class ParseErrorExeptionAgent extends ParseErrorException {

	private static final long serialVersionUID = -3549989889767386734L;

	public ParseErrorExeptionAgent(String message) {
		super();
		message = " Unexpected Agent name: '" + message + "'";
		setMessage(message);
	}
}
