package com.plectix.simulator.interfaces;

public interface INetworkNotation {

	public void addToAgents(ISite site, IStoriesSiteStates storiesSiteStates, int index);

	public void checkLinkForNetworkNotation(int index, ISite site);

	public void checkLinkForNetworkNotationDel(int index, ISite site);

}
