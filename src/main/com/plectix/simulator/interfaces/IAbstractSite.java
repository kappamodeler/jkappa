package com.plectix.simulator.interfaces;

import com.plectix.simulator.components.CInternalState;

public interface IAbstractSite {
	
	public CInternalState getInternalState();
	
	public int getNameId();
	
	public void setInternalState(CInternalState internalState);

	public int getLinkIndex();

	public String getName();

	public void setLinkIndex(int valueOf);
	
}
