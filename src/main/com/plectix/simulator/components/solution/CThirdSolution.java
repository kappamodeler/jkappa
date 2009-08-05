package com.plectix.simulator.components.solution;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.KappaSystem;

/*package*/ class CThirdSolution extends CAbstractSuperSolution {
	private final SuperStorage mySuperStorage;
	private final StraightStorage myStraightStorage;
	
	CThirdSolution(KappaSystem system) {
		super(system);
		mySuperStorage = getSuperStorage();
		myStraightStorage = getStraightStorage();
	}

	@Override
	protected void addConnectedComponent(IConnectedComponent component) {
		if (!mySuperStorage.tryIncrement(component)) { 
			myStraightStorage.addConnectedComponent(component);
		}
	}

	@Override
	public final RuleApplicationPool prepareRuleApplicationPool() {
		return new StandardRuleApplicationPool();
	}
	
	@Override
	public final void addInjectionToPool(RuleApplicationPool pool, CInjection injection) {
		StraightStorage storage = pool.getStorage();
		if (injection.isSuper()) {
			storage.addConnectedComponent(mySuperStorage.extractComponent(injection));
		} else {
			if (injection.getImageAgent() != null) {
				IConnectedComponent component = SolutionUtils.getConnectedComponent(injection.getImageAgent());
				for (CAgent agent : component.getAgents()) {
					storage.addAgent(agent);
					// if 'agent' is from straight storage, then it should be removed from there
					myStraightStorage.removeAgent(agent);
				}	
			}
		}
	}
}
