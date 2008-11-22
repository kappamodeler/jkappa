package com.plectix.simulator.components;

import java.util.List;

public class CRulePerturbation extends CRule {

	private int count;
	private boolean inf = false;

	public CRulePerturbation(CRule rule) {
		super(rule.getLeftHandSide(), rule.getRightHandSide(), rule.getName(),
				rule.getRuleRate(), rule.getRuleID());
	}

	public CRulePerturbation(List<CConnectedComponent> left,
			List<CConnectedComponent> right, String name, double ruleRate,
			int ruleID) {
		super(left, right, name, ruleRate, ruleID);
	}

	public final void setCount(double count) {
		if (count == Double.MAX_VALUE) {
			inf = true;
			this.count = -1;
		} else
			this.count = (int) count;
	}

	@Override
	public void applyRuleForStories(List<CInjection> injectionList,
			CNetworkNotation netNotation) {
		check();
		apply(injectionList, netNotation);
		count--;
	}

	@Override
	public void applyRule(List<CInjection> injectionList) {
		check();
		apply(injectionList, null);
		count--;
	}

	private void check() {
		if (!inf)
			if (count <= 1) {
				setRuleRate(0.0);
				setInfinityRate(false);
				setActivity(0.0);
			}
	}

}
