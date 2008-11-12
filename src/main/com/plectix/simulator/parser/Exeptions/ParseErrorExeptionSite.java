package com.plectix.simulator.parser.Exeptions;

public class ParseErrorExeptionSite extends ParseErrorException {
	private static final long serialVersionUID = -6773325030815363809L;

	public ParseErrorExeptionSite(String message) {
		super();
		message = " Unexpected Site name: '" + message + "'";
		setMessage(message);
	}

}
