package com.plectix.simulator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.plectix.simulator.rulecompression.TestsRuleCompressions;

@RunWith(value = Suite.class)
@SuiteClasses(value = { 
		TestsRuleCompressions.class})

public class RunAllUnitTests {

}
