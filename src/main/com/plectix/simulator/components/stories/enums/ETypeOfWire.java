package com.plectix.simulator.components.stories.enums;

public enum ETypeOfWire {
	AGENT(1),
	INTERNAL_STATE(2),
	LINK_STATE(3),
	BOUND_FREE(4);

	private int myId = -1;

	private ETypeOfWire(int id) {
		myId = id;
	}
	
	public int getId() {
		return myId;
	}

}
