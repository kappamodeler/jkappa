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

	@Override
	public void calculate() {
		long count = 1;

		for (CConnectedComponent cc : rule.getLeftHandSide())
			count *= cc.getInjectionsQuantity();

		countList.add(count);
	}

	@Override
	public String getLine() {
		return rule.getName();
	}

	@Override
	public String getName() {
		return rule.getName();
	}

	@Override
	public int getNameID() {
		return nameID;
	}

	@Override
	public byte getType() {
		return IObservablesComponent.TYPE_RULE_COMPONENT;
	}

	@Override
	public double getSize() {
		return rule.getRuleRate();
	}

	@Override
	public String getItem(int index, CObservables obs) {
		return countList.get(index).toString();
	}

}
