package com.plectix.simulator.probability;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.components.*;
import com.plectix.simulator.components.injections.*;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.*;
import com.plectix.simulator.util.Info.InfoType;

public final class CProbabilityCalculation {
	private List<CRule> rules;
	private final double[] rulesProbability;
	private final IRandom random;
	private double commonActivity;

	public CProbabilityCalculation(InfoType outputType, SimulationData simulationData) {
		this.rules = simulationData.getKappaSystem().getRules();
		rulesProbability = new double[rules.size()];

		if(!simulationData.getSimulationArguments().isShortConsoleOutput()) {
			outputType = InfoType.OUTPUT;
		}
		
		int seed = simulationData.getSimulationArguments().getSeed();
		random = ThreadLocalData.getRandom();
		random.setSeed(seed);
		simulationData.addInfo(outputType, InfoType.INFO,
				"--Seeding random number generator with given seed "
						+ Integer.valueOf(seed).toString());
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

	public final List<CInjection> getSomeInjectionList(CRule rule) {
		List<CInjection> list = new ArrayList<CInjection>();
		for (IConnectedComponent cc : rule.getLeftHandSide()) {
			CInjection inj = cc.getRandomInjection(random);
			list.add(inj);
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

	/**
	 * This method is used on each iteration of Simulator.runStories() 
	 * in order to get the latest information on simulation data
	 * @param data
	 */
	public final void refreshSimulationInfo(SimulationData data) {
		rules = data.getKappaSystem().getRules();
	}
}
