package com.plectix.simulator.interfaces;

import com.plectix.simulator.components.CNetworkNotation.NetworkNotationMode;
import com.plectix.simulator.components.CStoriesSiteStates.StateType;

public interface INetworkNotation {

	public void addToAgents(ISite site, IStoriesSiteStates storiesSiteStates,
			StateType index);

	public void addToAgentsFromRules(ISite site, NetworkNotationMode agentMode,
			NetworkNotationMode internalStateMode, NetworkNotationMode linkStateMode);

	public void addFixedSitesFromRules(ISite site, NetworkNotationMode agentMode,
			boolean internalStateMode, boolean linkStateMode);

	public void checkLinkForNetworkNotation(StateType index, ISite site);

	public void checkLinkForNetworkNotationDel(StateType index, ISite site);

}
