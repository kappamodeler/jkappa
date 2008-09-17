package com.plectix.simulator;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.plectix.simulator.parser.Parser;
import com.plectix.simulator.simulator.DataReading;
import com.plectix.simulator.simulator.SimulationData;

public class SimulationMain {

	private final static String SHORT_SIMULATIONFILE_OPTION = "s";
	private final static String LONG_SIMULATIONFILE_OPTION = "sim";
	
	private static SimulationMain instance;
	private static Options cmdLineOptions;
	private CommandLine cmdLineArgs;
	private SimulationData simData;
	
	
	static {
		cmdLineOptions = new Options();
		cmdLineOptions.addOption(SHORT_SIMULATIONFILE_OPTION, LONG_SIMULATIONFILE_OPTION, true, "Location for input file");
	}

	public static void main(String[] args) {
		instance = new SimulationMain();
		instance.doParseArguments(args);
		instance.readSimulatonFile();
		instance.runSimulator();
	}


	private void runSimulator() {
		// Simulator simulator = new Simulator(new Model(simData));
		// simulator.run();
		// simulator.outputData();		
	}


	private void readSimulatonFile() {
		if (!cmdLineArgs.hasOption(SHORT_SIMULATIONFILE_OPTION)) {

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("use --sim [file]", cmdLineOptions);
			System.exit(1);

		}
		String fileName = cmdLineArgs.getOptionValue(SHORT_SIMULATIONFILE_OPTION);
		DataReading data = new DataReading(fileName);		
		try {
			data.readData();
			simData = new SimulationData();
			Parser parser = new Parser(data, simData);
			parser.doParse();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Cannot read file with filename " + fileName);
		}
	}


	private void doParseArguments(String[] args) {
		CommandLineParser parser = new PosixParser();
		try {
			cmdLineArgs = parser.parse(cmdLineOptions, args);
		} catch (ParseException e) {
			e.printStackTrace();
			System.err.println("Error parsing arguments");
			System.exit(1);
		}
	}

}
