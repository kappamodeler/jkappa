package com.plectix.simulator.parser.Exeptions;

public class ParseErrorExeptionRuleRate extends ParseErrorException {
	private static final long serialVersionUID = 882465622526113671L;

	public ParseErrorExeptionRuleRate(String message, String activity) {
		super();
		message = message + " Unexpected Rule Rate: '" + activity + "'";
		setMessage(message);
	}

}
