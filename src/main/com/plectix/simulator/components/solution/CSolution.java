package com.plectix.simulator.components.solution;

import java.io.Serializable;
import java.util.*;

import com.plectix.simulator.action.*;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.KappaSystem;

@SuppressWarnings("serial")
/*package*/ final class CSolution extends ComplexSolution implements Serializable {
	private final StraightStorage myStraightStorage;
	
	// we instantiate this type through UniversalSolution only
	CSolution(KappaSystem system) {
		super(system);
		myStraightStorage = this.getStraightStorage();
	}

	//---------------ADDERS---------------------------------
	
	public final void addConnectedComponent(IConnectedComponent component) {
		myStraightStorage.addConnectedComponent(component);
	}

	//-----------------GETTERS----------------------------------
	
	public final Collection<IAgent> getStraightStorageAgents() {
		return myStraightStorage.getAgents();
	}

	public final Collection<IAgent> getSuperStorageAgents() {
		return null;
	}
	
	public final List<IConnectedComponent> split() {
		return myStraightStorage.split();
	}

	//----------------RULE APPLICATION---------------------------
	
	@Override
	public RuleApplicationPool prepareRuleApplicationPool(
			List<IInjection> injections) {
		return myStraightStorage.prepareRuleApplicationPool(injections);
	}

	@Override
	public void applyRule(RuleApplicationPool pool) {
		myStraightStorage.applyRule(pool);
	}
}
