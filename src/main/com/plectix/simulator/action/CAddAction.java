package com.plectix.simulator.action;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.stories.enums.EActionOfAEvent;
import com.plectix.simulator.components.stories.enums.ETypeOfWire;
import com.plectix.simulator.components.stories.storage.CEvent;
import com.plectix.simulator.components.stories.storage.WireHashKey;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.ThreadLocalData;

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
			CEvent eventContainer,
			SimulationData simulationData) {
		/**
		 * Done.
		 */
		CAgent agent = new CAgent(myToAgent.getNameId(), simulationData
				.getKappaSystem().generateNextAgentId());

		ThreadLocalData.getTypeById().setTypeOfAgent(agent.getId(), agent.getNameId());
		if (eventContainer != null) {
			eventContainer.addAtomicEvent(new WireHashKey(agent.getId(),
					ETypeOfWire.AGENT), null, EActionOfAEvent.MODIFICATION, CEvent.AFTER_STATE);
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
//			addToNetworkNotation(StateType.AFTER, netNotation,
//					siteAdd);
//			addRuleSitesToNetworkNotation(false, netNotation, siteAdd);
			addSiteToEventContainer(eventContainer, siteAdd);
		}
		if (myToAgent.getSites().size() == 0) {
//			addToNetworkNotation(StateType.AFTER, netNotation,
//					agent.getDefaultSite());
//			addRuleSitesToNetworkNotation(false, netNotation, agent
//					.getDefaultSite());
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
		ThreadLocalData.getTypeById().setTypeOfAgent(site.getAgentLink().getId(), site.getAgentLink().getNameId());

		eventContainer.addAtomicEvent(new WireHashKey(agentId, siteId,
				ETypeOfWire.BOUND_FREE), site, EActionOfAEvent.MODIFICATION,
				CEvent.AFTER_STATE);
		// UHashKey key = new UHashKey(agentId, siteId, EKeyOfState.BOUND_FREE);
		// AEvent<Boolean> event = new AEvent<Boolean>(eventContainer,
		// ECheck.MODIFICATION);
		// AState<Boolean> state = new AState<Boolean>();
		// state.setAfterState(true);
		// event.setState(state);
		// eventContainer.addEvent(key, event);

		eventContainer.addAtomicEvent(new WireHashKey(agentId, siteId,
				ETypeOfWire.INTERNAL_STATE), site, EActionOfAEvent.MODIFICATION,
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

		eventContainer.addAtomicEvent(new WireHashKey(agentId, siteId,
				ETypeOfWire.LINK_STATE), site, EActionOfAEvent.MODIFICATION,
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
