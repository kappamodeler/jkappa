package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationUtils;

public class CThirdSolution extends CAbstractSuperSolution {
	private final SuperStorage mySuperStorage;
	private final StraightStorage myStraightStorage;
	
	public CThirdSolution(KappaSystem system) {
		super(system);
		mySuperStorage = getSuperStorage();
		myStraightStorage = getStraightStorage();
	}

	private final void addConnectedComponent(IConnectedComponent component) {
		if (!mySuperStorage.tryIncrement(component)) { 
			myStraightStorage.addConnectedComponent(component);
		}
	}

	public final void addInitialConnectedComponents(long quant, List<CAgent> agents) {
		for (IConnectedComponent component : SimulationUtils.buildConnectedComponents(agents)) {
			mySuperStorage.addOrEvenIncrement(new SuperSubstance(quant, component));	
		}
	}

	public final void applyChanges(RuleApplicationPool pool) {
		// TODO Auto-generated method stub
		Collection<CAgent> agents = pool.getStorage().getAgents();
		if (!agents.isEmpty()) {
			Collection<IConnectedComponent> components = SimulationUtils.buildConnectedComponents(agents);
			for (IConnectedComponent cc : components) {
				this.addConnectedComponent(cc);
			}
		}
	}
	
	@Override
	public final RuleApplicationPool prepareRuleApplicationPool() {
		return new StandardRuleApplicationPool(new StraightStorage());
	}
	
	@Override
	public final void addInjectionToPool(RuleApplicationPool pool, CInjection injection) {
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
