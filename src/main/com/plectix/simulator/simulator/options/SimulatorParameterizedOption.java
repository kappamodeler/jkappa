package com.plectix.simulator.simulator.options;

public enum SimulatorParameterizedOption implements SimulatorOption {
	INPUT("input", "Location for input file", String.class),
	
	SIMULATIONFILE("s", "sim", "Location for input file", String.class),
	COMPILE("c", "compile",	"Location for input file", String.class),
	TIME("t", "time", "Time simulation count.", Double.class),
	SEED("seed", 
			"Seed the random generator using given integer " +
			"same integer will generate the same random number sequence", Integer.class),
	XML_SESSION_NAME("xml_session_name",
			"Name of the xml file containing results of the current session " +
			"(default simplx.xml)", String.class),
	OUTPUT_XML("output_xml", "Name of the xml file containing results of the current session " +
			"(default simplx.xml)", String.class),
	STORIFY("storify", "Name of the kappa file to storify", String.class),
	EVENT("event", "Number of rule applications", Long.class),
	
	SNAPSHOT_TIME("set_snapshot_time", 
			"Takes a snapshot of solution at specified time unit", String.class),
	INIT("init", "Start taking measures (stories) at indicated time", Double.class),
	RESCALE("rescale", "Rescaling factor (eg. '10.0' or '0.10')", Double.class),
	POINTS("points", "Number of data points per plots", Integer.class),
	MAX_CLASHES("max_clashes", 
			"Max number of consequtive clashes before aborting (default 10000, 0=infinite)", Integer.class),
			
	/**
	 * It is possible that we don't need this one
	 */
	GENERATE_INFLUENCE_MAP("generate_map", 
			"Name of the kappa file for which the influence map should be computed",
			String.class),
	CONTACT_MAP("contact_map", "Name of the kappa file to build contact map", String.class),
	OUTPUT_SCHEME("output_scheme", "(def: current dir) directory on which to put computed data", String.class),
	
	
	/**=====================================================================**/
	/*							STORIES										**/
	/**=====================================================================**/
	ITERATION("iteration", "Number of stories to be searched for (with --storify option only)", Integer.class),
	FOCUS_ON("focus_on", "(default: disabled) Focus contact maps around the given rules", String.class),
	
	CLOCK_PRECISION("clock_precision", "(def: 60) clock precision (number of ticks per run)", Integer.class),
	// Java specific options:
	WALL_CLOCK_TIME_LIMIT("wall_clock_time_limit", 
			"sets a wall clock time limit in milliseconds for the simulation", Long.class),
	
	OPERATION_MODE("operation_mode", "sets current operation mode", String.class),
	MONITOR_PEAK_MEMORY("monitor_peak_memory", 
			"turns on monitoring of peak memory usage at give periods in milliseconds", Long.class),
	
	AGENTS_LIMIT("agents_limit", 
			"Limits the number of agents which can form super substance in operation modes 2-4", Integer.class),
	LIVE_DATA_INTERVAL("live_data_interval", 
			"Time interval to update live data in milliseconds (default: -1)", Integer.class),
	LIVE_DATA_POINTS("live_data_points", 
			"Approximate number of data points to report live (default: 500)", Integer.class),
	QUALITATIVE_COMPRESSION("qualitative_compression", "execute qualitative compression", String.class),
	QUANTITATIVE_COMPRESSION("quantitative_compression", "execute quantitative compression", String.class),
	LIVE_DATA_CONSUMER_CLASSNAME("live_data_consumer_classname", 
			"sets the class name for Live Data ConsumerClass", String.class),
	;

	private String shortName = null;
	private final String longName;
	private final String description;
	private final Class<?> parameterClass;
	
	private SimulatorParameterizedOption(String shortName, String longName, 
			String description, Class<?> parameterClass) {
		this.parameterClass = parameterClass;
		this.longName = longName;
		this.shortName = shortName;
		this.description = description;
	}
	
	private SimulatorParameterizedOption(String longName, String description,
			Class<?> parameterClass) {
		this.parameterClass = parameterClass;
		this.shortName = null;
		this.longName = longName;
		this.description = description;
	}
	
	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getLongName() {
		return longName;
	}

	@Override
	public String getShortName() {
		return shortName;
	}
	
	@Override
	public boolean hasArguments() {
		return true;
	}

	public Class<?> getParameterType() {
		return parameterClass;
	}
	
	public boolean shouldHavePositivePrameter() {
		return this == RESCALE || this == MAX_CLASHES;
	}
}
