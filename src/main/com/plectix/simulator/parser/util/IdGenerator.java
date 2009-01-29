package com.plectix.simulator.parser.util;

public class IdGenerator {
	private long id = 0;
	
	public IdGenerator() {
		
	}
	
	public IdGenerator(long start) {
		id = start;
	}

	public long generateNextAgentId() {
		return id++;
	}
	
	public void reset() {
		id = 0;
	}
	
	//TODO remove
	public long check() {
		return id;
	}

	public void shift(long i) {
		// TODO Auto-generated method stub
		id += i;
	}
}
