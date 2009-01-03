package com.plectix.simulator.simulator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;


public class SimulatorCommandLine {
	public static final int CLOCK_PRECISION_MULTIPLIER = 60000;

	private final String commandLineString;
	
	private final CommandLine commandLine;
	
	private final SimulationArguments simulationArguments;

	public SimulatorCommandLine(String[] args) throws ParseException {
		// let's get the original command line before we change it below:
		this.commandLineString = SimulationUtils.getCommandLineString(args);
		// let's create the parser
		CommandLineParser parser = new PosixParser();
		// let's replace all '-' by '_' 
		args = SimulationUtils.changeArguments(args);
		// let's parse the command line
		this.commandLine = parser.parse(SimulatorOptions.COMMAND_LINE_OPTIONS, args);
		// let's create simulation arguments:
		this.simulationArguments = createSimulationArguments();
	}

	public final SimulationArguments getSimulationArguments() {
		return simulationArguments;
	}

	private final boolean hasOption(SimulatorOptions option) {
		return commandLine.hasOption(option.getLongName());
	}

	private final String getValue(SimulatorOptions option) {
		return commandLine.getOptionValue(option.getLongName());
	}

	private final int getIntValue(SimulatorOptions option) {
		return Integer.parseInt(getValue(option));
	}

	private final long getLongValue(SimulatorOptions option) {
		return Long.parseLong(getValue(option));
	}

	private final Double getDoubleValue(SimulatorOptions option) {
		return Double.parseDouble(getValue(option));
	}
	
	private SimulationArguments createSimulationArguments() {
		SimulationArguments simulationArguments = new SimulationArguments();

		simulationArguments.setCommandLineString(commandLineString);
		
		if (hasOption(SimulatorOptions.NO_DUMP_STDOUT_STDERR)) {
			simulationArguments.setNoDumpStdoutStderr(true);
		}

		if (hasOption(SimulatorOptions.HELP)) {
			simulationArguments.setHelp(true);
		}

		if (hasOption(SimulatorOptions.VERSION)) {
			simulationArguments.setVersion(true);
		}
		
		if (hasOption(SimulatorOptions.XML_SESSION_NAME)) {
			simulationArguments.setXmlSessionName(getValue(SimulatorOptions.XML_SESSION_NAME));
		}

		if (hasOption(SimulatorOptions.OUTPUT_XML)) {
			simulationArguments.setXmlSessionName(getValue(SimulatorOptions.OUTPUT_XML));
		}
		
		if (hasOption(SimulatorOptions.INIT)) {
			simulationArguments.setInitialTime(getDoubleValue(SimulatorOptions.INIT));
		}

		if (hasOption(SimulatorOptions.POINTS)) {
			simulationArguments.setPoints(getIntValue(SimulatorOptions.POINTS));
		}

		if (hasOption(SimulatorOptions.RESCALE)) {
			double rescale = getDoubleValue(SimulatorOptions.RESCALE);
			if (rescale > 0) {
				simulationArguments.setRescale(rescale);
			} else {
				throw new IllegalArgumentException("Negative rescale value: " + rescale);
			}
		}

		if (hasOption(SimulatorOptions.NO_SEED)) {
			simulationArguments.setSeed(0);
		}

		// TODO else?
		if (hasOption(SimulatorOptions.SEED)) {
			simulationArguments.setSeed(Integer.valueOf(getValue(SimulatorOptions.SEED)));
		}

		if (hasOption(SimulatorOptions.MAX_CLASHES)) {
			long max_clashes = getLongValue(SimulatorOptions.MAX_CLASHES);
			if (max_clashes > 0) {
				simulationArguments.setMaxClashes(max_clashes);
			} else {
				throw new IllegalArgumentException("Can't set negative max_clashes: " + max_clashes);
			}
		}

		if (hasOption(SimulatorOptions.ITERATION)) {
			simulationArguments.setIterations(getIntValue(SimulatorOptions.ITERATION));
		}

		if (hasOption(SimulatorOptions.RANDOMIZER_JAVA)) {
			simulationArguments.setRandomizer(getValue(SimulatorOptions.RANDOMIZER_JAVA));
		}

		if (hasOption(SimulatorOptions.NO_ACTIVATION_MAP)
				|| (hasOption(SimulatorOptions.NO_MAPS))
				|| (hasOption(SimulatorOptions.NO_BUILD_INFLUENCE_MAP))) {
			simulationArguments.setActivationMap(false);
		}

		if (hasOption(SimulatorOptions.MERGE_MAPS)) {
			simulationArguments.setInhibitionMap(true);
		}
		
		if (hasOption(SimulatorOptions.NO_INHIBITION_MAP)
				|| (hasOption(SimulatorOptions.NO_MAPS))
				|| (hasOption(SimulatorOptions.NO_BUILD_INFLUENCE_MAP))) {
			simulationArguments.setInhibitionMap(false);
		}

		if (hasOption(SimulatorOptions.COMPILE)) {
			simulationArguments.setCompile(true);
		}

		if (hasOption(SimulatorOptions.DEBUG_INIT)) {
			simulationArguments.setDebugInit(true);
		}

		if (hasOption(SimulatorOptions.GENERATE_MAP)) {
			simulationArguments.setGenereteMap(true);
		}

		if (hasOption(SimulatorOptions.CONTACT_MAP)) {
			simulationArguments.setContactMap(true);
		}

		if (hasOption(SimulatorOptions.NUMBER_OF_RUNS)) {
			simulationArguments.setNumberOfRuns(true);
		}

		if (hasOption(SimulatorOptions.STORIFY)) {
			simulationArguments.setStorify(true);
		}

		if (hasOption(SimulatorOptions.OCAML_STYLE_OBS_NAME)) {
			simulationArguments.setOcamlStyleObservableNames(true);
		}

		if (hasOption(SimulatorOptions.NUMBER_OF_RUNS)) {
			simulationArguments.setIterations(getIntValue(SimulatorOptions.NUMBER_OF_RUNS));

			if (!hasOption(SimulatorOptions.SEED)) {
				throw new IllegalArgumentException("No SEED OPTION");
			}

			simulationArguments.setSimulationType(SimulationArguments.SimulationType.AVERAGE_OF_RUNS);
		}

		if (hasOption(SimulatorOptions.CLOCK_PRECISION)) {
			simulationArguments.setClockPrecision(CLOCK_PRECISION_MULTIPLIER * getLongValue(SimulatorOptions.CLOCK_PRECISION));
		}

		if (hasOption(SimulatorOptions.OUTPUT_FINAL_STATE)) {
			simulationArguments.setOutputFinalState(true);
		}

		if (hasOption(SimulatorOptions.OUTPUT_SCHEME)) {
			simulationArguments.setXmlSessionPath(getValue(SimulatorOptions.OUTPUT_SCHEME));
		}

		if (hasOption(SimulatorOptions.NO_SAVE_ALL)) {
			simulationArguments.setSerializationMode(SimulationArguments.SerializationMode.NONE);
		}

		if (hasOption(SimulatorOptions.SAVE_ALL)) {
			simulationArguments.setSerializationFileName(getValue(SimulatorOptions.SAVE_ALL)) ;
		}

		if (hasOption(SimulatorOptions.DONT_COMPRESS_STORIES)) {
			simulationArguments.setStorifyMode(SimulationArguments.StorifyMode.NONE);
		}

		if (hasOption(SimulatorOptions.COMPRESS_STORIES)) {
			simulationArguments.setStorifyMode(SimulationArguments.StorifyMode.WEAK);
		}

		if (hasOption(SimulatorOptions.USE_STRONG_COMPRESSION)) {
			simulationArguments.setStorifyMode(SimulationArguments.StorifyMode.STRONG);
		}

		if (hasOption(SimulatorOptions.EVENT)) {
			simulationArguments.setEvent(getLongValue(SimulatorOptions.EVENT));
			simulationArguments.setTime(false);
		}
		
		if (hasOption(SimulatorOptions.TIME)) {
			simulationArguments.setTimeLength(getDoubleValue(SimulatorOptions.TIME));
			simulationArguments.setTime(true);
		} 
		
		boolean option = false;
		String fileName = null;
		
		if (hasOption(SimulatorOptions.STORIFY)) {
			fileName = getValue(SimulatorOptions.STORIFY);
			simulationArguments.setSimulationType(SimulationArguments.SimulationType.STORIFY);
			option = true;
		}
		
	
		if (!option && (hasOption(SimulatorOptions.SIMULATIONFILE))) {
			option = true;
			fileName = getValue(SimulatorOptions.SIMULATIONFILE);
			if (hasOption(SimulatorOptions.SNAPSHOT_TIME)) {
				option = true;
				try {
					simulationArguments.setSnapshotsTimeString(getValue(SimulatorOptions.SNAPSHOT_TIME));
				} catch (Exception e) {
					throw new IllegalArgumentException(e);
				}
			}
			simulationArguments.setSimulationType(SimulationArguments.SimulationType.SIM);
		}
		
		if (hasOption(SimulatorOptions.COMPILE)) {
			if (!option) {
				option = true;
				fileName = getValue(SimulatorOptions.COMPILE);
			} else {
				option = false;
			}
			simulationArguments.setSimulationType(SimulationArguments.SimulationType.COMPILE);
		}
	
		if (hasOption(SimulatorOptions.GENERATE_MAP)) {
			if (!option) {
				option = true;
				fileName = getValue(SimulatorOptions.GENERATE_MAP);
			} else {
				option = false;
			}
			
			simulationArguments.setSimulationType(SimulationArguments.SimulationType.GENERATE_MAP);
		}
	
		if (hasOption(SimulatorOptions.CONTACT_MAP)) {
			if (!option) {
				option = true;
				fileName = getValue(SimulatorOptions.CONTACT_MAP);
			} else {
				option = false;
			}
			
			simulationArguments.setSimulationType(SimulationArguments.SimulationType.CONTACT_MAP);
		}
	
		if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.NONE) {
			if (!simulationArguments.isHelp() && !simulationArguments.isVersion()) {
				// HelpFormatter formatter = new HelpFormatter();
				// formatter.printHelp("use --sim [file]", cmdLineOptions);
				throw new IllegalArgumentException("No option specified");
			}
		}
	
		simulationArguments.setInputFile(fileName);
		
		if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
			if (hasOption(SimulatorOptions.FOCUS_ON)) {
				simulationArguments.setFocusFilename(getValue(SimulatorOptions.FOCUS_ON));
			}
		}
		
		simulationArguments.setForward(hasOption(SimulatorOptions.FORWARD));
		
		return simulationArguments;
	}
}
