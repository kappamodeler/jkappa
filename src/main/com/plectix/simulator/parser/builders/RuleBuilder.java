package com.plectix.simulator.parser.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.plectix.simulator.component.Agent;
import com.plectix.simulator.component.Rule;
import com.plectix.simulator.parser.abstractmodel.ModelAgent;
import com.plectix.simulator.parser.abstractmodel.ModelRule;
import com.plectix.simulator.simulator.KappaSystemInterface;
import com.plectix.simulator.simulator.SimulationUtils;

public final class RuleBuilder {
	private final SubstanceBuilder substanceBuilder;
	private final KappaSystemInterface kappaSystem;
	
	public RuleBuilder(KappaSystemInterface system) {
		this.kappaSystem = system;
		this.substanceBuilder = new SubstanceBuilder(system);
	}
	
	public final List<Rule> build(Collection<ModelRule> rules) {
		List<Rule> result = new ArrayList<Rule>();
		for (ModelRule rule : rules) {
			result.add(convert(rule));
		}
		return result;
	}
	
	public final Rule convert(ModelRule abstractRule) {
		String name = abstractRule.getName();
		List<ModelAgent> lhs = abstractRule.getLHS();
		List<ModelAgent> rhs = abstractRule.getRHS();
		double rate = abstractRule.getRate();
		int id = abstractRule.getID();
		boolean isStorify = abstractRule.isStorify();
		
		List<Agent> lhsAgents = substanceBuilder.buildAgents(lhs);
		List<Agent> rhsAgents = substanceBuilder.buildAgents(rhs);
		Rule newRule = SimulationUtils.buildRule(lhsAgents, rhsAgents, name, rate, id, isStorify);
		newRule.setAdditionalRate(abstractRule.getBinaryRate());
		kappaSystem.generateNextRuleId();
		return newRule;
	}
}
