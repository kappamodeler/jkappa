package com.plectix.simulator.parser.Exeptions;

public class ParseErrorException extends Exception {

	private static final long serialVersionUID = 2958048616898350657L;
	private String myMessage = "";
	
	public ParseErrorException(String message) {
		super(message);
		myMessage = message;
	}
	
	public ParseErrorException() {
		super();
	}
	
	public void setMessage(String str) {
		myMessage = str;
	}
	
	public String getMyMessage(){
		return this.myMessage;
	}
}
