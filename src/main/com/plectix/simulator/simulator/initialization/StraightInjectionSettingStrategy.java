package com.plectix.simulator.simulator.initialization;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.staticanalysis.Agent;

public final class StraightInjectionSettingStrategy implements InjectionSettingStrategy {

	public final void process(ConnectedComponentInterface component, Agent agent) {
		Injection inj = component.createInjection(agent);
		if (inj != null) {
			if (!agent.hasSimilarInjection(inj)) {
				component.setInjection(inj);
			}
		}
	}
}
