package com.plectix.simulator.parser;

import java.io.File;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.plectix.simulator.TestRunner;

@RunWith(value = Suite.class)
@SuiteClasses(value = { TestParser.class, ExceptionsTest.class })
public class RunParserTests extends TestRunner {
	private static final String separator = File.separator;
	private static final String myTestFileNamePrefix = "test.data" + separator
			+ "new_parser" + separator;

	public static String getFileNamePrefix() {
		return myTestFileNamePrefix;
	}
}
