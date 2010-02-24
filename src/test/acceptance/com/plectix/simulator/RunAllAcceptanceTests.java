package com.plectix.simulator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.plectix.simulator.consoleOutput.TestConsoleOutput;
import com.plectix.simulator.injections.RunInjectionsTests;
import com.plectix.simulator.localviews.TestLocalViews;
import com.plectix.simulator.localviews.TestLocalViewsCompareXML;
import com.plectix.simulator.parser.RunParserTests;
import com.plectix.simulator.parser.TestParserSites;
import com.plectix.simulator.perturbations.RunPerturbationsTests;
import com.plectix.simulator.ruleapplication.RunActionTest;
import com.plectix.simulator.rulestudio.TestRuleStudioOptionsSets;
import com.plectix.simulator.simulationclasses.probability.TestWeightedItemSelector;
import com.plectix.simulator.smoke.SmokeTestMain;
import com.plectix.simulator.speciesenumeration.TestEnumOfSpecies;
import com.plectix.simulator.speciesenumeration.TestEnumOfSpeciesCompareXML;
import com.plectix.simulator.stories.weakcompression.RunTestWeakCompression;
import com.plectix.simulator.subviews.TestSubViews;
import com.plectix.simulator.updates.RunUpdateTests;
import com.plectix.simulator.xmlmap.TestContactMap;
import com.plectix.simulator.xmlmap.TestInfluenceMap;

@RunWith(value = Suite.class)
@SuiteClasses(value = {
		SmokeTestMain.class,

		RunInjectionsTests.class,
		TestParserSites.class,
		RunUpdateTests.class,
		// RunParserTests.class,
		RunPerturbationsTests.class,
		// RunTestStories.class,
		RunParserTests.class,
		// TestAction.class
		RunActionTest.class,
		// TestJavaXMLCompare.class,
		
		// Add nfedorov
		TestInfluenceMap.class, TestSubViews.class, TestEnumOfSpecies.class,
		TestEnumOfSpeciesCompareXML.class, TestLocalViews.class,
		TestLocalViewsCompareXML.class, 
//		RunAllUnitTests.class,
		RunTestWeakCompression.class,
		TestContactMap.class,
		TestWeightedItemSelector.class,
		TestConsoleOutput.class
//		TestRuleStudioOptionsSets.class
		})
public class RunAllAcceptanceTests {
//	public static final String FILENAME_EXTENSION = ".test";
//	public static final String LOG4J_PROPERTIES_FILENAME = "config/log4j.properties";
}
