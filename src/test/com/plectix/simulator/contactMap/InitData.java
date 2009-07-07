package com.plectix.simulator.contactMap;

import java.io.File;

public class InitData {
	
	private static final String separator = File.separator;
	private static final String allPath = "test.data" + separator + "contact_map_new" + separator;
	//public static final String pathForSourseRules = "test.data" + separator + "contact_map" + separator + "rules" + separator;
	public static final String pathForSourseModel =  allPath + "model" + separator;
	//public static final String pathForSourseAgents = "test.data" + separator + "contact_map" + separator + "agents" + separator;
	public static final String pathForResult = allPath + "results" + separator;
	
	public static int length = 0;

}
