package com.plectix.simulator.simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.CharBuffer;

import com.plectix.simulator.util.PersistenceUtils;

public class SimulationArguments {

	public static final byte DEFAULT_SEED = -1;
	public static final int DEFAULT_MAX_CLASHES = 100;

	public enum SimulationType { 
		NONE,
		COMPILE,
		STORIFY,
		SIM,
		AVERAGE_OF_RUNS,
		GENERATE_MAP,
		CONTACT_MAP
	}

	public enum StorifyMode {
		NONE,
		WEAK,
		STRONG
	}

	public enum SerializationMode {
		NONE,
		READ,
		SAVE
	}
	
	private boolean noDumpStdoutStderr = false;
	private boolean help = false;
	private boolean version = false;
	private boolean shortConsoleOutput = false;
	private String xmlSessionName = "simplx.xml";
	private double initialTime = 0.0;
	private int points = -1;
	private double rescale = Double.NaN;
	private int seed = DEFAULT_SEED;
	private long maxClashes = DEFAULT_MAX_CLASHES;
	private long event;
	private double timeLength = 0;
	private boolean isTime = false;
	private int iterations = 1;
	private String randomizer = null;
	private boolean activationMap = true;
	private boolean inhibitionMap = false;
	private boolean compile = false;
	private boolean debugInit = false;
	private boolean genereteMap = false;
	private boolean contactMap = false;
	private boolean numberOfRuns = false;
	private boolean storify = false;
	private boolean forwardOnly = false;
	private boolean ocamlStyleObservableNames = false;
	private long clockPrecision = 3600000;
	private boolean outputFinalState = false;
	private String xmlSessionPath = "";
	private String serializationFileName = "~tmp.sd";
	private String inputFile = null;
	private String inputFilename = null;	
	private String snapshotsTimeString = null;
	private String focusFilename = null;
	private String commandLineString = null;
	private SimulationType simulationType = SimulationType.NONE;
	private StorifyMode storifyMode = StorifyMode.NONE;
	private SerializationMode serializationMode = SerializationMode.NONE;
	
	
	public SimulationArguments() {
		super();
	}
	
	public static void main(String[] args) throws Exception {
		SimulationArguments simulationArguments = new SimulationArguments();
		PersistenceUtils.addAlias(simulationArguments);
		
		System.err.println("==================== DEFAULT SIMULATION ARGUMENTS ====================");
		System.err.println(PersistenceUtils.getXStream().toXML(simulationArguments));
		
		System.err.println("==================== SIMULATION ARGUMENTS EXAMPLE 1 ====================");
		SimulatorCommandLine commandLine = new SimulatorCommandLine(new String[]{"--sim", "file.ka", "--time", "100"});
		simulationArguments = commandLine.getSimulationArguments();
		System.err.println(PersistenceUtils.getXStream().toXML(simulationArguments));

		System.err.println("==================== SIMULATION ARGUMENTS EXAMPLE 2 ====================");
		commandLine = new SimulatorCommandLine(new String[]{"--sim", "file.ka", "--event", "1000"});
		simulationArguments = commandLine.getSimulationArguments();
		System.err.println(PersistenceUtils.getXStream().toXML(simulationArguments));
		
		System.err.println("==================== SIMULATION ARGUMENTS EXAMPLE 3 ====================");
		commandLine = new SimulatorCommandLine(new String[]{"--storify", "file.ka", "--iteration", "50"});
		simulationArguments = commandLine.getSimulationArguments();
		System.err.println(PersistenceUtils.getXStream().toXML(simulationArguments));
		
		System.err.println("==================== SIMULATION ARGUMENTS EXAMPLE 4 ====================");
		commandLine = new SimulatorCommandLine(new String[]{"--help"});
		simulationArguments = commandLine.getSimulationArguments();
		System.err.println(PersistenceUtils.getXStream().toXML(simulationArguments));
		
		System.err.println("==================== SIMULATION ARGUMENTS EXAMPLE 5 ====================");
		commandLine = new SimulatorCommandLine(new String[]{"--sim", "file.ka", "--time", "100"});
		simulationArguments = commandLine.getSimulationArguments();
		File file = new File("data/Example.ka");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		CharBuffer buffer = CharBuffer.allocate((int) file.length());
		reader.read(buffer);
		String inputFile = buffer.rewind().toString();
		simulationArguments.setInputFile(inputFile);
		System.err.println(PersistenceUtils.getXStream().toXML(simulationArguments));

	}
	
	
	//**************************************************************************
	//
	// GETTERS AND SETTERS
	// 
	//
	
	public boolean isShortConsoleOutput() {
		return shortConsoleOutput;
	}
	
	public void setShortConsoleOutput(boolean shortConsoleOutput) {
		this.shortConsoleOutput = shortConsoleOutput;
	}
	
	public final String getXmlSessionName() {
		return xmlSessionName;
	}
	public final void setXmlSessionName(String xmlSessionName) {
		this.xmlSessionName = xmlSessionName;
	}
	public final double getInitialTime() {
		return initialTime;
	}
	public final void setInitialTime(double initialTime) {
		this.initialTime = initialTime;
	}
	public final int getPoints() {
		return points;
	}
	public final void setPoints(int points) {
		this.points = points;
	}
	public final double getRescale() {
		return rescale;
	}
	public final void setRescale(double rescale) {
		this.rescale = rescale;
	}
	public final int getSeed() {
		return seed;
	}
	public final void setSeed(int seed) {
		this.seed = seed;
	}
	public final long getMaxClashes() {
		return maxClashes;
	}
	public final void setMaxClashes(long maxClashes) {
		this.maxClashes = maxClashes;
	}
	public final long getEvent() {
		return event;
	}
	public final void setEvent(long event) {
		this.event = event;
	}
	public final double getTimeLength() {
		return timeLength;
	}
	public final void setTimeLength(double timeLength) {
		this.timeLength = timeLength;
	}
	public final boolean isTime() {
		return isTime;
	}
	public final void setTime(boolean isTime) {
		this.isTime = isTime;
	}
	public final int getIterations() {
		return iterations;
	}
	public final void setIterations(int iterations) {
		this.iterations = iterations;
	}
	public final String getRandomizer() {
		return randomizer;
	}
	public final void setRandomizer(String randomizer) {
		this.randomizer = randomizer;
	}
	public final boolean isActivationMap() {
		return activationMap;
	}
	public final void setActivationMap(boolean activationMap) {
		this.activationMap = activationMap;
	}
	public final boolean isInhibitionMap() {
		return inhibitionMap;
	}
	public final void setInhibitionMap(boolean inhibitionMap) {
		this.inhibitionMap = inhibitionMap;
	}
	public final boolean isCompile() {
		return compile;
	}
	public final void setCompile(boolean compile) {
		this.compile = compile;
	}
	public final boolean isDebugInit() {
		return debugInit;
	}
	public final void setDebugInit(boolean debugInitOption) {
		this.debugInit = debugInitOption;
	}
	public final boolean isGenereteMap() {
		return genereteMap;
	}
	public final void setGenereteMap(boolean genereteMapOption) {
		this.genereteMap = genereteMapOption;
	}
	public final boolean isContactMap() {
		return contactMap;
	}
	public final void setContactMap(boolean contactMapOption) {
		this.contactMap = contactMapOption;
	}
	public final boolean isNumberOfRuns() {
		return numberOfRuns;
	}
	public final void setNumberOfRuns(boolean numberOfRunsOption) {
		this.numberOfRuns = numberOfRunsOption;
	}
	public final boolean isStorify() {
		return storify;
	}
	public final void setStorify(boolean storifyOption) {
		this.storify = storifyOption;
	}
	public final boolean isForwardOnly() {
		return forwardOnly;
	}
	public final void setForwardOnly(boolean forwardOption) {
		this.forwardOnly = forwardOption;
	}
	public final boolean isOcamlStyleObservableNames() {
		return ocamlStyleObservableNames;
	}
	public final void setOcamlStyleObservableNames(boolean ocamlStyleObservableNames) {
		this.ocamlStyleObservableNames = ocamlStyleObservableNames;
	}
	public final long getClockPrecision() {
		return clockPrecision;
	}
	public final void setClockPrecision(long clockPrecision) {
		this.clockPrecision = clockPrecision;
	}
	public final boolean isOutputFinalState() {
		return outputFinalState;
	}
	public final void setOutputFinalState(boolean outputFinalState) {
		this.outputFinalState = outputFinalState;
	}
	public final String getXmlSessionPath() {
		return xmlSessionPath;
	}
	public final void setXmlSessionPath(String xmlSessionPath) {
		this.xmlSessionPath = xmlSessionPath;
	}
	public final String getSerializationFileName() {
		return serializationFileName;
	}
	public final void setSerializationFileName(String serializationFileName) {
		this.serializationFileName = serializationFileName;
	}
	public final String getInputFilename() {
		return inputFilename;
	}
	public final void setInputFilename(String inputFile) {
		this.inputFilename = inputFile;
	}
	public final String getSnapshotsTimeString() {
		return snapshotsTimeString;
	}
	public final void setSnapshotsTimeString(String snapshotsTimeString) {
		this.snapshotsTimeString = snapshotsTimeString;
	}
	public final String getFocusFilename() {
		return focusFilename;
	}
	public final void setFocusFilename(String focusFilename) {
		this.focusFilename = focusFilename;
	}

	public final SimulationType getSimulationType() {
		return simulationType;
	}
	public final void setSimulationType(SimulationType simulationType) {
		this.simulationType = simulationType;
	}

	public final SerializationMode getSerializationMode() {
		return serializationMode;
	}

	public final void setSerializationMode(SerializationMode serializationMode) {
		this.serializationMode = serializationMode;
	}

	public final StorifyMode getStorifyMode() {
		return storifyMode;
	}

	public final void setStorifyMode(StorifyMode storifyMode) {
		this.storifyMode = storifyMode;
	}

	public final String getCommandLineString() {
		return commandLineString;
	}

	public final void setCommandLineString(String commandLineString) {
		this.commandLineString = commandLineString;
	}

	public final boolean isNoDumpStdoutStderr() {
		return noDumpStdoutStderr;
	}

	public final void setNoDumpStdoutStderr(boolean noDumpStdoutStderr) {
		this.noDumpStdoutStderr = noDumpStdoutStderr;
	}

	public final boolean isHelp() {
		return help;
	}

	public final void setHelp(boolean help) {
		this.help = help;
	}

	public final boolean isVersion() {
		return version;
	}

	public final void setVersion(boolean version) {
		this.version = version;
	}

	public final String getInputFile() {
		return inputFile;
	}

	public final void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}
}
