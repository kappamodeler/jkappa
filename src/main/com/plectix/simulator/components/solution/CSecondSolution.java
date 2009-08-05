package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationUtils;

/*package*/ final class CSecondSolution extends ComplexSolution {
	private final SuperStorage mySuperStorage;
	private final StraightStorage myStraightStorage;
	
	CSecondSolution(KappaSystem system) {
		super(system);
		mySuperStorage = getSuperStorage();
		myStraightStorage = getStraightStorage();
	}

	public final void addConnectedComponent(IConnectedComponent component) {
		myStraightStorage.addConnectedComponent(component);
	}

	@Override
	public final void addInjectionToPool(RuleApplicationPool pool, CInjection injection) {
		if (injection.isSuper()) {
			myStraightStorage.addConnectedComponent(mySuperStorage.extractComponent(injection));
		}
	}
	
	@Override
	public final RuleApplicationPool prepareRuleApplicationPool() {
		return new TransparentRuleApplicationPool(this);
	}
	
	@Override
	public final void flushPoolContent(RuleApplicationPool pool) {
		// empty!
	}

	@Override
	public final void addInitialConnectedComponents(long quant, List<CAgent> agents) {
		for (IConnectedComponent component : SimulationUtils.buildConnectedComponents(agents)) {
			mySuperStorage.addOrEvenIncrement(quant, component);	
		}
	}
}
