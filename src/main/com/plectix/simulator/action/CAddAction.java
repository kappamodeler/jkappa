package com.plectix.simulator.action;

import com.plectix.simulator.components.*;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.stories.CStoriesSiteStates;
import com.plectix.simulator.components.stories.CNetworkNotation.NetworkNotationMode;
import com.plectix.simulator.components.stories.CStoriesSiteStates.StateType;
import com.plectix.simulator.components.stories.newVersion.CEvent;
import com.plectix.simulator.components.stories.newVersion.ECheck;
import com.plectix.simulator.components.stories.newVersion.EKeyOfState;
import com.plectix.simulator.components.stories.newVersion.WireHashKey;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.SimulationData;

/**
 * Class implements "ADD" action type.
 * @author avokhmin
 * @see CActionType
 */
@SuppressWarnings("serial")
public class CAddAction extends CAction {
	private final CRule myRule;
	private final CAgent myToAgent;

	/**
	 * Constructor of CAddAction.<br>
	 * <br>
	 * Example:<br>
	 * <code>->A(x)</code>, creates <code>ADD</code> action.<br>
	 * <code>rule</code> - rule "->A(x)";<br>
	 * <code>ccR</code> - connected component "A(x)" from right handSide;<br>
	 * <code>toAgent</code> - agent "A(x)" from right handSide;<br>
	 * other fields from extended {@link CAction} - "null" ("fromAgent", "ccL"). 
	 * 
	 * @param rule given rule
	 * @param toAgent given agent from right handSide rule
	 * @param ccR given connected component, contains <b>toAgent</b>
	 */
	public CAddAction(CRule rule, CAgent toAgent, IConnectedComponent ccR) {
		super(rule, null, toAgent, null, ccR);
		myRule = rule;
		myToAgent = toAgent;
		setType(CActionType.ADD);
		createBound();
	}

	public void doAction(RuleApplicationPool pool, CInjection injection,
			INetworkNotation netNotation, CEvent eventContainer,
			SimulationData simulationData) {
		/**
		 * Done.
		 */
		CAgent agent = new CAgent(myToAgent.getNameId(), simulationData
				.getKappaSystem().generateNextAgentId());

		if (eventContainer != null) {
			eventContainer.addEvent(new WireHashKey(agent.getId(),
					EKeyOfState.AGENT), null, ECheck.MODIFICATION, CEvent.AFTER_STATE);
			// UHashKey key = new UHashKey(agent.getId(), EKeyOfState.AGENT);
			// AEvent<Boolean> event = new AEvent<Boolean>(eventContainer,
			// ECheck.MODIFICATION);
			// AState<Boolean> state = new AState<Boolean>();
			// state.setAfterState(true);
			// event.setState(state);
			// eventContainer.addEvent(key, event);
		}
		for (CSite site : myToAgent.getSites()) {
			CSite siteAdd = new CSite(site.getNameId());
			siteAdd.setInternalState(new CInternalState(site.getInternalState()
					.getNameId()));
			agent.addSite(siteAdd);
			addToNetworkNotation(StateType.AFTER, netNotation,
					siteAdd);
			addRuleSitesToNetworkNotation(false, netNotation, siteAdd);
			addSiteToEventContainer(eventContainer, siteAdd);
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

	private static void addSiteToEventContainer(CEvent eventContainer,
			CSite site) {
		if (eventContainer == null)
			return;

		long agentId = site.getAgentLink().getId();
		int siteId = site.getNameId();
		eventContainer.addEvent(new WireHashKey(agentId, siteId,
				EKeyOfState.BOUND_FREE), site, ECheck.MODIFICATION,
				CEvent.AFTER_STATE);
		// UHashKey key = new UHashKey(agentId, siteId, EKeyOfState.BOUND_FREE);
		// AEvent<Boolean> event = new AEvent<Boolean>(eventContainer,
		// ECheck.MODIFICATION);
		// AState<Boolean> state = new AState<Boolean>();
		// state.setAfterState(true);
		// event.setState(state);
		// eventContainer.addEvent(key, event);

		eventContainer.addEvent(new WireHashKey(agentId, siteId,
				EKeyOfState.INTERNAL_STATE), site, ECheck.MODIFICATION,
				CEvent.AFTER_STATE);
		// if(site.getInternalState().getNameId() !=
		// CInternalState.EMPTY_STATE.getNameId()){
		// key = new UHashKey(agentId,siteId,EKeyOfState.INTERNAL_STATE);
		// AEvent<Integer> event2 = new
		// AEvent<Integer>(eventContainer,ECheck.MODIFICATION);
		// AState<Integer> state2 = new AState<Integer>();
		// state2.setAfterState(site.getInternalState().getNameId());
		// event2.setState(state2);
		// eventContainer.addEvent(key, event2);
		// }

		eventContainer.addEvent(new WireHashKey(agentId, siteId,
				EKeyOfState.LINK_STATE), site, ECheck.MODIFICATION,
				CEvent.AFTER_STATE);
		// key = new UHashKey(agentId, siteId, EKeyOfState.LINK_STATE);
		// AEvent<CStateOfLink> event3 = new
		// AEvent<CStateOfLink>(eventContainer,
		// ECheck.MODIFICATION);
		// AState<CStateOfLink> state3 = new AState<CStateOfLink>();
		// state3.setAfterState(new CStateOfLink(CStateOfLink.FREE,
		// CStateOfLink.FREE));
		// event3.setState(state3);
		// eventContainer.addEvent(key, event3);

	}

	protected final void addRuleSitesToNetworkNotation(boolean existInRule,
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

	/**
	 * Util method. Find and add "BOUND" action with current add agent.
	 */
	private final void createBound() {
		for (CSite site : myToAgent.getSites()) {
			if (site.getLinkState().getConnectedSite() != null) {
				myRule
						.addAction(new CBoundAction(myRule, site, (site
								.getLinkState().getConnectedSite()), null,
								getRightCComponent()));
			}
		}
	}
}
