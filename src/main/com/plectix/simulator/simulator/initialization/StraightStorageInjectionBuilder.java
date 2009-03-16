package com.plectix.simulator.simulator.initialization;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.ISolutionComponent;
import com.plectix.simulator.simulator.KappaSystem;

public class StraightStorageInjectionBuilder extends
		InjectionsBuilder {

	public StraightStorageInjectionBuilder(KappaSystem system) {
		super(system);
	}

	public void setInjection(IConnectedComponent componentFrom, IAgent solutionAgent) {
		IInjection inj = componentFrom.createInjection(solutionAgent);
		if (inj != null) {
			if (!solutionAgent.isAgentHaveLinkToConnectedComponent(
					componentFrom, inj)) {
				componentFrom.setInjection(inj);
			}
		}
	}

	@Override
	public void build() {
		for (IAgent agent : getSolution().getStraightStorageAgents()) {
			this.walkInjectingComponents(agent);
		}
	}
}
