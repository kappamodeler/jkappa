package com.plectix.simulator.components.stories;

import java.util.*;

import com.plectix.simulator.components.stories.CStoriesSiteStates.StateType;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IStoriesSiteStates;

final class AgentSites {
	private Map<Integer, IStoriesSiteStates> sites;

	public Map<Integer, IStoriesSiteStates> getSites() {
		return sites;
	}

	public AgentSites(IAgent agent) {
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

	public final boolean isEqualsAgentSites(AgentSites checkAS){
		if(sites.size()!=checkAS.getSites().size())
			return false;
		
		Iterator<Integer> iterator = sites.keySet().iterator();
		while (iterator.hasNext()) {
            int key = iterator.next();
            IStoriesSiteStates checkSS =checkAS.getSites().get(key);
            IStoriesSiteStates ss = sites.get(key);
            if(checkSS==null)
            	return false;
            if(!ss.isEqualsAfterState(checkSS))
            	return false;
            
		}
		return true;
	}
}