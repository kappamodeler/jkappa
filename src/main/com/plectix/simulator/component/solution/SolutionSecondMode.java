package com.plectix.simulator.component.solution;

import java.util.List;

import com.plectix.simulator.component.Agent;
import com.plectix.simulator.component.injections.Injection;
import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationUtils;

/*package*/ final class SolutionSecondMode extends AbstractComplexSolution {
	private final SuperStorage superStorage;
	private final StraightStorage straightStorage;
	
	SolutionSecondMode(KappaSystem system) {
		super(system);
		superStorage = getSuperStorage();
		straightStorage = getStraightStorage();
	}

	@Override
	public final void addInjectionToPool(RuleApplicationPoolInterface pool, Injection injection) {
		if (injection.isSuper()) {
			straightStorage.addConnectedComponent(superStorage.extractComponent(injection));
		}
	}
	
	@Override
	public final RuleApplicationPoolInterface prepareRuleApplicationPool() {
		return new TransparentRuleApplicationPool(this);
	}
	
	@Override
	public final void flushPoolContent(RuleApplicationPoolInterface pool) {
		// empty!
	}

	@Override
	public final void addInitialConnectedComponents(long quantity, List<Agent> agents) {
		for (ConnectedComponentInterface component : SimulationUtils.buildConnectedComponents(agents)) {
			superStorage.addOrEvenIncrement(quantity, component);	
		}
	}
}
