package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.simulator.KappaSystem;

public abstract class CAbstractSuperSolution extends ComplexSolution {

	public CAbstractSuperSolution(KappaSystem system) {
		super(system);
	}

	public abstract void applyRule(RuleApplicationPool pool);

	public RuleApplicationPool prepareRuleApplicationPool(List<CInjection> injections) {
		StraightStorage storage = new StraightStorage();
		StandardRuleApplicationPool pool = new StandardRuleApplicationPool(storage);
		return pool;
	}
	
	public void addInjectionToPool(RuleApplicationPool pool, CInjection injection) {
		StraightStorage storage = pool.getStorage();
		if (injection.isSuper()) {
			storage.addConnectedComponent(getSuperStorage().extractComponent(injection));
		} else {
			for (CAgent agent : injection.getImage()) {
				storage.addAgent(agent);
			}
		}
	}
}
