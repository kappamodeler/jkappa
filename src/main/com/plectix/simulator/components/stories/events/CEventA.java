package com.plectix.simulator.components.stories.events;

class CEventA extends AEvent{
	private final static byte EMPTY_SETP = -1;
	
	public CEventA() {
		super(EMPTY_SETP);
	}

	public boolean isCausing() {
		return false;
	}
}
