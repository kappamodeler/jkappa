package com.plectix.simulator.action;

import com.plectix.simulator.components.CLinkState;
import com.plectix.simulator.components.CLinkStatus;
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

public class CBreakAction extends CAction {
	private final ISite mySiteFrom;
	private final ISite mySiteTo;
	private final CRule myRule;
	
	public CBreakAction(CRule rule, ISite siteFrom, ISite siteTo,
			IConnectedComponent ccL, IConnectedComponent ccR) {
		super(rule, null, null, ccL, ccR);
		myRule = rule;
		mySiteFrom = siteFrom;
		mySiteTo = siteTo;
		setSiteSet(mySiteFrom, mySiteTo);
		setType(CActionType.BREAK);
	}

	public final void doAction(IInjection injection, INetworkNotation netNotation, SimulationData simulationData) {
		IAgent agentFromInSolution;
		int agentIdInCC = getAgentIdInCCBySideId(mySiteFrom.getAgentLink());
		agentFromInSolution = getLeftCComponent().getAgentByIdFromSolution(
				agentIdInCC, injection);

		ISite injectedSite = agentFromInSolution.getSite(mySiteFrom.getNameId());

		addToNetworkNotation(CStoriesSiteStates.LAST_STATE, netNotation,
				injectedSite);
		addRuleSitesToNetworkNotation(true, netNotation, injectedSite);

		ISite linkSite = (ISite) injectedSite.getLinkState().getSite();
		if ((mySiteFrom.getLinkState().getSite() == null) && (linkSite != null)) {
			addToNetworkNotation(CStoriesSiteStates.LAST_STATE, netNotation,
					linkSite);

			linkSite.getLinkState().setSite(null);
			linkSite.getLinkState().setStatusLink(CLinkStatus.FREE);
			if (mySiteTo != null) {
				linkSite.setLinkIndex(mySiteTo.getLinkIndex());
			}
			injection.addToChangedSites(linkSite);
			getRightCComponent().addAgentFromSolutionForRHS(linkSite
					.getAgentLink());
			addToNetworkNotation(CStoriesSiteStates.CURRENT_STATE, netNotation,
					linkSite);

		}

		agentFromInSolution.getSite(mySiteFrom.getNameId()).getLinkState()
				.setSite(null);
		agentFromInSolution.getSite(mySiteFrom.getNameId()).getLinkState()
				.setStatusLink(CLinkStatus.FREE);
		// /////////////////////////////////////////////

		injection.addToChangedSites(injectedSite);

		addToNetworkNotation(CStoriesSiteStates.CURRENT_STATE, netNotation,
				injectedSite);
		/**
		 * Break a bond for this rules: A(x!_)->A(x)
		 */
		if (mySiteFrom.getLinkState().getSite() == null && linkSite != null) {
			addSiteToConnectedWithBroken(linkSite);
			addRuleSitesToNetworkNotation(false, netNotation, linkSite);
		}
		// /////////////////////////////////////////////
		agentFromInSolution.getSite(mySiteFrom.getNameId()).setLinkIndex(
				mySiteFrom.getLinkIndex());
	}

	private final void addSiteToConnectedWithBroken(ISite checkedSite) {
		for (ISite site : myRule.getSitesConnectedWithBroken()) {
			if (site == checkedSite) {
				return;
			}
		}
		myRule.addSiteConnectedWithBroken(checkedSite);
	}

	public final void addRuleSitesToNetworkNotation(boolean existInRule,
			INetworkNotation netNotation, ISite site) {
		if (netNotation != null) {
			NetworkNotationMode agentMode = NetworkNotationMode.NONE;
			NetworkNotationMode linkStateMode = NetworkNotationMode.NONE;
			NetworkNotationMode internalStateMode = NetworkNotationMode.NONE;

			if (existInRule) {
				agentMode = NetworkNotationMode.TEST;
				linkStateMode = NetworkNotationMode.TEST_OR_MODIFY;
			} else {
				linkStateMode = NetworkNotationMode.MODIFY;
			}
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
