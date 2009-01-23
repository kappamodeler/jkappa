package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISite;

public class CContactMapChangedSite extends ChangedSite {
	private List<Integer> usedRuleIDs;

	public List<Integer> getUsedRuleIDs() {
		return usedRuleIDs;
	}

	public CContactMapChangedSite(ISite site, boolean internalState,
			boolean linkState) {
		super(site, internalState, linkState);
		usedRuleIDs = new ArrayList<Integer>();
	}

	public void addRules(IRule rule) {
		if (rule != null) {
			int value = rule.getRuleID();
			if (!usedRuleIDs.contains(value))
				usedRuleIDs.add(value);
		}
	}

}
