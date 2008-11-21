package com.plectix.simulator.components;

import java.util.HashMap;
import java.util.List;

public class CNetworkNotation {

	int step;
	
	CRule rule;

	HashMap<Long, AgentSites> changedAgentsFromSolution;

	class AgentSites {
		HashMap<Integer, CStoriesSiteStates> sites;

		public AgentSites() {
			sites = new HashMap<Integer, CStoriesSiteStates>();
		}

		public void addToSites(int idSite, CStoriesSiteStates siteStates, int index) {
			CStoriesSiteStates ss = sites.get(idSite);
			if (ss == null) 
				sites.put(idSite, siteStates);
			else
				ss.addInformation(index, siteStates);
		}
	}

	public void addToAgents(CSite site, CStoriesSiteStates siteStates, int index) {
		if (site != null) {
			long key = site.getAgentLink().getHash();
			AgentSites as = changedAgentsFromSolution.get(key);
			if (as == null) {
				as = new AgentSites();
				changedAgentsFromSolution.put(key, as);
			}
			as.addToSites(site.getNameId(), siteStates, index);
		}
	}

	public CRule getRule() {
		return rule;
	}

	public CNetworkNotation(int step, CRule rule) {
		this.step = step;
		this.rule = rule;
		this.changedAgentsFromSolution = new HashMap<Long, AgentSites>();
	}

}
