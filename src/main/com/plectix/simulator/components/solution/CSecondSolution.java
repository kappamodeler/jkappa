package com.plectix.simulator.components.solution;

import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.KappaSystem;

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

	@Override
	public void addInjectionToPool(RuleApplicationPool pool, CInjection injection) {
		if (injection.isSuper()) {
			myStraightStorage.addConnectedComponent(mySuperStorage.extractComponent(injection));
		}
	}
	
	@Override
	public RuleApplicationPool prepareRuleApplicationPool() {
		return new TransparentRuleApplicationPool(myStraightStorage);
	}
	
	@Override
	public void applyChanges(RuleApplicationPool pool) {
		
	}
}
