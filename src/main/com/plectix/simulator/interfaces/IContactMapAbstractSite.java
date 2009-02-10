package com.plectix.simulator.interfaces;

import java.util.Collection;

public interface IContactMapAbstractSite extends IAbstractSite{
	
	public boolean includedInCollection(Collection<IContactMapAbstractSite> collection);
	
	public IContactMapAbstractAgent getAgentLink();

	public boolean equalz(IAbstractSite obj);
}
