package com.plectix.simulator.simulator.api.steps;

import java.util.Set;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;
import com.plectix.simulator.staticanalysis.subviews.MainSubViews;

public class DeadRuleDetectionOperation extends AbstractOperation {

	public DeadRuleDetectionOperation() {
		super(OperationType.DEAD_RULE_DETECTION);
	}
	
	/**
	 * Returns a set of dead rules' ids 
	 * @param simulator
	 * @return
	 */
	public Set<Integer> perform(KappaSystem kappaSystem) {
//		AllSubViewsOfAllAgentsInterface subViews = simulator.getSimulationData().getKappaSystem().getSubViews();
		
		AllSubViewsOfAllAgentsInterface subViews = kappaSystem.getSubViews();
		subViews.initDeadRules();
		return subViews.getDeadRules();
	}

}
