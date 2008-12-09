package com.plectix.simulator.options;

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
	STORIFY("storify", true, "Name of the kappa file to storify"),
	EVENT("event", true, "Number of rule applications"),
	RANDOMIZER_JAVA("randomizer", true,	"Use randomizer Java"),
	NUMBER_OF_RUNS("number_of_runs", true, "Number of runs, generates tmp file"),
	SNAPSHOT_TIME("set_snapshot_time", true, 
			"Takes a snapshot of solution at specified time unit"),
	DEBUG_INIT("debug", false, 
			"Program execution suspends right after initialization phase"),
	NO_ACTIVATION_MAP("no_activation_map", false, "Do not construct activation map"),
	INIT("init", true, "Start taking measures (stories) at indicated time"),
	RESCALE("rescale", true, "Rescaling factor (eg. '10.0' or '0.10')"),
	POINTS("points", true, "Number of data points per plots"),
	MAX_CLASHES("max_clashes", true,
			"Max number of consequtive clashes before aborting (default 100, 0=infinite)"),
	OCAML_STYLE_OBS_NAME("ocaml_style_obs_name", false,	"convert Obs names to simpx"),
	GENERATE_MAP("generate_map", true,
			"Name of the kappa file for which the influence map should be computed"),
	NO_MAPS("no_maps", false,
			"Do not construct inhibition/activation maps"),
	NO_BUILD_INFLUENCE_MAP("no_build_influence_map", false,
			"Do not construct influence map"),
	BUILD_INFLUENCE_MAP("build_influence_map", false, "Construct influence map"),
	CLOCK_PRECISION("clock_precision", true,
			"(default: 60)clock precision (number of ticks per run)"),
	FORWARD("forward", false, "do not consider backward rules"),
	OUTPUT_SCHEME("output_scheme", true,
			"(def: current dir) directory on which to put computed data"),
	KEY("key", true, "Name of the file containing the key for the crypted version"),
	
	
	
	//TODO USE THESE OPTIONS TOO =)
	
	COMPRESS_STORIES("compress_stories", false, "Weak compression of stories"),
	DONT_COMPRESS_STORIES("no_compress_stories", false, "Do not compress stories"),
	USE_STRONG_COMPRESSION("use_strong_compression", false,
			"Use strong compression to classify stories"),
	MERGE_MAPS("merge_maps", false, "Also constructs inhibition maps"),
	WARNINGS("W", false, "Output all warnings on standard error channel"),
	VERSION("version", false, "Print simplx version"),
	ITERATION("iteration", true, "Number of stories to be searched for (with __storify option only)"),
	FINAL_STATE("output_final_state", false, "Output final state"),
	NO_ARROW_CLOSURE("no_arrow_closure", false, "Do not perform arrows transitive closure when displaying stories"),
	NO_MEASURE("no_measure", false, "Causes simplx to ignore observables"),
	QUOTIENT_REFINEMENTS("quotient_refinements", false, "Replace each rule by the most general rule it is a refinement of when computing stories"),
	MEMORY_LIMIT("memory_limit", true, "Sets limit the usage of the memory (check in Mb). Default is infinite (0)"),
	CORES("cores", true, "Number of cores to use if multithreading is possible"),
	SAVE_MAP("save_map", true, "Saves influence map into a file"),
	LOAD_MAP("load_map", true, "Loads serialized influence map from file"),
	LOAD_COMPILATION("load_compilation", true, "Loads serialized kappa file compilation"),
	SAVE_COMPILATION("save_compilation", true, "Saves kappa file compilation into a file"),
	LOAD_ALL("load_all", true, "Loads serialized init file compilation"),
	SAVE_ALL("save_all", true, "Saves the whole initialization's marshalling (including influence maps) into a file"),
	//TODO description
	QA("QA", false, "Turns QA mode on (slower, but performs more sanity checks");
	
	private final String myLongLine;
	private final String myDescription;
	private final boolean hasArguments;
	private String myShortLine = null;
	
	private static Options cmdLineOptions = null;
	
	private SimulatorOptions(String shortLine, String longLine, boolean hasArguments, String description) {
		this.myLongLine = longLine;
		this.myShortLine = shortLine;
		this.myDescription = description;
		this.hasArguments = hasArguments;
	}
	
	private SimulatorOptions(String longLine, boolean hasArguments, String description) {
		this.myShortLine = null;
		this.myLongLine = longLine;
		this.myDescription = description;
		this.hasArguments = hasArguments;
	}

	public static Options options() {
		if (cmdLineOptions == null) {
			cmdLineOptions = new Options();
			for (SimulatorOptions option : values()) {
				if (option.myShortLine == null) {
					cmdLineOptions.addOption(option.myLongLine, 
							option.hasArguments, option.myDescription);
				} else {
					cmdLineOptions.addOption(option.myShortLine, 
							option.myLongLine, 
							option.hasArguments, option.myDescription);
				}
			}
		}
		return cmdLineOptions;
	}
	
	protected String getLine() {
		return myLongLine;
	}
}
