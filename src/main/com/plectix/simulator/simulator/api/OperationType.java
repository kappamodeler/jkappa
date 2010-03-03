package com.plectix.simulator.simulator.api;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SIMULATOR_CREATION - Creating a simulator and configuring it with SimulationArguments 
 * KAPPA_FILE_LOADING - Feeding a Kappa file to the Simulator, which only detects Kappa errors and warnings. 
 * KAPPA_FILE_COMPILATION - Compiling the Kappa file. One can get the compiled version by just calling a method
 * SUBVIEWS - Building a set of subviews
 * CONTACT_MAP
 * INFLUENCE_MAP
 * ..._MAP
 * SOLUTION_INITIALIZATION - Initializing the Solution. One can get the initial state of the system computed.
 * STORIES - Getting the Stories computed.
 * SIMULATION - Running the simulation. Again one can get a simulation run by calling a method.
 * 
 * @author eugene.vlasov
 *
 */
public enum OperationType {
	DO_NOTHING,
	SIMULATOR_INITIALIZATION, 
	KAPPA_FILE_LOADING, 
	KAPPA_MODEL_BUILDING,
	KAPPA_FILE_COMPILATION,
	INITIALIZATION,
	STORIES,
	SIMULATION,
	
	SUBVIEWS,
	
	NON_MODEL_CONTACT_MAP,
	MODEL_CONTACT_MAP,
	ACTIVATION_MAP,
	INHIBITION_MAP,
	INFLUENCE_MAP,
	LOCAL_VIEWS,
	RULE_COMPRESSION,
	SPECIES_ENUMERATION,
	DEAD_RULE_DETECTION, 
	INJECTIONS_BUILDING, 
	
	STANDARD_WORKFLOW("Workflow total time"), 
	DUMP_HELP,
	DUMP_VERSION,
	OUTPUT_COMPILED_DATA, 
	XML_OUTPUT("Results output to xml"),
	
	
	;
	
	private static Map<OperationType, OperationType> ordering 
		= new LinkedHashMap<OperationType, OperationType>();
	
	private final String message;

	static {
		addPair(KAPPA_MODEL_BUILDING, KAPPA_FILE_LOADING);
		addPair(KAPPA_FILE_COMPILATION, KAPPA_MODEL_BUILDING);

		addPair(INITIALIZATION, KAPPA_FILE_COMPILATION);
		addPair(INJECTIONS_BUILDING, KAPPA_FILE_COMPILATION);
		
		addPair(SIMULATION, INITIALIZATION);
		addPair(STORIES, INITIALIZATION);
		
		addPair(SUBVIEWS, KAPPA_FILE_COMPILATION);
		
		addPair(MODEL_CONTACT_MAP, SUBVIEWS);
		addPair(ACTIVATION_MAP, SUBVIEWS);
		addPair(INHIBITION_MAP, SUBVIEWS);
		addPair(INFLUENCE_MAP, SUBVIEWS);
		addPair(LOCAL_VIEWS, SUBVIEWS);
		addPair(RULE_COMPRESSION, SUBVIEWS);
		addPair(DEAD_RULE_DETECTION, SUBVIEWS);
		
		addPair(SPECIES_ENUMERATION, LOCAL_VIEWS);
		
		addPair(OUTPUT_COMPILED_DATA, KAPPA_FILE_COMPILATION);
		addPair(XML_OUTPUT, KAPPA_FILE_COMPILATION);
	}
	
	private OperationType() {
		message = null;
	}
	
	private OperationType(String message) {
		this.message = message;
	}
	
	private static void addPair(OperationType step, OperationType previousStep) {
		ordering.put(step, previousStep);
	}
	
	public OperationType getNecessaryOperation() {
		return ordering.get(this);
	}
	
	@Override
	public final String toString() {
		if (message != null) {
			return message;
		}
		
		String string = ("" + super.toString()).replaceAll("_", " ").toLowerCase();
		return (string.substring(0,1).toUpperCase() + string.substring(1)).intern() ;
	}
}
