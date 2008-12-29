package com.plectix.simulator.controller;

import java.io.PrintStream;

public class SimulatorInputData {

	private final String[] arguments;
	
	private PrintStream printStream;

	public SimulatorInputData(String[] args) {
		this.arguments = args;
	}

	public SimulatorInputData(String[] args, PrintStream printStream) {
		this.arguments = args;
		this.printStream = printStream;
	}

	public final PrintStream getPrintStream() {
		return printStream;
	}

	public final String[] getArguments() {
		return arguments;
	}
}
