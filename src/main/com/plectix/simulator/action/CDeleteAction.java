package com.plectix.simulator.action;

import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.injections.CLiftElement;
import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.stories.CNetworkNotation.NetworkNotationMode;
import com.plectix.simulator.components.stories.CStoriesSiteStates.StateType;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.interfaces.INetworkNotation;
import com.plectix.simulator.simulator.SimulationData;

@SuppressWarnings("serial")
public class CDeleteAction extends CAction {
	private final CRule myRule;
	private final CAgent myFromAgent;
	
	public CDeleteAction(CRule rule, CAgent fromAgent, IConnectedComponent ccL) {
		super(rule, fromAgent, null, ccL, null);
		myFromAgent = fromAgent;
		myRule = rule;
		setType(CActionType.DELETE);
	}

	public final void doAction(RuleApplicationPool pool, CInjection injection, 
			INetworkNotation netNotation, SimulationData simulationData) {
		/**
		 * Done.
		 */
		CAgent agent = injection.getAgentFromImageById(myFromAgent.getIdInConnectedComponent());
		for (CSite site : agent.getSites()) {
			removeSiteToConnectedWithDeleted(site);
			CSite solutionSite = (CSite) site.getLinkState().getConnectedSite();

			if (solutionSite != null) {
				addToNetworkNotation(StateType.BEFORE,
						netNotation, solutionSite);

				addSiteToConnectedWithDeleted(solutionSite);
				solutionSite.getLinkState().connectSite(null);
				solutionSite.getLinkState().setStatusLink(
						CLinkStatus.FREE);
				solutionSite.setLinkIndex(-1);
				addToNetworkNotation(StateType.AFTER,
						netNotation, solutionSite);
				addRuleSitesToNetworkNotation(false, netNotation, solutionSite);
				// solutionSite.removeInjectionsFromCCToSite(injection);
			}
		}

		for (CLiftElement lift : agent.getDefaultSite().getLift()) {
			agent.getDefaultSite().clearIncomingInjections(
					lift.getInjection());
			lift.getInjection().getConnectedComponent().removeInjection(
					lift.getInjection());
		}

		for (CSite site : agent.getSites()) {
			addToNetworkNotation(StateType.BEFORE, netNotation,
					site);
			addRuleSitesToNetworkNotation(true, netNotation, site);
			for (CLiftElement lift : site.getLift()) {
				site.clearIncomingInjections(lift.getInjection());
				lift.getInjection().getConnectedComponent().removeInjection(
						lift.getInjection());
			}
			site.clearLiftList();
			injection.removeSiteFromSitesList(site);
		}
		// injection.getConnectedComponent().getInjectionsList()
		// .remove(injection);

		pool.removeAgent(agent);
	}

	protected final void addRuleSitesToNetworkNotation(boolean existInRule,
			INetworkNotation netNotation, CSite site) {
		if (netNotation != null) {
			NetworkNotationMode agentMode = NetworkNotationMode.NONE;
			NetworkNotationMode linkStateMode = NetworkNotationMode.NONE;
			NetworkNotationMode internalStateMode = NetworkNotationMode.NONE;

			if (existInRule) {
				agentMode = NetworkNotationMode.TEST_OR_MODIFY;
				CSite siteFromRule = myFromAgent.getSiteById(site.getNameId());

				if (siteFromRule != null)
					linkStateMode = NetworkNotationMode.TEST_OR_MODIFY;
				else
					linkStateMode = NetworkNotationMode.MODIFY;

				if (siteFromRule != null
						&& siteFromRule.getInternalState().getNameId() != CSite.NO_INDEX)
					internalStateMode = NetworkNotationMode.TEST_OR_MODIFY;
				else
					internalStateMode = NetworkNotationMode.MODIFY;
			} else
				linkStateMode = NetworkNotationMode.MODIFY;
			
			netNotation.addToAgentsFromRules(site, agentMode,
					internalStateMode, linkStateMode);
		}
	}
	
	protected final void addToNetworkNotation(StateType index,
			INetworkNotation netNotation, CSite site) {
		if (netNotation != null) {
			netNotation.checkLinkForNetworkNotationDel(index, site);
		}
	}
	
	private final void addSiteToConnectedWithDeleted(CSite checkedSite) {
		for (CSite site : myRule.getSitesConnectedWithDeleted()) {
			if (site == checkedSite) {
				return;
			}
		}
		myRule.addSiteConnectedWithDeleted(checkedSite);
	}
	
	private final void removeSiteToConnectedWithDeleted(CSite checkedSite) {
		int size = myRule.getSitesConnectedWithDeleted().size();
		for (int i = 0; i < size; i++) {
			if (myRule.getSiteConnectedWithDeleted(i) == checkedSite) {
				myRule.removeSiteConnectedWithDeleted(i);
				return;
			}
		}
	}
}
