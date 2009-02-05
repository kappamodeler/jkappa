package com.plectix.simulator.parser.abstractmodel.observables;

public class AbstractObservableRule {
	private final String ruleName;
	private final int obsId;
	
	public AbstractObservableRule(int id, String name) {
		ruleName = name;
		obsId = id;
	}
	
	public String getName() {
		return ruleName;
	}
	
	public int getObsId() {
		return obsId;
	}
}	
