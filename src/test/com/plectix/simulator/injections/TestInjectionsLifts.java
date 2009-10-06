package com.plectix.simulator.injections;

import java.util.Collection;

import org.junit.Test;

import com.plectix.simulator.component.Site;
import com.plectix.simulator.component.injections.Injection;
import com.plectix.simulator.component.injections.LiftElement;
import com.plectix.simulator.interfaces.ObservableConnectedComponentInterface;
import com.plectix.simulator.util.Failer;
import com.plectix.simulator.util.MessageConstructor;

public class TestInjectionsLifts extends TestInjections {
	private final Failer failer = new Failer();

	private boolean testInjectionLifts(Injection injection) {
		for (Site site : injection.getSiteList()) {
			boolean exists = false;
			for (LiftElement lift : site.getLift()) {
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

		for (ObservableConnectedComponentInterface c : getInitializator()
				.getObservables()) {
			Collection<Injection> injectionsList = c.getInjectionsList();
			for (Injection injection : injectionsList) {
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
			failer.failOnMC(mc);
		}
	}
}
