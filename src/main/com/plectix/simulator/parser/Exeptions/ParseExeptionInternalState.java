package com.plectix.simulator.parser.Exeptions;

public class ParseExeptionInternalState extends ParseErrorException {
	private static final long serialVersionUID = 353888933622450859L;

	public ParseExeptionInternalState(String message) {
		super();
		message = " Unexpected Internal State name: '" + message + "'";
		setMessage(message);
	}

}
