package com.plectix.simulator.interfaces;

import com.plectix.simulator.components.CNetworkNotation.NetworkNotationMode;

public interface INetworkNotation {

	public void addToAgents(ISite site, IStoriesSiteStates storiesSiteStates,
			int index);

	public void addToAgentsFromRules(ISite site, NetworkNotationMode agentMode,
			NetworkNotationMode internalStateMode, NetworkNotationMode linkStateMode);

	public void addFixedSitesFromRules(ISite site, NetworkNotationMode agentMode,
			boolean internalStateMode, boolean linkStateMode);

	public void checkLinkForNetworkNotation(int index, ISite site);

	public void checkLinkForNetworkNotationDel(int index, ISite site);

}
