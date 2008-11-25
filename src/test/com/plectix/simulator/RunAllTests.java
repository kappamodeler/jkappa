package com.plectix.simulator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.plectix.simulator.injections.*;
import com.plectix.simulator.updates.*;
import com.plectix.simulator.parser.*;
import com.plectix.simulator.perturbations.*;

@RunWith(value=Suite.class)
@SuiteClasses(value = {
		RunInjectionsTests.class, 
		RunUpdateTests.class,
		RunParserTests.class,
		RunPerturbationsTests.class,
	})
public class RunAllTests {

}
