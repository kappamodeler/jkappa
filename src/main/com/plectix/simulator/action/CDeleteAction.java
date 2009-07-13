package com.plectix.simulator.action;

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
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.simulator.SimulationData;

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

	public final void doAction(RuleApplicationPool pool, CInjection injection,
			CEvent eventContainer,
			SimulationData simulationData) {
		/**
		 * Done.
		 */
		CAgent agent = injection.getAgentFromImageById(myFromAgent.getIdInConnectedComponent());
		addToEventContainer(eventContainer, agent, EActionOfAEvent.TEST_AND_MODIFICATION);
		addToEventContainerNotFixedSites(eventContainer, agent);
		for (CSite site : agent.getSites()) {
			removeSiteToConnectedWithDeleted(site);
			CSite solutionSite = (CSite) site.getLinkState().getConnectedSite();

			if (solutionSite != null) {
//				addToNetworkNotation(StateType.BEFORE,
//						netNotation, solutionSite);

				addToEventContainerConnectedSites(eventContainer, solutionSite,
						CEvent.BEFORE_STATE);
				addSiteToConnectedWithDeleted(solutionSite);
				solutionSite.getLinkState().connectSite(null);
				solutionSite.getLinkState().setStatusLink(
						CLinkStatus.FREE);
				solutionSite.setLinkIndex(-1);
//				addToNetworkNotation(StateType.AFTER,
//						netNotation, solutionSite);
//				addRuleSitesToNetworkNotation(false, netNotation, solutionSite);

				addToEventContainerConnectedSites(eventContainer, solutionSite,
						CEvent.AFTER_STATE);
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
//			addToNetworkNotation(StateType.BEFORE, netNotation,
//					site);
//			addRuleSitesToNetworkNotation(true, netNotation, site);
			for (CLiftElement lift : site.getLift()) {
				site.clearIncomingInjections(lift.getInjection());
				lift.getInjection().getConnectedComponent().removeInjection(
						lift.getInjection());
			}
			site.clearLifts();
			injection.removeSiteFromSitesList(site);
		}
		// injection.getConnectedComponent().getInjectionsList()
		// .remove(injection);

		pool.removeAgent(agent);
	}

	private void addToEventContainerConnectedSites(
			CEvent eventContainer, CSite siteFromSolution,
			boolean isBefore) {
		if (eventContainer == null)
			return;
		long agentId = siteFromSolution.getAgentLink().getId();
		int siteId = siteFromSolution.getNameId();

//		eventContainer.addAtomicEvent(new WireHashKey(agentId, ETypeOfWire.AGENT),
//				null, EActionOfAEvent.MODIFICATION, isBefore);

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
		// eventContainer.addEvent(new UHashKey(agentId,EKeyOfState.AGENT),
		// null, ECheck.MODIFICATION, )
		for (CSite siteFromSolution : agentFromInSolution.getSites()) {
			int siteId = siteFromSolution.getNameId();
//			if (myFromAgent.getSiteById(siteId) != null) {
//				if (myFromAgent.getSiteById(siteId).getInternalState()
//						.getNameId() == CInternalState.EMPTY_STATE.getNameId())
//					eventContainer.addEvent(new WireHashKey(agentId, siteId,
//							EKeyOfState.INTERNAL_STATE), siteFromSolution,
//							ECheck.MODIFICATION, CEventContainer.BEFORE_STATE);
//
//				continue;
//			}
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

		// if (eventContainer != null) {
		// // AGENT
		// AEvent<Boolean> event = (AEvent<Boolean>) eventContainer
		// .getEvent(new UHashKey(agentFromInSolution.getId(),
		// EKeyOfState.AGENT));
		// event.correctingType(ECheck.MODIFICATION);
		// for (CSite s : agentFromInSolution.getSites()) {
		// if(getAgentFrom().getSiteById(s.getNameId()) != null)
		// continue;
		// CSite site =s; //agentFromInSolution.getSiteById(s.getNameId());
		// CLinkRank linkRank = s.getLinkState().getStatusLinkRank();
		// if (linkRank != CLinkRank.BOUND_OR_FREE) {
		// // FREE/BOUND
		// AEvent<Boolean> event2 = new AEvent<Boolean>(
		// eventContainer, ECheck.MODIFICATION);
		// AState<Boolean> state2 = new AState<Boolean>();
		// state2.setBeforeState(linkRank.equals(CLinkRank.FREE));
		// event2.setState(state2);
		// eventContainer.addEvent(
		// new UHashKey(agentFromInSolution.getId(), site
		// .getNameId(), EKeyOfState.BOUND_FREE),
		// event2);
		//
		// if (linkRank != CLinkRank.SEMI_LINK) {
		// AEvent<CStateOfLink> event3 = new AEvent<CStateOfLink>(
		// eventContainer, ECheck.MODIFICATION);
		// AState<CStateOfLink> state3 = new AState<CStateOfLink>();
		// if (linkRank == CLinkRank.FREE)
		// state3.setBeforeState(new CStateOfLink(
		// CStateOfLink.FREE, CStateOfLink.FREE));
		// else
		// state3.setBeforeState(new CStateOfLink(site
		// .getLinkState().getConnectedSite()
		// .getAgentLink().getId(), site
		// .getLinkState().getConnectedSite()
		// .getNameId()));
		// event3.setState(state3);
		// eventContainer.addEvent(new UHashKey(
		// agentFromInSolution.getId(), site.getNameId(),
		// EKeyOfState.LINK_STATE), event3);
		// }
		// }
		//
		// eventContainer.addEvent(new UHashKey(agentFromInSolution
		// .getId(), site.getNameId(),
		// EKeyOfState.INTERNAL_STATE), agentFromInSolution, s,
		// ECheck.MODIFICATION, true);
		// // if (s.getInternalState().getNameId() !=
		// // CInternalState.EMPTY_STATE
		// // .getNameId()) {
		// // AEvent<Integer> event2 = new AEvent<Integer>(
		// // eventContainer, ECheck.MODIFICATION);
		// // AState<Integer> state2 = new AState<Integer>();
		// // state2.setBeforeState(s.getInternalState().getNameId());
		// // event2.setState(state2);
		// // eventContainer.addEvent(new UHashKey(agentFromInSolution
		// // .getId(), site.getNameId(),
		// // EKeyOfState.INTERNAL_STATE), event2);
		// // }
		//
		// }
		// }
	}

	/**
	 * Util method. Uses for fill {@link CRule#addSiteConnectedWithBroken(CSite)}.
	 * @param checkedSite given site.
	 */
	private final void addSiteToConnectedWithDeleted(CSite checkedSite) {
		for (CSite site : myRule.getSitesConnectedWithDeleted()) {
			if (site == checkedSite) {
				return;
			}
		}
		myRule.addSiteConnectedWithDeleted(checkedSite);
	}

	/**
	 * Util method. Uses for removed <b>checkSite</b> from util list in rule.
	 * @param checkedSite given site
	 */
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
