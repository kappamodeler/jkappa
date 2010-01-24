package com.plectix.simulator.parser.abstractmodel.observables;

final class ModelObservableRule {
	private final String name;
	private final int id;
	
	public ModelObservableRule(int id, String name) {
		this.name = name;
		this.id = id;
	}
	
	public final String getName() {
		return name;
	}
	
	public final int getObsId() {
		return id;
	}
}	
