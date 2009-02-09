package com.plectix.simulator.parser.exceptions;

import com.plectix.simulator.parser.KappaFileLine;

/*package*/ public abstract class SimulationDataFormatException extends Exception {

	private static final long serialVersionUID = -1146038853457517127L;
	private String myMessage = "";
	private String myLine = null;

	public SimulationDataFormatException(ParseErrorMessage message) {
		super(message.getMessage());
	}

	public SimulationDataFormatException(KappaFileLine line, ParseErrorMessage message) {
		super(line + "\n" + message.getMessage());
	}

	public SimulationDataFormatException(KappaFileLine line,
			ParseErrorMessage message, String sourceLine) {
		super(line + "\n" + message.getMessage() + " : " + sourceLine);
	}
	
	public SimulationDataFormatException(KappaFileLine line, String message) {
		super(line + "\n" + message);
	}

	public SimulationDataFormatException(String message) {
		super(message);
	}

	public SimulationDataFormatException(ParseErrorMessage message, String sourceLine) {
		super(message.getMessage() + " : " + sourceLine);
	}

	public final void setLineDescription(KappaFileLine line) {
		if (myLine == null) {
			myLine = line.toString();
		}
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
