package com.plectix.simulator.interfaces;

import java.util.*;

import com.plectix.simulator.components.solution.SuperSubstance;

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

	public void setSuperSubstance(SuperSubstance mySubstance);

	public SuperSubstance getSuperSubstance();

	public IAgent getImageAgent();
	
	public boolean isSuper();

	public boolean isEmpty();
}
