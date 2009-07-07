package com.plectix.simulator.components.stories.storage;

class CEventChanges {
	private final EMarkOfEvent before;
	private final EMarkOfEvent after;
	private final CEvent event;

	public CEventChanges(CEvent event, EMarkOfEvent before, EMarkOfEvent after) {
		this.before = before;
		this.after = after;
		this.event = event;
	}

	public EMarkOfEvent getBefore() {
		return before;
	}

	public EMarkOfEvent getAfter() {
		return after;
	}
	
	public CEvent getEvent(){
		return event;
	}
	
}