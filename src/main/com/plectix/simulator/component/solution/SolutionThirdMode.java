package com.plectix.simulator.component.solution;

import com.plectix.simulator.component.Agent;
import com.plectix.simulator.component.injections.Injection;
import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulator.KappaSystem;

/*package*/ class SolutionThirdMode extends AbstractSolutionForHigherModes {
	private final SuperStorage superStorage;
	private final StraightStorage straightStorage;
	
	SolutionThirdMode(KappaSystem system) {
		super(system);
		superStorage = getSuperStorage();
		straightStorage = getStraightStorage();
	}

	@Override
	protected void addConnectedComponent(ConnectedComponentInterface component) {
		if (!superStorage.tryIncrement(component)) { 
			straightStorage.addConnectedComponent(component);
		}
	}

	@Override
	public final RuleApplicationPoolInterface prepareRuleApplicationPool() {
		return new StandardRuleApplicationPool();
	}
	
	@Override
	public final void addInjectionToPool(RuleApplicationPoolInterface pool, Injection injection) {
		StraightStorage storage = pool.getStorage();
		if (injection.isSuper()) {
			storage.addConnectedComponent(superStorage.extractComponent(injection));
		} else {
			if (injection.getImageAgent() != null) {
				ConnectedComponentInterface component = SolutionUtils.getConnectedComponent(injection.getImageAgent());
				for (Agent agent : component.getAgents()) {
					storage.addAgent(agent);
					// if 'agent' is from straight storage, then it should be removed from there
					straightStorage.removeAgent(agent);
				}	
			}
		}
	}
}
