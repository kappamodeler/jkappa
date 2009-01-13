package com.plectix.simulator.action;

import com.plectix.simulator.components.CNetworkNotation;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CStoriesSiteStates;
import com.plectix.simulator.components.CNetworkNotation.NetworkNotationMode;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.INetworkNotation;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.simulator.SimulationData;

public class CModifyAction extends CAction {
	private final ISite mySiteTo;
	private final int myInternalStateNameId;
	
	public CModifyAction(CRule rule, ISite siteFrom, ISite siteTo, IConnectedComponent ccL,
			IConnectedComponent ccR) {
		super(rule, null, null, ccL, ccR);
		mySiteTo = siteTo;
		setSiteSet(siteFrom, mySiteTo);
		myInternalStateNameId = siteTo.getInternalState().getNameId();
		setType(CActionType.MODIFY);
	}
	
	public final void doAction(IInjection injection, INetworkNotation netNotation, SimulationData simulationData) {
		/**
		 * Done.
		 */
		int agentIdInCC = getAgentIdInCCBySideId(mySiteTo.getAgentLink());
		IAgent agentFromInSolution = getLeftCComponent()
				.getAgentByIdFromSolution(agentIdInCC, injection);

		// /////////////////////////////////////////////
		ISite injectedSite = agentFromInSolution.getSite(mySiteTo
				.getNameId());
		addToNetworkNotation(CStoriesSiteStates.LAST_STATE,
				netNotation, injectedSite);
		addRuleSitesToNetworkNotation(false, netNotation, injectedSite);

		injectedSite.getInternalState().setNameId(myInternalStateNameId);
		injection.addToChangedSites(injectedSite);

		addToNetworkNotation(CStoriesSiteStates.CURRENT_STATE,
				netNotation, injectedSite);

		// /////////////////////////////////////////////
	}
	
	public final void addRuleSitesToNetworkNotation(boolean existInRule,
			INetworkNotation netNotation, ISite site) {
		if (netNotation != null) {
			NetworkNotationMode agentMode = NetworkNotationMode.NONE;
			NetworkNotationMode linkStateMode = NetworkNotationMode.NONE;
			NetworkNotationMode internalStateMode = NetworkNotationMode.NONE;
				agentMode = NetworkNotationMode.TEST_OR_MODIFY;
				internalStateMode = NetworkNotationMode.TEST_OR_MODIFY;
			netNotation.addToAgentsFromRules(site, agentMode,
					internalStateMode, linkStateMode);
		}
	}
	
	protected final void addToNetworkNotation(int index,
			INetworkNotation netNotation, ISite site) {
		if (netNotation != null) {
			netNotation.addToAgents(site, new CStoriesSiteStates(index,
					site.getInternalState().getNameId()), index); 
		}
	}
}
