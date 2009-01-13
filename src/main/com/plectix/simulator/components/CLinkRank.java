package com.plectix.simulator.components;

public enum CLinkRank {
	BOUND_OR_FREE(1),
	SEMI_LINK(2),
	BOUND(3),
	FREE(4);
	
	private int myOrderNumber;
	
	private CLinkRank(int order) {
		myOrderNumber = order;
	}
	
	public boolean smaller(CLinkRank arg) {
		return myOrderNumber < arg.myOrderNumber;
	}
}
