package com.plectix.simulator;

import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.plectix.simulator.controller.SimulationService;
import com.plectix.simulator.controller.SimulatorCallable;
import com.plectix.simulator.controller.SimulatorCallableListener;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.controller.SimulatorInterface;
import com.plectix.simulator.controller.SimulatorResultsData;
import com.plectix.simulator.parser.DataReading;
import com.plectix.simulator.parser.Parser;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.options.*;
import com.plectix.simulator.util.Info;

public class SimulationMain implements SimulatorCallableListener {

	private static final Options myOptions = SimulatorOptions.options();
	private static final String LOG4J_PROPERTIES_FILENAME = "config/log4j.properties";

	private static Logger LOGGER = Logger.getLogger(SimulationMain.class);
	private static final PrintStream myOutputStream = System.out;
	private static final String VERSION = "0.6";
	
	public static void main(String[] args) {
		// Initialize log4j
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
		LOGGER.info("Build Date: " + BuildConstants.BUILD_DATE);
		LOGGER.info("Build OS: " + BuildConstants.BUILD_OS_NAME);
		LOGGER.info("SVN Revision: " + BuildConstants.BUILD_SVN_REVISION);
		LOGGER.info("Ant Java Version: " + BuildConstants.ANT_JAVA_VERSION);

		new SimulationMain().start(args);
	}

	private void start(String[] args) {
		SimulatorInterface simulator = new Simulator();
		SimulationService service = new SimulationService(simulator);
		service.submit(new SimulatorInputData(args, myOutputStream), this);
		service.shutdown();
	}

	public final static String[] changeArgs(String[] args) {
		String[] argsNew = new String[args.length];
		int i = 0;
		for (String st : args)
			if (st.startsWith("-"))
				argsNew[i++] = st.substring(0, 2)
						+ st.substring(2).replaceAll("-", "_");
			else
				argsNew[i++] = st;
		return argsNew;
	}

	public static final SimulatorArguments parseArguments(
			SimulationData simulationData, String[] args) 
					throws IllegalArgumentException {
		simulationData.addInfo(new Info(Info.TYPE_INFO, "-Initialization..."));
		SimulatorArguments arguments = new SimulatorArguments(args);
		try {
			arguments.parse();
		} catch (ParseException e) {
			Simulator.println("Error parsing arguments:");
			e.printStackTrace(Simulator.getErrorStream());
			throw new IllegalArgumentException(e);
		}
		
		if (arguments.hasOption(SimulatorOptions.HELP)) {
			 HelpFormatter formatter = new HelpFormatter();
			 formatter.printHelp("use --sim [file] [options]", myOptions);
			 //TODO are we to exit here?
			 System.exit(0);
		}
		
		if (arguments.hasOption(SimulatorOptions.VERSION)) {
			myOutputStream.println("Java simulator v." + VERSION);
			 //TODO are we to exit here?
			 System.exit(0);
		}
		
		if (arguments.hasOption(SimulatorOptions.XML_SESSION_NAME)) {
			simulationData.setXmlSessionName(arguments.getValue(SimulatorOptions.XML_SESSION_NAME));
		}
		try {
			if (arguments.hasOption(SimulatorOptions.INIT)) {
				simulationData.setInitialTime(Double.valueOf(arguments.getValue(SimulatorOptions.INIT)));
			}
			if (arguments.hasOption(SimulatorOptions.POINTS)) {
				simulationData.setPoints(Integer.valueOf(arguments.getValue(SimulatorOptions.POINTS)));
			}
			if (arguments.hasOption(SimulatorOptions.RESCALE)) {
				double rescale = Double.valueOf(arguments.getValue(SimulatorOptions.RESCALE));
				if (rescale > 0)
					simulationData.setRescale(rescale);
				else
					throw new Exception();
			}

			if (arguments.hasOption(SimulatorOptions.NO_SEED)) {
				simulationData.setSeed(0);
			}
			//TODO else?
			if (arguments.hasOption(SimulatorOptions.SEED)) {
				int seed = 0;
				seed = Integer.valueOf(arguments.getValue(SimulatorOptions.SEED));
				simulationData.setSeed(seed);
			}

			if (arguments.hasOption(SimulatorOptions.MAX_CLASHES)) {
				int max_clashes = 0;
				max_clashes = Integer.valueOf(arguments.getValue(SimulatorOptions.MAX_CLASHES));
				simulationData.setMaxClashes(max_clashes);
			}

			if (arguments.hasOption(SimulatorOptions.EVENT)) {
				long event = 0;
				event = Long.valueOf(arguments.getValue(SimulatorOptions.EVENT));
				simulationData.setEvent(event);
			}

			if (arguments.hasOption(SimulatorOptions.ITERATION)) {
				simulationData.setIterations(Integer.valueOf(arguments
						.getValue(SimulatorOptions.ITERATION)));
			}

		} catch (Exception e) {
			e.printStackTrace(Simulator.getErrorStream());
			throw new IllegalArgumentException(e);
		}

		if (arguments.hasOption(SimulatorOptions.RANDOMIZER_JAVA)) {
			simulationData.setRandomizer(arguments.getValue(SimulatorOptions.RANDOMIZER_JAVA));
		}

		if (arguments.hasOption(SimulatorOptions.NO_ACTIVATION_MAP)
				|| (arguments.hasOption(SimulatorOptions.NO_MAPS))
				|| (arguments.hasOption(SimulatorOptions.NO_BUILD_INFLUENCE_MAP))) {
			simulationData.setActivationMap(false);
		}

		if (arguments.hasOption(SimulatorOptions.MERGE_MAPS)){
			simulationData.setInhibitionMap(true);
		}
		
		if (arguments.hasOption(SimulatorOptions.NO_INHIBITION_MAP)
				|| (arguments.hasOption(SimulatorOptions.NO_MAPS))
				|| (arguments.hasOption(SimulatorOptions.NO_BUILD_INFLUENCE_MAP))) {
			simulationData.setInhibitionMap(false);
		}
		
		if (arguments.hasOption(SimulatorOptions.OCAML_STYLE_OBS_NAME)) {
			simulationData.setOcamlStyleObsName(true);
		}

		if (arguments.hasOption(SimulatorOptions.NUMBER_OF_RUNS)) {
			int iteration = 0;
			boolean exp = false;
			try {
				iteration = Integer.valueOf(arguments.getValue(SimulatorOptions.NUMBER_OF_RUNS));
			} catch (Exception e) {
				exp = true;
			}
			if ((exp) || (!arguments.hasOption(SimulatorOptions.SEED))) {
				throw new IllegalArgumentException("No SEED OPTION");
			}
			simulationData
					.setSimulationType(SimulationData.SIMULATION_TYPE_ITERATIONS);
			simulationData.setIterations(iteration);
		}

		if (arguments.hasOption(SimulatorOptions.CLOCK_PRECISION)) {
			long clockPrecision = 0;
			clockPrecision = Long.valueOf(arguments.getValue(SimulatorOptions.CLOCK_PRECISION));
			clockPrecision *= 60000;
			simulationData.setClockPrecision(clockPrecision);
		}

		if (arguments.hasOption(SimulatorOptions.OUTPUT_SCHEME)) {
			simulationData.setXmlSessionPath(arguments.getValue(SimulatorOptions.OUTPUT_SCHEME));
		}
		return arguments;
	}

	public static final void readSimulatonFile(Simulator simulator,
			SimulatorArguments options) {

		SimulationData simulationData = simulator.getSimulationData();
		boolean option = false;
		String fileName = null;
		double timeSim = 0.;
		double snapshotTime = -1.;

		if (options.hasOption(SimulatorOptions.STORIFY)) {
			fileName = options.getValue(SimulatorOptions.STORIFY);
			simulationData
					.setSimulationType(SimulationData.SIMULATION_TYPE_STORIFY);
			option = true;
		}
		if (options.hasOption(SimulatorOptions.TIME)) {
			try {
				timeSim = Double.valueOf(options.getValue(SimulatorOptions.TIME));
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
			simulationData.setTimeLength(timeSim);
		} else
			Simulator.println("*Warning* No time limit.");

		if (!option && (options.hasOption(SimulatorOptions.SIMULATIONFILE))) {
			option = true;
			fileName = options.getValue(SimulatorOptions.SIMULATIONFILE);
			if (options.hasOption(SimulatorOptions.SNAPSHOT_TIME)) {
				option = true;
				try {
					snapshotTime = Double.valueOf(options.getValue(SimulatorOptions.SNAPSHOT_TIME));
				} catch (Exception e) {
					throw new IllegalArgumentException(e);
				}
				simulationData.setSnapshotTime(snapshotTime);
			}
			simulationData
					.setSimulationType(SimulationData.SIMULATION_TYPE_SIM);
		}
		if (options.hasOption(SimulatorOptions.COMPILE)) {
			if (!option) {
				option = true;
				fileName = options.getValue(SimulatorOptions.COMPILE);
			} else
				option = false;
			simulationData
					.setSimulationType(SimulationData.SIMULATION_TYPE_COMPILE);
		}

		if (options.hasOption(SimulatorOptions.GENERATE_MAP)) {
			if (!option) {
				option = true;
				fileName = options.getValue(SimulatorOptions.GENERATE_MAP);
			} else
				option = false;
			simulationData
					.setSimulationType(SimulationData.SIMULATION_TYPE_CONTACT_MAP);
		}

		if (options.hasOption(SimulatorOptions.CONTACT_MAP)) {
			if (!option) {
				option = true;
				fileName = options.getValue(SimulatorOptions.CONTACT_MAP);
			} else
				option = false;
			simulationData
					.setSimulationType(SimulationData.SIMULATION_TYPE_CONTACT_MAP);
		}
		
		if (simulationData.getSimulationType() == SimulationData.SIMULATION_TYPE_NONE) {
			// HelpFormatter formatter = new HelpFormatter();
			// formatter.printHelp("use --sim [file]", cmdLineOptions);
			throw new IllegalArgumentException("No option specified");
		}

		simulationData.setInputFile(fileName);
		DataReading data = new DataReading(fileName);
		try {
			data.readData();
			Parser parser = new Parser(data, simulationData, simulator);
			parser.setForwarding(options.hasOption(SimulatorOptions.FORWARD));
			parser.parse();
		} catch (Exception e) {
			Simulator.println("Error in file \"" + fileName + "\" :");
			e.printStackTrace(Simulator.getErrorStream());
			throw new IllegalArgumentException(e);
		}
	}

	public void finished(SimulatorCallable simulatorCallable) {
		SimulatorResultsData results = simulatorCallable
				.getSimulatorResultsData();
		// TODO process results
	}

	public static Options getOptions() {
		return myOptions;
	}

}
