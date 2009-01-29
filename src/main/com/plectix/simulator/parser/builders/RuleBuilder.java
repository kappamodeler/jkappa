package com.plectix.simulator.parser.builders;

import java.util.List;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.parser.abstractmodel.AbstractRule;

public class RuleBuilder {

	public IRule build(AbstractRule rule) {
		String name = rule.getName();
		List<IConnectedComponent> lhs = rule.getLHS();
		List<IConnectedComponent> rhs = rule.getRHS();
		double rate = rule.getRate();
		int id = rule.getID();
		boolean isStorify = rule.isStorify();
		return new CRule(lhs, rhs, name, rate, id, isStorify);
	}

}
