package com.plectix.simulator.action;

import com.plectix.simulator.components.CNetworkNotation;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CStoriesSiteStates;
import com.plectix.simulator.components.CNetworkNotation.NetworkNotationMode;
import com.plectix.simulator.components.CStoriesSiteStates.StateType;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.INetworkNotation;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.simulator.SimulationData;

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

	public final void doAction(IInjection injection, INetworkNotation netNotation, SimulationData simulationData) {
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

			addToNetworkNotation(StateType.BEFORE, netNotation,
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

		addToNetworkNotation(StateType.AFTER, netNotation,
				agentFromInSolution.getSite(mySiteFrom.getNameId()));

		agentFromInSolution.getSite(mySiteFrom.getNameId()).setLinkIndex(
				mySiteFrom.getLinkIndex());
		agentToInSolution.getSite(mySiteTo.getNameId()).setLinkIndex(
				mySiteTo.getLinkIndex());

	}

	public final void addRuleSitesToNetworkNotation(boolean existInRule,
			INetworkNotation netNotation, ISite site) {
		if (netNotation != null) {
			NetworkNotationMode agentMode = NetworkNotationMode.NONE;
			NetworkNotationMode linkStateMode = NetworkNotationMode.NONE;
			NetworkNotationMode internalStateMode = NetworkNotationMode.NONE;

			agentMode = NetworkNotationMode.TEST;
			linkStateMode = NetworkNotationMode.TEST_OR_MODIFY;
			netNotation.addToAgentsFromRules(site, agentMode,
					internalStateMode, linkStateMode);
		}
	}

	protected final void addToNetworkNotation(StateType index,
			INetworkNotation netNotation, ISite site) {
		if (netNotation != null) {
			netNotation.checkLinkForNetworkNotation(index, site);
			netNotation.checkLinkToUsedSites(index, site);
		}
	}
}
