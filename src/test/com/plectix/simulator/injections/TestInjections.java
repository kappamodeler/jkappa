
package com.plectix.simulator.injections;

import com.plectix.simulator.*;

public abstract class TestInjections implements Test {
	public static Initializator getInitializator() {
		return TestRunner.getInitializator();
	}
}
