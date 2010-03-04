package com.plectix.simulator.simulator.api.steps.experiments;

import com.plectix.simulator.parser.abstractmodel.ModelRule;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;

public class SimulationDataProcessor {
	private final SimulationData simulationData;
	
	public SimulationDataProcessor(Simulator simulator) {
		this.simulationData = simulator.getSimulationData();
	}
	
	protected final void setRuleRate(RulePattern pattern, double rate) { 
		ModelRule rule = simulationData.getInitialModel().getRuleByPattern(pattern);
		if (rule != null) {
			rule.setRate(rate);
		}
	}
	
	protected final void setRuleRate(String ruleName, double rate) {
		ModelRule rule = simulationData.getInitialModel().getRuleByName(ruleName);
		if (rule != null) {
			rule.setRate(rate);
		}
	}
	
	protected final void incrementRuleRate(RulePattern pattern, double additionalRate) { 
		ModelRule modelRule = simulationData.getInitialModel().getRuleByPattern(pattern);
		if (modelRule != null) {
			modelRule.setRate(modelRule.getRate() + additionalRate);
		} else {
			System.err.println("Rule not found =(");
		}
	}
	
	public SimulationData getSimulationData() {
		return simulationData;
	}
}
