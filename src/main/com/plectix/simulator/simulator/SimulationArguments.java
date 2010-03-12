package com.plectix.simulator.simulator;

import static com.plectix.simulator.simulator.options.SimulatorFlagOption.ALLOW_INCOMPLETE_SUBSTANCE;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.BUILD_INFLUENCE_MAP;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.COMPRESS_STORIES;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.COMPUTE_DEAD_RILES;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.COMPUTE_LOCAL_VIEWS;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.COMPUTE_SUB_VIEWS;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.DEBUG_INIT;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.DONT_COMPRESS_STORIES;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.DONT_USE_STRONG_COMPRESSION;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.ENUMERATE_COMPLEXES;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.FORWARD;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.HELP;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.INHIBITION_MAP;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.MERGE_MAPS;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.NO_ACTIVATION_MAP;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.NO_BUILD_INFLUENCE_MAP;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.NO_DUMP_STDOUT_STDERR;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.NO_INHIBITION_MAP;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.NO_MAPS;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.NO_SEED;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.OCAML_STYLE_OBS_NAME;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.OUTPUT_FINAL_STATE;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.REPORT_AFTER_SAMPLE_TIME;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.REPORT_EXACT_SAMPLE_TIME;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.USE_STRONG_COMPRESSION;
import static com.plectix.simulator.simulator.options.SimulatorFlagOption.VERSION;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.AGENTS_LIMIT;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.CLOCK_PRECISION;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.COMPILE;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.CONTACT_MAP;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.EVENT;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.FOCUS_ON;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.GENERATE_INFLUENCE_MAP;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.INIT;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.INPUT;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.ITERATION;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.LIVE_DATA_CONSUMER_CLASSNAME;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.LIVE_DATA_INTERVAL;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.LIVE_DATA_POINTS;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.MAX_CLASHES;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.MONITOR_PEAK_MEMORY;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.OPERATION_MODE;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.OUTPUT_SCHEME;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.OUTPUT_XML;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.POINTS;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.QUALITATIVE_COMPRESSION;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.QUANTITATIVE_COMPRESSION;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.RESCALE;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.SEED;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.SIMULATIONFILE;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.SNAPSHOT_TIME;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.STORIFY;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.TIME;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.WALL_CLOCK_TIME_LIMIT;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.XML_SESSION_NAME;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

import com.plectix.simulator.simulationclasses.solution.OperationMode;
import com.plectix.simulator.simulator.options.CommandLineParametersManager;
import com.plectix.simulator.simulator.options.SimulatorArgumentsDefaultValues;
import com.plectix.simulator.simulator.options.SimulatorFlagOption;
import com.plectix.simulator.simulator.options.SimulatorParameter;
import com.plectix.simulator.simulator.options.SimulatorParameterizedOption;
import com.plectix.simulator.util.Info.InfoType;

public class SimulationArguments {
	public enum StoryCompressionMode {
		/** Sets the mode for stories to No Compression */
		NONE,
		/** Sets the mode for stories to Weak Compression */
		WEAK,
		/** Sets the mode for stories to Strong Compression */
		STRONG
	}
	
	public enum SimulationType { 
		NONE,
		COMPILE,
		STORIFY,
		SIM,
		GENERATE_MAP,
		CONTACT_MAP;
		
		//TODO maybe some corrections here
		public boolean hasSimilarCompilationStage(SimulationType type) {
			return (this == type) || (this == NONE); 
		}
	}
	
	private CommandLine commandLine;
	private CommandLineParametersManager commandLineManager;
	private Set<SimulatorFlagOption> flags = new HashSet<SimulatorFlagOption>();
	private final Map<SimulatorParameterizedOption, SimulatorParameter<?>> parameters
			= new LinkedHashMap<SimulatorParameterizedOption, SimulatorParameter<?>>();
	
	private boolean activationMap = false;
	private boolean inhibitionMap = false;
	private boolean unifiedTimeSeriesOutput;
	private InfoType shortConsoleOutput = InfoType.OUTPUT;
	private char[] inputCharArray = null;
	private String commandLineString = null;
	private SimulationType simulationType = SimulationType.NONE;
	private StoryCompressionMode storifyMode = StoryCompressionMode.NONE;
	
	
	
	public SimulationArguments() {
		this.parameters.putAll(SimulatorArgumentsDefaultValues.DEFAULT_VALUES);
		this.addUncheckedFlag(REPORT_EXACT_SAMPLE_TIME);
		this.addUncheckedFlag(ALLOW_INCOMPLETE_SUBSTANCE);
	}

	public final void read(CommandLine commandLine, String commandLineString) throws ParseException {
		
		this.commandLineString = commandLineString;
		this.commandLine = commandLine;
		this.commandLineManager = new CommandLineParametersManager(commandLine);
		
		// TODO add all flags within one loop
		for (SimulatorFlagOption flag : SimulatorFlagOption.values()) {
			this.addFlag(flag);
		}
		
		for (SimulatorParameterizedOption parameter : SimulatorParameterizedOption.values()) {
			if (parameter.shouldHavePositivePrameter()) {
				this.addPositiveParameter(parameter);
			} else {
				this.addParameter(parameter);
			}
		}
		
		/*
		 * TODO <EVIL>
		 */
		
		this.addAllBadBadBadOptions();
		this.initStorifyMode();
		this.initMapsStuff();
		
		/*
		 * TODO </EVIL>
		 */
	}
	
	private final void initMapsStuff() {
		if (commandLineManager.hasOption(NO_ACTIVATION_MAP)
				|| (commandLineManager.hasOption(NO_MAPS))
				|| (commandLineManager.hasOption(NO_BUILD_INFLUENCE_MAP))) {
			
			this.setActivationMap(false);
		}
		
		if (commandLineManager.hasOption(MERGE_MAPS)) {
			this.setInhibitionMap(true);
			this.setActivationMap(true);
		}
		
		if (commandLineManager.hasOption(NO_INHIBITION_MAP)
				|| (commandLineManager.hasOption(NO_MAPS))
				|| (commandLineManager.hasOption(NO_BUILD_INFLUENCE_MAP))) {
			this.setInhibitionMap(false);
		}
		
		if (commandLineManager.hasOption(INHIBITION_MAP)
				&& !(commandLineManager.hasOption(NO_MAPS))
				&& !(commandLineManager.hasOption(NO_BUILD_INFLUENCE_MAP))) {
			this.setInhibitionMap(true);
		}

		if (commandLineManager.hasOption(BUILD_INFLUENCE_MAP)){
			this.setActivationMap(true);
			this.setInhibitionMap(true);
		}	
	}
	
	// TODO HEAVY REFACTORING
	private final void initStorifyMode() {
		if (commandLineManager.hasOption(DONT_COMPRESS_STORIES) 
				&& commandLineManager.hasOption(DONT_USE_STRONG_COMPRESSION)){
			this.setStorifyMode(StoryCompressionMode.NONE);
		}

		if (commandLineManager.hasOption(USE_STRONG_COMPRESSION) 
				|| commandLineManager.hasOption(COMPRESS_STORIES)) {
			this.setStorifyMode(StoryCompressionMode.STRONG);
		}

		if(commandLineManager.hasOption(COMPRESS_STORIES) 
				&& commandLineManager.hasOption(DONT_USE_STRONG_COMPRESSION)){
			this.setStorifyMode(StoryCompressionMode.WEAK);
		}	
	}
	
	public void setStorifyMode(StoryCompressionMode mode) {
		this.storifyMode = mode;
	}

	private String setNewFileName(String fileName, SimulatorParameterizedOption option) throws ParseException {
		if( fileName != null){
			throw new ParseException("two input files!");
		} else {
			String newName = commandLineManager.retrieveParameterAsString(option);
			this.setInputFileName(newName);
			this.addParameter(option, new SimulatorParameter<String>(newName));
			return newName;
		}
	}
	
	// TODO HEAVY REFACTORING
	private final void addAllBadBadBadOptions() throws ParseException {
		boolean option = false;
		String fileName = null;
		
		if (commandLineManager.hasOption(STORIFY)) {
			this.addParameter(STORIFY);
			fileName = setNewFileName(fileName, STORIFY);
			setSimulationType(SimulationType.STORIFY);
			option = true;
		}
	
		if (!option && (commandLineManager.hasOption(SIMULATIONFILE))) {
			option = true;
			fileName = setNewFileName(fileName, SIMULATIONFILE);
			this.setSimulationType(SimulationType.SIM);
		}
		
		if (!option && commandLineManager.hasOption(COMPILE)) {
			fileName = setNewFileName(fileName, COMPILE);
			this.setSimulationType(SimulationType.COMPILE);
		}
	
		if (!option && commandLineManager.hasOption(GENERATE_INFLUENCE_MAP)) {
			fileName = setNewFileName(fileName, GENERATE_INFLUENCE_MAP);
			this.setSimulationType(SimulationType.GENERATE_MAP);
		}
		
		if (!option && commandLineManager.hasOption(CONTACT_MAP)) {
			fileName = setNewFileName(fileName, CONTACT_MAP);
			this.setSimulationType(SimulationType.CONTACT_MAP);
		}
		
		if (!option && commandLineManager.hasOption(QUALITATIVE_COMPRESSION)) {
			fileName = setNewFileName(fileName, QUALITATIVE_COMPRESSION);
			this.setSimulationType(SimulationType.CONTACT_MAP);
		}
		
		if (!option && commandLineManager.hasOption(QUANTITATIVE_COMPRESSION)) {
			fileName = setNewFileName(fileName, QUANTITATIVE_COMPRESSION);
			this.setSimulationType(SimulationType.CONTACT_MAP);
		}
	}
	
	private final void addFlag(SimulatorFlagOption flag) {
		if (commandLineManager.hasOption(flag)) {
			this.addUncheckedFlag(flag);
		}
	}
	
	private final void addUncheckedFlag(SimulatorFlagOption flag) {
		flags.add(flag);
	}
	
	private final void addUncheckedFlag(SimulatorFlagOption flag, boolean switcher) {
		if (switcher) {
			this.addUncheckedFlag(flag);
		} else {
			this.removeFlag(flag);
		}
	}
	
	private void removeFlag(SimulatorFlagOption flag) {
		flags.remove(flag);
	}
	
	private final boolean containsFlag(SimulatorFlagOption flag) {
		return flags.contains(flag);
	}
	
	private final boolean containsParameter(SimulatorParameterizedOption option) {
		return parameters.containsKey(option);
	}
	
	private final void addPositiveParameter(SimulatorParameterizedOption option) {
		this.addParameter(option, commandLineManager.retrievePositiveParameter(option));
	}
	
	private final void addParameter(SimulatorParameterizedOption option) {
		if (commandLine.hasOption(option.getLongName())) {
			SimulatorParameter<?> parameter = commandLineManager.retrieveParameter(option);
			this.addParameter(option, parameter);
		}
	}
	
	private final void addParameter(SimulatorParameterizedOption option, SimulatorParameter<?> parameter) {
		if (commandLine.hasOption(option.getLongName())) {
			this.addUncheckedParameter(option, parameter);
		}
	}
	
	private final void addUncheckedParameter(SimulatorParameterizedOption option, SimulatorParameter<?> parameter) {
		if (option.getParameterType().equals(parameter.getType())) {
			parameters.put(option, parameter);
		} else {
			throw new IllegalArgumentException(option + " parameter must have type " + option.getParameterType());
		}
	}
	
	private void removeParameter(SimulatorParameterizedOption option) {
		this.parameters.remove(option);
	}

	
	
	/*
	 * ------------------------------------------
	 * 			GETTERS / SETTERS
	 * ------------------------------------------
	 */
	public final <E> E nullSafeValueRetrieve(SimulatorParameterizedOption option, Class<E> type) {
		SimulatorParameter<?> parameter = parameters.get(option);
		if (parameter == null) {
			return null;
		} else {
			return (E)parameter.getValue();
		}
	}
	
	public final String getCommandLineString() {
		return commandLineString;
	}
	
	public final double getRescale() {
		return nullSafeValueRetrieve(RESCALE, Double.class);
	}
	
	/**
	 * Use only this method to check XMLOutputPath
	 */
	public final String getXmlOutputDestination() {
		if (this.getXmlSessionPath() != null && this.getXmlSessionPath().length() > 0) {
			return this.getXmlSessionPath() + File.separator
					+ this.getXmlSessionName();
		} else {
			return this.getXmlSessionName();
		}
	}
	
	private final String getXmlSessionPath() {
		return nullSafeValueRetrieve(OUTPUT_SCHEME, String.class);
	}
	
	private final String getXmlSessionName() {
		String xmlOutput = nullSafeValueRetrieve(OUTPUT_XML, String.class);
		if (xmlOutput != null) {
			return xmlOutput;
		} else {
			return nullSafeValueRetrieve(XML_SESSION_NAME, String.class);	
		}
	}
	
	public final void setXmlOutputDestination(String xmlOutputDestinaion) {
		this.addUncheckedParameter(XML_SESSION_NAME, new SimulatorParameter<String>(xmlOutputDestinaion));
	}
	
	public final int getSeed() {
		Integer seedParameter = nullSafeValueRetrieve(SEED, Integer.class); 
		if (seedParameter != null) {
			return seedParameter;
		} else if (this.containsFlag(NO_SEED)) {
			return 0;
		} else {
			return SimulatorArgumentsDefaultValues.DEFAULT_SEED;
		}
	}
	
	public final long getMaxClashes() {
		return nullSafeValueRetrieve(MAX_CLASHES, Long.class);
	}
	
	public boolean getReportExactSampleTime() {
		if (this.containsFlag(REPORT_AFTER_SAMPLE_TIME)) {
			return false;
		} else {
			return this.containsFlag(REPORT_EXACT_SAMPLE_TIME);
		}
	}
	
	public final void setSimulationType(SimulationType type) {
		this.simulationType = type;
	}
	
	public final SimulationType getSimulationType() {
		return simulationType;
	}
	
	public final boolean getGenereteMap() {
		return this.containsParameter(GENERATE_INFLUENCE_MAP);
	}
	
	public void setInputFileName(String fileName) {
		this.addUncheckedParameter(INPUT, new SimulatorParameter<String>(fileName));
	}
	
	private void setInhibitionMap(boolean b) {
		this.inhibitionMap = b;
	}

	private void setActivationMap(boolean b) {
		this.activationMap = b;
	}

	public long getWallClockTimeLimit() {
		return nullSafeValueRetrieve(WALL_CLOCK_TIME_LIMIT, Long.class);
	}

	public boolean isTime() {
		return !this.parameterHasDefaultValue(TIME);
	}

	public double getTimeLimit() {
		return nullSafeValueRetrieve(TIME, Double.class);
	}

	public long getMaxNumberOfEvents() {
		return nullSafeValueRetrieve(EVENT, Long.class);
	}

	public void setStorifyFlag(boolean b) {
		this.addUncheckedFlag(SimulatorFlagOption.STORIFY, b);
	}
	
	public boolean needToStorify() {
		return this.containsFlag(SimulatorFlagOption.STORIFY)
			|| this.containsParameter(STORIFY);
	}

	public int getIterations() {
		return nullSafeValueRetrieve(ITERATION, Integer.class);
	}

	public int getClockPrecision() {
		return nullSafeValueRetrieve(CLOCK_PRECISION, Integer.class);
	}

	public void updateRandom() {
		ThreadLocalData.getRandom().setSeed(this.getSeed());
	}

	public boolean needToDumpVersion() {
		return this.containsFlag(VERSION);
	}

	public long getMonitorPeakMemory() {
		return nullSafeValueRetrieve(MONITOR_PEAK_MEMORY, Long.class);
	}

	public boolean isOutputFinalState() {
		return this.containsFlag(OUTPUT_FINAL_STATE);
	}

	/**
	 * When you use this method, you should remember that
	 * it's triggers simulation mode to the "events" one.
	 * i.e. you lose the data about previous time limit 
	 * @param event new events number limit
	 */
	public void setMaxNumberOfEvents(long event) {
		this.addUncheckedParameter(EVENT, new SimulatorParameter<Long>(event));
		this.addUncheckedParameter(TIME, SimulatorArgumentsDefaultValues.DEFAULT_VALUES.get(TIME));
	}

	/**
	 * When you use this method, you should remember that
	 * it's triggers simulation mode to the "time" one.
	 * i.e. you lose the data about previous events limit 
	 * @param timeLimit new time limit
	 */
	public void setTimeLimit(double timeLimit) {
		this.addUncheckedParameter(TIME, new SimulatorParameter<Double>(timeLimit));
		this.addUncheckedParameter(EVENT, SimulatorArgumentsDefaultValues.DEFAULT_VALUES.get(EVENT));
	}

	public double getInitialTime() {
		return nullSafeValueRetrieve(INIT, Double.class);
	}

	public int getPoints() {
		return nullSafeValueRetrieve(POINTS, Integer.class);
	}

	public InfoType getOutputTypeForAdditionalInfo() {
		return this.shortConsoleOutput;
	}

	public boolean isOcamlStyleNameingInUse() {
		return this.containsFlag(OCAML_STYLE_OBS_NAME);
	}

	public String getInputFileName() {
		return nullSafeValueRetrieve(INPUT, String.class);
	}

	public boolean needToEnumerationOfSpecies() {
		return this.containsFlag(ENUMERATE_COMPLEXES);
	}

	public boolean createSubViews() {
		return this.containsFlag(COMPUTE_SUB_VIEWS);
	}

	public boolean createLocalViews() {
		return this.containsFlag(COMPUTE_LOCAL_VIEWS);
	}

	public boolean needToRunQualitativeCompression() {
		return this.containsFlag(SimulatorFlagOption.QUALITATIVE_COMPRESSION)
			|| this.containsParameter(QUALITATIVE_COMPRESSION);
	}
	
	public boolean needToRunQuantitativeCompression() {
		return this.containsFlag(SimulatorFlagOption.QUANTITATIVE_COMPRESSION)
			|| this.containsParameter(QUANTITATIVE_COMPRESSION);
	}

	public boolean isUnifiedTimeSeriesOutput() {
		return unifiedTimeSeriesOutput;
	}

	public boolean isForwardOnly() {
		return this.containsFlag(FORWARD);
	}

	public boolean solutionNeedsToBeRead() {
		return true;
	}

	public OperationMode getOperationMode() {
		return OperationMode.getValue(nullSafeValueRetrieve(OPERATION_MODE, String.class));
	}

	public void setInputCharArray(char[] inputCharArray) {
		this.inputCharArray = inputCharArray;
	}
	
	public char[] getInputCharArray() {
		return this.inputCharArray;
	}

	public String getSnapshotsTimeString() {
		return nullSafeValueRetrieve(SNAPSHOT_TIME, String.class);
	}

	public String getFocusFilename() {
		return nullSafeValueRetrieve(FOCUS_ON, String.class);
	}

	public boolean needToCompile() {
		return this.containsFlag(SimulatorFlagOption.COMPILE)
			|| this.containsParameter(COMPILE);
	}

	public boolean isGenereteMap() {
		return this.containsParameter(GENERATE_INFLUENCE_MAP);
	}

	public boolean needToOutputDebugInformation() {
		return this.containsFlag(DEBUG_INIT);
	}

	public String getLiveDataConsumerClassname() {
		return nullSafeValueRetrieve(LIVE_DATA_CONSUMER_CLASSNAME, String.class);
	}

	public int getLiveDataPoints() {
		return nullSafeValueRetrieve(LIVE_DATA_POINTS, Integer.class);
	}

	public int getLiveDataInterval() {
		return nullSafeValueRetrieve(LIVE_DATA_INTERVAL, Integer.class);
	}

	public boolean needToDumpHelp() {
		return this.containsFlag(HELP);
	}

	public boolean isNoDumpStdoutStderr() {
		return this.containsFlag(NO_DUMP_STDOUT_STDERR);
	}

	public boolean needToFindDeadRules() {
		return this.containsFlag(COMPUTE_DEAD_RILES);
	}

	public int getAgentsLimit() {
		return nullSafeValueRetrieve(AGENTS_LIMIT, Integer.class);
	}

	public void setEnumerationOfSpecies(boolean b) {
		this.addUncheckedFlag(ENUMERATE_COMPLEXES, b);
	}

	public StoryCompressionMode getStorifyMode() {
		return this.storifyMode;
	}

	public void setIterations(int iterationsNumber) {
		this.addUncheckedParameter(ITERATION, new SimulatorParameter<Integer>(iterationsNumber));
	}

	public void setAllowIncompleteSubstance(boolean b) {
		this.addUncheckedFlag(ALLOW_INCOMPLETE_SUBSTANCE, b);
	}

	public boolean incompletesAllowed() {
		return this.containsFlag(ALLOW_INCOMPLETE_SUBSTANCE);
	}

	public boolean needToSimulate() {
		return this.containsFlag(SimulatorFlagOption.SIMULATE)
			|| this.containsParameter(SIMULATIONFILE);
	}

	public boolean needToBuildContactMap() {
		return this.containsFlag(SimulatorFlagOption.CONTACT_MAP)
			|| this.containsParameter(CONTACT_MAP);
	}
	
	public boolean needToBuildInhibitionMap() {
		return inhibitionMap;
	}
	
	public boolean needToBuildActivationMap() {
		return activationMap;
	}

	public boolean needToBuildInfluenceMap() {
		return this.containsFlag(SimulatorFlagOption.GENERATE_INFLUENCE_MAP)
			|| this.containsParameter(GENERATE_INFLUENCE_MAP)
			|| this.containsFlag(SimulatorFlagOption.BUILD_INFLUENCE_MAP);
	}
	
	/*
	 * Some methods needed for testing, if we don't want them to be here
	 * we can just move this code to the test class and append some getters here
	 */
	
	private boolean parameterHasDefaultValue(SimulatorParameterizedOption option) {
		SimulatorParameter<?> defaultValue = SimulatorArgumentsDefaultValues.DEFAULT_VALUES.get(option);
		if (defaultValue == null) {
			return false;
		}
		SimulatorParameter<?> parameter = parameters.get(option);
		if (parameter == null) {
			return false;
		} else if (defaultValue.getValue().equals(Double.NaN) 
				&& parameter.getValue().equals(Double.NaN)) {
			return true;
		}
		return defaultValue.getValue().equals(parameter.getValue()); 
	}
	
	public boolean allParametersAreDefaultOrEqualTo(int value, String stringValue, 
			int nonDefaultsCounter, boolean fileNameWasSet) {
		int counter = 0;
		for (Map.Entry<SimulatorParameterizedOption, SimulatorParameter<?>> 
				entry : parameters.entrySet()) {
			if (this.parameterHasDefaultValue(entry.getKey())) {
				continue;
			}
			SimulatorParameter<?> parameter = entry.getValue();
			if (parameter.getType().equals(String.class)) {
				if (parameter.getValue().equals(stringValue)) {
					counter++;
				} else {
					if (!parameter.getValue().equals("filename")) {
						return false;	
					}
				}
			} else {
				int storedValue = ((Number)parameter.getValue()).intValue();
				if (storedValue == value) {
					counter++;
				} else {
					return false;
				}
			}
			
		}
		if (fileNameWasSet ^ parameters.get(INPUT) != null) {
			return false;
		}
		return counter == nonDefaultsCounter;
	}

	public void setSeed(int seed) {
		this.addUncheckedParameter(SEED, new SimulatorParameter<Integer>(seed));
		this.updateRandom();
	}
}
