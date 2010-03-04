package com.plectix.simulator.simulator.api.steps.experiments;

import java.util.List;

import com.plectix.simulator.parser.IncompletesDisabledException;
import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.abstractmodel.ModelAgent;
import com.plectix.simulator.parser.abstractmodel.ModelRule;
import com.plectix.simulator.parser.abstractmodel.ModelSolution;
import com.plectix.simulator.parser.abstractmodel.reader.ModelParseHelper;
import com.plectix.simulator.parser.abstractmodel.util.ModelSolutionManager;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;

public abstract class SimulationDataProcessor {
	private final SimulationData simulationData;
	
	public abstract void process() throws IncompletesDisabledException, SimulationDataFormatException;
	
	public SimulationDataProcessor(Simulator simulator) {
		this.simulationData = simulator.getSimulationData();
	}
	
	protected final void setRuleRate(RulePattern pattern, double rate) { 
		ModelRule rule = this.getModel().getRuleByPattern(pattern);
		if (rule != null) {
			rule.setRate(rate);
		} else {
			System.err.println("Rule " + pattern + " was not found =(");
		}
	}
	
	protected final void setRuleRate(String ruleName, double rate) {
		ModelRule rule = this.getModel().getRuleByName(ruleName);
		if (rule != null) {
			rule.setRate(rate);
		} else {
			System.err.println("There's no rule with the name '" + ruleName + "'");
		}
	}
	
	protected final void incrementRuleRate(RulePattern pattern, double additionalRate) { 
		ModelRule modelRule = this.getModel().getRuleByPattern(pattern);
		if (modelRule != null) {
			modelRule.setRate(modelRule.getRate() + additionalRate);
		} else {
			System.err.println("Rule " + pattern + " was not found =(");
		}
	}
	
	protected final void removeInitialCondition(String connectedComponents) {
		ModelSolution solution = simulationData.getInitialModel().getSolution();
		ModelSolutionManager solutionManager = new ModelSolutionManager(solution);
		solutionManager.removeSubstance(connectedComponents);
	}
	
	protected final void addInitialCondition(String connectedComponent, int count) 
				throws IncompletesDisabledException, SimulationDataFormatException {
		List<ModelAgent> agents = ModelParseHelper.readAgents(true, connectedComponent);
		this.getModel().getSolution().addAgents(count, agents);
	}
	
	protected final void changeInitialCondition(String connectedComponents, int newCount) 
				throws IncompletesDisabledException, SimulationDataFormatException {
		this.removeInitialCondition(connectedComponents);
		this.addInitialCondition(connectedComponents, newCount);
	}
	
	private final KappaModel getModel() {
		return simulationData.getInitialModel();
	}
	
	public SimulationData getSimulationData() {
		return simulationData;
	}
	
	
}
