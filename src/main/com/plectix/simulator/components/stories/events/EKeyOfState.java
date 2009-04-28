package com.plectix.simulator.components.stories.events;

public enum EKeyOfState {
	INTERNAL_STATE(1), LINK_STATE(2);

	private int myId = -1;

	private EKeyOfState(int id) {
		myId = id;
	}
	
	public int getId() {
		return myId;
	}

}
