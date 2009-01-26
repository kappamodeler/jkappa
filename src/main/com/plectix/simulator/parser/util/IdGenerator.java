package com.plectix.simulator.parser.util;

public class IdGenerator {
	private int id = 0;
	
	public int generateNextAgentId() {
		return id++;
	}
}
