package com.plectix.simulator.parser;

/*package*/ final class KappaFileLine{
	private final int lineNumber;
	private final String line;
	
	public final int getLineNumber() {
		return lineNumber;
	}

	public final String getLine() {
		return line;
	}

	public KappaFileLine(int lineNumber, String line){
		this.line=line.intern();
		this.lineNumber=lineNumber;
	}
	
	@Override
	public String toString() {
		return "line " + lineNumber + " : [" + line + "]";
	}
}
