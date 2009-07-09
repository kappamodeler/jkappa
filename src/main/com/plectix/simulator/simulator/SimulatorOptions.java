package com.plectix.simulator.simulator;

import org.apache.commons.cli.Options;

public enum SimulatorOptions {
	SIMULATIONFILE("s", "sim", true, "Location for input file"),
	COMPILE("c", "compile",	true, "Location for input file"),
	TIME("t", "time", true,	"Time simulation count."),
	SEED("seed", true,
			"Seed the random generator using given integer " +
			"same integer will generate the same random number sequence"),
	NO_SEED("no_seed", false,
			"Equivalent to --seed 0. Kept for compatibilty issue"),
	XML_SESSION_NAME("xml_session_name", true,
			"Name of the xml file containing results of the current session " +
			"(default simplx.xml)"),
	OUTPUT_XML("output_xml", true,
					"Name of the xml file containing results of the current session " +
			"(default simplx.xml)"),
	DO_XML("do_XML", false,
					"(default: enabled) dump XML session" +
			"(default simplx.xml)"),
	STORIFY("storify", true, "Name of the kappa file to storify"),
	EVENT("event", true, "Number of rule applications"),
	RANDOMIZER_JAVA("randomizer", true,	"Use randomizer Java"),
	NUMBER_OF_RUNS("number_of_runs", false, "Number of runs, generates tmp file"),
	SNAPSHOT_TIME("set_snapshot_time", true, 
			"Takes a snapshot of solution at specified time unit"),
	//TODO is this similar to "--debug debug mode (very verbose!)" ?
	DEBUG_INIT("debug", false, 
			"Program execution suspends right after initialization phase"),
	NO_ACTIVATION_MAP("no_activation_map", false, "Do not construct activation map"),
	INIT("init", true, "Start taking measures (stories) at indicated time"),
	RESCALE("rescale", true, "Rescaling factor (eg. '10.0' or '0.10')"),
	POINTS("points", true, "Number of data points per plots"),
	MAX_CLASHES("max_clashes", true,
			"Max number of consequtive clashes before aborting (default 10000, 0=infinite)"),
	GENERATE_MAP("generate_map", true,
			"Name of the kappa file for which the influence map should be computed"),
	CONTACT_MAP("contact_map", true, "Name of the kappa file to build contact map"),
	NO_MAPS("no_maps", false,
			"Do not construct inhibition/activation maps"),
	NO_BUILD_INFLUENCE_MAP("no_build_influence_map", false,
			"Do not construct influence map"),
	BUILD_INFLUENCE_MAP("build_influence_map", false, "Construct influence map"),
	FORWARD("forward", false, "do not consider backward rules"),
	OUTPUT_SCHEME("output_scheme", true,
			"(def: current dir) directory on which to put computed data"),
	HELP("help", "help", false, "Display this list of options"),
	VERSION("version", false, "Print simplx version"),
	//TODO WE ARE UNABLE TO USE THIS OPTION INSIDE OF OUR SIMULATOR!
	NO_GC("no_gc", false, "Prevent garbage collection"),
	SAVE_ALL("save_all", true, 
			"Name of the file in which to save the whole initialization's marshalling (including influence maps)"),
	NO_SAVE_ALL("no_save_all",false, 
			"do not saving the initialization of the simulation data"),
	
	//TODO USE THESE OPTIONS TOO =)
	PROFILE("profile", false, "Produces profile"),
	KEY("key", true, "Name of the file containing the key for the crypted version"),
	NO_INHIBITION_MAP("no_inhibition_map", false, "Don't construct inhibition map"),
	COMPRESS_STORIES("compress_stories", false, "Weak compression of stories"),
	DONT_COMPRESS_STORIES("no_compress_stories", false, "Do not compress stories"),
	USE_STRONG_COMPRESSION("use_strong_compression", false,
			"Use strong compression to classify stories"),
	MERGE_MAPS("merge_maps", false, "Also constructs inhibition maps"),
	WARNINGS("W", false, "Output all warnings on standard error channel"),
	ITERATION("iteration", true, "Number of stories to be searched for (with --storify option only)"),
	FINAL_STATE("output_final_state", false, "Output final state"),
	NO_ARROW_CLOSURE("no_arrow_closure", false, 
			"Do not perform arrows transitive closure when displaying stories"),
	NO_MEASURE("no_measure", false, "Causes simplx to ignore observables"),
	QUOTIENT_REFINEMENTS("quotient_refinements", false, 
			"Replace each rule by the most general rule it is a refinement of when computing stories"),
	//TODO WE ARE UNABLE TO USE THIS OPTION INSIDE OF OUR SIMULATOR!
	MEMORY_LIMIT("memory_limit", true, "Sets limit the usage of the memory (check in Mb). "
			+ "Default is infinite (0)"),
	CORES("cores", true, "Number of cores to use if multithreading is possible"),
	SAVE_MAP("save_map", true, "Saves influence map into a file"),
	LOAD_MAP("load_map", true, "Loads serialized influence map from file"),
	LOAD_COMPILATION("load_compilation", true, "Loads serialized kappa file compilation"),
	SAVE_COMPILATION("save_compilation", true, "Saves kappa file compilation into a file"),
	LOAD_ALL("load_all", true, "Loads serialized init file compilation"),
//	SAVE_ALL("save_all", true, "Saves the whole initialization's marshalling (including influence maps) into a file"),
	//TODO description
	QA("QA", false, "Turns QA mode on (slower, but performs more sanity checks"),
	SNAPHOT_TMP("snapshot_tmp_file", true, 
			"Sets the name of temp snapshot files (default snapshots.tmp)"),
	DONT_USE_STRONG_COMPRESSION("no_use_strong_compression", false,
			"Don't use strong compression to classify stories"),
	LOG_COMPRESSION("log_compression", true, "Displays the before/after "
			+ "compression status in the html desktop"),
	BACKTRACK_LIMIT("backtrack_limit", true, "Limits the exploration when scanning for stories"),
	MAX_PER_TIME_COMPRESSION("max_time_per_compression", true, 
			"Limits the exploration when scanning for stories"),
	REORDER_BY_DEPTH("reorder_by_depth", false, 
			"Reoders events according to their depth before strong compression"),
	MULTISET_ORDER("use_multiset_order_in_compression", false, 
			"Use the multi-set of depths to	compare stories in strong compression"),
	LINEAR_ORDER("use_linear_order", false, "Use linear order to compare stories in strong compression"),
	HTML_OUTPUT("html_output", false, "HTML rendering"),
	DOT_OUTPUT("dot_output", false, "Dot output for stories"),
	NO_RULES("no_rules", false, "No recomputation of html rule rendering"),
	PLOT("plot", true, "Creates a file containing the simulation data in clear text"),
	NO_ABSTRACTION("no_abstraction", false, "Deactivate complx abstraction (will slow down influence"
			+ "map generation for large systems"),
	OUTPUT_FINAL_STATE("output_final_state", false, 
			"output final state (same as --set-snapshot-time for the last time unit)"),
	//TODO several times!
	SET_SNAPSHOT_TIME("set_snapshot_time", true, 
			"takes a snapshot of solution at specified time unit (may use option several times)"),
	TIME_SAMPLE("time_sample", true, "Sets sample size in time units (default: 0.01)"),
	EVENT_SAMPLE("event_sample", true, "Sets sample size in events (default: 100)"),
	NO_DUMP_ITERATION_NUMBER("no_dump_iteration_number", false, "No dump iteration number"),
	NO_DUMP_RULE_ITERATION("no_dump_rule_iteration", false, "No dump rule iteration"),
	NO_COMPUTE_QUANTITATIVE_COMPRESSION("no_compute_quantitative_compression", false, "No compute quantitative compression"),
	NO_COMPUTE_QUALITATIVE_COMPRESSION("no_compute_qualitative_compression", false, "No compute qualitative compression"),
	NO_ENUMERATE_COMPLEXES("no_enumerate_complexes", false, "No enumerate complexes"),
	RESET_ALL("reset_all", false, "Reset all"),
	FOCUS_ON("focus_on", true, "(default: disabled) Focus contact maps around the given rules"),
	DO_LOW_RES_CONTACT_MAP("do_low_res_contact_map", false, "(default: enabled)construct the low resolution contact map"),
	CLOCK_PRECISION("clock_precision", true, "(def: 60) clock precision (number of ticks per run)"),
	// Java specific options:
	WALL_CLOCK_TIME_LIMIT("wall_clock_time_limit", true, "sets a wall clock time limit in milliseconds for the simulation"),
	OCAML_STYLE_OBS_NAME("ocaml_style_obs_name", false,	"convert observable names to simplx variants"),
	NO_DUMP_STDOUT_STDERR("no_dump_stdout_stderr", false, "don't dump information to stdout and/or stderr"),
	SHORT_CONSOLE_OUTPUT("short_console_output", false, "Short console output"),
	OPERATION_MODE("operation_mode", true, "sets current operation mode"),
	MONITOR_PEAK_MEMORY("monitor_peak_memory", true, "turns on monitoring of peak memory usage at give periods in milliseconds"),
	COMPUTE_SUB_VIEWS("compute_sub_views", false, "Compute sub views."),
	ALLOW_INCOMPLETES("allow_incompletes", false, "Allows incomplete substances in solution"),
	AGENTS_LIMIT("agents_limit", true, "Limits the number of agents which can form super substance in operation modes 2-4"),
	LIVE_DATA_INTERVAL("live_data_interval", true, "Time interval to update live data in seconds (default: -1)"),
	LIVE_DATA_POINTS("live_data_points", true, "Approximate number of data points to report live (default: 500)"),
	;

	private String shortName = null;
	private final String longName;
	private final String description;
	private final boolean hasArguments;
	
	public static final Options COMMAND_LINE_OPTIONS; 
	static {
		COMMAND_LINE_OPTIONS = new Options();
		for (SimulatorOptions option : values()) {
			if (option.shortName == null) {
				COMMAND_LINE_OPTIONS.addOption(option.longName, 
						option.hasArguments, option.description);
			} else {
				COMMAND_LINE_OPTIONS.addOption(option.shortName, option.longName, 
						option.hasArguments, option.description);
			}
		}
	}
	
	private SimulatorOptions(String shortName, String longName, boolean hasArguments, String description) {
		this.longName = longName;
		this.shortName = shortName;
		this.description = description;
		this.hasArguments = hasArguments;
	}
	
	private SimulatorOptions(String longName, boolean hasArguments, String description) {
		this.shortName = null;
		this.longName = longName;
		this.description = description;
		this.hasArguments = hasArguments;
	}
	
	protected final String getLongName() {
		return longName;
	}
}
