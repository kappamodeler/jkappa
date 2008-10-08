package com.plectix.simulator.components;

import java.util.List;

public class CStories extends CObservables{

	private int numberOfSimulations;

	private int ruleID;

	List<Integer> ways;

	public CStories(){
		
	}
	
	public CStories(int numberOfSimulations, int ruleID) {
		this.numberOfSimulations = numberOfSimulations;
		this.ruleID = ruleID;
	}

}
