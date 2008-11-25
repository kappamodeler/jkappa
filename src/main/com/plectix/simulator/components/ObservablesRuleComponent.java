package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.plectix.simulator.interfaces.*;

public class ObservablesRuleComponent implements IObservablesRuleComponent {
	private IRule rule;
	private int nameID;
	private final List<Long> countList = new ArrayList<Long>();

	public final IRule getRule() {
		return rule;
	}

	public final List<Long> getCountList() {
		return countList;
	}

	public ObservablesRuleComponent(IRule rule, int nameID) {
		this.rule = rule;
		this.nameID = nameID;
	}

	private long lastInjectionsQuantity = -1;

	public void updateLastValue() {
		lastInjectionsQuantity = getCount();
	}

	private final long getCount() {
		long count = 1;

		for (IConnectedComponent cc : rule.getLeftHandSide())
			count *= cc.getInjectionsList().size();
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

//	public byte getType() {
//		return IObservablesComponent.TYPE_RULE_COMPONENT;
//	}

	public double getSize() {
		return rule.getRuleRate();
	}

	public String getItem(int index, CObservables obs) {
		return countList.get(index).toString();
	}

	@Override
	public void addAutomorphicObservables(int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMainAutomorphismNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMainAutomorphismNumber(int index) {
		// TODO Auto-generated method stub
		
	}

}
