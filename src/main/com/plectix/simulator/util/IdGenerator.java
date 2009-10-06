package com.plectix.simulator.util;

public final class IdGenerator {
	private long id = 0;
	
	public final long generateNext() {
		return id++;
	}
	
	public final void reset() {
		id = 0;
	}
}
