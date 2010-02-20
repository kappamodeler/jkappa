package com.plectix.simulator.simulator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.plectix.simulator.simulator.options.OptionsSetSingleton;
import com.plectix.simulator.util.CommandLineUtils;


public final class SimulatorCommandLine {
	private final String commandLineString;
	
	private final CommandLine commandLine;
	
	private final SimulationArguments simulationArguments;

	public SimulatorCommandLine(String[] commandLineArguments) throws ParseException {
		// let's get the original command line before we change it below:
		this.commandLineString = CommandLineUtils.getCommandLineString(commandLineArguments);
		// let's create the parser
		CommandLineParser parser = new PosixParser();
		// let's replace all '-' by '_' 
		commandLineArguments = CommandLineUtils.normalize(commandLineArguments);
		// let's parse the command line
		this.commandLine = parser.parse(OptionsSetSingleton.getInstance(), commandLineArguments);
		// let's create simulation arguments:
		this.simulationArguments = createSimulationArguments();
	}
	
	public SimulatorCommandLine(String commandLineString) throws ParseException {
		// let's get the original command line before we change it below:
		this.commandLineString = commandLineString;
		// let's replace all '-' by '_' 
		String[] args = CommandLineUtils.normalize(commandLineString.split(" "));
		// let's parse the command line
		this.commandLine = (new PosixParser()).parse(OptionsSetSingleton.getInstance(), args);
		// let's create simulation arguments:
		this.simulationArguments = createSimulationArguments();
	}
	
	private SimulationArguments createSimulationArguments() throws ParseException {
		SimulationArguments arguments = new SimulationArguments();
		arguments.read(commandLine, commandLineString);
		return arguments;
	}

	public final SimulationArguments getSimulationArguments() {
		return simulationArguments;
	}

}
