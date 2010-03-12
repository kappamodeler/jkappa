package com.plectix.simulator.parser;

/**
 * Inner representation of kappa file line.
 * @see KappaFile
 * @author evlasov
 */
public class KappaFileLine{
	private final int lineNumber;
	private final String line;
	
	public KappaFileLine(int lineNumber, String line){
		this.line=line.intern();
		this.lineNumber=lineNumber;
	}
	
	public final int getLineNumber() {
		return lineNumber;
	}

	public final String getLine() {
		return line;
	}

	@Override
	public final String toString() {
		return "line " + lineNumber + " : [" + line + "]";
	}
}
