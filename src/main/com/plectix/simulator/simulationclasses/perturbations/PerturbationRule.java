package com.plectix.simulator.simulationclasses.perturbations;

import java.util.List;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.stories.storage.EventBuilder;
import com.plectix.simulator.staticanalysis.stories.storage.NullEvent;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;

/**
 * This class implements rule of perturbation.
 * @author avokhmin
 *
 */
@SuppressWarnings("serial")
public final class PerturbationRule extends Rule {

	private int count;
	// TODO cannot we just use MAX_VALUE here?
	private boolean inf = false;
	private static NullEvent nullEvent = new NullEvent(); 

	/**
	 * The CRulePerturbation constructor.
	 * 
	 * @param ruleRate rate of the rule
	 * @param ruleID unique rule identificator
	 * @param isStorify <tt>true</tt> if simulator run in storify mode, <tt>false</tt> otherwise
	 */
	public PerturbationRule(List<ConnectedComponentInterface> left,
			List<ConnectedComponentInterface> right, String name, double ruleRate,
			int ruleID, boolean isStorify) {
		super(left, right, name, ruleRate, ruleID, isStorify);
	}

	/**
	 * This method sets count of apply this rule.
	 * @param count given count
	 */
	public final void setCount(double count) {
		if (count == Double.POSITIVE_INFINITY) {
			inf = true;
			this.count = -1;
		} else
			this.count = (int) count;
	}

	public final void applyRuleForStories(List<Injection> injectionList,
			EventBuilder eventContainer, SimulationData simulationData, boolean isLast) throws StoryStorageException {
		check();
		apply(injectionList, eventContainer, simulationData, false);
		count--;
	}

	@Override
	public final void applyRule(List<Injection> injections, SimulationData simulationData) throws StoryStorageException {
		check();
		apply(injections, nullEvent, simulationData, false);
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
			if (getLeftHandSide().get(0).getInjectionsWeight() == 1)
				downRule();
		}
	}

	/**
	 * Util method. Switch off current rule.
	 */
	private final void downRule() {
		setRuleRate(0.0);
		setInfinityRateFlag(false);
		setActivity(0.0);
	}
}
