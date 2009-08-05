package com.plectix.simulator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.plectix.simulator.XMLmaps.TestContactMap;
import com.plectix.simulator.XMLmaps.TestInfluenceMap;
import com.plectix.simulator.doAction.RunActionTest;
import com.plectix.simulator.enumerationOfSpecies.TestEnumOfSpecies;
import com.plectix.simulator.enumerationOfSpecies.TestEnumOfSpeciesCompareXML;
import com.plectix.simulator.injections.RunInjectionsTests;
import com.plectix.simulator.localViews.TestLocalViews;
import com.plectix.simulator.localViews.TestLocalViewsCompareXML;
import com.plectix.simulator.parser.newtests.RunParserTests;
import com.plectix.simulator.perturbations.RunPerturbationsTests;
import com.plectix.simulator.smokeTests.SmokeTestMain;
import com.plectix.simulator.stories.weakCompression.RunTestWeakCompression;
import com.plectix.simulator.subViews.TestSubViews;
import com.plectix.simulator.updates.RunUpdateTests;

@RunWith(value=Suite.class)
@SuiteClasses(value = {
		SmokeTestMain.class,
		
		RunInjectionsTests.class, 
		RunUpdateTests.class,
		//RunParserTests.class,
		RunPerturbationsTests.class,
//		RunTestStories.class,
		RunParserTests.class,
//		TestAction.class
		RunActionTest.class,
		//TestJavaXMLCompare.class,	
		TestContactMap.class,
		// Add nfedorov
		TestInfluenceMap.class,
		
		TestSubViews.class,
		
		TestEnumOfSpecies.class,
		TestEnumOfSpeciesCompareXML.class,
		
		TestLocalViews.class,
		TestLocalViewsCompareXML.class,
		
		
		
		RunTestWeakCompression.class
	})
public class RunAllTests {
	public static final String FILENAME_EXTENSION = ".test";
	public static final String RESULTS_EXTENSION = ".tmp";
	public static final String LOG4J_PROPERTIES_FILENAME = "config/log4j.properties";
}
