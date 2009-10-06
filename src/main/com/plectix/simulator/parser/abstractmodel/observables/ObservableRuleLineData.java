package com.plectix.simulator.parser.abstractmodel.observables;

public final class ObservableRuleLineData extends ObservablesLineData {
	private final String ruleName;
	
	public ObservableRuleLineData(String ruleName, int id) {
		super(id);
		this.ruleName = ruleName;
	}
	
	public final String getRuleName() {
		return ruleName;
	}
	
	@Override
	public final String toString() {
		return "'" + ruleName + "'";
	}
}
