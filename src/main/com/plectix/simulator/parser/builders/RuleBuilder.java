package com.plectix.simulator.parser.builders;

import java.util.*;

import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.parser.abstractmodel.*;
import com.plectix.simulator.simulator.*;

public class RuleBuilder {
	private final SubstanceBuilder mySubstanceBuilder;
	private final KappaSystem myKappaSystem;
	
	public RuleBuilder(KappaSystem system) {
		myKappaSystem = system;
		mySubstanceBuilder = new SubstanceBuilder(system);
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
		myKappaSystem.generateNextRuleId();
		return newRule;
	}
}
