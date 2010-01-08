package com.plectix.simulator.simulator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

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
		this.commandLine = parser.parse(SimulatorOption.COMMAND_LINE_OPTIONS, commandLineArguments);
		// let's create simulation arguments:
		this.simulationArguments = createSimulationArguments();
	}

	public SimulatorCommandLine(String commandLineString) throws ParseException {
		// let's get the original command line before we change it below:
		this.commandLineString = commandLineString;
		// let's replace all '-' by '_' 
		String[] args = CommandLineUtils.normalize(commandLineString.split(" "));
		// let's parse the command line
		this.commandLine = (new PosixParser()).parse(SimulatorOption.COMMAND_LINE_OPTIONS, args);
		// let's create simulation arguments:
		this.simulationArguments = createSimulationArguments();
	}

	public final SimulationArguments getSimulationArguments() {
		return simulationArguments;
	}

	private final boolean hasOption(SimulatorOption option) {
		return commandLine.hasOption(option.getLongName());
	}

	private final String getValue(SimulatorOption option) {
		return commandLine.getOptionValue(option.getLongName());
	}

	private final int getIntValue(SimulatorOption option) {
		return Integer.parseInt(getValue(option));
	}

	private final long getLongValue(SimulatorOption option) {
		return Long.parseLong(getValue(option));
	}

	private final Double getDoubleValue(SimulatorOption option) {
		return Double.parseDouble(getValue(option));
	}
	
	private final SimulationArguments createSimulationArguments() throws ParseException {
		SimulationArguments simulationArguments = new SimulationArguments();

		simulationArguments.setCommandLineString(commandLineString);
		
		if (hasOption(SimulatorOption.NO_DUMP_STDOUT_STDERR)) {
			simulationArguments.setNoDumpStdoutStderr(true);
		}

		if (hasOption(SimulatorOption.HELP)) {
			simulationArguments.setHelp(true);
		}

		if (hasOption(SimulatorOption.VERSION)) {
			simulationArguments.setVersion(true);
		}
		
		if (hasOption(SimulatorOption.SHORT_CONSOLE_OUTPUT)) {
			simulationArguments.setShortConsoleOutput();
		}
		
		if (hasOption(SimulatorOption.OPERATION_MODE)) {
			simulationArguments.setOperationMode(getValue(SimulatorOption.OPERATION_MODE));
		}

		if (hasOption(SimulatorOption.XML_SESSION_NAME)) {
			simulationArguments.setXmlSessionName(getValue(SimulatorOption.XML_SESSION_NAME));
		}

		if (hasOption(SimulatorOption.OUTPUT_XML)) {
			simulationArguments.setXmlSessionName(getValue(SimulatorOption.OUTPUT_XML));
		}
		
		if (hasOption(SimulatorOption.INIT)) {
			simulationArguments.setInitialTime(getDoubleValue(SimulatorOption.INIT));
		}

		if (hasOption(SimulatorOption.POINTS)) {
			simulationArguments.setPoints(getIntValue(SimulatorOption.POINTS));
		}

		if (hasOption(SimulatorOption.RESCALE)) {
			double rescale = getDoubleValue(SimulatorOption.RESCALE);
			if (rescale > 0) {
				simulationArguments.setRescale(rescale);
			} else {
				throw new IllegalArgumentException("Negative rescale value: " + rescale);
			}
		}

		if (hasOption(SimulatorOption.NO_SEED)) {
			simulationArguments.setSeed(0);
		}

		// TODO else?
		if (hasOption(SimulatorOption.SEED)) {
			simulationArguments.setSeed(Integer.valueOf(getValue(SimulatorOption.SEED)));
		}

		if (hasOption(SimulatorOption.MAX_CLASHES)) {
			long max_clashes = getLongValue(SimulatorOption.MAX_CLASHES);
			if (max_clashes > 0) {
				simulationArguments.setMaxClashes(max_clashes);
			} else {
				throw new IllegalArgumentException("Can't set negative max_clashes: " + max_clashes);
			}
		}

		if (hasOption(SimulatorOption.ITERATION)) {
			simulationArguments.setIterations(getIntValue(SimulatorOption.ITERATION));
		}

		if (hasOption(SimulatorOption.RANDOMIZER_JAVA)) {
			//simulationArguments.setRandomizer(getValue(SimulatorOptions.RANDOMIZER_JAVA));
		}

		if (hasOption(SimulatorOption.NO_ACTIVATION_MAP)
				|| (hasOption(SimulatorOption.NO_MAPS))
				|| (hasOption(SimulatorOption.NO_BUILD_INFLUENCE_MAP))) {
			simulationArguments.setActivationMap(false);
		}
		
		if (hasOption(SimulatorOption.MERGE_MAPS)) {
			simulationArguments.setInhibitionMap(true);
			simulationArguments.setActivationMap(true);
		}
		
		if (hasOption(SimulatorOption.NO_INHIBITION_MAP)
				|| (hasOption(SimulatorOption.NO_MAPS))
				|| (hasOption(SimulatorOption.NO_BUILD_INFLUENCE_MAP))) {
			simulationArguments.setInhibitionMap(false);
		}
		
		if (hasOption(SimulatorOption.INHIBITION_MAP)
				&& !(hasOption(SimulatorOption.NO_MAPS))
				&& !(hasOption(SimulatorOption.NO_BUILD_INFLUENCE_MAP))) {
			simulationArguments.setInhibitionMap(true);
		}

		if (hasOption(SimulatorOption.BUILD_INFLUENCE_MAP)){
			simulationArguments.setActivationMap(true);
			simulationArguments.setInhibitionMap(true);
		}

		if (hasOption(SimulatorOption.COMPILE)) {
			simulationArguments.setCompile(true);
		}

		if (hasOption(SimulatorOption.DEBUG_INIT)) {
			simulationArguments.setDebugInit(true);
		}

		if (hasOption(SimulatorOption.STORIFY)) {
			simulationArguments.setStorify(true);
		}

		if (hasOption(SimulatorOption.OCAML_STYLE_OBS_NAME)) {
			simulationArguments.setOcamlStyleObservableNames(true);
		}

		if (hasOption(SimulatorOption.UNIFIED_TIME_SERIES_OUTPUT)) {
			simulationArguments.setUnifiedTimeSeriesOutput(true);
		}

		/*
		 * old code
		 *
		 * if (hasOption(SimulatorOptions.WALL_CLOCK_TIME_LIMIT)) {
		 *  	simulationArguments.setWallClockTimeLimit(1000 * getLongValue(SimulatorOptions.WALL_CLOCK_TIME_LIMIT));
		 * }
		 *   
		 */
		
		if (hasOption(SimulatorOption.WALL_CLOCK_TIME_LIMIT)) {
			simulationArguments.setWallClockTimeLimit(getLongValue(SimulatorOption.WALL_CLOCK_TIME_LIMIT));
		}

		if (hasOption(SimulatorOption.MONITOR_PEAK_MEMORY)) {
			simulationArguments.setMonitorPeakMemory(getLongValue(SimulatorOption.MONITOR_PEAK_MEMORY));
		}

		if (hasOption(SimulatorOption.REJECT_INCOMPLETES)) {
			simulationArguments.rejectIncompletes();
		}
		
		if (hasOption(SimulatorOption.CLOCK_PRECISION)) {
			simulationArguments.setClockPrecision(getIntValue(SimulatorOption.CLOCK_PRECISION));
		}
		
		if (hasOption(SimulatorOption.OUTPUT_FINAL_STATE)) {
			simulationArguments.setOutputFinalState(true);
		}

		if (hasOption(SimulatorOption.OUTPUT_SCHEME)) {
			simulationArguments.setXmlSessionPath(getValue(SimulatorOption.OUTPUT_SCHEME));
		}

		/**=====================================================================**/
		/*							STORIES										**/
		/**=====================================================================**/

		if(hasOption(SimulatorOption.DONT_COMPRESS_STORIES) && hasOption(SimulatorOption.DONT_USE_STRONG_COMPRESSION)){
			simulationArguments.setStorifyMode(SimulationArguments.StoryCompressionMode.NONE);
		}

		if (hasOption(SimulatorOption.USE_STRONG_COMPRESSION) || hasOption(SimulatorOption.COMPRESS_STORIES)) {
			simulationArguments.setStorifyMode(SimulationArguments.StoryCompressionMode.STRONG);
		}

		if(hasOption(SimulatorOption.COMPRESS_STORIES) && hasOption(SimulatorOption.DONT_USE_STRONG_COMPRESSION)){
			simulationArguments.setStorifyMode(SimulationArguments.StoryCompressionMode.WEAK);
		}

		if (hasOption(SimulatorOption.EVENT)) {
			simulationArguments.setMaxNumberOfEvents(getLongValue(SimulatorOption.EVENT));
			simulationArguments.setTime(false);
		}
		
		if (hasOption(SimulatorOption.TIME)) {
			simulationArguments.setTimeLength(getDoubleValue(SimulatorOption.TIME));
			simulationArguments.setTime(true);
		} 

		if (hasOption(SimulatorOption.AGENTS_LIMIT)) {
			simulationArguments.setAgentsLimit(getIntValue(SimulatorOption.AGENTS_LIMIT));
		}
		
		if (hasOption(SimulatorOption.LIVE_DATA_INTERVAL)) {
			simulationArguments.setLiveDataInterval(getIntValue(SimulatorOption.LIVE_DATA_INTERVAL));
		}
		
		if (hasOption(SimulatorOption.LIVE_DATA_CONSUMER_CLASSNAME)) {
			simulationArguments.setLiveDataConsumerClassname(getValue(SimulatorOption.LIVE_DATA_CONSUMER_CLASSNAME));
		}

		if (hasOption(SimulatorOption.ALLOW_INCOMPLETE_SUBSTANCE)) {
			simulationArguments.setAllowIncompleteSubstance(true);
		}
		
		if (hasOption(SimulatorOption.REPORT_EXACT_SAMPLE_TIME)) {
			simulationArguments.setReportExactSampleTime(true);
		}

		if (hasOption(SimulatorOption.REPORT_AFTER_SAMPLE_TIME)) {
			simulationArguments.setReportExactSampleTime(false);
		}

		if (hasOption(SimulatorOption.LIVE_DATA_POINTS)) {
			simulationArguments.setLiveDataPoints(getIntValue(SimulatorOption.LIVE_DATA_POINTS));
		}
		
		
		boolean option = false;
		String fileName = null;
		
		if (hasOption(SimulatorOption.STORIFY)) {
			fileName = setNewFileName(fileName,getValue(SimulatorOption.STORIFY));
			simulationArguments.setSimulationType(SimulationArguments.SimulationType.STORIFY);
			option = true;
		}
		
	
		if (!option && (hasOption(SimulatorOption.SIMULATIONFILE))) {
			option = true;
			fileName = setNewFileName(fileName,getValue(SimulatorOption.SIMULATIONFILE));
			if (hasOption(SimulatorOption.SNAPSHOT_TIME)) {
				option = true;
				try {
					simulationArguments.setSnapshotsTimeString(getValue(SimulatorOption.SNAPSHOT_TIME));
				} catch (Exception e) {
					throw new IllegalArgumentException(e);
				}
			}
			simulationArguments.setSimulationType(SimulationArguments.SimulationType.SIM);
		}
		
		if (hasOption(SimulatorOption.COMPILE)) {
			if (!option) {
				option = true;
				fileName = setNewFileName(fileName,getValue(SimulatorOption.COMPILE));
			} else {
				option = false;
			}
			simulationArguments.setSimulationType(SimulationArguments.SimulationType.COMPILE);
		}
	
		if (hasOption(SimulatorOption.GENERATE_MAP)) {
			if (!option) {
				option = true;
				fileName = setNewFileName(fileName,getValue(SimulatorOption.GENERATE_MAP));
			} else {
				option = false;
			}
			simulationArguments.setGenereteMap(true);
			simulationArguments.setSimulationType(SimulationArguments.SimulationType.GENERATE_MAP);
		}
		
		if (hasOption(SimulatorOption.COMPUTE_SUB_VIEWS)) {
			simulationArguments.setCreateSubViews(true);
		}
	
		if (hasOption(SimulatorOption.COMPUTE_DEAD_RILES)) {
			simulationArguments.setShowDeadRules(true);
		}

		if (hasOption(SimulatorOption.CONTACT_MAP)) {
			if (!option) {
				option = true;
				fileName = setNewFileName(fileName,getValue(SimulatorOption.CONTACT_MAP));
			} else {
				option = false;
			}
			
			simulationArguments.setSimulationType(SimulationArguments.SimulationType.CONTACT_MAP);
		}
		
		if (hasOption(SimulatorOption.QUALITATIVE_COMPRESSION)) {
			if (!option) {
				option = true;
				fileName = setNewFileName(fileName,getValue(SimulatorOption.QUALITATIVE_COMPRESSION));
				simulationArguments.setRunQualitativeCompression(true);
				
			} else {
				option = false;
			}
			simulationArguments.setSimulationType(SimulationArguments.SimulationType.CONTACT_MAP);
		}
		
		if (hasOption(SimulatorOption.QUANTITATIVE_COMPRESSION)) {
			if (!option) {
				option = true;
				fileName = setNewFileName(fileName,getValue(SimulatorOption.QUANTITATIVE_COMPRESSION));
				simulationArguments.setRunQuantitativeCompression(true);
			} else {
				option = false;
			}
			simulationArguments.setSimulationType(SimulationArguments.SimulationType.CONTACT_MAP);
		}
		
		if (hasOption(SimulatorOption.COMPUTE_LOCAL_VIEWS)) {
			simulationArguments.setCreateLocalViews(true);
		}

		if (hasOption(SimulatorOption.NO_COMPUTE_LOCAL_VIEWS)) {
			simulationArguments.setCreateLocalViews(false);
		}
		
		if (hasOption(SimulatorOption.ENUMERATE_COMPLEXES)) {
			simulationArguments.setEnumerationOfSpecies(true);
		}

		if(hasOption(SimulatorOption.OUTPUT_QUANTITATIVE_COMPRESSION)){
			simulationArguments.setRunQuantitativeCompression(true);
		}

		if(hasOption(SimulatorOption.OUTPUT_QUALITATIVE_COMPRESSION)){
			simulationArguments.setRunQualitativeCompression(true);
		}

		if (hasOption(SimulatorOption.NO_ENUMERATE_COMPLEXES)) {
			simulationArguments.setEnumerationOfSpecies(false);
		}

		if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.NONE) {
			if (!simulationArguments.isHelp() && !simulationArguments.isVersion()) {
				// HelpFormatter formatter = new HelpFormatter();
				// formatter.printHelp("use --sim [file]", cmdLineOptions);
				throw new IllegalArgumentException("No option specified");
			}
		}
	
		simulationArguments.setInputFilename(fileName);
		
		if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
			if (hasOption(SimulatorOption.FOCUS_ON)) {
				simulationArguments.setFocusFilename(getValue(SimulatorOption.FOCUS_ON));
			}
		}
		
		simulationArguments.setForwardOnly(hasOption(SimulatorOption.FORWARD));
		
		return simulationArguments;
	}

	private String setNewFileName(String fileName, String value) throws ParseException {
		if(fileName!=null){
			throw new ParseException("two input files!");
		}
		return value;
	}
}
