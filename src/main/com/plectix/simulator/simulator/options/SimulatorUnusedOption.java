package com.plectix.simulator.simulator.options;

/**
 * This enumeration was extracted just for helping us to get through 
 * the large number of options.
 * Unused option are those that we keep for compatibility with simplx 
 * or those which we haven't implemented yet.
 * If someone decides to implement one of them, please move corresponding enum element
 * to  
 * @author evlasov
 *
 */
public enum SimulatorUnusedOption implements SimulatorOption {
	DO_XML("do_XML", false, "(default: enabled) dump XML session (default simplx.xml)"),
	
	//TODO WE ARE UNABLE TO USE THIS OPTION INSIDE OF OUR SIMULATOR!
	NO_GC("no_gc", false, "Prevent garbage collection"),
	
	PROFILE("profile", false, "Produces profile"),
	KEY("key", true, "Name of the file containing the key for the crypted version"),
	WARNINGS("W", false, "Output all warnings on standard error channel"),
	NO_ARROW_CLOSURE("no_arrow_closure", false, 
			"Do not perform arrows transitive closure when displaying stories"),
	NO_MEASURE("no_measure", false, "Causes simplx to ignore observables"),
	QUOTIENT_REFINEMENTS("quotient_refinements", false, 
			"Replace each rule by the most general rule it is a refinement of when computing stories"),
	
	//TODO WE ARE UNABLE TO USE THIS OPTION INSIDE OF OUR SIMULATOR!
	MEMORY_LIMIT("memory_limit", true, "Sets limit the usage of the memory (check in Mb). "
					+ "Default is infinite (0)"),
			
	CORES("cores", true, "Number of cores to use if multithreading is possible"),
	QA("QA", false, "Turns QA mode on (slower, but performs more sanity checks"),
	SNAPSHOT_TMP("snapshot_tmp_file", true, 
			"Sets the name of temp snapshot files (default snapshots.tmp)"),
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
	
	TIME_SAMPLE("time_sample", true, "Sets sample size in time units (default: 0.01)"),
	EVENT_SAMPLE("event_sample", true, "Sets sample size in events (default: 100)"),
	RESET_ALL("reset_all", false, "Reset all"),
	DO_LOW_RES_CONTACT_MAP("do_low_res_contact_map", false, 
			"(default: enabled)construct the low resolution contact map"),
	
	NO_DO_COMPUTE_DAG_REFINEMENT_RELATION("no_do_compute_dag_refinement_relation", false, 
			"No compute the DAG for the refinement relation"),
	RANDOMIZER_JAVA("randomizer", true, "Use randomizer Java"),
	
	;
	private String shortName = null;
	private final String longName;
	private final String description;
	private final boolean hasArguments;
	
	private SimulatorUnusedOption(String shortName, String longName, boolean hasArguments, String description) {
		this.longName = longName;
		this.shortName = shortName;
		this.description = description;
		this.hasArguments = hasArguments;
	}
	
	private SimulatorUnusedOption(String longName, boolean hasArguments, String description) {
		this.shortName = null;
		this.longName = longName;
		this.description = description;
		this.hasArguments = hasArguments;
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
		return hasArguments;
	}

}
