package com.plectix.simulator.components;

import java.util.List;
import java.util.Random;

public class CProbabilityCalculation {
	private List<CRule> rules;
	private double commonActivity;
	private double[] rulesProbability;

	public CProbabilityCalculation(List<CRule> rules) {
		this.rules = rules;
		rulesProbability = new double[rules.size()];
	}

	private void calculateRulesActivity() {
		for (CRule rule : rules)
			rule.calcultateActivity();
	}

	private void calculateProbability() {
		rulesProbability[0] = rules.get(0).getActivity() / commonActivity;
		for (int i = 1; i < rulesProbability.length; i++) {
			rulesProbability[i] = rulesProbability[i - 1]
					+ rules.get(i).getActivity() / commonActivity;
		}
	}

	private void recalculateCommonActivity() {
		commonActivity = 0.;
		for (CRule rule : rules) {
			commonActivity += rule.getActivity();
		}
	}

	private int getRandomIndex() {
		
		for (int i = 0; i < rulesProbability.length; i++) {
			if (rules.get(i).isInfinityRate() && rulesProbability[i]!=0)
				return i;
		}
		
		Random rand = new Random();
		double randomValue = rand.nextDouble();

		for (int i = 0; i < rulesProbability.length; i++) {
			if (randomValue < rulesProbability[i])
				return i;
		}
		return -1;
	}

	public CRule getRandomRule() {
		calculateRulesActivity();
		recalculateCommonActivity();
		calculateProbability();
		int index = getRandomIndex();
		if (index == -1)
			return null;
		return rules.get(index);
	}

	public double getTimeValue() {
		Random rand = new Random();
		double randomValue = rand.nextDouble();

		while (randomValue == 0.0)
			randomValue = rand.nextDouble();

		return -1. / commonActivity * java.lang.Math.log(rand.nextDouble());
	}

}
