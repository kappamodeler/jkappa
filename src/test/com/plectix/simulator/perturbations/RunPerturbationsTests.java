package com.plectix.simulator.perturbations;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.plectix.simulator.TestRunner;

@RunWith(value=Suite.class)
@SuiteClasses(value = {
		TestTimeCondition.class
	})
public class RunPerturbationsTests extends TestRunner {

}
