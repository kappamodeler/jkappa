package com.plectix.simulator.component.stories;

public enum TypeOfWire {
	AGENT(1), INTERNAL_STATE(2), LINK_STATE(3), BOUND_FREE(4);

	private final int id;

	private TypeOfWire(int id) {
		this.id = id;
	}

	public final int getId() {
		return id;
	}
}
