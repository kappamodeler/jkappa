package com.plectix.simulator.components.solution;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.KappaSystem;

@SuppressWarnings("serial")
/*package*/ final class CFirstSolution extends ComplexSolution implements Serializable {
	private final StraightStorage myStraightStorage;
	
	// we instantiate this type through UniversalSolution only
	CFirstSolution(KappaSystem system) {
		super(system);
		myStraightStorage = this.getStraightStorage();
	}

	//-----------------GETTERS----------------------------------
	
	@Override
	public final Collection<IConnectedComponent> split() {
		return myStraightStorage.split();
	}

	//----------------RULE APPLICATION---------------------------

	@Override
	public RuleApplicationPool prepareRuleApplicationPool() {
		return new TransparentRuleApplicationPool(this);
	}

	/**
	 * This one does nothing, because we use transparent pool to apply any rule here,
	 * so all the changes are applied already
	 */
	@Override
	public void flushPoolContent(RuleApplicationPool pool) {
	}

	@Override
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
	
	/**
	 * This one does nothing, because we use transparent pool to apply any rule here,
	 * so all the changes are applied already
	 */
	@Override
	public void addInjectionToPool(RuleApplicationPool prepareRuleApplicationPool, CInjection injection) {
		// it should be empty
	}
}
