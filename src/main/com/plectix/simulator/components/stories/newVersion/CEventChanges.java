package com.plectix.simulator.components.stories.newVersion;

class CEventChanges {
	private final EEvent before;
	private final EEvent after;
	private final CEvent event;

	public CEventChanges(CEvent event, EEvent before, EEvent after) {
		this.before = before;
		this.after = after;
		this.event = event;
	}

	public EEvent getBefore() {
		return before;
	}

	public EEvent getAfter() {
		return after;
	}
	
	public CEvent getEvent(){
		return event;
	}
	
}