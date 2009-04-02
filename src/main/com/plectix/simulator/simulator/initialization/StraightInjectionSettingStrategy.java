package com.plectix.simulator.simulator.initialization;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.*;

/*package*/ class StraightInjectionSettingStrategy implements InjectionSettingStrategy {

	public void process(IConnectedComponent component, CAgent agent) {
		CInjection inj = component.createInjection(agent);
		if (inj != null) {
			if (!agent.hasSimilarInjection(inj)) {
				component.setInjection(inj);
			}
		}
	}
}
