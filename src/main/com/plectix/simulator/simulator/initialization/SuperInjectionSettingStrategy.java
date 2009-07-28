package com.plectix.simulator.simulator.initialization;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.SuperSubstance;
import com.plectix.simulator.interfaces.IConnectedComponent;

public class SuperInjectionSettingStrategy implements InjectionSettingStrategy {

	private final SuperSubstance superSubstance;
	
	public SuperInjectionSettingStrategy(SuperSubstance substance) {
		superSubstance = substance;
	}
	
	public final void process(IConnectedComponent component, CAgent agent) {
		CInjection injection = component.createInjection(agent);
		if (injection != null) {
			injection.setSuperSubstance(superSubstance);
			if (!agent.hasSimilarInjection(injection)) {
				component.setInjection(injection);
			}
		}
	}

}
