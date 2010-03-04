package com.plectix.simulator.simulator.api.steps.experiments;

import com.plectix.simulator.parser.abstractmodel.ModelRule;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.staticanalysis.Rule;

public abstract class SimulationDataProcessor {
	private final SimulationData simulationData;
	
	public SimulationDataProcessor(Simulator simulator) {
		this.simulationData = simulator.getSimulationData();
	}
	
	public abstract void process(int experimentNumber);
	
	protected final void setRuleRate(RulePattern pattern, double rate) { 
		Rule rule = simulationData.getKappaSystem().getRuleByPattern(pattern);
		if (rule != null) {
			rule.setRuleRate(rate);
		}
	}
	
	protected final void setRuleRate(String ruleName, double rate) {
		Rule rule = simulationData.getKappaSystem().getRuleByName(ruleName);
		if (rule != null) {
			rule.setRuleRate(rate);
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
