package com.plectix.simulator.interfaces;

public interface IStates {

	public int getIdLinkSite();

	public long getIdLinkAgent();
	
	public void setIdLinkAgent(long id);

	public int getIdInternalState();

	public void addInformation(int idInternalState, long idLinkAgent,
			int idLinkSite);
	public boolean equalz(IStates states);
}
