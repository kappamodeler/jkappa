package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.SimulationMain;

public class CStories extends CObservables {

	public static int numberOfSimulations = 10;

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

	public void handling() {
		List<List<Integer>> significantWays = new ArrayList<List<Integer>>();
		for (int i = 0; i < numberOfSimulations; i++) {
			significantWays.add(getSignificantRules(this.ways.get(i)));
		}
		this.significantWays = significantWays;
	}

	// private double

	private List<Integer> getSignificantRules(List<Integer> ruleWay) {
		List<Integer> signRules = new ArrayList<Integer>();
		int k = ruleWay.size();
		int indexOfCheckingRule = ruleID;
		signRules.add(indexOfCheckingRule);
		if (k == 1) {
			return signRules;
		}

		int indexOfCurrentRule;
		for (int i = k - 2; i >= 0; i--) {
			indexOfCurrentRule = ruleWay.get(i);
			if (isRuleActivate(indexOfCheckingRule, indexOfCurrentRule)) {
				if (signRules.contains(indexOfCurrentRule))
					continue;
				signRules.add(indexOfCurrentRule);
				// indexOfCurrentRule = indexOfCheckingRule;
				indexOfCheckingRule = indexOfCurrentRule;
			}
		}
		return signRules;
	}

	private boolean isRuleActivate(int indexOfCheckingRule,
			int indexOfCurrentRule) {
		CRule currentRule = SimulationMain.getSimulationManager().getRules()
				.get(indexOfCurrentRule);
		// List<CRule> listRules =
		// SimulationMain.getSimulationManager().getRules
		// ().get(indexOfCheckingRule).getActivatedRule();
		List<CRule> listRules = currentRule.getActivatedRule();

		// for(CRule rule : listRules)
		// if (rule==currentRule)
		// return true;
		for (CRule rule : listRules)
			if (rule.getRuleID() == indexOfCheckingRule)
				return true;

		return false;
	}

}
