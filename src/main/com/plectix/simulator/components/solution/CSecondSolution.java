package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationUtils;

/*package*/ class CSecondSolution extends ComplexSolution {
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

	public RuleApplicationPool prepareRuleApplicationPool(List<CInjection> injections) {
		StraightStorage storage = new StraightStorage();
		for (CInjection injection : injections) {
			if (injection.isSuper()) {
				storage.addConnectedComponent(mySuperStorage.extractComponent(injection));
			} else {
				storage.addConnectedComponent(myStraightStorage.extractComponent(injection));
			}
		}
		StandardRuleApplicationPool pool = new StandardRuleApplicationPool(storage);
		return pool;
	}
	
	public void applyRule(RuleApplicationPool pool) {
		// we can skip checking that getStorage() returns temporary storage
		for (CAgent agent : pool.getStorage().getAgents()) {
			myStraightStorage.addAgent(agent);
		}
	}

	//-----------------AGENTS GETTERS---------------------------
	
	public void addInitialConnectedComponents(long quant, List<CAgent> agentsList) {
		for (IConnectedComponent component : SimulationUtils.buildConnectedComponents(agentsList)) {
			mySuperStorage.addSuperSubstance(new SuperSubstance(quant, component));	
		}	
	}
}
