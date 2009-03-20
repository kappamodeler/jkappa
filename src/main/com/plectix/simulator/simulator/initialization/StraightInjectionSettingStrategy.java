package com.plectix.simulator.simulator.initialization;

import com.plectix.simulator.interfaces.*;

/*package*/ class StraightInjectionSettingStrategy implements InjectionSettingStrategy {

	public void process(IConnectedComponent component, IAgent agent) {
		IInjection inj = component.createInjection(agent);
		if (inj != null) {
			if (!agent.isAgentHaveLinkToConnectedComponent(
					component, inj)) {
				component.setInjection(inj);
			}
		}
	}
}
