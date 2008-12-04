package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.IRandom;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.simulator.SimulationData;

public final class CProbabilityCalculation {
	private final List<IRule> rules;
	private final double[] rulesProbability;
	private final IRandom random;
	private double commonActivity;

	public CProbabilityCalculation(SimulationData simulationData) {
		this.rules = simulationData.getRules();
		rulesProbability = new double[rules.size()];

		String randomizerPatch = simulationData.getRandomizer();
		if (randomizerPatch == null)
			random = new CRandomJava(simulationData);
		else
			random = new CRandomOCaml(randomizerPatch, simulationData.getSeed());

	}

	private final void calculateRulesActivity() {
		for (IRule rule : rules)
			rule.calcultateActivity();
	}

	private final void calculateProbability() {
		rulesProbability[0] = rules.get(0).getActivity() / commonActivity;
		for (int i = 1; i < rulesProbability.length; i++) {
			rulesProbability[i] = rulesProbability[i - 1]
					+ rules.get(i).getActivity() / commonActivity;
		}
	}

	public final List<IInjection> getSomeInjectionList(IRule rule) {
		List<IInjection> list = new ArrayList<IInjection>();
		for (IConnectedComponent cc : rule.getLeftHandSide()) {
			list.add(cc.getRandomInjection(random));
		}
		return list;
	}

	private final void recalculateCommonActivity() {
		commonActivity = 0.;
		for (IRule rule : rules) {
			commonActivity += rule.getActivity();
		}
	}

	private final int getRandomIndex() {

		for (int i = 0; i < rulesProbability.length; i++) {
			if (rules.get(i).isInfinityRate() && (rules.get(i).getActivity()>0.0) 
					&& (!(rules.get(i).isClashForInfiniteRule())))
				return i;
		}

		double randomValue = random.getDouble();
		for (int i = 0; i < rulesProbability.length; i++) {
			if (randomValue < rulesProbability[i])
				return i;
		}
		return -1;
	}

	public final IRule getRandomRule() {
		calculation();
		int index = getRandomIndex();
		if (index == -1)
			return null;
		return rules.get(index);
	}

	public final void calculation() {
		calculateRulesActivity();
		recalculateCommonActivity();
		calculateProbability();
	}

	public final double getTimeValue() {
		double randomValue = random.getDouble();

		while (randomValue == 0.0)
			randomValue = random.getDouble();

		return -1. / commonActivity * java.lang.Math.log(randomValue);
	}

}
