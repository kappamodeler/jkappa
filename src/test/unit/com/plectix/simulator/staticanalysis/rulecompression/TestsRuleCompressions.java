package com.plectix.simulator.staticanalysis.rulecompression;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.plectix.simulator.staticanalysis.LibraryOfRules;
import com.plectix.simulator.staticanalysis.localviews.LibraryOfLocalViews;

@RunWith(value = Suite.class)
@SuiteClasses(value = { TestRuleMaster.class, TestRootedRule.class,
		TestRootedRulesGroup.class, TestQualitativeCompression.class,
		TestQuantitativeCompression.class, 
		TestRuleUtils.class })
public class TestsRuleCompressions {
	public static LibraryOfRules libraryOfRules = new LibraryOfRules();
	public static LibraryOfLocalViews libraryOfViews = new LibraryOfLocalViews();

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.plectix.simulator.rulecompression");
		// $JUnit-BEGIN$

		// $JUnit-END$
		return suite;
	}

}
