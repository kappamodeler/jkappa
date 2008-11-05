package com.plectix.simulator.injections;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.plectix.simulator.*;

@RunWith(value=Suite.class)
@SuiteClasses(value = {
		TestInjectionsCorrection.class, 
		TestInjectionsLifts.class,
		TestInjectionsQuantity.class,
		TestInjectionsAgentLinking.class
	})
public class RunInjectionsTests extends TestRunner {
	@BeforeClass
	public static void setup() {
		String fullTestFilePath = "test.data/TheGreatTestFile";
		getInitializator().init(fullTestFilePath);
	}
}