package com.plectix.simulator.probability;

import java.util.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.components.injections.*;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.*;
import com.plectix.simulator.util.Info.InfoType;

public final class CProbabilityCalculation {
	private final IRandom random;
	private double commonActivity;
	private SimulationData simulationData;

	public CProbabilityCalculation(InfoType outputType, SimulationData simulationData) {
		this.simulationData = simulationData;

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

//	public final List<CInjection> getSomeInjectionList(CRule rule) {
//		List<CInjection> list = new ArrayList<CInjection>();
//		for (IConnectedComponent cc : rule.getLeftHandSide()) {
//			CInjection inj = cc.getRandomInjection();
//			list.add(inj);
//		}
//		return list;
//	}

	// TODO move it out of here
	public final List<CInjection> chooseInjectionsForRuleApplication(CRule rule) {
		List<CInjection> list = new ArrayList<CInjection>();
		rule.preparePool(simulationData);
		for (IConnectedComponent cc : rule.getLeftHandSide()) {
			CInjection inj = cc.getRandomInjection();
			list.add(inj);
			simulationData.getKappaSystem().getSolution().addInjectionToPool(rule.getPool(), inj);
		}
		if (!rule.isClash(list)) {
			return list;
		} else {
			return null;
		}
	}
	
//	/**
//	 * This method is used on each iteration of Simulator.runStories() 
//	 * in order to get the latest information on simulation data
//	 * @param data
//	 */
//	public final void refreshSimulationInfo(SimulationData data) {
//		rules = data.getKappaSystem().getRules();
//	}
}
