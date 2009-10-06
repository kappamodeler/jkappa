package com.plectix.simulator.simulator.initialization;

import com.plectix.simulator.component.Agent;
import com.plectix.simulator.component.injections.Injection;
import com.plectix.simulator.component.solution.SuperSubstance;
import com.plectix.simulator.interfaces.ConnectedComponentInterface;

public final class SuperInjectionSettingStrategy implements InjectionSettingStrategy {

	private final SuperSubstance superSubstance;
	
	public SuperInjectionSettingStrategy(SuperSubstance substance) {
		superSubstance = substance;
	}
	
	public final void process(ConnectedComponentInterface component, Agent agent) {
		Injection injection = component.createInjection(agent);
		if (injection != null) {
			injection.setSuperSubstance(superSubstance);
			if (!agent.hasSimilarInjection(injection)) {
				component.setInjection(injection);
			}
		}
	}

}
