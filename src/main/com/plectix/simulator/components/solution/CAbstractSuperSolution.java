package com.plectix.simulator.components.solution;

import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.KappaSystem;

public abstract class CAbstractSuperSolution extends ComplexSolution {

	public CAbstractSuperSolution(KappaSystem system) {
		super(system);
	}

	public void addConnectedComponent(IConnectedComponent component) {
		// TODO Auto-generated method stub

	}

	public void addInitialConnectedComponents(long quant, List<CAgent> agents) {
		// TODO Auto-generated method stub

	}

	public abstract void applyRule(RuleApplicationPool pool);

	public RuleApplicationPool prepareRuleApplicationPool(List<CInjection> injections) {
		StraightStorage storage = new StraightStorage();
		for (CInjection injection : injections) {
			if (injection.isSuper()) {
				storage.addConnectedComponent(getSuperStorage().extractComponent(injection));
			} else {
				storage.addConnectedComponent(getStraightStorage().extractComponent(injection));
			}
		}
		StandardRuleApplicationPool pool = new StandardRuleApplicationPool(storage);
		return pool;
	}
}
