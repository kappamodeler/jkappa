package com.plectix.simulator.parser.abstractmodel.observables;

public abstract class ObservablesLineData  {
	private final int id;
	
	public ObservablesLineData(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}
