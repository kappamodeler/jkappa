package com.plectix.simulator.injections;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.plectix.simulator.RunAllTests;
import com.plectix.simulator.TestRunner;

@RunWith(value = Suite.class)
@SuiteClasses(value = { TestInjectionsCorrection.class,
		TestInjectionsLifts.class, TestInjectionsQuantity.class,
		TestInjectionsAgentLinking.class })
public class RunInjectionsTests extends TestRunner {
	private static final String separator = File.separator;

	@BeforeClass
	public static void setup() {
		String fullTestFilePath = "test.data" + separator + "TheGreatTestFile"
				+ DEFAULT_EXTENSION_FILE;
		getInitializator().init(fullTestFilePath);
	}
}