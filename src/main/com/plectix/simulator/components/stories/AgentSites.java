package com.plectix.simulator.components.stories;

import java.util.*;

import com.plectix.simulator.components.stories.CStoriesSiteStates.StateType;
import com.plectix.simulator.interfaces.IStates;
import com.plectix.simulator.interfaces.IStoriesSiteStates;

final class AgentSites{
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
		Iterator<Integer> iterator = sites.keySet().iterator();
		while (iterator.hasNext()) {
            int key = iterator.next();
            IStoriesSiteStates checkSS =sites.get(key);
            if(checkSS.getAfterState().getIdLinkAgent()==agentIDToDelete)
            	checkSS.getAfterState().setIdLinkAgent(agentID);
            
		}
	}	
	
	public final AgentSites clone(){
		AgentSites as = new AgentSites();
		Iterator<Integer> iterator = this.sites.keySet().iterator();

		while (iterator.hasNext()) {
			int key = iterator.next();
			IStoriesSiteStates sss = this.sites.get(key);
			as.getSites().put(key, ((CStoriesSiteStates)sss).clone());
		}
		
		return as;
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
