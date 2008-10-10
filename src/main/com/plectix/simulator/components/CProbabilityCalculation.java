package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CProbabilityCalculation {
	private List<CRule> rules;
	private double commonActivity;
	private double[] rulesProbability;
	private CRandom randomOCAML;

	public CProbabilityCalculation(List<CRule> rules, int seed) {
		this.rules = rules;
		rulesProbability = new double[rules.size()];
		randomOCAML = new CRandom(seed);
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

	public final List<CInjection> getSomeInjectionList(CRule rule) {
		List<CInjection> list = new ArrayList<CInjection>();
		
		for (CConnectedComponent cc : rule.getLeftHandSide()) {
			list.add(cc.getInjectionsList().get(
					randomOCAML.getInteger(cc.getInjectionsList().size())));
		}
		return list;
	}
	
	/*public final List<CInjection> getSomeInjectionList() {
		List<CInjection> list = new ArrayList<CInjection>();
		Random rand = new Random();

		for (CConnectedComponent cc : this.leftHandSide) {
			list.add(cc.getInjectionsList().get(
					rand.nextInt(cc.getInjectionsList().size())));
		}
		return list;
	}
*/	
	
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
		
		/*Random rand = new Random();
		double randomValue = rand.nextDouble();
*/
		double randomValue = randomOCAML.getDouble();
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
/*		Random rand = new Random();
		double randomValue = rand.nextDouble();
*/
		double randomValue = randomOCAML.getDouble();

		while (randomValue == 0.0)
			randomValue = randomOCAML.getDouble();

		return -1. / commonActivity * java.lang.Math.log(randomValue);
	}

}
