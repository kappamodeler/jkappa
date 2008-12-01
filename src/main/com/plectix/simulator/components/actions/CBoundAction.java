package com.plectix.simulator.components.actions;

import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.*;

public class CBoundAction extends CAction {
	private final ISite mySiteFrom;
	private final ISite mySiteTo;
	private CRule myRule;
	
	public CBoundAction(CRule rule, ISite siteFrom, ISite siteTo, IConnectedComponent ccL,
			IConnectedComponent ccR) {
		super(rule, null, null, ccL, ccR);
		myRule = rule;
		mySiteFrom = siteFrom;
		mySiteTo = siteTo;
		setSiteSet(mySiteFrom, mySiteTo);
		setType(CActionType.BOUND);
	}

	public final void doAction(IInjection injection, INetworkNotation netNotation) {
		//	TODO remove copypaste
		/**
		 * Done.
		 */

		IAgent agentFromInSolution;
		if (mySiteFrom.getAgentLink().getIdInRuleSide() > myRule.getAgentsFromConnectedComponent(
				myRule.getLeftHandSide()).size()) {
			agentFromInSolution = myRule.getAgentAdd(mySiteFrom.getAgentLink());
		} else {
			int agentIdInCC = getAgentIdInCCBySideId(mySiteFrom.getAgentLink());

			agentFromInSolution = getLeftCComponent()
					.getAgentByIdFromSolution(agentIdInCC, injection);

			// /////////////////////////////////////////////
			ISite injectedSite = agentFromInSolution.getSite(mySiteFrom
					.getNameId());
			injection.addToChangedSites(injectedSite);

			addToNetworkNotation(CStoriesSiteStates.LAST_STATE, netNotation,
					injectedSite);
			addRuleSitesToNetworkNotation(false, netNotation, injectedSite);
			// /////////////////////////////////////////////
		}

		IAgent agentToInSolution;
		if (mySiteTo.getAgentLink().getIdInRuleSide() > myRule.getAgentsFromConnectedComponent(
				myRule.getLeftHandSide()).size()) {
			agentToInSolution = myRule.getAgentAdd(mySiteTo.getAgentLink());
		} else {
			int agentIdInCC = getAgentIdInCCBySideId(mySiteTo.getAgentLink());
			IInjection inj = myRule.getInjectionBySiteToFromLHS(mySiteTo);
			agentToInSolution = getLeftCComponent()
					.getAgentByIdFromSolution(agentIdInCC, inj);
		}

		agentFromInSolution.getSite(mySiteFrom.getNameId()).getLinkState()
				.setSite(agentToInSolution.getSite(mySiteTo.getNameId()));

		addToNetworkNotation(CStoriesSiteStates.CURRENT_STATE, netNotation,
				agentFromInSolution.getSite(mySiteFrom.getNameId()));

		agentFromInSolution.getSite(mySiteFrom.getNameId()).setLinkIndex(
				mySiteFrom.getLinkIndex());
		agentToInSolution.getSite(mySiteTo.getNameId()).setLinkIndex(
				mySiteTo.getLinkIndex());

	}

	public final void addRuleSitesToNetworkNotation(boolean existInRule,
			INetworkNotation netNotation, ISite site) {
		if (netNotation != null) {
			byte agentMode = CNetworkNotation.MODE_NONE;
			byte linkStateMode = CNetworkNotation.MODE_NONE;
			byte internalStateMode = CNetworkNotation.MODE_NONE;

			agentMode = CNetworkNotation.MODE_TEST;
			linkStateMode = CNetworkNotation.MODE_TEST_OR_MODIFY;
			netNotation.addToAgentsFromRules(site, agentMode,
					internalStateMode, linkStateMode);
		}
	}

	protected final void addToNetworkNotation(int index,
			INetworkNotation netNotation, ISite site) {
		if (netNotation != null) {
			netNotation.checkLinkForNetworkNotation(index, site);
		}
	}
}
