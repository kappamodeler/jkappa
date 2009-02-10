package com.plectix.simulator.parser.exceptions;

public enum ParseErrorMessage {
	// solution
	BAD_RESCALE("Integer quantity expected, use proper '--rescale' option"),
	UNEXPECTED_AGENT_NAME("Unexpected agent name"),
	UNEXPECTED_SITE_NAME("Unexpected site name"),
	UNEXPECTED_INTERNAL_STATE("Unexpected internal state"),
	BAD_CONNECTIONS_COORDINATION("Wrong co-ordination of connections"),
	CONNECTION_SYMBOL_EXPECTED("Connection symbol expected (!_, !n, ?)"),
	
	// rules
	UNEXPECTED_RULE_NAME("Unexpected rule name (please use apostrophes)"),
	UNEXPECTED_RULE_RATE("Unexpected rule rate (real number expected)"),
	ARROW_EXPECTED("Rule should have '->' or '<->' as a reflection symbol"),
	
	// observables
	NO_SUCH_RULE("No such rule"),
	
	// perturbations
	$INF_USED_WITH_$ADDONCE("$INF cannot be used within $ADDONCE"),
	ONCE_QUANTITY_FORMAT("Quantity must be a number or $INF (positive infinity)"),
	$ADDONCE_OR_$DELETEONCE("There can only be $ADDONCE or $DELETEONCE"),
	MODIFICATION_EXPECTED("Rate or -once modification expected after 'do'"),
	DO_EXPECTED("'do' expected"),
	WRONG_TIME_PERTURBATION_SYNTAX("Time condition syntax : '$T > x', where x means time boundary"),
	WRONG_TIME_BOUNDARY("Wrong time boundary (real number expected)"),
	
	// misc
	INTEGER_EXPECTED("Integer number expected"),
	UNEXPECTED_LINE("Unexpected line"), 
	BAD_LINEAR_EXPRESSION("Bad linear expression format, use (a_1*'rule_1' + ... + a_n*'rule_n' + K) for "
			+ "rate modification or (a_1*['specie_1'] + ... + a_n*['specie_n'] + K) for species condition"),
	SENSE_OF_INEQUALITY_EXPECTED("> or < expected"),
	
	;
	
	private final String myMessage;

	private ParseErrorMessage(String message) {
		myMessage = message;
	}
	
	public String getMessage() {
		return myMessage;
	}

	public String getMessage(String line) {
		return myMessage + " : " + line;
	}
}
