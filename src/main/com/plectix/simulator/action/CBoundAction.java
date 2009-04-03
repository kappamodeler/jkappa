package com.plectix.simulator.action;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.stories.CNetworkNotation;
import com.plectix.simulator.components.stories.CStoriesSiteStates;
import com.plectix.simulator.components.stories.CNetworkNotation.NetworkNotationMode;
import com.plectix.simulator.components.stories.CStoriesSiteStates.StateType;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.interfaces.INetworkNotation;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.simulator.SimulationData;

public class CBoundAction extends CAction {
	private final CSite mySiteFrom;
	private final CSite mySiteTo;
	private CRule myRule;
	
	public CBoundAction(CRule rule, CSite siteFrom, CSite siteTo, IConnectedComponent ccL,
			IConnectedComponent ccR) {
		super(rule, null, null, ccL, ccR);
		myRule = rule;
		mySiteFrom = siteFrom;
		mySiteTo = siteTo;
		setSiteSet(mySiteFrom, mySiteTo);
		setType(CActionType.BOUND);
	}

	public final void doAction(RuleApplicationPool pool, CInjection injection, 
			INetworkNotation netNotation, SimulationData simulationData) {
		//	TODO remove copypaste
		/**
		 * Done.
		 */

		CAgent agentFromInSolution;
		if (mySiteFrom.getAgentLink().getIdInRuleHandside() > myRule.getAgentsFromConnectedComponent(
				myRule.getLeftHandSide()).size()) {
			agentFromInSolution = myRule.getAgentAdd(mySiteFrom.getAgentLink());
		} else {
			int agentIdInCC = getAgentIdInCCBySideId(mySiteFrom.getAgentLink());

			agentFromInSolution = injection.getAgentFromImageById(agentIdInCC);

			// /////////////////////////////////////////////
			CSite injectedSite = agentFromInSolution.getSiteById(mySiteFrom
					.getNameId());
			injection.addToChangedSites(injectedSite);

			addToNetworkNotation(StateType.BEFORE, netNotation,
					injectedSite);
			addRuleSitesToNetworkNotation(false, netNotation, injectedSite);
			// /////////////////////////////////////////////
		}

		CAgent agentToInSolution;
		if (mySiteTo.getAgentLink().getIdInRuleHandside() > myRule.getAgentsFromConnectedComponent(
				myRule.getLeftHandSide()).size()) {
			agentToInSolution = myRule.getAgentAdd(mySiteTo.getAgentLink());
		} else {
			int agentIdInCC = getAgentIdInCCBySideId(mySiteTo.getAgentLink());
			CInjection inj = myRule.getInjectionBySiteToFromLHS(mySiteTo);
			agentToInSolution = inj.getAgentFromImageById(agentIdInCC);
		}

		agentFromInSolution.getSiteById(mySiteFrom.getNameId()).getLinkState()
				.connectSite(agentToInSolution.getSiteById(mySiteTo.getNameId()));

		agentToInSolution.getSiteById(mySiteTo.getNameId()).getLinkState()
		.connectSite(agentFromInSolution.getSiteById(mySiteFrom.getNameId()));

		addToNetworkNotation(StateType.AFTER, netNotation,
				agentFromInSolution.getSiteById(mySiteFrom.getNameId()));

		agentFromInSolution.getSiteById(mySiteFrom.getNameId()).setLinkIndex(
				mySiteFrom.getLinkIndex());
		agentToInSolution.getSiteById(mySiteTo.getNameId()).setLinkIndex(
				mySiteTo.getLinkIndex());

	}

	public final void addRuleSitesToNetworkNotation(boolean existInRule,
			INetworkNotation netNotation, CSite site) {
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
			INetworkNotation netNotation, CSite site) {
		if (netNotation != null) {
			netNotation.checkLinkForNetworkNotation(index, site);
			netNotation.checkLinkToUsedSites(index, site);
		}
	}
}
