package com.plectix.simulator.simulator.api.steps;

import java.util.Set;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;

public class DeadRuleDetectionOperation extends AbstractOperation<Set<Integer>> {
	private final KappaSystem kappaSystem;
	
	public DeadRuleDetectionOperation(KappaSystem kappaSystem) {
		super(kappaSystem.getSimulationData(), OperationType.DEAD_RULE_DETECTION);
		this.kappaSystem = kappaSystem;
	}
	
	/**
	 * Returns a set of dead rules' ids 
	 * @param simulator
	 * @return
	 */
	protected Set<Integer> performDry() {
//		AllSubViewsOfAllAgentsInterface subViews = simulator.getSimulationData().getKappaSystem().getSubViews();
		
		AllSubViewsOfAllAgentsInterface subViews = kappaSystem.getSubViews();
		subViews.initDeadRules();
		return subViews.getDeadRules();
	}

}
