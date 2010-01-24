package com.plectix.simulator.staticanalysis.rulecompression;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.Site;
import com.plectix.simulator.util.NameDictionary;
/**
 * RuleMaster work with one rule. Find all possibilities for roots, find all
 * rooted versions and create actionsString for them.
 * 
 */
public class RuleMaster {

	private final Map<Integer, ShadowAgent> mapAfter;
	private final Map<Integer, ShadowAgent> mapBefore;
	private final Rule rule;

	/**
	 *  agent's ids from each of left component in rule
	 *  needs for creating roots sets
	 */
	private final List<Set<Integer>> leftComponents;

	/**
	 * RuleMaster work with one rule. Find all possibilities for roots, find all
	 * rooted versions and create actionsString for them.
	 * 
	 * @param rule
	 */
	public RuleMaster(Rule rule) {
		this.rule = rule;
		mapAfter = new LinkedHashMap<Integer, ShadowAgent>();
		mapBefore = new LinkedHashMap<Integer, ShadowAgent>();
		leftComponents = new LinkedList<Set<Integer>>();
		for (ConnectedComponentInterface cc : rule.getLeftHandSide()) {
			Set<Integer> ids = new LinkedHashSet<Integer>();
			for (ShadowAgent sa : RuleCompressionUtils
					.shadowClone((ArrayList<Agent>) cc.getAgents())) {
				if (NameDictionary.isDefaultAgentName(sa.getName()))
					continue;
				mapBefore.put(sa.getIdInRuleHandside(), sa);
				ids.add(sa.getIdInRuleHandside());
			}
			if (!ids.isEmpty())
				leftComponents.add(ids);
		}
		if (rule.getRightHandSide() != null) {
			for (ConnectedComponentInterface cc : rule.getRightHandSide()) {
				for (ShadowAgent sa : RuleCompressionUtils
						.shadowClone((ArrayList<Agent>) cc.getAgents())) {
					mapAfter.put(sa.getIdInRuleHandside(), sa);
				}
			}
		}
		findActionAgents();
	}

	/**
	 * find agents changed by this rule (in leftside or added agents in
	 * rigthside)
	 */
	private void findActionAgents() {
		for (Integer i : mapAfter.keySet()) {
			if (mapBefore.get(i) == null) {
				mapAfter.get(i).setActionAgent();
			}
		}

		for (Integer i : mapBefore.keySet()) {

			ShadowAgent shadowAgentBefore = mapBefore.get(i);
			ShadowAgent shadowAgentAfter = mapAfter.get(i);
			if (shadowAgentAfter == null) {
				shadowAgentBefore.setActionAgent();
				continue;
			}
			for (Site s : shadowAgentBefore.getSites()) {
				Site other = shadowAgentAfter.getSiteByName(s.getName());
				if (!s.getInternalState().equalz(other.getInternalState())) {
					shadowAgentBefore.setActionAgent();
				}
				if (!s.getLinkState().equalz(other.getLinkState())) {
					shadowAgentBefore.setActionAgent();

				}
			}
		}
	}

	/**
	 * 
	 * @return all rooted versions of rule
	 */
	public List<RootedRule> getAllRootedVersions() {
		List<RootedRule> rootedRules = new LinkedList<RootedRule>();
		List<Set<Integer>> rootsOfComponents = getAllPossibleRoots();
		for (Set<Integer> set : rootsOfComponents) {
			rootedRules.add(cloneRootedRule(set));
		}
		return rootedRules;

	}

	/**
	 * set roots and create rooted rule
	 * 
	 * @param set
	 *            of root's ids
	 * @return
	 */
	private RootedRule cloneRootedRule(Set<Integer> set) {
		RootedRule rr = new RootedRule(mapBefore, mapAfter);
		rr.setRule(this.rule);
		rr.setRootsAndFullActionInfo(set);
		return rr;
	}

	// ================================================================
	// generate roots

	/**
	 * find all possible combination of roots actionAgent from each component,
	 * all added agents and any agents form tested component
	 * 
	 * @return
	 */
	private List<Set<Integer>> getAllPossibleRoots() {
		clearComponents();
		List<Set<Integer>> answer = new LinkedList<Set<Integer>>();
		Set<Integer> imperativeRoots = new LinkedHashSet<Integer>();
		answer.add(imperativeRoots);
		for (Integer i : mapAfter.keySet()) {
			if (mapBefore.get(i) == null) {
				imperativeRoots.add(i);
			}
		}
		for (Set<Integer> set : leftComponents) {
			answer = RuleCompressionUtils.addAllVariants(answer, set);
		}
		return answer;

	}

	/**
	 * clear leftComponent. If some components contains actionAgents then we
	 * delete tested agents from them
	 */
	private void clearComponents() {
		for (Set<Integer> component : leftComponents) {
			if (!isTest(component)) {
				Set<Integer> deleted = new HashSet<Integer>();
				for (Integer i : component) {
					if (!mapBefore.get(i).isActionAgent()) {
						deleted.add(i);
					}
				}
				for (Integer i : deleted) {
					component.remove(i);
				}
			}
		}

	}

	/**
	 * 
	 * @param component
	 * @return is this component only test by rule?
	 */
	private boolean isTest(Set<Integer> component) {

		for (Integer i : component) {
			if (mapBefore.get(i).isActionAgent()) {
				return false;
			}
		}
		return true;
	}

	// =================================================
	// getters and setters

	public Rule getRule() {
		return rule;
	}

	public Map<Integer, ShadowAgent> getMapBefore() {
		return mapBefore;
	}

	public Map<Integer, ShadowAgent> getMapAfter() {
		return mapAfter;
	}

}
