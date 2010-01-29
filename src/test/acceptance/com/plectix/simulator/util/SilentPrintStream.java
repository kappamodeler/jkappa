package com.plectix.simulator.util;

import java.io.PrintStream;

public class SilentPrintStream extends PrintStream {
	public SilentPrintStream() {
		super(System.out);
	}
	
	@Override
	public void println() {
	}
	
	@Override
	public void println(String s) {
	}
	
	@Override
	public void print(String s) {
	}
}
