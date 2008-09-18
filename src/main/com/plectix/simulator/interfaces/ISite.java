package com.plectix.simulator.interfaces;


public interface ISite {

	//TODO specify details
//	public IState getState();
	
	public IInternalState getInternalState();
	
	public String getName();
	
	public ILinkState getLinkState();
	
	public boolean isChanged();
	
	public void setState(IState state);
	
	public IAgent getAgentLink();
//	public void setLink(ISite site);
	
//	public ISite getLink();
}
