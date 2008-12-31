package com.plectix.simulator.action;

import com.plectix.simulator.components.CLinkState;
import com.plectix.simulator.components.CNetworkNotation;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.CStoriesSiteStates;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.ILiftElement;
import com.plectix.simulator.interfaces.INetworkNotation;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.simulator.SimulationData;

public class CDeleteAction extends CAction {
	private final CRule myRule;
	private final IAgent myFromAgent;
	
	public CDeleteAction(CRule rule, IAgent fromAgent, IConnectedComponent ccL) {
		super(rule, fromAgent, null, ccL, null);
		myFromAgent = fromAgent;
		myRule = rule;
		setType(CActionType.DELETE);
	}

	public final void doAction(IInjection injection, INetworkNotation netNotation, SimulationData simulationData) {
		/**
		 * Done.
		 */
		IAgent agent = getLeftCComponent().getAgentByIdFromSolution(
				myFromAgent.getIdInConnectedComponent(), injection);
		for (ISite site : agent.getSites()) {
			removeSiteToConnectedWithDeleted(site);
			ISite solutionSite = (ISite) site.getLinkState().getSite();

			if (solutionSite != null) {
				addToNetworkNotation(CStoriesSiteStates.LAST_STATE,
						netNotation, solutionSite);

				addSiteToConnectedWithDeleted(solutionSite);
				solutionSite.getLinkState().setSite(null);
				solutionSite.getLinkState().setStatusLink(
						CLinkState.STATUS_LINK_FREE);
				solutionSite.setLinkIndex(-1);
				addToNetworkNotation(CStoriesSiteStates.CURRENT_STATE,
						netNotation, solutionSite);
				addRuleSitesToNetworkNotation(false, netNotation, solutionSite);
				// solutionSite.removeInjectionsFromCCToSite(injection);
			}
		}

		for (ILiftElement lift : agent.getEmptySite().getLift()) {
			agent.getEmptySite().removeInjectionsFromCCToSite(
					lift.getInjection());
			lift.getInjection().getConnectedComponent().removeInjection(
					lift.getInjection());
		}

		for (ISite site : agent.getSites()) {
			addToNetworkNotation(CStoriesSiteStates.LAST_STATE, netNotation,
					site);
			addRuleSitesToNetworkNotation(true, netNotation, site);
			for (ILiftElement lift : site.getLift()) {
				site.removeInjectionsFromCCToSite(lift.getInjection());
				lift.getInjection().getConnectedComponent().removeInjection(
						lift.getInjection());
			}
			site.clearLift();
			injection.removeSiteFromSitesList(site);
		}
		// injection.getConnectedComponent().getInjectionsList()
		// .remove(injection);

		simulationData.getSolution().removeAgent(agent);
	}

	@Override
	public final void addRuleSitesToNetworkNotation(boolean existInRule,
			INetworkNotation netNotation, ISite site) {
		if (netNotation != null) {
			byte agentMode = CNetworkNotation.MODE_NONE;
			byte linkStateMode = CNetworkNotation.MODE_NONE;
			byte internalStateMode = CNetworkNotation.MODE_NONE;

			if (existInRule) {
				agentMode = CNetworkNotation.MODE_TEST_OR_MODIFY;
				ISite siteFromRule = myFromAgent.getSite(site.getNameId());

				if (siteFromRule != null)
					linkStateMode = CNetworkNotation.MODE_TEST_OR_MODIFY;
				else
					linkStateMode = CNetworkNotation.MODE_MODIFY;

				if (siteFromRule != null
						&& siteFromRule.getInternalState().getNameId() != CSite.NO_INDEX)
					internalStateMode = CNetworkNotation.MODE_TEST_OR_MODIFY;
				else
					internalStateMode = CNetworkNotation.MODE_MODIFY;
			} else
				linkStateMode = CNetworkNotation.MODE_MODIFY;
			
			netNotation.addToAgentsFromRules(site, agentMode,
					internalStateMode, linkStateMode);
		}
	}
	
	protected final void addToNetworkNotation(int index,
			INetworkNotation netNotation, ISite site) {
		if (netNotation != null) {
			netNotation.checkLinkForNetworkNotationDel(index, site);
		}
	}
	
	private final void addSiteToConnectedWithDeleted(ISite checkedSite) {
		for (ISite site : myRule.getSitesConnectedWithDeleted()) {
			if (site == checkedSite) {
				return;
			}
		}
		myRule.addSiteConnectedWithDeleted(checkedSite);
	}
	
	private final void removeSiteToConnectedWithDeleted(ISite checkedSite) {
		int size = myRule.getSitesConnectedWithDeleted().size();
		for (int i = 0; i < size; i++) {
			if (myRule.getSiteConnectedWithDeleted(i) == checkedSite) {
				myRule.removeSiteConnectedWithDeleted(i);
				return;
			}
		}
	}
}
