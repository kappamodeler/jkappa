package com.plectix.simulator.components.solution;

import java.util.Collection;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CConnectedComponent;
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

	private boolean tryAddToSuperStorage(IConnectedComponent component) {
		for (SuperSubstance ss : mySuperStorage.getComponents()) {
			if (ss.matches(component)) {
				ss.add();
				return true;
			}
		}
		return false;
	}
	
	public void addConnectedComponent(IConnectedComponent component) {
		if (!tryAddToSuperStorage(component)) { 
			myStraightStorage.addConnectedComponent(component);
		}
	}

	public void addInitialConnectedComponents(long quant, List<CAgent> agents) {
		for (IConnectedComponent component : SimulationUtils.buildConnectedComponents(agents)) {
			mySuperStorage.addOrEvenIncrement(new SuperSubstance(quant, component));	
		}
	}

	public void applyRule(RuleApplicationPool pool) {
		// TODO Auto-generated method stub
		Collection<CAgent> agents = pool.getStorage().getAgents();
		if (!agents.isEmpty()) {
			Collection<IConnectedComponent> components = SimulationUtils.buildConnectedComponents(agents);
			for (IConnectedComponent cc : components) {
				this.addConnectedComponent(cc);
			}
		}
	}
}
