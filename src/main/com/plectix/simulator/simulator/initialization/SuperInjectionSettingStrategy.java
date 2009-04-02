package com.plectix.simulator.simulator.initialization;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.SuperSubstance;
import com.plectix.simulator.interfaces.*;

/*package*/ class SuperInjectionSettingStrategy implements InjectionSettingStrategy {

	private final SuperSubstance mySubstance;
	
	public SuperInjectionSettingStrategy(SuperSubstance substance) {
		mySubstance = substance;
	}
	
	public void process(IConnectedComponent component, CAgent agent) {
		CInjection inj = component.createInjection(agent);
		if (inj != null) {
			inj.setSuperSubstance(mySubstance);
			if (!agent.hasSimilarInjection(inj)) {
				component.setInjection(inj);
			}
		}
	}

}
