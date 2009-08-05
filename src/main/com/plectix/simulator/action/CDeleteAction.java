package com.plectix.simulator.action;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.injections.CLiftElement;
import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.stories.enums.EActionOfAEvent;
import com.plectix.simulator.components.stories.enums.ETypeOfWire;
import com.plectix.simulator.components.stories.storage.CEvent;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.components.stories.storage.WireHashKey;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.ThreadLocalData;

/**
 * Class implements "DELETE" action type.
 * @author avokhmin
 * @see CActionType
 */
@SuppressWarnings("serial")
public class CDeleteAction extends CAction {
	private final CRule myRule;
	private final CAgent myFromAgent;

	/**
	 * Constructor of CDeleteAction.<br>
	 * <br>
	 * Example:<br>
	 * <code>A(x)-></code>, creates <code>DELETE</code> action.<br>
	 * <code>fromAgent</code> - agent "A(x)" from left handSide;<br>
	 * <code>ccL</code> - connected component "A(x)" from left handSide.<br>
	 * <code>rule</code> - rule "A(x)->".<br>
	 * 
	 * @param rule given rule
	 * @param fromAgent given agent from left handSide rule
	 * @param ccL given connected component, contains <b>fromAgent</b>
	 */
	public CDeleteAction(CRule rule, CAgent fromAgent, IConnectedComponent ccL) {
		super(rule, fromAgent, null, ccL, null);
		myFromAgent = fromAgent;
		myRule = rule;
		setType(CActionType.DELETE);
	}

	@Override
	public final void doAction(RuleApplicationPool pool, CInjection injection,
			CEvent eventContainer, SimulationData simulationData) {
		
		CAgent agent = injection.getAgentFromImageById(myFromAgent.getIdInConnectedComponent());
		ThreadLocalData.getTypeById().setTypeOfAgent(agent.getId(), agent.getNameId());
		
		addToEventContainer(eventContainer, agent, EActionOfAEvent.TEST_AND_MODIFICATION);
		addToEventContainerNotFixedSites(eventContainer, agent);
		for (CSite site : agent.getSites()) {
			removeSiteToConnectedWithDeleted(site);
			CSite solutionSite = (CSite) site.getLinkState().getConnectedSite();

			if (solutionSite != null) {
				addToEventContainerConnectedSites(eventContainer, solutionSite,
						CEvent.BEFORE_STATE);
				addSiteToConnectedWithDeleted(solutionSite);
				solutionSite.getLinkState().connectSite(null);
				solutionSite.getLinkState().setStatusLink(
						CLinkStatus.FREE);
				solutionSite.setLinkIndex(-1);

				addToEventContainerConnectedSites(eventContainer, solutionSite,
						CEvent.AFTER_STATE);
			}
		}

		for (CLiftElement lift : agent.getDefaultSite().getLift()) {
			agent.getDefaultSite().clearIncomingInjections(
					lift.getInjection());
			lift.getInjection().getConnectedComponent().removeInjection(
					lift.getInjection());
		}

		for (CSite site : agent.getSites()) {
			for (CLiftElement lift : site.getLift()) {
				site.clearIncomingInjections(lift.getInjection());
				lift.getInjection().getConnectedComponent().removeInjection(
						lift.getInjection());
			}
			site.clearLifts();
			injection.removeSiteFromSitesList(site);
		}
		pool.removeAgent(agent);
	}

	private final void addToEventContainerConnectedSites(
			CEvent eventContainer, CSite siteFromSolution,
			boolean isBefore) {
		if (eventContainer == null)
			return;
		long agentId = siteFromSolution.getParentAgent().getId();
		int siteId = siteFromSolution.getNameId();

		ThreadLocalData.getTypeById().setTypeOfAgent(siteFromSolution.getParentAgent().getId(), siteFromSolution.getParentAgent().getNameId());
		
		eventContainer.addAtomicEvent(new WireHashKey(agentId, siteId,
				ETypeOfWire.BOUND_FREE), siteFromSolution, EActionOfAEvent.MODIFICATION,
				isBefore);
		eventContainer.addAtomicEvent(new WireHashKey(agentId, siteId,
				ETypeOfWire.LINK_STATE), siteFromSolution, EActionOfAEvent.MODIFICATION,
				isBefore);

	}

	private void addToEventContainerNotFixedSites(
			CEvent eventContainer, CAgent agentFromInSolution) {
		if (eventContainer == null)
			return;
		long agentId = agentFromInSolution.getId();
		ThreadLocalData.getTypeById().setTypeOfAgent(agentFromInSolution.getId(), agentFromInSolution.getNameId());

		for (CSite siteFromSolution : agentFromInSolution.getSites()) {
			int siteId = siteFromSolution.getNameId();
			try {
				WireHashKey key = new WireHashKey(agentId, siteId,ETypeOfWire.BOUND_FREE);
				eventContainer.addAtomicEvent(key, siteFromSolution,EActionOfAEvent.MODIFICATION, CEvent.BEFORE_STATE);
				eventContainer.getAtomicEvent(key).getState().setAfterState(null);
			
				key = new WireHashKey(agentId, siteId,	ETypeOfWire.LINK_STATE);
				eventContainer.addAtomicEvent(key, siteFromSolution,EActionOfAEvent.MODIFICATION, CEvent.BEFORE_STATE);
				eventContainer.getAtomicEvent(key).getState().setAfterState(null);
				
				key = new WireHashKey(agentId, siteId,ETypeOfWire.INTERNAL_STATE);
				if(siteFromSolution.getInternalState().getNameId() != CInternalState.EMPTY_STATE.getNameId()){
					eventContainer.addAtomicEvent(key, siteFromSolution,EActionOfAEvent.MODIFICATION, CEvent.BEFORE_STATE);
					eventContainer.getAtomicEvent(key).getState().setAfterState(null);
				}
			} catch (StoryStorageException e) {
				e.printStackTrace();
			}
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
