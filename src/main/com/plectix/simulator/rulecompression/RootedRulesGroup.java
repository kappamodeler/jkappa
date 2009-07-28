package com.plectix.simulator.rulecompression;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CRule;

/*package*/ class RootedRulesGroup {
	/**
	 * This is grouping actions by their roots. we have to physically divide them 
	 * in order to compare very similar actions  
	 */
	private Map<CAgent, RootSpecifiedActionsInfo> actionsSetToCompareTo = new LinkedHashMap<CAgent, RootSpecifiedActionsInfo>();
	// this is too hard to keep an eye on different hashCodes, so we just keep strings to compare
	private Set<String> valuesToCompare = new HashSet<String>();
	private final RootedRule canonicalRepresentator; 
	private final Set<RootedRule> rules = new LinkedHashSet<RootedRule>();
	private final Set<CRule> matchedRules = new LinkedHashSet<CRule>();
	
	public RootedRulesGroup(RootedRule rr) {
		this.canonicalRepresentator = rr;
		rules.add(rr);
		for (ActionInfo ai : rr.getActionsInfo()) {
			// so then we should add these action to all it's roots
			for (CAgent root : ai.getRoots()) {
				RootSpecifiedActionsInfo rootsActions = actionsSetToCompareTo
						.get(root);
				if (rootsActions == null) {
					rootsActions = new RootSpecifiedActionsInfo();
					actionsSetToCompareTo.put(root, rootsActions);
				}
				rootsActions.addAction(ai);
				valuesToCompare.add(ai.toString());
			}
		}
	}
	
	public RootedRulesGroup tryAdd(RootedRule rr) {
		RootedRulesGroup group = new RootedRulesGroup(rr);
		if (this.actionsSetToCompareTo.size() == group.actionsSetToCompareTo.size()	
					&& (this.valuesToCompare.equals(group.valuesToCompare))) {
			rules.add(rr);
			matchedRules.add(rr.getPrototypeRule());
			return this;
		}
		return group;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (RootSpecifiedActionsInfo value : actionsSetToCompareTo.values()) {
			sb.append(value + "\n");
		}
		return sb.toString();
	}
	
	private String newRuleName() {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (RootedRule rule : rules) {
			if (!first) {
				sb.append(", ");
			} else {
				first = false;
			}
			sb.append(rule.getPrototypeRule().getName());
		}
		return sb.toString();
	}
	
	private double getRate() {
		double rate = 0;
		for (RootedRule rule : rules) {
			rate += rule.getPrototypeRule().getRate();
		}
		return rate;
	}
	
	public CRule getCompressedCandidate(NewRuleBuilder nrb) {
		if (rules.size() == 1) {
			return canonicalRepresentator.getPrototypeRule();
		} else {
			return nrb.getShorterVersionForQuantitative(this.canonicalRepresentator, 
					this.newRuleName(), getRate());
		}
	}
	
	public Set<CRule> getMatchedRules() {
		return matchedRules;
	}

}
