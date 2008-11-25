package com.plectix.simulator.parser;

import com.plectix.simulator.components.CDataString;

public class ParseErrorException extends Exception {

	private static final long serialVersionUID = 2958048616898350657L;
	private String myMessage = "";
	private String myLine = null;
	
	public ParseErrorException(CDataString line, String message) {
		super(line + "\n" + message);
		myLine = line.toString();
		myMessage = message;
	}
	
	public ParseErrorException(String message) {
		super(message);
		myMessage = message;
	}
	
	public void setLineDescription(CDataString line) {
		if (myLine == null) {
			myLine = line.toString();
		}
	}
	
	@Override
	public String getMessage(){
		if (myLine != null) {
			return myLine + "\n" + myMessage;
		} else {
			return "\n" + myMessage;
		}
	}
}
