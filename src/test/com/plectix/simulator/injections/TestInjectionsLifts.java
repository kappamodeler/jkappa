package com.plectix.simulator.injections;


import java.util.*;

import org.junit.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.util.Failer;
import com.plectix.simulator.util.MessageConstructor;

public class TestInjectionsLifts extends TestInjections  {
	private Failer myFailer = new Failer();
	
	private boolean testInjectionLifts(CInjection injection) {
			for (CSite site : injection.getSiteList()) {
				boolean exists = false;
				for (CLiftElement lift : site.getLift()) {
					if (lift.getInjection() == injection) {
						exists = true;
						break;
					}
				}
				if (!exists) {
					return false;
				}
			}
		return true;
	}
	
	@Test
	public void testAllInjections() {
		boolean fail = false;
		boolean temporaryFail = false;
		MessageConstructor mc = new MessageConstructor();
		
		for (ObservablesConnectedComponent c : getInitializator().getObservables()) {
			List<CInjection> injectionsList = c.getInjectionsList();
			for (CInjection injection : injectionsList) {
				if (!testInjectionLifts(injection)) {
					temporaryFail = true;
				}
			}
			
			if (temporaryFail) {
				if (!fail) {
					fail = true;
				}
				mc.addValue(c.getName());
				temporaryFail = false;
			}
		}
		
		if (fail) {
			myFailer.failOnMC(mc);
		}
	}
}
