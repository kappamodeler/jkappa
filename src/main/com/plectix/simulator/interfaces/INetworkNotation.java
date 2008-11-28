package com.plectix.simulator.interfaces;

public interface INetworkNotation {

	public void addToAgents(ISite site, IStoriesSiteStates storiesSiteStates,
			int index);

	public void addToAgentsFromRules(ISite site, byte agentMode,
			byte internalStateMode, byte linkStateMode);

	public void addFixedSitesFromRules(ISite site, byte agentMode,
			boolean internalStateMode, boolean linkStateMode);

	public void checkLinkForNetworkNotation(int index, ISite site);

	public void checkLinkForNetworkNotationDel(int index, ISite site);

}
