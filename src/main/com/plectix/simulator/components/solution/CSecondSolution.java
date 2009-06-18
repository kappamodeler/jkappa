package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.*;

/*package*/ class CSecondSolution extends CAbstractSuperSolution {
	private final SuperStorage mySuperStorage;
	private final StraightStorage myStraightStorage;
	
	public CSecondSolution(KappaSystem system) {
		super(system);
		mySuperStorage = getSuperStorage();
		myStraightStorage = getStraightStorage();
	}

	public void addConnectedComponent(IConnectedComponent component) {
		myStraightStorage.addConnectedComponent(component);
	}

	public void applyRule(RuleApplicationPool pool) {
		// we can skip checking that getStorage() returns temporary storage
		Collection<CAgent> agents = pool.getStorage().getAgents();
		for (CAgent agent : agents) {
			myStraightStorage.addAgent(agent);
		}
	}

	//-----------------AGENTS GETTERS---------------------------
	
	public void addInitialConnectedComponents(long quant, List<CAgent> agentsList) {
		for (IConnectedComponent component : SimulationUtils.buildConnectedComponents(agentsList)) {
			mySuperStorage.addOrEvenIncrement(new SuperSubstance(quant, component));	
		}	
	}
}
