package com.plectix.simulator.simulator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;


public class SimulatorCommandLine {
	private final CommandLine commandLine;

	public SimulatorCommandLine(String[] args) throws ParseException {
		CommandLineParser parser = new PosixParser();
		commandLine = parser.parse(SimulatorOptions.COMMAND_LINE_OPTIONS, args);
	}

	public final boolean hasOption(SimulatorOptions option) {
		return commandLine.hasOption(option.getLongName());
	}

	public final String getValue(SimulatorOptions option) {
		return commandLine.getOptionValue(option.getLongName());
	}

	public final int getIntValue(SimulatorOptions option) {
		return Integer.parseInt(getValue(option));
	}

	public final long getLongValue(SimulatorOptions option) {
		return Long.parseLong(getValue(option));
	}

	public final Double getDoubleValue(SimulatorOptions option) {
		return Double.parseDouble(getValue(option));
	}
	
}
