package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.simulator.KappaSystem;

/*package*/ class CSecondSolution extends ComplexSolution {
	private final SuperStorage mySuperStorage;
	private final StraightStorage myStraightStorage;
	
	public CSecondSolution(KappaSystem system) {
		super(system);
		mySuperStorage = new SuperStorage();
		myStraightStorage = new StraightStorage();
	}

	public void addConnectedComponent(IConnectedComponent component) {
		getStraightStorage().addConnectedComponent(component);
	}

	public List<IConnectedComponent> split() {
		// TODO Auto-generated method stub
		return null;
	}

	public StandardRuleApplicationPool prepareRuleApplicationPool(
			List<IInjection> injections) {
		// TODO Auto-generated method stub
		return null;
	}

	public void applyRule(RuleApplicationPool pool) {
		// TODO Auto-generated method stub
		
	}

	//-----------------AGENTS GETTERS---------------------------
	
	public Collection<IAgent> getSuperStorageAgents() {
		return mySuperStorage.getAgents();
	}

	public Collection<IAgent> getStraightStorageAgents() {
		return myStraightStorage.getAgents();
	}

	
}
