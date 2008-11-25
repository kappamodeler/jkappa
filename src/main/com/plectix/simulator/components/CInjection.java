package com.plectix.simulator.components;

import java.util.*;

import com.plectix.simulator.interfaces.*;

public final class CInjection implements IInjection {

	private List<IAgentLink> agentLinkList;

	public static IInjection EMPTY_INJECTION = new CInjection();
	
	private List<ISite> sitesList = new ArrayList<ISite>();

	private List<ISite> changedSites;

	private int myId = 0;
	
	private CConnectedComponent connectedComponent;

	private CInjection() {
		
	}
	
	public CInjection(CConnectedComponent connectedComponent,
			List<ISite> sitesList, List<IAgentLink> agentLinkList) {
		this.connectedComponent = connectedComponent;
		this.sitesList = sitesList;
		this.agentLinkList = agentLinkList;
		this.changedSites = new ArrayList<ISite>();
	}

	public void removeSiteFromSitesList(ISite site){
		for (ISite siteL : this.sitesList)
			if (site==siteL){
				this.sitesList.remove(site);
				return;}
	}
	
	public void addToChangedSites(ISite site) {
		if (!(checkSiteExistanceAmongChangedSites(site)))
			this.changedSites.add(site);
	}

	public void clearChangedSites() {
		changedSites.clear();
	}

	public boolean checkSiteExistanceAmongChangedSites(ISite site) {
		for (ISite chSite : this.changedSites)
			if (chSite == site)
				return true;
		return false;
	}
	
	public void setId(int id) {
		myId = id;
	}
	
	public int getId() {
		return myId;
	}
	
	public List<ISite> getChangedSites() {
		return Collections.unmodifiableList(changedSites);
	}

	public void setChangedSites(List<ISite> changedSites) {
		this.changedSites = changedSites;
	}

	public List<IAgentLink> getAgentLinkList() {
		return Collections.unmodifiableList(agentLinkList);
	}

	public List<ISite> getSiteList() {
		return Collections.unmodifiableList(sitesList);
	}

	public void setSiteList(List<ISite> siteList) {
		this.sitesList = siteList;
	}

	public CConnectedComponent getConnectedComponent() {
		return connectedComponent;
	}
}
