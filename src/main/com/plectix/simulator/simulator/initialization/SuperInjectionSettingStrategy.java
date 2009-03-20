package com.plectix.simulator.simulator.initialization;

import com.plectix.simulator.components.solution.SuperSubstance;
import com.plectix.simulator.interfaces.*;

/*package*/ class SuperInjectionSettingStrategy implements InjectionSettingStrategy {

	private final SuperSubstance mySubstance;
	
	public SuperInjectionSettingStrategy(SuperSubstance substance) {
		mySubstance = substance;
	}
	
	public void process(IConnectedComponent component, IAgent agent) {
		IInjection inj = component.createInjection(agent);
		if (inj != null) {
			inj.setSuperSubstance(mySubstance);
			if (!agent.isAgentHaveLinkToConnectedComponent(
					component, inj)) {
				component.setInjection(inj);
			}
		}
	}

}
