package com.plectix.simulator.simulator.initialization;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.simulator.KappaSystem;

public class SuperStorageInjectionsBuilder extends
		InjectionsBuilder {

	public SuperStorageInjectionsBuilder(KappaSystem system) {
		super(system);
	}

	@Override
	public void setInjection(IConnectedComponent componentFrom,	IAgent solutionAgent) {
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
		for (IAgent agent : getSolution().getSuperStorageAgents()) {
			this.walkInjectingComponents(agent);
		}
	}
}
