package com.plectix.simulator.parser.Exeptions;

public class ParseErrorExeptionRuleName extends ParseErrorException {
	private static final long serialVersionUID = -4134439080656788298L;

	public ParseErrorExeptionRuleName(String message,String name) {
		super();
		message = message+" Unexpected Rule name: '" + name + "'";
		setMessage(message);
	}
}
