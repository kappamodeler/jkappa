package com.plectix.simulator.parser;

/**
 * Exception of this type should be thrown in any case, when given kappa file
 * cannot be used for proper KappaSystem object creation
 */
/*package*/ public abstract class SimulationDataFormatException extends Exception {

	private static final long serialVersionUID = -1146038853457517127L;
	private String lineDetails = null;
	private final ParseErrorMessage errorMessage;

	public SimulationDataFormatException(ParseErrorMessage message) {
		super(message.getMessage());
		this.errorMessage = message;
	}

	public SimulationDataFormatException(KappaFileLine line, ParseErrorMessage message) {
		super(line + "\n" + message.getMessage());
		this.errorMessage = message;
	}

	public SimulationDataFormatException(KappaFileLine line,
			ParseErrorMessage message, String sourceLine) {
		super(line + "\n" + message.getMessage() + " : " + sourceLine);
		this.errorMessage = message;
	}
	
	public SimulationDataFormatException(KappaFileLine line, String message) {
		super(line + "\n" + message);
		this.errorMessage = null;
	}

	public SimulationDataFormatException(String message) {
		super(message);
		errorMessage = null;
	}

	public SimulationDataFormatException(ParseErrorMessage message, String sourceLine) {
		super(message.getMessage() + " : " + sourceLine);
		errorMessage = message;
	}

	public final void setLineDescription(KappaFileLine line) {
		if (lineDetails == null) {
			lineDetails = line.toString();
		}
	}
	
	public ParseErrorMessage getErrorType() {
		return errorMessage;
	}
	
	@Override
	public final String getMessage(){
		if (lineDetails != null) {
			return lineDetails + "\n" + this.getMessage();
		} else {
			return "\n" + this.getMessage();
		}
	}
}
