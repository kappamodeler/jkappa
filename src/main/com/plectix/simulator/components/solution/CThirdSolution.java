package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.CAgent;
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

	private void addConnectedComponent(IConnectedComponent component) {
		if (!mySuperStorage.tryIncrement(component)) { 
			myStraightStorage.addConnectedComponent(component);
		}
	}

	public void addInitialConnectedComponents(long quant, List<CAgent> agents) {
		for (IConnectedComponent component : SimulationUtils.buildConnectedComponents(agents)) {
			mySuperStorage.addOrEvenIncrement(new SuperSubstance(quant, component));	
		}
	}

	public void applyChanges(RuleApplicationPool pool) {
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
