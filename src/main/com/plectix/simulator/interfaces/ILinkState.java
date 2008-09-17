package com.plectix.simulator.interfaces;


public interface ILinkState extends IState{

	
	public ISite getSite();
	
	public void setSite(ISite site);
	
	public void setStatusLink(byte statusLink);
	
	public byte getStatusLink();
	
	//TODO specify details
	
}
