package com.plectix.simulator.simulator.api.steps.experiments.test;

import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.api.steps.experiments.RulePattern;
import com.plectix.simulator.simulator.api.steps.experiments.SimulationDataProcessor;

public class TestSimulationDataProcessor extends SimulationDataProcessor {
	public TestSimulationDataProcessor(Simulator simulator) {
		super(simulator);
	}
	
	@Override
	public void process(int experimentNumber) {
		this.incrementRuleRate(new RulePattern("A(l), A(r) -> A(l!1), A(r!1)"), experimentNumber + 1);
	}
}
