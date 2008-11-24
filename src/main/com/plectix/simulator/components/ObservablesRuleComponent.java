package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.IObservablesComponent;

public class ObservablesRuleComponent implements IObservablesComponent {
	private CRule rule;
	private int nameID;
	private final List<Long> countList = new ArrayList<Long>();

	public final CRule getRule() {
		return rule;
	}

	public final List<Long> getCountList() {
		return countList;
	}

	public ObservablesRuleComponent(CRule rule, int nameID) {
		this.rule = rule;
		this.nameID = nameID;
	}

	private long lastInjectionsQuantity = -1;

	public void updateLastValue() {
		lastInjectionsQuantity = getCount();
	}

	private final long getCount() {
		long count = 1;

		for (CConnectedComponent cc : rule.getLeftHandSide())
			count *= cc.getInjectionsQuantity();
		return count;
	}

	public void calculate(boolean replaceLast) {

		if (replaceLast)
			countList.set(countList.size() - 1, getCount());
		else
			countList.add(lastInjectionsQuantity);
	}

	public String getLine() {
		return rule.getName();
	}

	public String getName() {
		return rule.getName();
	}

	public int getNameID() {
		return nameID;
	}

	public byte getType() {
		return IObservablesComponent.TYPE_RULE_COMPONENT;
	}

	public double getSize() {
		return rule.getRuleRate();
	}

	public String getItem(int index, CObservables obs) {
		return countList.get(index).toString();
	}

}
