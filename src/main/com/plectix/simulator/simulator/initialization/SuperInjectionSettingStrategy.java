package com.plectix.simulator.simulator.initialization;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulationclasses.solution.SuperSubstance;
import com.plectix.simulator.staticanalysis.Agent;

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
