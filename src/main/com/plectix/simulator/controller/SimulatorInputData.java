package com.plectix.simulator.controller;

import java.io.PrintStream;

import com.plectix.simulator.simulator.SimulationArguments;

public class SimulatorInputData {
	
	private final SimulationArguments simulationArguments;
	
	private final PrintStream printStream;

	public SimulatorInputData(SimulationArguments simulationArguments) {
		this.simulationArguments = simulationArguments;
		this.printStream = null;
	}

	public SimulatorInputData(SimulationArguments simulationArguments, PrintStream printStream) {
		this.simulationArguments = simulationArguments;
		this.printStream = printStream;
	}

	public final PrintStream getPrintStream() {
		return printStream;
	}

	public final SimulationArguments getSimulationArguments() {
		return simulationArguments;
	}
}
