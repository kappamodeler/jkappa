package com.plectix.simulator.action;

import com.plectix.simulator.components.*;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.stories.CStoriesSiteStates;
import com.plectix.simulator.components.stories.CNetworkNotation.NetworkNotationMode;
import com.plectix.simulator.components.stories.CStoriesSiteStates.StateType;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.SimulationData;

public class CAddAction extends CAction {
	private final CRule myRule;
	private final CAgent myToAgent;
	
	public CAddAction(CRule rule, CAgent toAgent, IConnectedComponent ccR) {
		super(rule, null, toAgent, null, ccR);
		myRule = rule;
		myToAgent = toAgent;
		setType(CActionType.ADD);
		createBound();
	}

	public void doAction(RuleApplicationPool pool, CInjection injection, 
			INetworkNotation netNotation, SimulationData simulationData) {
		/**
		 * Done.
		 */
		CAgent agent = new CAgent(myToAgent.getNameId(),
				simulationData.getKappaSystem().generateNextAgentId());
		
		for (CSite site : myToAgent.getSites()) {
			CSite siteAdd = new CSite(site.getNameId());
			siteAdd.setInternalState(new CInternalState(site.getInternalState()
					.getNameId()));
			agent.addSite(siteAdd);
			addToNetworkNotation(StateType.AFTER, netNotation,
					siteAdd);
			addRuleSitesToNetworkNotation(false, netNotation, siteAdd);
		}
		if (myToAgent.getSites().size() == 0) {
			addToNetworkNotation(StateType.AFTER, netNotation,
					agent.getDefaultSite());
			addRuleSitesToNetworkNotation(false, netNotation, agent
					.getDefaultSite());
		}
		
		getRightCComponent().addAgentFromSolutionForRHS(agent);
		pool.addAgent(agent);

		myRule.putAgentAdd(myToAgent, agent);
		// toAgent.setIdInRuleSide(maxAgentID++);
	}

	public final void addRuleSitesToNetworkNotation(boolean existInRule,
			INetworkNotation netNotation, CSite site) {
		if (netNotation != null) {
			NetworkNotationMode agentMode = NetworkNotationMode.NONE;
			NetworkNotationMode linkStateMode = NetworkNotationMode.NONE;
			NetworkNotationMode internalStateMode = NetworkNotationMode.NONE;

			agentMode = NetworkNotationMode.TEST_OR_MODIFY;
			if (site.getInternalState().getNameId() != CSite.NO_INDEX) {
				internalStateMode = NetworkNotationMode.TEST_OR_MODIFY;
			}
			if (site.getLinkState().getStatusLinkRank() != CLinkRank.SEMI_LINK) {
				linkStateMode = NetworkNotationMode.TEST_OR_MODIFY;
			}

			netNotation.addToAgentsFromRules(site, agentMode,
					internalStateMode, linkStateMode);
		}
	}

	protected final void addToNetworkNotation(StateType index,
			INetworkNotation netNotation, CSite site) {
		if (netNotation != null) {
			netNotation.addToAgents(site, new CStoriesSiteStates(index, site
					.getInternalState().getNameId()), index);
		}
	}

	private final void createBound() {
		for (CSite site : myToAgent.getSites()) {
			if (site.getLinkState().getSite() != null) {
				myRule
						.addAction(new CBoundAction(myRule, site, (site
								.getLinkState().getSite()), null,
								getRightCComponent()));
			}
		}
	}
}
