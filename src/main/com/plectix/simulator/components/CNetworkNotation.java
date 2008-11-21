package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

public class CNetworkNotation {

	CRule rule;

	List<CSite> changedSitesFrimSolution;

	public List<CSite> getChangedSitesFrimSolution() {
		return changedSitesFrimSolution;
	}

	public CRule getRule() {
		return rule;
	}

	public CNetworkNotation(CRule rule, List<CInjection> injections) {
		this.rule = rule;
		this.changedSitesFrimSolution = new ArrayList<CSite>();

		for (CInjection inj : injections)
			this.changedSitesFrimSolution.addAll(inj.getChangedSites());

	}

}
