package com.plectix.simulator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.plectix.simulator.staticanalysis.graphs.GraphsTest;
import com.plectix.simulator.staticanalysis.rulecompression.TestsRuleCompressions;

@RunWith(value = Suite.class)
@SuiteClasses(value = { 
		TestsRuleCompressions.class,
		GraphsTest.class})

public class RunAllUnitTests {

}
