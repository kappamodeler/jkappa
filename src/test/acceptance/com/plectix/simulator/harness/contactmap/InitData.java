package com.plectix.simulator.harness.contactmap;

import java.io.File;

public class InitData {

	private static final String separator = File.separator;
	private static final String allPath = "test.data" + separator + "harness"
			+ separator + "contactMap" + separator;
	public static final String pathForSourseModel = allPath + "model"
			+ separator;
	public static final String pathForResult = allPath + "results" + separator;

	public static int length = 4;

}
