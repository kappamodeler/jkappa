package com.plectix.simulator.components.perturbations;

import java.util.List;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.interfaces.INetworkNotation;

import com.plectix.simulator.simulator.SimulationData;

/**
 * This class implements rule of perturbation.
 * @author avokhmin
 *
 */
@SuppressWarnings("serial")
public final class CRulePerturbation extends CRule {

	private int count;
	private boolean inf = false;

	/**
	 * The CRulePerturbation constructor.
	 * 
	 * @param leftHandsideComponents left handside of the rule, list of connected components
	 * @param rightHandsideComponents right handside of the rule, list of connected components
	 * @param ruleName name of the rule 
	 * @param ruleRate rate of the rule
	 * @param ruleID unique rule identificator
	 * @param isStorify <tt>true</tt> if simulator run in storify mode, <tt>false</tt> otherwise
	 */
	public CRulePerturbation(List<IConnectedComponent> left,
			List<IConnectedComponent> right, String name, double ruleRate,
			int ruleID, boolean isStorify) {
		super(left, right, name, ruleRate, ruleID, isStorify);
	}

	/**
	 * This method sets count of apply this rule.
	 * @param count given count
	 */
	public final void setCount(double count) {
		if (count == Double.MAX_VALUE) {
			inf = true;
			this.count = -1;
		} else
			this.count = (int) count;
	}

	@Override
	public final void applyRuleForStories(List<CInjection> injectionList,
			INetworkNotation netNotation, SimulationData simulationData, boolean isLast) {
		check();
		apply(injectionList, netNotation, simulationData, false);
		count--;
	}

	@Override
	public final void applyRule(List<CInjection> injectionList, SimulationData simulationData) {
		check();
		apply(injectionList, null, simulationData, false);
		count--;
	}

	/**
	 * Util method. Check a need to apply this and {@link #downRule()} if it need.
	 */
	private final void check() {
		if (!inf) {
			if (count <= 1)
				downRule();
		} else {
			if (getLeftHandSide().get(0).getInjectionsList().size() == 1)
				downRule();
		}
	}

	/**
	 * Util method. Switch off current rule.
	 */
	private final void downRule() {
		setRuleRate(0.0);
		setInfinityRate(false);
		setActivity(0.0);
	}

}
