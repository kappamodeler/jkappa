package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IInjection;

public class CInjection implements IInjection {

	private List<CAgentLink> agentLinkList;

	private List<CSite> sitesList = new ArrayList<CSite>();

	private List<CSite> changedSites;

	public List<CSite> getChangedSites() {
		return changedSites;
	}

	public void setChangedSites(List<CSite> changedSites) {
		this.changedSites = changedSites;
	}

	private CConnectedComponent connectedComponent;

	public CInjection() {
	}

	public void addToChangedSites(CSite site) {
		if (!(checkSiteExistanceAmongChangedSites(site)))
			this.changedSites.add(site);
	}

	public void clearChangedSites() {
		changedSites.clear();
	}

	public boolean checkSiteExistanceAmongChangedSites(CSite site) {
		for (CSite chSite : this.changedSites)
			if (chSite == site)
				return true;
		return false;
	}

	public CInjection(CConnectedComponent connectedComponent,
			List<CSite> sitesList, List<CAgentLink> agentLinkList) {
		this.connectedComponent = connectedComponent;
		this.sitesList = sitesList;
		this.agentLinkList = agentLinkList;
		this.changedSites = new ArrayList<CSite>();
	}

	public List<CAgentLink> getAgentLinkList() {
		return agentLinkList;
	}

	public List<CSite> getSiteList() {
		return sitesList;
	}

	public void setSiteList(List<CSite> siteList) {
		this.sitesList = siteList;
	}

	public CConnectedComponent getConnectedComponent() {
		return connectedComponent;
	}

	@Override
	public List<IAgent> getAgents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAgents(List<IAgent> agents) {
		// TODO Auto-generated method stub

	}

}
