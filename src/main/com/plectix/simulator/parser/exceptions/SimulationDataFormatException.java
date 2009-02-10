package com.plectix.simulator.parser.exceptions;

import com.plectix.simulator.parser.KappaFileLine;

/*package*/ public abstract class SimulationDataFormatException extends Exception {

	private static final long serialVersionUID = -1146038853457517127L;
	private String myMessage = "";
	private String myLine = null;
	private final ParseErrorMessage myError;

	public SimulationDataFormatException(ParseErrorMessage message) {
		super(message.getMessage());
		myError = message;
	}

	public SimulationDataFormatException(KappaFileLine line, ParseErrorMessage message) {
		super(line + "\n" + message.getMessage());
		myError = message;
	}

	public SimulationDataFormatException(KappaFileLine line,
			ParseErrorMessage message, String sourceLine) {
		super(line + "\n" + message.getMessage() + " : " + sourceLine);
		myError = message;
	}
	
	public SimulationDataFormatException(KappaFileLine line, String message) {
		super(line + "\n" + message);
		myError = null;
	}

	public SimulationDataFormatException(String message) {
		super(message);
		myError = null;
	}

	public SimulationDataFormatException(ParseErrorMessage message, String sourceLine) {
		super(message.getMessage() + " : " + sourceLine);
		myError = message;
	}

	public final void setLineDescription(KappaFileLine line) {
		if (myLine == null) {
			myLine = line.toString();
		}
	}
	
	public ParseErrorMessage getErrorType() {
		return myError;
	}
	
	@Override
	public final String getMessage(){
		if (myLine != null) {
			return myLine + "\n" + myMessage;
		} else {
			return "\n" + myMessage;
		}
	}
}
