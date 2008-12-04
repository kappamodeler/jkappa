package com.plectix.simulator.components.actions;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.Simulator;

public class CAddAction extends CAction {
	private final CRule myRule;
	private final IAgent myToAgent;
	
	public CAddAction(CRule rule, IAgent toAgent, IConnectedComponent ccR) {
		super(rule, null, toAgent, null, ccR);
		myRule = rule;
		myToAgent = toAgent;
		setType(CActionType.ADD);
		createBound();
	}

	public void doAction(IInjection injection, INetworkNotation netNotation, Simulator simulator) {
		/**
		 * Done.
		 */
		IAgent agent = new CAgent(myToAgent.getNameId(), simulator.generateNextAgentId());
		for (ISite site : myToAgent.getSites()) {
			ISite siteAdd = new CSite(site.getNameId());
			siteAdd.setInternalState(new CInternalState(site.getInternalState()
					.getStateNameId()));
			agent.addSite(siteAdd);
			addToNetworkNotation(CStoriesSiteStates.CURRENT_STATE, netNotation,
					siteAdd);
			addRuleSitesToNetworkNotation(false, netNotation, siteAdd);
		}
		getRightCComponent().addAgentFromSolutionForRHS(agent);
		simulator.getSimulationData().getSolution().addAgent(agent);

		agent.storifyAgent();
		myRule.putAgentAdd(myToAgent, agent);
		// toAgent.setIdInRuleSide(maxAgentID++);
	}

	public final void addRuleSitesToNetworkNotation(boolean existInRule,
			INetworkNotation netNotation, ISite site) {
		if (netNotation != null) {
			byte agentMode = CNetworkNotation.MODE_NONE;
			byte linkStateMode = CNetworkNotation.MODE_NONE;
			byte internalStateMode = CNetworkNotation.MODE_NONE;

			agentMode = CNetworkNotation.MODE_TEST_OR_MODIFY;
			if (site.getInternalState().getNameId() != CSite.NO_INDEX) {
				internalStateMode = CNetworkNotation.MODE_TEST_OR_MODIFY;
			}
			if (site.getLinkState().getStatusLinkRank() != CLinkState.RANK_SEMI_LINK) {
				linkStateMode = CNetworkNotation.MODE_TEST_OR_MODIFY;
			}
			
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
	
	private final void createBound() {
		for (ISite site : myToAgent.getSites()) {
			if (site.getLinkState().getSite() != null) {
				myRule.addAction(new CBoundAction(myRule, site, (site.getLinkState()
						.getSite()), null, getRightCComponent()));
			}
		}
	}
}
