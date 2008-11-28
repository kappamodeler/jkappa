package com.plectix.simulator.updates;

import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value = Suite.class)
@SuiteClasses(value = { 
		TestNegativeUpdate.class,
		TestPositiveUpdate.class
		//TestActivatedRules.class 
		})
public class RunUpdateTests {

}
