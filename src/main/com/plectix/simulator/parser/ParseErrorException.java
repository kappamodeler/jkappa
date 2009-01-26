package com.plectix.simulator.parser;




public class ParseErrorException extends Exception {

	private static final long serialVersionUID = 2958048616898350657L;
	private String myMessage = "";
	private String myLine = null;
	
	public ParseErrorException(KappaFileLine line, String message) {
		super(line + "\n" + message);
		myLine = line.toString();
		myMessage = message;
	}
	
	public ParseErrorException(String message) {
		super(message);
		myMessage = message;
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
