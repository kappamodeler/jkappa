package com.plectix.simulator.parser.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.abstractmodel.ModelAgent;
import com.plectix.simulator.parser.abstractmodel.ModelRule;
import com.plectix.simulator.simulator.KappaSystemInterface;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.util.SpeciesManager;

public final class RuleBuilder {
	private final SubstanceBuilder substanceBuilder;
	private final KappaSystemInterface kappaSystem;
	
	public RuleBuilder(KappaSystemInterface system) {
		this.kappaSystem = system;
		this.substanceBuilder = new SubstanceBuilder(system);
	}
	
	public final List<Rule> build(Collection<ModelRule> rules, MasterSolutionModel masterSolutionModel) throws ParseErrorException {
		List<Rule> result = new ArrayList<Rule>();
		for (ModelRule rule : rules) {
			Rule newRule = convert(rule);
			if(masterSolutionModel != null)
				masterSolutionModel.checkCorrect(newRule, rule.toString());
			result.add(newRule);
		}
		return result;
	}
	
	public final Rule convert(ModelRule abstractRule) throws ParseErrorException {
		String name = abstractRule.getName();
		List<ModelAgent> lhs = abstractRule.getLHS();
		List<ModelAgent> rhs = abstractRule.getRHS();
		double rate = abstractRule.getRate();
		int id = abstractRule.getID();
		boolean isStorify = abstractRule.isStorify();
		
		List<Agent> lhsAgents = substanceBuilder.buildAgents(lhs);
		List<Agent> rhsAgents = substanceBuilder.buildAgents(rhs);
		Rule newRule = buildRule(lhsAgents, rhsAgents, name, rate, id, isStorify);
		newRule.setAdditionalRate(abstractRule.getBinaryRate());
		kappaSystem.generateNextRuleId();
		return newRule;
	}
	
	private static final Rule buildRule(List<Agent> leftHandSideAgents, List<Agent> rightHandSideAgents,
			String name, double activity, int id, boolean isStorify) {
		return new Rule(
				SpeciesManager.formConnectedComponents(leftHandSideAgents),
				SpeciesManager.formConnectedComponents(rightHandSideAgents), 
				name, activity, id, isStorify);
	}
}
