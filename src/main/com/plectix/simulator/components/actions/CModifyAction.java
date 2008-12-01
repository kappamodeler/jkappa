package com.plectix.simulator.components.actions;

import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.*;

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
	
	public void doAction(IInjection injection,
			INetworkNotation netNotation) {
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
	
	public void addRuleSitesToNetworkNotation(boolean existInRule,
			INetworkNotation netNotation, ISite site) {
		if (netNotation != null) {
			byte agentMode = CNetworkNotation.MODE_NONE;
			byte linkStateMode = CNetworkNotation.MODE_NONE;
			byte internalStateMode = CNetworkNotation.MODE_NONE;
				agentMode = CNetworkNotation.MODE_TEST_OR_MODIFY;
				internalStateMode = CNetworkNotation.MODE_TEST_OR_MODIFY;
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
