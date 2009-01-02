package com.plectix.simulator.simulator;

public class SimulationArguments {

	public static final byte DEFAULT_SEED = -1;
	public static final int DEFAULT_MAX_CLASHES = 100;
	
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
	private boolean debugInitOption = false;
	private boolean genereteMapOption = false;
	private boolean contactMapOption = false;
	private boolean numberOfRunsOption = false;
	private boolean storifyOption = false;
	private boolean forwardOption = false;
	private boolean ocamlStyleObservableNames = false;
	private long clockPrecision = 3600000;
	private boolean outputFinalState = false;
	private String xmlSessionPath = "";
	private String serializationFileName = "~tmp.sd";
	private String inputFile = null;	
	private String snapshotsTimeString = null;
	private String focusFilename = null;
	// For future use, DO NO DELETE:
	private String simulationTypeName = null;    // SIMULATION_TYPE_NONE
	private String serializationModeName = null; // MODE_NONE
	private String storifyModeName = null; // STORIFY_MODE_NONE;

	//**************************************************************************
	//
	// GETTERS AND SETTERS
	// 
	//
	
	public SimulationArguments() {
		super();
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
	public final boolean isDebugInitOption() {
		return debugInitOption;
	}
	public final void setDebugInitOption(boolean debugInitOption) {
		this.debugInitOption = debugInitOption;
	}
	public final boolean isGenereteMapOption() {
		return genereteMapOption;
	}
	public final void setGenereteMapOption(boolean genereteMapOption) {
		this.genereteMapOption = genereteMapOption;
	}
	public final boolean isContactMapOption() {
		return contactMapOption;
	}
	public final void setContactMapOption(boolean contactMapOption) {
		this.contactMapOption = contactMapOption;
	}
	public final boolean isNumberOfRunsOption() {
		return numberOfRunsOption;
	}
	public final void setNumberOfRunsOption(boolean numberOfRunsOption) {
		this.numberOfRunsOption = numberOfRunsOption;
	}
	public final boolean isStorifyOption() {
		return storifyOption;
	}
	public final void setStorifyOption(boolean storifyOption) {
		this.storifyOption = storifyOption;
	}
	public final boolean isForwardOption() {
		return forwardOption;
	}
	public final void setForwardOption(boolean forwardOption) {
		this.forwardOption = forwardOption;
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
	public final String getInputFile() {
		return inputFile;
	}
	public final void setInputFile(String inputFile) {
		this.inputFile = inputFile;
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
	public final String getSimulationTypeName() {
		return simulationTypeName;
	}
	public final void setSimulationTypeName(String simulationTypeName) {
		this.simulationTypeName = simulationTypeName;
	}
	public final String getSerializationModeName() {
		return serializationModeName;
	}
	public final void setSerializationModeName(String serializationModeName) {
		this.serializationModeName = serializationModeName;
	}
	public final String getStorifyModeName() {
		return storifyModeName;
	}
	public final void setStorifyModeName(String storifyModeName) {
		this.storifyModeName = storifyModeName;
	}
	
}
