package com.plectix.simulator.components;

import java.util.*;

import com.plectix.simulator.components.CStoriesSiteStates.StateType;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IStoriesSiteStates;

/*package*/ final class AgentSites {
	IAgent agent;
	public IAgent getAgent() {
		return agent;
	}

	private Map<Integer, IStoriesSiteStates> sites;

	public Map<Integer, IStoriesSiteStates> getSites() {
		return sites;
	}

	public AgentSites(IAgent agent) {
		this.agent = agent;
		sites = new HashMap<Integer, IStoriesSiteStates>();
	}

	public final void addToSites(int idSite, IStoriesSiteStates siteStates,
			StateType index) {
		IStoriesSiteStates ss = sites.get(idSite);
		if (ss == null)
			sites.put(idSite, siteStates);
		else
			ss.addInformation(index, siteStates);
	}

	public final void addToSites(int nameId, CStoriesSiteStates siteStates,
			int index) {
		// TODO Auto-generated method stub

	}
}
