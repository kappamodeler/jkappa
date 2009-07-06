package com.plectix.simulator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.plectix.simulator.doAction.*;
import com.plectix.simulator.injections.*;
import com.plectix.simulator.stories.*;
import com.plectix.simulator.updates.*;
import com.plectix.simulator.parser.newtests.*;
import com.plectix.simulator.perturbations.*;
import com.plectix.simulator.probability.*;

@RunWith(value=Suite.class)
@SuiteClasses(value = {
		RunInjectionsTests.class, 
		RunUpdateTests.class,
		//RunParserTests.class,
		RunPerturbationsTests.class,
//		RunTestStories.class,
		RunParserTests.class,
//		TestAction.class
		RunActionTest.class
		//TestJavaXMLCompare.class,	
		
	
	})
public class RunAllTests {

}
