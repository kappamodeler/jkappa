package com.plectix.simulator.injections;


import java.util.*;

import org.junit.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.util.Failer;
import com.plectix.simulator.util.MessageConstructor;

public class TestInjectionsLifts extends TestInjections  {
	private Failer myFailer = new Failer();
	
	private boolean testInjectionLifts(IInjection injection) {
			for (ISite site : injection.getSiteList()) {
				boolean exists = false;
				for (ILiftElement lift : site.getLift()) {
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
		
		for (IObservablesConnectedComponent c : getInitializator().getObservables()) {
			Collection<IInjection> injectionsList = c.getInjectionsList();
			for (IInjection injection : injectionsList) {
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
