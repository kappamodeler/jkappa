/**
 * 
 */
package com.plectix.simulator.components.stories;

import java.util.HashMap;

import com.plectix.simulator.components.stories.CNetworkNotation.NetworkNotationMode;
import com.plectix.simulator.interfaces.IAgent;

final class AgentSitesFromRules {
	private HashMap<Integer, SitesFromRules> sites;
	private NetworkNotationMode mode;
	private int agentName;

	public int getAgentName() {
		return agentName;
	}

	public AgentSitesFromRules(NetworkNotationMode mode, IAgent agent) {
		this.mode = mode;
		sites = new HashMap<Integer, SitesFromRules>();
		this.agentName = agent.getNameId();
	}
	
	public HashMap<Integer, SitesFromRules> getSites() {
		return sites;
	}
	
	public void setSites(HashMap<Integer, SitesFromRules> sites) {
		this.sites = sites;
	}

	public final void addToSitesFromRules(int idSite,
			NetworkNotationMode internalStateMode,
			NetworkNotationMode linkStateMode, int linkAgentNameID) {
		SitesFromRules sFR = sites.get(idSite);
		if (sFR == null) {
			sFR = new SitesFromRules();
			sites.put(idSite, sFR);
		}
		if (internalStateMode != NetworkNotationMode.NONE)
			sFR.setInternalStateMode(internalStateMode, linkAgentNameID);
		if (linkStateMode != NetworkNotationMode.NONE)
			sFR.setLinkStateMode(linkStateMode, linkAgentNameID);
	}
}