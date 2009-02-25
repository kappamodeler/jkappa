package com.plectix.simulator.interfaces;

import java.util.*;

public interface IInjection {
	
	public List<ISite> getSiteList();
	
	public int getId();
	
	public void setId(int id);

	public List<IAgentLink> getAgentLinkList();

	public IConnectedComponent getConnectedComponent();

	public void addToChangedSites(ISite injectedSite);

	public void removeSiteFromSitesList(ISite site);

	public Collection<ISite> getChangedSites();

	public boolean checkSiteExistanceAmongChangedSites(ISite site);

	public IAgent getAgentFromImageById(int agentIdInCC);
}
