package com.plectix.simulator.components;

public class CDataString{
	private int lineNumber;
	private String line;
	
	public final int getLineNumber() {
		return lineNumber;
	}

	public final String getLine() {
		return line;
	}

	public CDataString(int lineNumber, String line){
		this.line=line;
		this.lineNumber=lineNumber;
	}
	
	public String toString() {
		return "line " + lineNumber + " : [" + line + "]";
	}
	

}
