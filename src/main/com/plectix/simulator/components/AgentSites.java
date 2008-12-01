package com.plectix.simulator.components;

import java.util.*;

import com.plectix.simulator.interfaces.IStoriesSiteStates;

/*package*/ final class AgentSites {
	Map<Integer, IStoriesSiteStates> sites;

	public AgentSites() {
		sites = new HashMap<Integer, IStoriesSiteStates>();
	}

	public final void addToSites(int idSite, IStoriesSiteStates siteStates,
			int index) {
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
