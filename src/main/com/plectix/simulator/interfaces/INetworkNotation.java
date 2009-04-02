package com.plectix.simulator.interfaces;

import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.stories.CNetworkNotation.NetworkNotationMode;
import com.plectix.simulator.components.stories.CStoriesSiteStates.StateType;

public interface INetworkNotation {

	public void addToAgents(CSite site, IStoriesSiteStates storiesSiteStates,
			StateType index);

	public void addToAgentsFromRules(CSite site, NetworkNotationMode agentMode,
			NetworkNotationMode internalStateMode,
			NetworkNotationMode linkStateMode);

	public void addFixedSitesFromRules(CSite site,
			NetworkNotationMode agentMode, boolean internalStateMode,
			boolean linkStateMode);

	public void checkLinkForNetworkNotation(StateType index, CSite site);
	public void checkLinkToUsedSites(StateType index, CSite site);
	public void checkLinkForNetworkNotationDel(StateType index, CSite site);
	public boolean changedSitesContains(CSite site);
}
