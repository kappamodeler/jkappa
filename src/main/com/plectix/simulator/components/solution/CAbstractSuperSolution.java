package com.plectix.simulator.components.solution;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.*;

public abstract class CAbstractSuperSolution extends ComplexSolution {
	public CAbstractSuperSolution(KappaSystem system) {
		super(system);
	}

	public abstract void applyChanges(RuleApplicationPool pool);

	public RuleApplicationPool prepareRuleApplicationPool() {
		return new StandardRuleApplicationPool(new StraightStorage());
	}
	
	public void addInjectionToPool(RuleApplicationPool pool, CInjection injection) {
		StraightStorage storage = pool.getStorage();
		if (injection.isSuper()) {
			storage.addConnectedComponent(getSuperStorage().extractComponent(injection));
		} else {
			if (injection.getImageAgent() != null) {
				IConnectedComponent component = SolutionUtils.getConnectedComponent(injection.getImageAgent());
				for (CAgent agent : component.getAgents()) {
					storage.addAgent(agent);
					getStraightStorage().removeAgent(agent);
				}	
			}
		}
	}
}
