package com.plectix.simulator.parser.abstractmodel.observables;

import com.plectix.simulator.parser.KappaFileLine;

public final class ObservableRuleLineData extends ObservablesLineData {
	private final String ruleName;
	private final KappaFileLine observableLine;
	
	public ObservableRuleLineData(String ruleName, int id, KappaFileLine observableLine) {
		super(id);
		this.ruleName = ruleName;
		this.observableLine = observableLine;
	}
	
	public final String getRuleName() {
		return ruleName;
	}
	
	public KappaFileLine getKappaFileLine(){
		return observableLine;
	}
	
	@Override
	public final String toString() {
		return "'" + ruleName + "'";
	}
}
