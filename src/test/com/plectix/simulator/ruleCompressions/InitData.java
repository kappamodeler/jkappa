package com.plectix.simulator.ruleCompressions;


import java.io.File;

import com.plectix.simulator.RunAllTests;

public class InitData {
	
	private static final String separator = File.separator;
	private static final String allPath = "test.data" + separator + "ruleCompressions" + separator;
	public static final String pathForSourseModel =  allPath + "model" + separator + "qualitative" + separator;
	public static final String pathForResult = allPath + "results" + separator;
	public static final String pathForSource = allPath + "source" + separator;
	
	public static final String FILENAME_EXTENSION = RunAllTests.FILENAME_EXTENSION;
	public static final String LOG4J_PROPERTIES_FILENAME = "config/log4j.properties";
	
	public static int length = 14;
	
	public static boolean isPrintinConsoleAndFile  = true; //true;
	// public static boolean isPrintinConsoleAndFile  = true;

}

