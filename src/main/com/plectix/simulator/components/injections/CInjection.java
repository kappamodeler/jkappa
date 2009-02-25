package com.plectix.simulator.components.injections;

import java.io.Serializable;
import java.util.*;

import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.interfaces.*;

public final class CInjection implements IInjection, Serializable {

	public static final IInjection EMPTY_INJECTION = new CInjection();

	private List<IAgentLink> agentLinkList;
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

	public final void removeSiteFromSitesList(ISite site) {
		int index = 0;
		for (ISite siteL : this.sitesList) {
			if (site == siteL) {
				this.sitesList.remove(index);
				return;
			}
			index++;
		}
	}

	public final void addToChangedSites(ISite site) {
		if (!(checkSiteExistanceAmongChangedSites(site)))
			this.changedSites.add(site);
	}

	public final void clearChangedSites() {
		changedSites.clear();
	}

	public final boolean checkSiteExistanceAmongChangedSites(ISite site) {
		for (ISite chSite : this.changedSites)
			if (chSite == site)
				return true;
		return false;
	}

	public final void setId(int id) {
		myId = id;
	}

	public final int getId() {
		return myId;
	}

	public final IAgent getAgentFromImageById(int id) {
		for (IAgentLink agentL : agentLinkList)
			if (agentL.getIdAgentFrom() == id)
				return agentL.getAgentTo();
		return null;
	}
	
	public final List<ISite> getChangedSites() {
		return Collections.unmodifiableList(changedSites);
	}

	public final void setChangedSites(List<ISite> changedSites) {
		this.changedSites = changedSites;
	}

	public final List<IAgentLink> getAgentLinkList() {
		return Collections.unmodifiableList(agentLinkList);
	}

	public final List<ISite> getSiteList() {
		return Collections.unmodifiableList(sitesList);
	}

	public final void setSiteList(List<ISite> siteList) {
		this.sitesList = siteList;
	}

	public final CConnectedComponent getConnectedComponent() {
		return connectedComponent;
	}
}
