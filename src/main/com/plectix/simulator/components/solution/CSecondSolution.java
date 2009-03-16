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

	@Override
	public void addConnectedComponent(IConnectedComponent component) {
		getStraightStorage().addConnectedComponent(component);
	}

	@Override
	public List<IConnectedComponent> split() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StandardRuleApplicationPool prepareRuleApplicationPool(
			List<IInjection> injections) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyRule(RuleApplicationPool pool) {
		// TODO Auto-generated method stub
		
	}

	//-----------------AGENTS GETTERS---------------------------
	
	@Override
	public Collection<IAgent> getSuperStorageAgents() {
		return mySuperStorage.getAgents();
	}

	@Override
	public Collection<IAgent> getStraightStorageAgents() {
		return myStraightStorage.getAgents();
	}

	
}
