package com.plectix.simulator.parser.abstractmodel;

public abstract class ObservablesLineData  {
	private final int id;
	
	public ObservablesLineData(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}
