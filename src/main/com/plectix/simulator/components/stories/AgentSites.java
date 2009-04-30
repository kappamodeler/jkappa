package com.plectix.simulator.components.stories;

import java.util.*;

import com.plectix.simulator.components.stories.CStoriesSiteStates.StateType;
import com.plectix.simulator.interfaces.IStates;
import com.plectix.simulator.interfaces.IStoriesSiteStates;

public final class AgentSites{
	private Map<Integer, IStoriesSiteStates> sites;

	public Map<Integer, IStoriesSiteStates> getSites() {
		return sites;
	}

	public AgentSites() {
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
	
	public final void changeLinkAgents(Long agentIDToDelete,
			Long agentID){
		for (IStoriesSiteStates checkSS : sites.values()) {
            if (checkSS.getAfterState().getIdLinkAgent() == agentIDToDelete)
            	checkSS.getAfterState().setIdLinkAgent(agentID);
		}
	}	
	
	public final AgentSites clone(){
		AgentSites as = new AgentSites();

		for (Map.Entry<Integer, IStoriesSiteStates> entry : sites.entrySet()) {
			IStoriesSiteStates sss = entry.getValue();
			as.getSites().put(entry.getKey(), ((CStoriesSiteStates)sss).clone());
		}
		
		return as;
	}

	public final boolean isEqualsAgentSites(AgentSites checkAS){
		if(sites.size()!=checkAS.getSites().size())
			return false;
		
		for (Map.Entry<Integer, IStoriesSiteStates> entry : sites.entrySet()) {
			IStoriesSiteStates checkSS = checkAS.getSites().get(entry.getKey());
            IStoriesSiteStates ss = entry.getValue();
            if(checkSS==null)
            	return false;
            
            IStates state = ss.getBeforeState();
			IStates stateNext = checkSS.getBeforeState();

			if (!state.equalz(stateNext))
				return false;

			state = ss.getAfterState();
			stateNext = checkSS.getAfterState();

			if (!state.equalz(stateNext))
				return false;
		}
		return true;
	}
}
