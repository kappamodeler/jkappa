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

	@Override
	public final void doAction(RuleApplicationPool pool, CInjection injection,
			CEvent eventContainer,	SimulationData simulationData) {
		
		CAgent agent = new CAgent(myToAgent.getNameId(), simulationData
				.getKappaSystem().generateNextAgentId());

		ThreadLocalData.getTypeById().setTypeOfAgent(agent.getId(), agent.getNameId());
		if (eventContainer != null) {
			eventContainer.addAtomicEvent(new WireHashKey(agent.getId(),
					ETypeOfWire.AGENT), null, EActionOfAEvent.MODIFICATION, CEvent.AFTER_STATE);
		}
		for (CSite site : myToAgent.getSites()) {
			CSite siteAdd = new CSite(site.getNameId());
			siteAdd.setInternalState(new CInternalState(site.getInternalState()
					.getNameId()));
			agent.addSite(siteAdd);
			this.addSiteToEventContainer(eventContainer, siteAdd);
		}

		getRightCComponent().addAgentFromSolutionForRHS(agent);
		pool.addAgent(agent);

		myRule.putAgentAdd(myToAgent, agent);
	}

	private final void addSiteToEventContainer(CEvent eventContainer, CSite site) {
		if (eventContainer == null)
			return;

		long agentId = site.getParentAgent().getId();
		int siteId = site.getNameId();
		ThreadLocalData.getTypeById().setTypeOfAgent(site.getParentAgent().getId(), site.getParentAgent().getNameId());

		eventContainer.addAtomicEvent(new WireHashKey(agentId, siteId,
				ETypeOfWire.BOUND_FREE), site, EActionOfAEvent.MODIFICATION,
				CEvent.AFTER_STATE);

		eventContainer.addAtomicEvent(new WireHashKey(agentId, siteId,
				ETypeOfWire.INTERNAL_STATE), site, EActionOfAEvent.MODIFICATION,
				CEvent.AFTER_STATE);

		eventContainer.addAtomicEvent(new WireHashKey(agentId, siteId,
				ETypeOfWire.LINK_STATE), site, EActionOfAEvent.MODIFICATION,
				CEvent.AFTER_STATE);
	}

	private final void createBound() {
		for (CSite site : myToAgent.getSites()) {
			if (site.getLinkState().getConnectedSite() != null) {
				myRule.addAction(new CBoundAction(myRule, 
						site, (site.getLinkState().getConnectedSite()), null, getRightCComponent()));
			}
		}
	}
}
