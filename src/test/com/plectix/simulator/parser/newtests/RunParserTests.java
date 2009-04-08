package com.plectix.simulator.parser.newtests;

import java.io.File;

import com.plectix.simulator.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value = Suite.class)
@SuiteClasses(value = {
		TestParser.class,
		ExceptionsTest.class
		})
public class RunParserTests extends TestRunner {
	private static final String separator = File.separator;
	private static final String myTestFileNamePrefix = "test.data" + separator + "new_parser" + separator;
	
	public static String getFileNamePrefix() { 
		return myTestFileNamePrefix;
	}
}
