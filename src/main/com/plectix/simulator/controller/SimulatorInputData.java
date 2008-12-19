package com.plectix.simulator.controller;

import java.io.PrintStream;

public class SimulatorInputData {

	private final String[] args;
	
	private PrintStream printStream;

	public SimulatorInputData(String[] args) {
		this.args = args;
	}

	public SimulatorInputData(String[] args, PrintStream printStream) {
		this.args = args;
		this.printStream = printStream;
	}

	public final PrintStream getPrintStream() {
		return printStream;
	}

	public final String[] getArgs() {
		return args;
	}
}
