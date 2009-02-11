package com.plectix.simulator.interfaces;

import java.util.Collection;
import java.util.List;

public interface ISite {

	public boolean includedInCollection(Collection<ISite> collection);
		
	public ILinkState getLinkState();
	
	public boolean isChanged();
	
	public IAgent getAgentLink();

	public IInternalState getInternalState();
	
	public void addToLift(ILiftElement liftElement);
	
	public int getNameId();
	
	public void removeInjectionFromLift(IInjection injection);

	public boolean isConnectedComponentInLift(IConnectedComponent cc);

	public List<IInjection> getInjectionFromLift(IConnectedComponent cc);

	public List<ILiftElement> getLift();

	public void removeInjectionsFromCCToSite(IInjection injection);

	public void setInternalState(IInternalState internalState);

	public void setAgentLink(IAgent agent);

	public int getLinkIndex();

	public String getName();

	public void clearLiftList();

	//TODO test only
	public void setLinkIndex(int valueOf);

	public void clearLift();

	public boolean equalz(ISite siteThat);
}
