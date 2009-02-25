package com.plectix.simulator.interfaces;

import java.util.List;
import java.util.Map;

public interface IContactMapAbstractAgent extends IAbstractAgent{
	
	public void addSites(IAgent agent,
			Map<Integer, IContactMapAbstractAgent> agentNameIdToAgent);
	
	public boolean equalz(IAbstractAgent obj);

	public Map<Integer, IContactMapAbstractSite> getSitesMap();

	public IContactMapAbstractSite getEmptySite();

	public void setId(long id);
	
	public void addModelSite(IContactMapAbstractSite siteToAdd);
	
}
