package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.SimulationMain;

public class CStories extends CObservables {

	public static int numberOfSimulations = 1;

	private int ruleID;

	List<List<Integer>> ways;
	List<List<Integer>> significantWays;

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
		List<List<Integer>> significantWays = new ArrayList<List<Integer>>();
		for (int i=0;i<numberOfSimulations;i++){
			significantWays.add(getSignificantRules(this.ways.get(i)));
		}
		
	}
	
	//private double 
	
	private List<Integer> getSignificantRules(List<Integer> ruleWay){
		List<Integer> signRules = new ArrayList<Integer>();
		if (ruleWay.size()<=1)
			return signRules;
		
		int indexOfCheckingRule=ruleID;
		int indexOfCurrentRule;
		for(int i=ruleWay.size()-2;i>=0;i--){
			indexOfCurrentRule = i;
			if(isRuleActivate(indexOfCheckingRule, indexOfCurrentRule)){
				signRules.add(indexOfCheckingRule);
				indexOfCurrentRule = indexOfCheckingRule;
			}
		}
		return signRules;
	}
	
	private boolean isRuleActivate(int indexOfCheckingRule, int indexOfCurrentRule){
		CRule currentRule = SimulationMain.getSimulationManager().getRules().get(indexOfCurrentRule);
		List<CRule> listRules  = SimulationMain.getSimulationManager().getRules().get(indexOfCheckingRule).getActivatedRule();
		
		for(CRule rule : listRules)
			if (rule==currentRule)
				return true;
		
		return false;
	}

}
