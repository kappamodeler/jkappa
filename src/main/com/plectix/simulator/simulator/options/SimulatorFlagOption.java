package com.plectix.simulator.simulator.options;

public enum SimulatorFlagOption implements SimulatorOption {
	STORIFY("storify_job", "Tells simulator that it's time to compute some stories"),
	SIMULATE("simulate_job", "Asks simulator to perform a simulation"),
	COMPILE("compile_job",	"Forces simulator to compile "),
	CONTACT_MAP("contact_map_job", "Asks simulator to build contact map"),
	GENERATE_INFLUENCE_MAP("generate_map_job", 
			"Tells simulator to generate influence and inhibition maps"),
	QUALITATIVE_COMPRESSION("qualitative_compression_job", "execute qualitative compression"),
	QUANTITATIVE_COMPRESSION("quantitative_compression_job", "execute quantitative compression"),
			
			
	NO_SEED("no_seed", "Equivalent to --seed 0. Kept for compatibilty issue"),
	
	//TODO is this similar to "--debug debug mode (very verbose!)" ?
	DEBUG_INIT("debug", "Program execution suspends right after initialization phase"),
	NO_ACTIVATION_MAP("no_activation_map", "Do not construct activation map"),
	NO_MAPS("no_maps", "Do not construct inhibition/activation maps"),
	NO_BUILD_INFLUENCE_MAP("no_build_influence_map", "Do not construct influence map"),
	BUILD_INFLUENCE_MAP("build_influence_map", "Construct influence map"),
	FORWARD("forward", "do not consider backward rules"),
	HELP("help", "help", "Display this list of options"),
	VERSION("version", "Print simplx version"),
	
	NO_INHIBITION_MAP("no_inhibition_map", "Don't construct inhibition map"),
	INHIBITION_MAP("inhibition_map", "Construct inhibition map"),

	COMPRESS_STORIES("compress_stories", "Weak compression of stories"),
	DONT_COMPRESS_STORIES("no_compress_stories", "Do not compress stories. 'No compression'"),
	USE_STRONG_COMPRESSION("use_strong_compression", "Use strong compression to classify stories"),
	DONT_USE_STRONG_COMPRESSION("no_use_strong_compression", 
			"Don't use strong compression to classify stories"),
			
	MERGE_MAPS("merge_maps", "Also constructs inhibition maps"),
	OUTPUT_FINAL_STATE("output_final_state", 
			"output final state (same as --set-snapshot-time for the last time unit)"),

	NO_DUMP_ITERATION_NUMBER("no_dump_iteration_number", "No dump iteration number"),
	NO_DUMP_RULE_ITERATION("no_dump_rule_iteration", "No dump rule iteration"),
			
	NO_COMPUTE_QUANTITATIVE_COMPRESSION("no_compute_quantitative_compression", 
			"No compute quantitative compression"),
	OUTPUT_QUANTITATIVE_COMPRESSION("output_quantitative_compression", 
			"Output compute quantitative compression"),
	
	NO_COMPUTE_QUALITATIVE_COMPRESSION("no_compute_qualitative_compression", 
			"No compute qualitative compression"),
	OUTPUT_QUALITATIVE_COMPRESSION("output_qualitative_compression", 
			"Output compute qualitative compression"),
			
	NO_ENUMERATE_COMPLEXES("no_enumerate_complexes", "No enumerate complexes"),
	ENUMERATE_COMPLEXES("enumerate_complexes", "Enumerate complexes"),
	OCAML_STYLE_OBS_NAME("ocaml_style_obs_name",	"convert observable names to simplx variants"),
	UNIFIED_TIME_SERIES_OUTPUT("unified_time_series_output",	
			"command line option under JSIM so that both for event based and time "
			+ "based simulations we have the same columns in the data section: " 
			+ "time, event, obs1, obs2, obs3, ... , obsn."),

	NO_DUMP_STDOUT_STDERR("no_dump_stdout_stderr", "don't dump information to stdout and/or stderr"),
	SHORT_CONSOLE_OUTPUT("short_console_output", "Short console output"),
			
	COMPUTE_SUB_VIEWS("compute_sub_views", "Compute sub views."),
	COMPUTE_DEAD_RILES("compute_dead_rules", "Compute dead rules."),

	NO_COMPUTE_LOCAL_VIEWS("no_compute_local_views", "No compute reachability analysis"),
	COMPUTE_LOCAL_VIEWS("compute_local_views", "Compute reachability analysis"),

	ALLOW_INCOMPLETE_SUBSTANCE("allow_incomplete_substance", "AllowIncompleteSubstance"),
	
	REPORT_EXACT_SAMPLE_TIME("report_exact_sample_time", "Report observables at exact sample time"),
	REPORT_AFTER_SAMPLE_TIME("report_after_sample_time", "Report observables at first event after sample time"),
	

	
	;

	
	private String shortName = null;
	private final String longName;
	private final String description;
	
	private SimulatorFlagOption(String shortName, String longName, String description) {
		this.longName = longName;
		this.shortName = shortName;
		this.description = description;
	}
	
	private SimulatorFlagOption(String longName, String description) {
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
		return false;
	}
}
