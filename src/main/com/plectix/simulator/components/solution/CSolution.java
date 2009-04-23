package com.plectix.simulator.components.solution;

import java.io.Serializable;
import java.util.*;

import com.plectix.simulator.action.*;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.injections.CInjection;
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

	//-----------------GETTERS----------------------------------
	
	public final Collection<CAgent> getStraightStorageAgents() {
		return myStraightStorage.getAgents();
	}

	public final List<IConnectedComponent> split() {
		return myStraightStorage.split();
	}

	//----------------RULE APPLICATION---------------------------
	
	public RuleApplicationPool prepareRuleApplicationPool(
			List<CInjection> injections) {
		return new TransparentRuleApplicationPool(myStraightStorage);
	}

	public void applyRule(RuleApplicationPool pool) {
	}

	public void addInitialConnectedComponents(long quant, List<CAgent> components) {
		for (CAgent agent : components) {
			myStraightStorage.addAgent(agent);
		}
		for (int i = 1; i < quant; i++) {
			for (CAgent agent : this.cloneAgentsList(components)) {
				myStraightStorage.addAgent(agent);
			}
		}
	}
}
