package com.plectix.simulator.component.stories.storage;

import com.plectix.simulator.action.ActionObserverInteface;
import com.plectix.simulator.component.Agent;
import com.plectix.simulator.component.InternalState;
import com.plectix.simulator.component.Link;
import com.plectix.simulator.component.LinkRank;
import com.plectix.simulator.component.Site;
import com.plectix.simulator.component.stories.ActionOfAEvent;
import com.plectix.simulator.component.stories.State;
import com.plectix.simulator.component.stories.TypeOfWire;

public class EventBuilder implements ActionObserverInteface {
	private StoriesAgentTypesStorage typeById;
	private Event event;

	public EventBuilder() {
	}

	public void addAtomicEvent(WireHashKey key, Site site, ActionOfAEvent type,
			boolean isBefore) {
		switch (key.getTypeOfWire()) {
		case AGENT:
			addEventAgent(key, type, isBefore);
			break;
		case BOUND_FREE:
			addEventBoundFree(key, site.getLinkState(), type, isBefore);
			break;
		case INTERNAL_STATE:
			addEventInternalState(key, site.getInternalState(), type, isBefore);
			break;
		case LINK_STATE:
			addEventLinkState(key, site.getLinkState(), type, isBefore);
			break;
		}
	}

	@SuppressWarnings("unchecked")
	protected void addEventLinkState(WireHashKey key, Link linkState,
			ActionOfAEvent type, boolean isBefore) {
		AtomicEvent<StateOfLink> aEvent = (AtomicEvent<StateOfLink>) event
				.addAtomicEvent(key, type, TypeOfWire.LINK_STATE);

		Site connectedSite = linkState.getConnectedSite();
		if (isBefore)
			if (linkState.getConnectedSite() == null)
				aEvent.getState().setBeforeState(new StateOfLink());
			else
				aEvent.getState().setBeforeState(
						new StateOfLink(connectedSite.getParentAgent().getId(),
								connectedSite.getName()));
		else if (linkState.getConnectedSite() == null)
			aEvent.getState().setAfterState(new StateOfLink());
		else
			aEvent.getState().setAfterState(
					new StateOfLink(connectedSite.getParentAgent().getId(),
							connectedSite.getName()));
	}

	@SuppressWarnings("unchecked")
	protected void addEventInternalState(WireHashKey key,
			InternalState internalState, ActionOfAEvent type, boolean isBefore) {
		if (internalState.hasDefaultName())
			return;

		AtomicEvent<String> aEvent = (AtomicEvent<String>) event
				.addAtomicEvent(key, type, TypeOfWire.BOUND_FREE);

		if (isBefore)
			aEvent.getState().setBeforeState(internalState.getName());
		else
			aEvent.getState().setAfterState(internalState.getName());
	}

	@SuppressWarnings("unchecked")
	protected void addEventBoundFree(WireHashKey key, Link linkState,
			ActionOfAEvent type, boolean isBefore) {
		AtomicEvent<State> aEvent = (AtomicEvent<State>) event.addAtomicEvent(
				key, type, TypeOfWire.BOUND_FREE);

		if (isBefore)
			if (linkState.getConnectedSite() == null)
				aEvent.getState().setBeforeState(State.FREE_LINK_STATE);
			else
				aEvent.getState().setBeforeState(State.BOUND_LINK_STATE);
		else if (linkState.getConnectedSite() == null)
			aEvent.getState().setAfterState(State.FREE_LINK_STATE);
		else
			aEvent.getState().setAfterState(State.BOUND_LINK_STATE);

	}

	@SuppressWarnings("unchecked")
	protected void addEventAgent(WireHashKey key, ActionOfAEvent type,
			boolean existsBefore) {
		AtomicEvent<State> aEvent = (AtomicEvent<State>) event.addAtomicEvent(
				key, type, TypeOfWire.AGENT);

		if (existsBefore)
			aEvent.getState().setBeforeState(State.CHECK_AGENT);
		else {
			if (aEvent.getType() != ActionOfAEvent.TEST_AND_MODIFICATION)
				aEvent.getState().setAfterState(State.CHECK_AGENT);
		}

	}

	public void addToEvent(Agent agent, ActionOfAEvent type,
			Agent agentFrom) {
		// AGENT
		long id = agent.getId();

		typeById.setTypeOfAgent(id, agent.getName());

		addAtomicEvent(new WireHashKey(id, TypeOfWire.AGENT), null, type,
				Event.BEFORE_STATE);
		for (Site agentFromSite : agentFrom.getSites()) {
			Site site = agent.getSiteByName(agentFromSite.getName());
			LinkRank linkRank = agentFromSite.getLinkState()
					.getStatusLinkRank();
			if (linkRank != LinkRank.BOUND_OR_FREE) {
				// FREE/BOUND
				addAtomicEvent(new WireHashKey(id, site.getName(),
						TypeOfWire.BOUND_FREE), site, type, Event.BEFORE_STATE);

				if (linkRank != LinkRank.SEMI_LINK) {
					addAtomicEvent(new WireHashKey(id, site.getName(),
							TypeOfWire.LINK_STATE), site, type,
							Event.BEFORE_STATE);
				}
			}

			if (!agentFromSite.getInternalState().hasDefaultName())
				addAtomicEvent(new WireHashKey(id, site.getName(),
						TypeOfWire.INTERNAL_STATE), site, type,
						Event.BEFORE_STATE);
		}
	}

	public void addSiteToEvent(Site site) {
		long id = site.getParentAgent().getId();
		long agentId = id;
		String siteName = site.getName();
		String name = site.getParentAgent().getName();

		typeById.setTypeOfAgent(id, name);

		addAtomicEvent(
				new WireHashKey(agentId, siteName, TypeOfWire.BOUND_FREE),
				site, ActionOfAEvent.MODIFICATION, Event.AFTER_STATE);

		addAtomicEvent(new WireHashKey(agentId, siteName,
				TypeOfWire.INTERNAL_STATE), site, ActionOfAEvent.MODIFICATION,
				Event.AFTER_STATE);

		addAtomicEvent(
				new WireHashKey(agentId, siteName, TypeOfWire.LINK_STATE),
				site, ActionOfAEvent.MODIFICATION, Event.AFTER_STATE);
	}

	public void boundAddToEventContainer(Site site, boolean stateFlag) {
		long id = site.getParentAgent().getId();
		String name = site.getParentAgent().getName();

		typeById.setTypeOfAgent(id, name);
		addAtomicEvent(new WireHashKey(id, site.getName(),
				TypeOfWire.LINK_STATE), site, ActionOfAEvent.MODIFICATION,
				stateFlag);

		addAtomicEvent(new WireHashKey(id, site.getName(),
				TypeOfWire.BOUND_FREE), site, ActionOfAEvent.MODIFICATION,
				stateFlag);
	}

	public void breakAddToEvent(Site site, boolean state) {
		if (site == null) {
			return;
		}
		long id = site.getParentAgent().getId();
		typeById.setTypeOfAgent(id, site.getParentAgent().getName());
		String name = site.getName();
		addAtomicEvent(new WireHashKey(id, name, TypeOfWire.LINK_STATE), site,
				ActionOfAEvent.MODIFICATION, state);

		addAtomicEvent(new WireHashKey(id, name, TypeOfWire.BOUND_FREE), site,
				ActionOfAEvent.MODIFICATION, state);
	}

	public void deleteAddToEvent(Site siteFromSolution, boolean stateFlag) {
		long agentId = siteFromSolution.getParentAgent().getId();
		String siteName = siteFromSolution.getName();

		typeById.setTypeOfAgent(agentId, siteFromSolution.getParentAgent()
				.getName());

		addAtomicEvent(
				new WireHashKey(agentId, siteName, TypeOfWire.BOUND_FREE),
				siteFromSolution, ActionOfAEvent.MODIFICATION, stateFlag);
		addAtomicEvent(
				new WireHashKey(agentId, siteName, TypeOfWire.LINK_STATE),
				siteFromSolution, ActionOfAEvent.MODIFICATION, stateFlag);
	}

	public void deleteAddNonFixedSites(Agent agent) throws StoryStorageException {
		long agentId = agent.getId();

		typeById.setTypeOfAgent(agent.getId(),
				agent.getName());

		for (Site siteFromSolution : agent.getSites()) {
			String siteName = siteFromSolution.getName();
			WireHashKey key = new WireHashKey(agentId, siteName,
					TypeOfWire.BOUND_FREE);
			addAtomicEvent(key, siteFromSolution, ActionOfAEvent.MODIFICATION,
					Event.BEFORE_STATE);
			event.getAtomicEvent(key).getState().setAfterState(null);

			key = new WireHashKey(agentId, siteName, TypeOfWire.LINK_STATE);
			addAtomicEvent(key, siteFromSolution, ActionOfAEvent.MODIFICATION,
					Event.BEFORE_STATE);
			event.getAtomicEvent(key).getState().setAfterState(null);

			key = new WireHashKey(agentId, siteName, TypeOfWire.INTERNAL_STATE);
			if (!siteFromSolution.getInternalState().hasDefaultName()) {
				addAtomicEvent(key, siteFromSolution,
						ActionOfAEvent.MODIFICATION, Event.BEFORE_STATE);
				event.getAtomicEvent(key).getState().setAfterState(null);
			}
		}
	}

	public void modifyAddSite(Site site, boolean stateFlag) {

		long id = site.getParentAgent().getId();

		typeById.setTypeOfAgent(id, site.getParentAgent().getName());
		addAtomicEvent(new WireHashKey(id, site.getName(),
				TypeOfWire.INTERNAL_STATE), site, ActionOfAEvent.MODIFICATION,
				stateFlag);
	}


	public void registerAgent(Agent agent) {
		typeById.setTypeOfAgent(agent.getId(), agent.getName());
	}


	public Event getEvent() {
		return event;
	}

	public void setNewEvent(long currentEventNumber, int ruleId) {
		event = new Event(currentEventNumber, ruleId);

	}

	public void setTypeById(StoriesAgentTypesStorage typeById) {
		this.typeById = typeById;
	}



	
	


}
