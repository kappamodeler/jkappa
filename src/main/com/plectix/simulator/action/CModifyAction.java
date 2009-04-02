package com.plectix.simulator.action;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.stories.CStoriesSiteStates;
import com.plectix.simulator.components.stories.CNetworkNotation.NetworkNotationMode;
import com.plectix.simulator.components.stories.CStoriesSiteStates.StateType;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.interfaces.INetworkNotation;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.simulator.SimulationData;

public class CModifyAction extends CAction {
	private final CSite mySiteTo;
	private final int myInternalStateNameId;
	
	public CModifyAction(CRule rule, CSite siteFrom, CSite siteTo, IConnectedComponent ccL,
			IConnectedComponent ccR) {
		super(rule, null, null, ccL, ccR);
		mySiteTo = siteTo;
		setSiteSet(siteFrom, siteTo);
		myInternalStateNameId = siteTo.getInternalState().getNameId();
		setType(CActionType.MODIFY);
	}
	
	public final void doAction(RuleApplicationPool pool, CInjection injection, 
			INetworkNotation netNotation, SimulationData simulationData) {
		/**
		 * Done.
		 */
		int agentIdInCC = getAgentIdInCCBySideId(mySiteTo.getAgentLink());
		CAgent agentFromInSolution = injection.getAgentFromImageById(agentIdInCC);

		// /////////////////////////////////////////////
		CSite injectedSite = agentFromInSolution.getSiteById(mySiteTo
				.getNameId());
		addToNetworkNotation(StateType.BEFORE,
				netNotation, injectedSite);
		addRuleSitesToNetworkNotation(false, netNotation, injectedSite);

		injectedSite.getInternalState().setNameId(myInternalStateNameId);
		injection.addToChangedSites(injectedSite);

		addToNetworkNotation(StateType.AFTER,
				netNotation, injectedSite);

		// /////////////////////////////////////////////
	}
	
	public final void addRuleSitesToNetworkNotation(boolean existInRule,
			INetworkNotation netNotation, CSite site) {
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
	
	protected final void addToNetworkNotation(StateType index,
			INetworkNotation netNotation, CSite site) {
		if (netNotation != null) {
			netNotation.addToAgents(site, new CStoriesSiteStates(index,
					site.getInternalState().getNameId()), index);
		}
	}
}
