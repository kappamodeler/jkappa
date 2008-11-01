package com.plectix.simulator.parser;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.plectix.simulator.simulator.DataReading;

@RunWith(value = Suite.class)
@SuiteClasses(value = { 
		TestParseRuleCorrection.class,
		TestParseAgents.class,
		})
public class RunParserTests {
	private static final String myTestFileNamePrefix = "test.data/parser/";
	
	public static String getFileNamePrefix() { 
		return myTestFileNamePrefix;
	}
}
