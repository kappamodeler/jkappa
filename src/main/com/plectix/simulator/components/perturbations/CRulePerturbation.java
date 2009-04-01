package com.plectix.simulator.components.perturbations;

import java.util.List;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.INetworkNotation;

import com.plectix.simulator.simulator.SimulationData;

public final class CRulePerturbation extends CRule {

	private int count;
	private boolean inf = false;

	public CRulePerturbation(CRule rule, boolean isStorify) {
		super(rule.getLeftHandSide(), rule.getRightHandSide(), rule.getName(),
				rule.getRate(), rule.getRuleID(), isStorify);
	}

//	public CRulePerturbation(List<IConnectedComponent> left,
//			List<IConnectedComponent> right, String name, ConstraintData ruleRate,
//			int ruleID, boolean isStorify) {
//		super(left, right, name, ruleRate, ruleID, isStorify);
//	}
	
	public CRulePerturbation(List<IConnectedComponent> left,
			List<IConnectedComponent> right, String name, double ruleRate,
			int ruleID, boolean isStorify) {
		super(left, right, name, ruleRate, ruleID, isStorify);
	}

	public final void setCount(double count) {
		if (count == Double.MAX_VALUE) {
			inf = true;
			this.count = -1;
		} else
			this.count = (int) count;
	}

	@Override
	public final void applyRuleForStories(List<IInjection> injectionList,
			INetworkNotation netNotation, SimulationData simulationData, boolean isLast) {
		check();
		apply(injectionList, netNotation, simulationData, false);
		count--;
	}

	@Override
	public final void applyRule(List<IInjection> injectionList, SimulationData simulationData) {
		check();
		apply(injectionList, null, simulationData, false);
		count--;
	}

	private final void check() {
		if (!inf) {
			if (count <= 1)
				downRule();
		} else {
			if (getLeftHandSide().get(0).getInjectionsList().size() == 1)
				downRule();
		}
	}

	private final void downRule() {
		setRuleRate(0.0);
		setInfinityRate(false);
		setActivity(0.0);
	}

}
