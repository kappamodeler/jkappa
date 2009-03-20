package com.plectix.simulator.components.solution;

import java.io.Serializable;
import java.util.*;

import com.plectix.simulator.action.*;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationUtils;

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
	
	public RuleApplicationPool prepareRuleApplicationPool(
			List<IInjection> injections) {
		return new TransparentRuleApplicationPool(myStraightStorage);
	}

	public void applyRule(RuleApplicationPool pool) {
		myStraightStorage.applyRule(pool);
	}

	public void addInitialConnectedComponents(long quant, List<IAgent> components) {
		for (IAgent agent : components) {
			myStraightStorage.addAgent(agent);
		}
		for (int i = 1; i < quant; i++) {
			for (IAgent agent : this.cloneAgentsList(components)) {
				myStraightStorage.addAgent(agent);
			}
		}
	}
}
