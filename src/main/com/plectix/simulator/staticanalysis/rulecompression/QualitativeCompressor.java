package com.plectix.simulator.staticanalysis.rulecompression;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.localviews.LocalViewsMain;

public class QualitativeCompressor {
	/**
	 * we use localViews for checking reachables 
	 */
	private final LocalViewsMain localViews;
	private Map<Rule, RootedRulesGroup> groups;

	public QualitativeCompressor(LocalViewsMain localViews) {
		this.localViews = localViews;
		groups = new LinkedHashMap<Rule, RootedRulesGroup>();
	}

	public void buildGroups(Collection<Rule> rules) {
		for (Rule rule : rules) {
			addRuleToGroup(rule);
		}
	}

	/**
	 * generate all rooted version of this rule, find group with same actions
	 * If there in no such group then create new RootedRulesGroup
	 * @param rule
	 */
	protected void addRuleToGroup(Rule rule) {
		RuleMaster master = new RuleMaster(rule);
		List<RootedRule> rootedVersions = master.getAllRootedVersions();
		for (RootedRule rr : rootedVersions) {
			if (groups.isEmpty()) {
				RootedRulesGroup firstGroup = new RootedRulesGroup(rr);
				groups.put(rule, firstGroup);
				return;
			}
			for (RootedRulesGroup group : groups.values()) {
				if (group.tryAdd(rr)) {
					groups.put(rule, group);
					return;
				}
			}
		}
		groups.put(rule, new RootedRulesGroup(rootedVersions.get(0)));
	}


	public void compressGroups() {
		boolean doProcess = true;
		while (doProcess) {
			doProcess = false;
			LinkedHashSet<RootedRulesGroup> groupses = new LinkedHashSet<RootedRulesGroup>();
			//we use set because in *.values contains replicates
			groupses.addAll(groups.values());
			for (RootedRulesGroup group : groupses) {
				
				//group remember information about troubles
				group.findCommonPart();
				
				//if findCommonPart has problem then group will be divided
				if (group.divide(groups)) {
					doProcess = true;
				}
			}
		}

	}

	
	public Rule getCompressedRule(Rule r) {
		return groups.get(r).getCompressedRule();
	}

	public void setLocalViews() {
		for (RootedRulesGroup rg : groups.values()) {
			rg.setViews(localViews);
		}
	}
	
	public Collection<RootedRulesGroup> getGroups() {
		return groups.values();
	}
}
