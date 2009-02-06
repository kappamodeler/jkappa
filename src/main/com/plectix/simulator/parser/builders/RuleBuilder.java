package com.plectix.simulator.parser.builders;

import java.util.*;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.parser.abstractmodel.AbstractAgent;
import com.plectix.simulator.parser.abstractmodel.AbstractRule;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationUtils;

public class RuleBuilder {
	private final SubstanceBuilder mySubstanceBuilder;
	private final SimulationData myData;
	
	public RuleBuilder(SimulationData data) {
		myData = data;
		mySubstanceBuilder = new SubstanceBuilder(data);
	}
	
	public List<IRule> build(Collection<AbstractRule> rules) {
		List<IRule> result = new ArrayList<IRule>();
		for (AbstractRule rule : rules) {
			result.add(convert(rule));
		}
		return result;
	}
	
	private IRule convert(AbstractRule rule) {
		String name = rule.getName();
		List<AbstractAgent> lhs = rule.getLHS();
		List<AbstractAgent> rhs = rule.getRHS();
		double rate = rule.getRate();
		int id = rule.getID();
		boolean isStorify = rule.isStorify();
		
		List<IAgent> lhsAgents = mySubstanceBuilder.buildAgents(lhs);
		List<IAgent> rhsAgents = mySubstanceBuilder.buildAgents(rhs);
		IRule newRule = SimulationUtils.buildRule(lhsAgents, rhsAgents, name, rate, id, isStorify);
		myData.generateNextRuleId();
		return newRule;
	}
}
