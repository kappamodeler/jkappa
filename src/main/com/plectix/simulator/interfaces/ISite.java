package com.plectix.simulator.interfaces;


public interface ISite {

	public IInternalState getInternalState();
	
	public String getName();
	
	public ILinkState getLinkState();
	
	public boolean isChanged();
	
	public void setState(IState state);
	
	public IAgent getAgentLink();
}
