package com.plectix.simulator.components.contactMap;

enum ECorrelationType {
	CORRELATION_LHS_AND_RHS(-1), CORRELATION_LHS_AND_SOLUTION(0);

	private int myId = -100;

	private ECorrelationType(int id) {
		myId = id;
	}

	public int getId() {
		return myId;
	}

	public static ECorrelationType getById(int id) {
		if (id == -1) {
			return CORRELATION_LHS_AND_RHS;
		} else if (id == 0) {
			return CORRELATION_LHS_AND_SOLUTION;
		} 
		return null;
	}
}
