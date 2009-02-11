package com.plectix.simulator.interfaces;

import java.util.List;
import java.util.Map;

public interface IContactMapAbstractAgent extends IAbstractAgent{
	
	public boolean addSite(ISite site);
	
	public boolean addSites(IAgent agent);
	
	public boolean equalz(IAbstractAgent obj);

	public boolean containsSite(IContactMapAbstractSite site);

	public List<IContactMapAbstractSite> getSites();

	public Map<Integer, List<IContactMapAbstractSite>> getSitesMap();
}
