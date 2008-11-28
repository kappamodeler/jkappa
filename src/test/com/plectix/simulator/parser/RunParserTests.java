package com.plectix.simulator.parser;

import com.plectix.simulator.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value = Suite.class)
@SuiteClasses(value = { 
		TestParseRuleCorrection.class,
		TestParseAgentCorrection.class,
		TestParseAgents.class,
		TestParseResult.class,
		TestSuccessParsing.class
		})
public class RunParserTests extends TestRunner {
	private static final String myTestFileNamePrefix = "test.data/parser/";
	
	public static String getFileNamePrefix() { 
		return myTestFileNamePrefix;
	}
}
