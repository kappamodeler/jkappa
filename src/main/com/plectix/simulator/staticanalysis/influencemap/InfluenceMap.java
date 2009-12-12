package com.plectix.simulator.staticanalysis.influencemap;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.staticanalysis.Observables;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.contactmap.ContactMap;
import com.plectix.simulator.staticanalysis.subviews.base.AbstractionRule;

public abstract class InfluenceMap {
	private final Map<Integer, List<InfluenceMapEdge>> activationMap;
	private final Map<Integer, List<InfluenceMapEdge>> activationMapObservables;
	private final Map<Integer, List<InfluenceMapEdge>> inhibitionMap;
	private final Map<Integer, List<InfluenceMapEdge>> inhibitionMapObservables;
	private Map<Integer, List<AbstractionRule>> observbableRules;

	public InfluenceMap() {
		activationMap = new LinkedHashMap<Integer, List<InfluenceMapEdge>>();
		inhibitionMap = new LinkedHashMap<Integer, List<InfluenceMapEdge>>();
		activationMapObservables = new LinkedHashMap<Integer, List<InfluenceMapEdge>>();
		inhibitionMapObservables = new LinkedHashMap<Integer, List<InfluenceMapEdge>>();
	}
	
	public abstract void initInfluenceMap(List<AbstractionRule> rules,
			Observables observables, ContactMap contactMap,
			Map<String, AbstractAgent> agentNameToAgent);

	public final List<Integer> getActivationByRule(Integer ruleId) {
		List<Integer> answer = new LinkedList<Integer>();
		List<InfluenceMapEdge> list = activationMap.get(ruleId);
		if (list == null) {
			return null;
		}
		for (InfluenceMapEdge iE : activationMap.get(ruleId)) {
			answer.add(iE.getTargetRule());
		}
		return answer;
	}

	public final void fillActivatedInhibitedRules(List<Rule> rules,
			KappaSystem kappaSystem, Observables observables) {
		for (Rule rule : rules) {
			if (activationMap.containsKey(rule.getRuleId()))
				for (InfluenceMapEdge edge : activationMap
						.get(rule.getRuleId())) {
					Rule ruleAdd = kappaSystem.getRuleById(edge.getTargetRule());
					rule.addActivatedRule(ruleAdd);
				}
			if (activationMapObservables.containsKey(rule.getRuleId()))
				for (InfluenceMapEdge edge : activationMapObservables.get(rule
						.getRuleId())) {
					for (AbstractionRule obsRule : observbableRules.get(edge.getTargetRule()))
						rule.addActivatedObs(obsRule.getObservableComponent());
				}
			if (inhibitionMapObservables.containsKey(rule.getRuleId()))
				for (InfluenceMapEdge edge : inhibitionMapObservables.get(rule
						.getRuleId())) {
					for (AbstractionRule obsRule : observbableRules.get(edge.getTargetRule()))
						rule.addinhibitedObs(obsRule.getObservableComponent());
				}
			
			if (inhibitionMap.containsKey(rule.getRuleId()))
				for (InfluenceMapEdge edge : inhibitionMap
						.get(rule.getRuleId())) {
					Rule ruleAdd = kappaSystem.getRuleById(edge.getTargetRule());
					rule.addinhibitedRule(ruleAdd);
				}
		}
	}
	
	public final Map<Integer, List<InfluenceMapEdge>> getActivationMap() {
		return activationMap;
	}

	public final Map<Integer, List<InfluenceMapEdge>> getActivationMapObservables() {
		return activationMapObservables;
	}

	public final Map<Integer, List<InfluenceMapEdge>> getInhibitionMap() {
		return inhibitionMap;
	}

	public final Map<Integer, List<InfluenceMapEdge>> getInhibitionMapObservables() {
		return inhibitionMapObservables;
	}

	public final Map<Integer, List<AbstractionRule>> getObservbableRules() {
		return observbableRules;
	}

	public final void setObservbableRules(
			Map<Integer, List<AbstractionRule>> observbableRules) {
		this.observbableRules = observbableRules;
	}
}
