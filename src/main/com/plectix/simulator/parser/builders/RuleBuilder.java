package com.plectix.simulator.parser.builders;

import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.parser.abstractmodel.*;
import com.plectix.simulator.parser.util.IdGenerator;
import com.plectix.simulator.simulator.*;

public class RuleBuilder {
	private final SubstanceBuilder mySubstanceBuilder;
	private final KappaSystem myKappaSystem;
	
	public RuleBuilder(KappaSystem system) {
		myKappaSystem = system;
		mySubstanceBuilder = new SubstanceBuilder(system);
	}
	
	public List<CRule> build(Collection<AbstractRule> rules) {
		List<CRule> result = new ArrayList<CRule>();
		for (AbstractRule rule : rules) {
			result.add(convert(rule));
		}
		return result;
	}
	
	private CRule convert(AbstractRule rule) {
		String name = rule.getName();
		List<AbstractAgent> lhs = rule.getLHS();
		List<AbstractAgent> rhs = rule.getRHS();
		double rate = rule.getRate();
		int id = rule.getID();
		boolean isStorify = rule.isStorify();
		
		List<CAgent> lhsAgents = mySubstanceBuilder.buildAgents(lhs);
		List<CAgent> rhsAgents = mySubstanceBuilder.buildAgents(rhs);
		CRule newRule = SimulationUtils.buildRule(lhsAgents, rhsAgents, name, rate, id, isStorify);
		myKappaSystem.generateNextRuleId();
		return newRule;
	}
}
