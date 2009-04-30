package com.plectix.simulator.components.stories.events;

import java.util.TreeMap;

public class CWire {
	private final long agentNameId;
	private final int siteNameId;
	private TreeMap<Long, AEvent> eventsMap;
	private final EKeyOfState type;

	public CWire(long agentNameId, int siteNameId,EKeyOfState type) {
		this.type = type;
		this.agentNameId = agentNameId;
		this.siteNameId = siteNameId;
		this.eventsMap = new TreeMap<Long, AEvent>();
	}
	
	public void addEvent(AEvent event){
		eventsMap.put(event.getStepId(), event);
	}

	public EKeyOfState getType() {
		return type;
	}

	public long getAgentNameId() {
		return agentNameId;
	}

	public int getSiteNameId() {
		return siteNameId;
	}
	
	public TreeMap<Long, AEvent> getEventsMap() {
		return eventsMap;
	}

}
