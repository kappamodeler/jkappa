package com.plectix.simulator.components.stories.newVersion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CSite;

public class CEvent {
	public final static boolean BEFORE_STATE = true;
	public final static boolean AFTER_STATE = false;

	private EEvent state = null;

	
	private HashMap<WireHashKey, AtomicEvent<?>> eventsMap;
	private final long stepId;

	private final int ruleId;

	public CEvent(long stepId, int ruleId) {
		this.ruleId = ruleId;
		this.stepId = stepId;
		eventsMap = new HashMap<WireHashKey, AtomicEvent<?>>();
	}

	public void addEvent(WireHashKey key, CSite site, ECheck type,
			boolean isBefore) {
		int i = 0;
		switch (key.getKeyOfState()) {
		case AGENT:
			addEventAgent(key, type, isBefore);
			break;
		case BOUND_FREE:
			addEventBoundFree(key, site, type, isBefore);
			break;
		case INTERNAL_STATE:
			addEventInternalState(key, site, type, isBefore);
			break;
		case LINK_STATE:
			addEventLinkState(key, site, type, isBefore);
			break;
		}
	}

	@SuppressWarnings("unchecked")
	private void addEventLinkState(WireHashKey key, CSite site, ECheck type,
			boolean isBefore) {
		AtomicEvent<CStateOfLink> event = (AtomicEvent<CStateOfLink>) eventsMap.get(key);
		if (event == null) {
			event = new AtomicEvent<CStateOfLink>(this, type);
			eventsMap.put(key, event);
		} else
			event.correctingType(type);

		CSite connectedSite = site.getLinkState().getConnectedSite();
		if (isBefore)
			if (site.getLinkState().getConnectedSite() == null)
				event.getState().setBeforeState(new CStateOfLink());
			else
				event.getState().setBeforeState(
						new CStateOfLink(connectedSite.getAgentLink().getId(),
								connectedSite.getNameId()));
		else if (site.getLinkState().getConnectedSite() == null)
			event.getState().setAfterState(new CStateOfLink());
		else
			event.getState().setAfterState(
					new CStateOfLink(connectedSite.getAgentLink().getId(),
							connectedSite.getNameId()));
	}

	@SuppressWarnings("unchecked")
	private void addEventInternalState(WireHashKey key, CSite site,
			ECheck type, boolean isBefore) {
		if (site.getInternalState().getNameId() == CInternalState.EMPTY_STATE
				.getNameId())
			return;

		AtomicEvent<Integer> event = (AtomicEvent<Integer>) eventsMap.get(key);
		if (event == null) {
			event = new AtomicEvent<Integer>(this, type);
			eventsMap.put(key, event);
		} else
			event.correctingType(type);

		if (isBefore)
			event.getState()
					.setBeforeState(site.getInternalState().getNameId());
		else
			event.getState().setAfterState(site.getInternalState().getNameId());
	}

	@SuppressWarnings("unchecked")
	private void addEventBoundFree(WireHashKey key, CSite site, ECheck type,
			boolean isBefore) {
		AtomicEvent<EState> event = (AtomicEvent<EState>) eventsMap.get(key);
		if (event == null) {
			event = new AtomicEvent<EState>(this, type);
			eventsMap.put(key, event);
		} else
			event.correctingType(type);

		if (isBefore)
			if (site.getLinkState().getConnectedSite() == null)
				event.getState().setBeforeState(EState.FREE_LINK_STATE);
			else
				event.getState().setBeforeState(EState.BOUND_LINK_STATE);
		else if (site.getLinkState().getConnectedSite() == null)
			event.getState().setAfterState(EState.FREE_LINK_STATE);
		else
			event.getState().setAfterState(EState.BOUND_LINK_STATE);

	}

	@SuppressWarnings("unchecked")
	private void addEventAgent(WireHashKey key, ECheck type, boolean isBefore) {
		AtomicEvent<EState> event = (AtomicEvent<EState>) eventsMap.get(key);
		if (event == null) {
			event = new AtomicEvent<EState>(this, type);
			eventsMap.put(key, event);
		} else
			event.correctingType(type);

		if (isBefore)
			event.getState().setBeforeState(EState.CHECK_AGENT);
		else
			event.getState().setAfterState(EState.CHECK_AGENT);

	}

	public long getStepId() {
		return stepId;
	}

	public int getRuleId() {
		return ruleId;
	}

	public HashMap<WireHashKey, AtomicEvent<?>> getAtomicEvents() {
		return eventsMap;
	}

	@SuppressWarnings("unchecked")
	public void clearsLinkStates() {
		List<WireHashKey> listForDel = null;
		for (Map.Entry entry : eventsMap.entrySet()) {
			WireHashKey key = (WireHashKey) entry.getKey();
			if (key.getKeyOfState() != EKeyOfState.LINK_STATE)
				continue;
			AtomicEvent<CStateOfLink> event = (AtomicEvent<CStateOfLink>) entry
					.getValue();
			if (event.getType() != ECheck.TEST)
				continue;
			if (event.getState().getBeforeState().isFree()) {
				if (listForDel == null)
					listForDel = new ArrayList<WireHashKey>();
				listForDel.add(key);
			}

		}
		
		if(listForDel!=null)
			for(WireHashKey key : listForDel)
				eventsMap.remove(key);
	}

	public void setState(EEvent state){
		this.state = state;
	}
	
	public void initState() {
		// TODO init ADD action to KEPT
		this.state = EEvent.UNRESOLVED;
	}

	public EEvent getState() {
		return state;
	}
	
	public Map.Entry<WireHashKey, AtomicEvent<?>> getAtomicAction(){
//		return eventsMap.entrySet();
		return null;
	}
}
