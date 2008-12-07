package com.plectix.simulator.controller;

import java.io.PrintStream;

public class SimulatorInputData {

	private final String[] args;
	private PrintStream printStream;

	public String[] getArgs() {
		return args;
	}

	public SimulatorInputData(String[] args) {
		this.args = args;
	}

	public SimulatorInputData(String[] args, PrintStream printStream) {
		this.args = args;
		this.printStream = printStream;
	}

	public PrintStream getPrintStream() {
		return printStream;
	}
	
}
