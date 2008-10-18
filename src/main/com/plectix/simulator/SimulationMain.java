package com.plectix.simulator;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.PropertyConfigurator;

import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.Parser;
import com.plectix.simulator.simulator.DataReading;
import com.plectix.simulator.simulator.Model;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorManager;

public class SimulationMain {

	private final static String SHORT_SIMULATIONFILE_OPTION = "s";
	private final static String LONG_SIMULATIONFILE_OPTION = "sim";
	private final static String SHORT_COMPILE_OPTION = "c";
	private final static String LONG_COMPILE_OPTION = "compile";
	private final static String SHORT_TIME_OPTION = "t";
	private final static String LONG_TIME_OPTION = "time";
	private final static String LONG_SEED_OPTION = "seed";
	private final static String LONG_XML_SESSION_NAME_OPTION = "xml_session_name";
	private final static String LONG_STORIFY_OPTION = "storify";
	private final static String LONG_EVENT_OPTION = "event";
	private final static String LONG_RANDOMIZER_JAVA_OPTION = "randomizer";
	private final static String LONG_ITERATIONS_OPTION = "iterations";

	private static final String LOG4J_PROPERTIES_FILENAME = "config/log4j.properties";

	private static SimulationMain instance;
	private static Options cmdLineOptions;
	private CommandLine cmdLineArgs;
	private static SimulatorManager simulationManager = new SimulatorManager();

	static {
		cmdLineOptions = new Options();
		cmdLineOptions.addOption(SHORT_SIMULATIONFILE_OPTION,
				LONG_SIMULATIONFILE_OPTION, true, "Location for input file");
		cmdLineOptions.addOption(SHORT_COMPILE_OPTION, LONG_COMPILE_OPTION,
				true, "Location for input file");
		cmdLineOptions.addOption(SHORT_TIME_OPTION, LONG_TIME_OPTION, true,
				"Time simulation count.");
		cmdLineOptions
				.addOption(
						LONG_SEED_OPTION,
						true,
						"Seed the random generator using given integer (same integer will generate the same random number sequence)");
		cmdLineOptions
				.addOption(
						LONG_XML_SESSION_NAME_OPTION,
						true,
						"Name of the xml file containing results of the current session (default simplx.xml)");
		cmdLineOptions.addOption(LONG_STORIFY_OPTION, true,
				"Name of the kappa file to storify");
		cmdLineOptions.addOption(LONG_EVENT_OPTION, true,
				"Number of rule applications");
		cmdLineOptions.addOption(LONG_RANDOMIZER_JAVA_OPTION, true,
				"Use randomizer Java");

		cmdLineOptions
				.addOption(LONG_ITERATIONS_OPTION, true,
						"To run the same simulation given number of times and get averages");

	}

	public static void main(String[] args) {
		// Initialize log4j
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);

		instance = new SimulationMain();
		simulationManager.startTimer();
		instance.parseArguments(args);
		instance.readSimulatonFile();
		instance.initialize();
		instance.runSimulator();
	}

	public void initialize() {
		simulationManager.initialize();
		System.out.println("-Initialization: " + simulationManager.getTimer()
				+ " sec. CPU");
		if (cmdLineArgs.hasOption(SHORT_COMPILE_OPTION)) {
			simulationManager.outputData();
			System.exit(1);
		}

	}

	private final void runSimulator() {

		Simulator simulator = new Simulator(new Model(instance
				.getSimulationManager().getSimulationData()));

		if (cmdLineArgs.hasOption(LONG_ITERATIONS_OPTION))
			simulator.runIterations();
		else
			simulator.run(null);
	}

	public final void readSimulatonFile() {

		boolean option = false;
		String fileName = null;
		double timeSim = 0.;
		if (cmdLineArgs.hasOption(LONG_STORIFY_OPTION)) {
			fileName = cmdLineArgs.getOptionValue(LONG_STORIFY_OPTION);
			SimulationMain.getSimulationManager().getSimulationData()
					.setStorify(true);
			option = true;
		}

		if (!option && (cmdLineArgs.hasOption(SHORT_SIMULATIONFILE_OPTION))) {
			option = true;
			fileName = cmdLineArgs.getOptionValue(SHORT_SIMULATIONFILE_OPTION);
			if (cmdLineArgs.hasOption(LONG_TIME_OPTION)) {
				option = true;
				try {
					timeSim = Double.valueOf(cmdLineArgs
							.getOptionValue(LONG_TIME_OPTION));
				} catch (Exception e) {
					HelpFormatter formatter = new HelpFormatter();
					formatter.printHelp("use --sim [file]", cmdLineOptions);
				}
				simulationManager.getSimulationData().setTimeLength(timeSim);
			} else
				System.out.println("*Warning* No time limit.");
		}
		if (cmdLineArgs.hasOption(SHORT_COMPILE_OPTION)) {
			simulationManager.getSimulationData().setCompile(true);
			if (!option) {
				option = true;
				fileName = cmdLineArgs.getOptionValue(SHORT_COMPILE_OPTION);
			} else
				option = false;
		}

		if (!option) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("use --sim [file]", cmdLineOptions);
			System.exit(1);
		}

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

	public final void parseArguments(String[] args) {
		CommandLineParser parser = new PosixParser();
		try {
			cmdLineArgs = parser.parse(cmdLineOptions, args);
		} catch (ParseException e) {
			e.printStackTrace();
			System.err.println("Error parsing arguments");
			System.exit(1);
		}

		if (cmdLineArgs.hasOption(LONG_XML_SESSION_NAME_OPTION)) {
			SimulationMain
					.getSimulationManager()
					.getSimulationData()
					.setXmlSessionName(
							cmdLineArgs
									.getOptionValue(LONG_XML_SESSION_NAME_OPTION));
		}

		if (cmdLineArgs.hasOption(LONG_SEED_OPTION)) {
			int seed = 0;
			try {
				seed = Integer.valueOf(cmdLineArgs
						.getOptionValue(LONG_SEED_OPTION));
			} catch (NumberFormatException e) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("use --sim [file]", cmdLineOptions);
			}
			SimulationMain.getSimulationManager().getSimulationData().setSeed(
					seed);
		}

		if (cmdLineArgs.hasOption(LONG_EVENT_OPTION)) {
			long event = 0;
			try {
				event = Long.valueOf(cmdLineArgs
						.getOptionValue(LONG_EVENT_OPTION));
			} catch (Exception e) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("use --sim [file]", cmdLineOptions);
			}
			SimulationMain.getSimulationManager().getSimulationData().setEvent(
					event);
		}

		if (cmdLineArgs.hasOption(LONG_RANDOMIZER_JAVA_OPTION)) {
			simulationManager.getSimulationData().setRandomizer(
					cmdLineArgs.getOptionValue(LONG_RANDOMIZER_JAVA_OPTION));
		}

		if (cmdLineArgs.hasOption(LONG_ITERATIONS_OPTION)) {
			int iteration = 0;
			boolean exp = false;
			try {
				iteration = Integer.valueOf(cmdLineArgs
						.getOptionValue(LONG_ITERATIONS_OPTION));
			} catch (Exception e) {
				exp = true;
			}
			if ((exp) || (!cmdLineArgs.hasOption(LONG_SEED_OPTION))) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("use --sim [file]", cmdLineOptions);
				System.exit(1);
			}
			SimulationMain.getSimulationManager().getSimulationData()
					.setIterations(iteration);
		}
	}

	public final static SimulatorManager getSimulationManager() {
		return simulationManager;
	}

	public final static SimulationMain getInstance() {
		return instance;
	}

}
