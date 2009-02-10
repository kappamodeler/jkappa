package com.plectix.simulator.interfaces;

public interface IContactMapAbstractAgent extends IAbstractAgent{
	
	public boolean addSite(ISite site);
	
	public boolean addSites(IAgent agent);
	
	public boolean equalz(IAbstractAgent obj);
}
