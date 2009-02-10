package com.plectix.simulator.interfaces;


public interface IAbstractSite {
	
	public IInternalState getInternalState();
	
	public int getNameId();
	
	public void setInternalState(IInternalState internalState);

	public int getLinkIndex();

	public String getName();

	public void setLinkIndex(int valueOf);
	
}
