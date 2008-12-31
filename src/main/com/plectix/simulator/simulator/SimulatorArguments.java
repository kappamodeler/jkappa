package com.plectix.simulator.simulator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;


public class SimulatorArguments {
	private CommandLine commandLine;

	public SimulatorArguments(String[] args) throws ParseException {
		CommandLineParser parser = new PosixParser();
		commandLine = parser.parse(SimulatorOptions.COMMAND_LINE_OPTIONS, args);
	}

	public boolean hasOption(SimulatorOptions option) {
		return commandLine.hasOption(option.getLongName());
	}

	public String getValue(SimulatorOptions option) {
		return commandLine.getOptionValue(option.getLongName());
	}
}
