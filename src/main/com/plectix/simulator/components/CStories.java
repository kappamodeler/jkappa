package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

public class CStories extends CObservables {

	public static int numberOfSimulations = 10;

	private int ruleID;

	List<List<Integer>> ways;

	public CStories() {

	}

	public CStories(int numberOfSimulations, int ruleID) {
		this.numberOfSimulations = numberOfSimulations;
		this.ruleID = ruleID;
	}

	public CStories(int ruleID) {
		this.ruleID = ruleID;
		ways = new ArrayList<List<Integer>>();
		for (int i = 0; i < numberOfSimulations; i++)
			ways.add(new ArrayList<Integer>());
	}

	public boolean checkRule(int checkRuleID, int number) {
		ways.get(number).add(checkRuleID);
		if (this.ruleID == checkRuleID)
			return true;
		return false;
	}
	
	public void handling(){
		
	}

}
