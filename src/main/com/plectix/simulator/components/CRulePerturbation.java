package com.plectix.simulator.components;

import java.util.List;

import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.Simulator;

public final class CRulePerturbation extends CRule {

	private int count;
	private boolean inf = false;

	public CRulePerturbation(IRule rule, boolean isStorify) {
		super(rule.getLeftHandSide(), rule.getRightHandSide(), rule.getName(),
				rule.getRuleRate(), rule.getRuleID(), isStorify);
	}

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
			INetworkNotation netNotation, Simulator simulator, boolean isLast) {
		check();
		apply(injectionList, netNotation, simulator, false);
		count--;
	}

	@Override
	public final void applyRule(List<IInjection> injectionList, Simulator simulator) {
		check();
		apply(injectionList, null, simulator, false);
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
