package com.plectix.simulator.parser.Exeptions;

public class ParseErrorExeptionInternalState extends ParseErrorException {
	private static final long serialVersionUID = 214863089680231523L;

	public ParseErrorExeptionInternalState(String message) {
		super();
		message = " Unexpected Internal State name: '" + message + "'";
		setMessage(message);
	}

}
