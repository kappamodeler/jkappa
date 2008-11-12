package com.plectix.simulator.parser.Exeptions;

public class ParseErrorExceptionLine extends ParseErrorException {
	private static final long serialVersionUID = -8095436732779871216L;

	public ParseErrorExceptionLine(String line) {
		super();
		String message = " Unexpected line: '" + line + "'";
		setMessage(message);
	}
}
