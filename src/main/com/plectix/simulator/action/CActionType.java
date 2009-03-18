package com.plectix.simulator.action;

public enum CActionType {
	NONE(-1),
	BREAK(0),
	DELETE(1),
	ADD(2),
	BOUND(3),
	MODIFY(4);
//	ABSTRACT_BREAK_OR_BOUND(5),
//	ABSTRACT_BREAK_OR_BOUND_AND_MODIFY(6);
	
	private int myId = -100;
	
	private CActionType(int id) {
		myId = id;
	}
	
	public int getId() {
		return myId;
	}
	
	public static CActionType getById(int id) {
		if (id == -1) {
			return NONE;
		} else if (id == 0) {
			return BREAK;
		} else if (id == 1) {
			return DELETE;
		} else if (id == 2) {
			return ADD;
		} else if (id == 3) {
			return BOUND;
		} else if (id == 4) {
			return MODIFY;
		}
		return null;
	}
}
