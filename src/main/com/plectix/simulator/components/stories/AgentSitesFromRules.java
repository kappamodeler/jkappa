/**
 * 
 */
package com.plectix.simulator.components.stories;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.plectix.simulator.components.stories.CNetworkNotation.NetworkNotationMode;

final class AgentSitesFromRules {
	private HashMap<Integer, SitesFromRules> sites;
	private int agentNameID;

	public int getAgentNameID() {
		return agentNameID;
	}

	public AgentSitesFromRules(int agentNameID) {
		sites = new HashMap<Integer, SitesFromRules>();
		this.agentNameID = agentNameID;
	}
	
	public final AgentSitesFromRules clone(){
		AgentSitesFromRules aSFR = new AgentSitesFromRules(this.agentNameID);
		
		for (Map.Entry<Integer, SitesFromRules> entry : sites.entrySet()) {
			SitesFromRules sfr = entry.getValue();
			aSFR.getSites().put(entry.getKey(), sfr.clone());
		}
		
		return aSFR;
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