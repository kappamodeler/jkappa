package com.plectix.simulator.parser.newtests;

import com.plectix.simulator.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value = Suite.class)
@SuiteClasses(value = {
		TestParser.class
		})
public class RunParserTests extends TestRunner {
	private static final String myTestFileNamePrefix = "test.data/new_parser/";
	
	public static String getFileNamePrefix() { 
		return myTestFileNamePrefix;
	}
}
