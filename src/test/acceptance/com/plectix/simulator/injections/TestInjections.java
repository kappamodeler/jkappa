package com.plectix.simulator.injections;

import com.plectix.simulator.Initializator;
import com.plectix.simulator.Test;
import com.plectix.simulator.TestRunner;
import com.plectix.simulator.util.DefaultPropertiesForTest;

public abstract class TestInjections extends DefaultPropertiesForTest implements Test {
	
	public static Initializator getInitializator() {
		return TestRunner.getInitializator();
	}
}
