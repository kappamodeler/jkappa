package com.plectix.simulator.components;

import java.io.Serializable;
import java.util.*;

import com.plectix.simulator.interfaces.*;

/**
 * This class describes observables of rule storage.
 * In general we have kappa file line like
 * <br><br>
 * <code>'observableName'</code>,
 * where 
 * <br>
 * <li><code>observableName</code> - name of this observable rule</li>
 * @author avokhmin
 *
 */
public final class ObservablesRuleComponent implements IObservablesRuleComponent, Serializable {
	private final CRule rule;
	private final int nameID;
	private final List<Long> countList = new ArrayList<Long>();
	private long lastInjectionsQuantity = -1;
	
	/**
	 * Constructor ObservablesRuleComponent<br>
	 * For example, we have kappa file line such as :<br> 
	 * <code>'name'</code> - This one means rule observable.<br>
	 * @param rule given rule
	 * @param nameID given name id
	 */
	public ObservablesRuleComponent(CRule rule, int nameID) {
		this.rule = rule;
		this.nameID = nameID;
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

	public final int getNameID() {
		return nameID;
	}

	public double getCurrentState(CObservables obs) {
		return rule.getRate();
	}

	public String getStringItem(int index, CObservables obs) {
		return countList.get(index).toString();
	}

	public void addAutomorphicObservables(int index) {
	}

	public int getMainAutomorphismNumber() {
		return 0;
	}

	public void setMainAutomorphismNumber(int index) {
	}

	public boolean isUnique() {
		return true;
	}

	public long getLongItem(int index, CObservables obs) {
		return countList.get(index);
	}

}
