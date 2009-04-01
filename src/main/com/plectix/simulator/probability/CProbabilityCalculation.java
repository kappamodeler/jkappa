package com.plectix.simulator.probability;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.IRandom;

import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.util.Info.InfoType;

public final class CProbabilityCalculation {
	private final List<CRule> rules;
	private final double[] rulesProbability;
	private final IRandom random;
	private double commonActivity;

	public CProbabilityCalculation(InfoType outputType,SimulationData simulationData) {
		this.rules = simulationData.getKappaSystem().getRules();
		rulesProbability = new double[rules.size()];

		String randomizerPatch = simulationData.getSimulationArguments().getRandomizer();
		if(!simulationData.getSimulationArguments().isShortConsoleOutput())
			outputType = InfoType.OUTPUT;
		
		if (randomizerPatch == null)
			random = new CRandomJava(outputType,simulationData);
		else
			random = new CRandomOCaml(randomizerPatch, simulationData.getSimulationArguments().getSeed());

	}

	private final void calculateRulesActivity() {
		for (CRule rule : rules)
			rule.calcultateActivity();
	}

	private final void calculateProbability() {
		rulesProbability[0] = rules.get(0).getActivity() / commonActivity;
		for (int i = 1; i < rulesProbability.length; i++) {
			rulesProbability[i] = rulesProbability[i - 1]
					+ rules.get(i).getActivity() / commonActivity;
		}
	}

	public final List<IInjection> getSomeInjectionList(CRule rule) {
		List<IInjection> list = new ArrayList<IInjection>();
		for (IConnectedComponent cc : rule.getLeftHandSide()) {
			list.add(cc.getRandomInjection(random));
		}
		return list;
	}

	private final void recalculateCommonActivity() {
		commonActivity = 0.;
		for (CRule rule : rules) {
			commonActivity += rule.getActivity();
		}
	}

	private final int getRandomIndex() {

		for (int i = 0; i < rulesProbability.length; i++) {
			if (rules.get(i).isInfiniteRated() && (rules.get(i).getActivity()>0.0) 
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

	public final CRule getRandomRule() {
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
