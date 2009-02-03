package com.plectix.simulator.parser.abstractmodel;

public class ObservableRuleLineData extends ObservablesLineData {
	private final String myRuleName;
	
	public ObservableRuleLineData(String ruleName, int id) {
		super(id);
		myRuleName = ruleName;
	}
	
	public String getRuleName() {
		return myRuleName;
	}
	
	
	//-----------------------toString--------------------------
	
	
	public String toString() {
		return "'" + myRuleName + "'";
	}
}
