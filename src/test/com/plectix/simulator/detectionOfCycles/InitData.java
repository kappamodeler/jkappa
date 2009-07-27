package com.plectix.simulator.detectionOfCycles;


import java.io.File;

import com.plectix.simulator.RunAllTests;

public class InitData {
	
	private static final String separator = File.separator;
	private static final String allPath = "test.data" + separator + "detectionOfCycles" + separator;
	public static final String pathForSourseModel =  allPath ;
	
	public static final String FILENAME_EXTENSION = RunAllTests.FILENAME_EXTENSION;
	public static final String LOG4J_PROPERTIES_FILENAME = "config/log4j.properties";
	
	public static int length = 3;
	
	public static boolean isPrintinConsoleAndFile  = false; //true;
	// public static boolean isPrintinConsoleAndFile  = true;

}

