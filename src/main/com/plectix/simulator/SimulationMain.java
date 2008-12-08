package com.plectix.simulator;

import java.io.InputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.plectix.simulator.controller.SimulationService;
import com.plectix.simulator.controller.SimulatorCallable;
import com.plectix.simulator.controller.SimulatorCallableListener;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.controller.SimulatorInterface;
import com.plectix.simulator.controller.SimulatorResultsData;
import com.plectix.simulator.parser.DataReading;
import com.plectix.simulator.parser.FileReadingException;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.Parser;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.util.Info;

public class SimulationMain implements SimulatorCallableListener {

	private final static String SHORT_SIMULATIONFILE_OPTION = "s";
	private final static String LONG_SIMULATIONFILE_OPTION = "sim";
	private final static String LONG_COMPILE_OPTION = "compile";

	private final static String SHORT_TIME_OPTION = "t";
	private final static String LONG_TIME_OPTION = "time";
	private final static String LONG_SEED_OPTION = "seed";
	private final static String LONG_NO_SEED_OPTION = "no_seed";
	private final static String LONG_XML_SESSION_NAME_OPTION = "xml_session_name";
	private final static String LONG_EVENT_OPTION = "event";
	private final static String LONG_RANDOMIZER_JAVA_OPTION = "randomizer";
	private final static String LONG_SNAPSHOT_TIME = "set_snapshot_time";
	private final static String LONG_NO_ACTIVATION_MAP_OPTION = "no_activation_map";
	private final static String LONG_NO_MAPS_OPTION = "no_maps";
	private final static String LONG_NO_BUILD_INFLUENCE_MAP_OPTION = "no_build_influence_map";
	private final static String LONG_BUILD_INFLUENCE_MAP_OPTION = "build_influence_map";
	private final static String LONG_INIT_OPTION = "init";
	private final static String LONG_POINTS_OPTION = "points";
	private final static String LONG_RESCALE_OPTION = "rescale";
	private final static String LONG_MAX_CLASHES_OPTION = "max_clashes";
	private final static String LONG_OCAML_STYLE_OBS_NAME_OPTION = "ocaml_style_obs_name";
	private static final String LOG4J_PROPERTIES_FILENAME = "config/log4j.properties";
	private static final String LONG_CLOCK_PRECISION_OPTION = "clock_precision";
	private static final String LONG_FORWARD_OPTION = "forward";
	private static final String LONG_OUTPUT_SCHEME_OPTION = "output_scheme";
	private static final String LONG_KEY_OPTION = "key";
	private static final String LONG_NO_COMPRESS_STORIES_OPTION = "no_compress_stories";
	private static final String LONG_COMPRESS_STORIES_OPTION = "compress_stories";
	private static final String LONG_USE_STRONG_COMPRESSION_OPTION = "use_strong_compression";

	public final static String SHORT_COMPILE_OPTION = "c";
	public final static String LONG_GENERATE_MAP_OPTION = "generate_map";
	public final static String LONG_NUMBER_OF_RUNS_OPTION = "number_of_runs";
	public final static String LONG_STORIFY_OPTION = "storify";
	public final static String DEBUG_INIT_OPTION = "debug";

	public static Options cmdLineOptions;
	public CommandLine cmdLineArgs;

	private static Logger LOGGER = Logger.getLogger(SimulationMain.class);

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

		cmdLineOptions.addOption(LONG_NUMBER_OF_RUNS_OPTION, true,
				"Number of runs, generates tmp file");
		cmdLineOptions.addOption(LONG_SNAPSHOT_TIME, true,
				"Takes a snapshot of solution at specified time unit");

		cmdLineOptions.addOption(DEBUG_INIT_OPTION, false,
				"Program execution suspends right after initialization phase");
		cmdLineOptions.addOption(LONG_NO_ACTIVATION_MAP_OPTION, false,
				"Do not construct activation map");
		cmdLineOptions.addOption(LONG_INIT_OPTION, true,
				"Start taking measures (stories) at indicated time");
		cmdLineOptions.addOption(LONG_RESCALE_OPTION, true,
				"Rescaling factor (eg. '10.0' or '0.10')");
		cmdLineOptions.addOption(LONG_POINTS_OPTION, true,
				"Number of data points per plots");
		cmdLineOptions
				.addOption(LONG_MAX_CLASHES_OPTION, true,
						"Max number of consequtive clashes before aborting (default 100, 0=infinite)");
		cmdLineOptions.addOption(LONG_OCAML_STYLE_OBS_NAME_OPTION, false,
				"convert Obs names to simpx");
		cmdLineOptions
				.addOption(LONG_GENERATE_MAP_OPTION, true,
						"Name of the kappa file for which the influence map should be computed");
		cmdLineOptions.addOption(LONG_NO_SEED_OPTION, false,
				"Equivalent to --seed 0. Kept for compatibilty issue");
		cmdLineOptions.addOption(LONG_NO_MAPS_OPTION, false,
				"Do not construct inhibition/activation maps");
		cmdLineOptions.addOption(LONG_NO_BUILD_INFLUENCE_MAP_OPTION, false,
				"Do not construct influence map");
		cmdLineOptions.addOption(LONG_BUILD_INFLUENCE_MAP_OPTION, false,
				"Construct influence map");
		cmdLineOptions.addOption(LONG_CLOCK_PRECISION_OPTION, true,
				"(default: 60)clock precision (number of ticks per run)");
		cmdLineOptions.addOption(LONG_FORWARD_OPTION, false,
				"do not consider backward rules");
		cmdLineOptions.addOption(LONG_OUTPUT_SCHEME_OPTION, true,
				"(def: current dir) directory on which to put computed data");
		cmdLineOptions.addOption(LONG_KEY_OPTION, true,
				"Name of the file containing the key for the crypted version");
		cmdLineOptions.addOption(LONG_COMPRESS_STORIES_OPTION, false,
				"Weak compression of stories");
		cmdLineOptions.addOption(LONG_NO_COMPRESS_STORIES_OPTION, false,
				"Do not compress stories");
		cmdLineOptions.addOption(LONG_USE_STRONG_COMPRESSION_OPTION, false,
				"Use strong compression to classify stories");
	}

	public SimulationMain() {
	}

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
		service.submit(new SimulatorInputData(args, System.out), this);
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

	public static final CommandLine parseArguments(
			SimulationData simulationData, String[] args, Options cmdLineOptions) {
		simulationData.addInfo(new Info(Info.TYPE_INFO, "-Initialization..."));
		CommandLineParser parser = new PosixParser();
		CommandLine cmdLineArgs = null;
		try {
			cmdLineArgs = parser.parse(cmdLineOptions, args);
		} catch (ParseException e) {
			Simulator.println("Error parsing arguments:");
			e.printStackTrace(Simulator.getErrorStream());
			throw new IllegalArgumentException(e);
		}

		if (cmdLineArgs.hasOption(SimulationMain.LONG_XML_SESSION_NAME_OPTION)) {
			simulationData.setXmlSessionName(cmdLineArgs
					.getOptionValue(LONG_XML_SESSION_NAME_OPTION));
		}

		try {
			if (cmdLineArgs.hasOption(LONG_INIT_OPTION)) {
				simulationData.setInitialTime(Double.valueOf(cmdLineArgs
						.getOptionValue(LONG_INIT_OPTION)));
			}
			if (cmdLineArgs.hasOption(LONG_POINTS_OPTION)) {
				simulationData.setPoints(Integer.valueOf(cmdLineArgs
						.getOptionValue(LONG_POINTS_OPTION)));
			}
			if (cmdLineArgs.hasOption(LONG_RESCALE_OPTION)) {
				double rescale = Double.valueOf(cmdLineArgs
						.getOptionValue(LONG_RESCALE_OPTION));
				if (rescale > 0)
					simulationData.setRescale(rescale);
				else
					throw new Exception();
			}

			if (cmdLineArgs.hasOption(LONG_SEED_OPTION)) {
				int seed = 0;
				seed = Integer.valueOf(cmdLineArgs
						.getOptionValue(LONG_SEED_OPTION));
				simulationData.setSeed(seed);
			}

			if (cmdLineArgs.hasOption(LONG_MAX_CLASHES_OPTION)) {
				int max_clashes = 0;
				max_clashes = Integer.valueOf(cmdLineArgs
						.getOptionValue(LONG_MAX_CLASHES_OPTION));
				simulationData.setMaxClashes(max_clashes);
			}

			if (cmdLineArgs.hasOption(LONG_EVENT_OPTION)) {
				long event = 0;
				event = Long.valueOf(cmdLineArgs
						.getOptionValue(LONG_EVENT_OPTION));
				simulationData.setEvent(event);
			}

		} catch (Exception e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("use --sim [file]", cmdLineOptions);
			e.printStackTrace(Simulator.getErrorStream());
			throw new IllegalArgumentException(e);
		}

		if (cmdLineArgs.hasOption(LONG_RANDOMIZER_JAVA_OPTION)) {
			simulationData.setRandomizer(cmdLineArgs
					.getOptionValue(LONG_RANDOMIZER_JAVA_OPTION));
		}

		if (cmdLineArgs.hasOption(LONG_NO_ACTIVATION_MAP_OPTION)
				|| (cmdLineArgs.hasOption(LONG_NO_MAPS_OPTION))
				|| (cmdLineArgs.hasOption(LONG_NO_BUILD_INFLUENCE_MAP_OPTION))) {
			simulationData.setActivationMap(false);
		}

		if (cmdLineArgs.hasOption(LONG_OCAML_STYLE_OBS_NAME_OPTION)) {
			simulationData.setOcamlStyleObsName(true);
		}

		if (cmdLineArgs.hasOption(LONG_NUMBER_OF_RUNS_OPTION)) {
			int iteration = 0;
			boolean exp = false;
			try {
				iteration = Integer.valueOf(cmdLineArgs
						.getOptionValue(LONG_NUMBER_OF_RUNS_OPTION));
			} catch (Exception e) {
				exp = true;
			}
			if ((exp) || (!cmdLineArgs.hasOption(LONG_SEED_OPTION))) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("use --sim [file]", cmdLineOptions);
				throw new IllegalArgumentException("No SEED OPTION");
			}
			simulationData.setIterations(iteration);
		}

		if (cmdLineArgs.hasOption(LONG_CLOCK_PRECISION_OPTION)) {
			long clockPrecision = 0;
			clockPrecision = Long.valueOf(cmdLineArgs
					.getOptionValue(LONG_CLOCK_PRECISION_OPTION));
			clockPrecision *= 60000;
			simulationData.setClockPrecision(clockPrecision);
		}

		if (cmdLineArgs.hasOption(LONG_OUTPUT_SCHEME_OPTION)) {
			simulationData.setXmlSessionPath(cmdLineArgs
					.getOptionValue(LONG_OUTPUT_SCHEME_OPTION));
		}
		return cmdLineArgs;
	}

	public static final void readSimulatonFile(Simulator simulator,
			CommandLine cmdLineArgs) {

		SimulationData simulationData = simulator.getSimulationData();
		boolean option = false;
		String fileName = null;
		double timeSim = 0.;
		double snapshotTime = -1.;

		if (cmdLineArgs.hasOption(LONG_STORIFY_OPTION)) {
			fileName = cmdLineArgs.getOptionValue(LONG_STORIFY_OPTION);
			simulationData.setStorify(true);
			option = true;
		}
		if (cmdLineArgs.hasOption(LONG_TIME_OPTION)) {
			try {
				timeSim = Double.valueOf(cmdLineArgs
						.getOptionValue(LONG_TIME_OPTION));
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
			simulationData.setTimeLength(timeSim);
		} else
			Simulator.println("*Warning* No time limit.");

		if (!option && (cmdLineArgs.hasOption(SHORT_SIMULATIONFILE_OPTION))) {
			option = true;
			fileName = cmdLineArgs.getOptionValue(SHORT_SIMULATIONFILE_OPTION);
			if (cmdLineArgs.hasOption(LONG_SNAPSHOT_TIME)) {
				option = true;
				try {
					snapshotTime = Double.valueOf(cmdLineArgs
							.getOptionValue(LONG_SNAPSHOT_TIME));
				} catch (Exception e) {
					throw new IllegalArgumentException(e);
				}
				simulationData.setSnapshotTime(snapshotTime);
			}
		}
		if (cmdLineArgs.hasOption(SHORT_COMPILE_OPTION)) {
			simulationData.setCompile(true);
			if (!option) {
				option = true;
				fileName = cmdLineArgs.getOptionValue(SHORT_COMPILE_OPTION);
			} else
				option = false;
		}

		if (cmdLineArgs.hasOption(LONG_GENERATE_MAP_OPTION)) {
			if (!option) {
				option = true;
				fileName = cmdLineArgs.getOptionValue(LONG_GENERATE_MAP_OPTION);
			} else
				option = false;
		}

		if (!option) {
			// HelpFormatter formatter = new HelpFormatter();
			// formatter.printHelp("use --sim [file]", cmdLineOptions);
			throw new IllegalArgumentException("No option specified");
		}

		simulationData.setInputFile(fileName);
		DataReading data = new DataReading(fileName);
		try {
			data.readData();
			Parser parser = new Parser(data, simulationData, simulator);
			parser.setForwarding(cmdLineArgs.hasOption(LONG_FORWARD_OPTION));
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

}
