package com.plectix.simulator.components;

import java.io.Serializable;
import java.util.*;

import com.plectix.simulator.interfaces.*;

/**
 * This class implements rule observable. This one is the object which affected by fixed rule,
 * which lets us keep an eye on that rule during the simulation.
 * In general we have kappa file line like
 * <br><br>
 * <code>%obs 'observableRuleName'</code>
 * <br><br>
 * where <code>observableRuleName</code> is the name of the fixed rule.<br>
 * This line means that the rule with that name is now in observables.
 * @author avokhmin
 *
 */
@SuppressWarnings("serial")
public final class ObservablesRuleComponent implements IObservablesRuleComponent, Serializable {
	private final CRule rule;
	private final int id;
	private final List<Long> countList = new ArrayList<Long>();
	private long lastInjectionsQuantity = -1;
	
	/**
	 * Constructor. Creates observable rule using existing rule.
	 * @param rule rule to create component from
	 * @param id id of this component in observables
	 */
	public ObservablesRuleComponent(CRule rule, int id) {
		this.rule = rule;
		this.id = id;
	}
	
	public final void updateLastValue() {
		lastInjectionsQuantity = getCount();
	}

	/**
	 * This method calculates current state of observables. Util method.
	 * @return current state of observables.
	 */
	private final long getCount() {
		long count = 1;

		for (IConnectedComponent cc : rule.getLeftHandSide())
			count *= cc.getInjectionsList().size();
		return count;
	}

	public final void calculate(boolean replaceLast) {
		if (replaceLast)
			countList.set(countList.size() - 1, getCount());
		else
			countList.add(lastInjectionsQuantity);
	}

	public final String getLine() {
		return rule.getName();
	}

	public final String getName() {
		return rule.getName();
	}

	public final int getId() {
		return id;
	}

	public double getCurrentState(CObservables obs) {
		return rule.getRate();
	}

	public String getStringItem(int index, CObservables obs) {
		return countList.get(index).toString();
	}

	public boolean isUnique() {
		return true;
	}

	public long getLongItem(int index, CObservables obs) {
		return countList.get(index);
	}

}
