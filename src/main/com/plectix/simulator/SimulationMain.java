package com.plectix.simulator;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.parser.Parser;
import com.plectix.simulator.simulator.DataReading;
import com.plectix.simulator.simulator.Model;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorManager;

public class SimulationMain {

	private final static String SHORT_SIMULATIONFILE_OPTION = "s";
	private final static String LONG_SIMULATIONFILE_OPTION = "sim";

	private static SimulationMain instance;
	private static Options cmdLineOptions;
	private CommandLine cmdLineArgs;
	private static SimulatorManager simulationManager = new SimulatorManager();

	static {
		cmdLineOptions = new Options();
		cmdLineOptions.addOption(SHORT_SIMULATIONFILE_OPTION,
				LONG_SIMULATIONFILE_OPTION, true, "Location for input file");
	}

	public static void main(String[] args) {
		instance = new SimulationMain();
		instance.parseArguments(args);
		instance.readSimulatonFile();
		instance.initialize();
		instance.runSimulator();
	}

	private void initialize() {
		simulationManager.initialize();
	}

	private final void runSimulator() {
		
		 Simulator simulator = new Simulator(new Model(instance.getSimulationManager().getSimulationData()));
		// simulator.run();
//		 simulator.outputData();
	}

	private final void readSimulatonFile() {
		if (!cmdLineArgs.hasOption(SHORT_SIMULATIONFILE_OPTION)) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("use --sim [file]", cmdLineOptions);
			System.exit(1);
		}
		
		String fileName = cmdLineArgs.getOptionValue(SHORT_SIMULATIONFILE_OPTION);
		DataReading data = new DataReading(fileName);
		try {
			data.readData();
			Parser parser = new Parser(data);
			parser.parse();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Cannot read file with filename " + fileName);
			System.exit(1);
		} catch (ParseErrorException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private final void parseArguments(String[] args) {
		CommandLineParser parser = new PosixParser();
		try {
			cmdLineArgs = parser.parse(cmdLineOptions, args);
		} catch (ParseException e) {
			e.printStackTrace();
			System.err.println("Error parsing arguments");
			System.exit(1);
		}
	}

	public final static SimulatorManager getSimulationManager() {
		return simulationManager;
	}
}
