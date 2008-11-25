package com.plectix.simulator.components;

public final class CDataString{
	private final int lineNumber;
	private final String line;
	
	public final int getLineNumber() {
		return lineNumber;
	}

	public final String getLine() {
		return line;
	}

	public CDataString(int lineNumber, String line){
		this.line=line.intern();
		this.lineNumber=lineNumber;
	}
	
	@Override
	public String toString() {
		return "line " + lineNumber + " : [" + line + "]";
	}
}
