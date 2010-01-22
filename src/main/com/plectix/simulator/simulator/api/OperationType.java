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
	KAPPA_FILE_COMPILATION,
	SOLUTION_INITIALIZATION,
	STORIES,
	SIMULATION,
	
	SUBVIEWS,
	
	CONTACT_MAP,
	ACTIVATION_MAP,
	INHIBITION_MAP,
	INFLUENCE_MAP,
	LOCAL_VIEWS,
	RULE_COMPRESSION,
	SPECIES_ENUMERATION,
	DEAD_RULE_DETECTION, 
	INJECTIONS
	
	;
	
	private static Map<OperationType, OperationType> ordering 
		= new LinkedHashMap<OperationType, OperationType>(); 

	static {
		addPair(SIMULATOR_INITIALIZATION, DO_NOTHING);
		addPair(KAPPA_FILE_LOADING, SIMULATOR_INITIALIZATION);
		addPair(KAPPA_FILE_COMPILATION, KAPPA_FILE_LOADING);

		addPair(SOLUTION_INITIALIZATION, KAPPA_FILE_COMPILATION);
		
		addPair(SIMULATION, SOLUTION_INITIALIZATION);
		addPair(STORIES, SOLUTION_INITIALIZATION);
		
		addPair(SUBVIEWS, KAPPA_FILE_COMPILATION);
		
		addPair(CONTACT_MAP, SUBVIEWS);
		addPair(INHIBITION_MAP, SUBVIEWS);
		addPair(LOCAL_VIEWS, SUBVIEWS);
		addPair(RULE_COMPRESSION, SUBVIEWS);
		addPair(DEAD_RULE_DETECTION, SUBVIEWS);
		
		addPair(SPECIES_ENUMERATION, LOCAL_VIEWS);
	}
	
	private static void addPair(OperationType step, OperationType previousStep) {
		ordering.put(step, previousStep);
	}
}
