package com.plectix.simulator.components;

import java.io.Serializable;
import java.util.*;

import com.plectix.simulator.interfaces.*;

public final class ObservablesRuleComponent implements IObservablesRuleComponent, Serializable {
	private final IRule rule;
	private final int nameID;
	private final List<Long> countList = new ArrayList<Long>();
	private long lastInjectionsQuantity = -1;
	
	public ObservablesRuleComponent(IRule rule, int nameID) {
		this.rule = rule;
		this.nameID = nameID;
	}
	
	public final IRule getRule() {
		return rule;
	}

	public final List<Long> getCountList() {
		return Collections.unmodifiableList(countList);
	}

	public final void updateLastValue() {
		lastInjectionsQuantity = getCount();
	}

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

	public double getSize(IObservables obs) {
		return rule.getRuleRate();
	}

	public String getItem(int index, IObservables obs) {
		return countList.get(index).toString();
	}

	public void addAutomorphicObservables(int index) {
		// TODO Auto-generated method stub

	}

	public int getMainAutomorphismNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setMainAutomorphismNumber(int index) {
		// TODO Auto-generated method stub

	}

	public boolean isUnique() {
		return true;
	}

	public long getValue(int index, IObservables obs) {
		return countList.get(index);
	}

}
