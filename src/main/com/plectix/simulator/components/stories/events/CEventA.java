package com.plectix.simulator.components.stories.events;

class CEventA extends AEvent{
	public CEventA() {
		super(EMPTY_SETP);
	}

	public boolean isCausing() {
		return false;
	}
}
