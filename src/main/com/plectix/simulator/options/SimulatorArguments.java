package com.plectix.simulator.options;

import org.apache.commons.cli.*;

import com.plectix.simulator.SimulationMain;

public class SimulatorArguments {
	private CommandLine cmdLineArgs;
	private String[] myArguments;

	public SimulatorArguments(String[] args) {
		myArguments = args;
	}
	
	public void parse() throws ParseException {
		CommandLineParser parser = new PosixParser();
		cmdLineArgs = parser.parse(SimulationMain.getOptions(), myArguments);
	}

	public boolean hasOption(SimulatorOptions option) {
		return cmdLineArgs.hasOption(option.getLongName());
	}

	public String getValue(SimulatorOptions option) {
		return cmdLineArgs.getOptionValue(option.getLongName());
	}
}
