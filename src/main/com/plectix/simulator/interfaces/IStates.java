package com.plectix.simulator.interfaces;

public interface IStates {

	public int getIdLinkSite();

	public long getIdLinkAgent();

	public int getIdInternalState();

	public void addInformation(int idInternalState, long idLinkAgent,
			int idLinkSite);

}
