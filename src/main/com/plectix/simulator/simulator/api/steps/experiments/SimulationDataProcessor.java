package com.plectix.simulator.simulator.api.steps.experiments;

import java.util.List;

import org.apache.log4j.Level;

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
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.util.io.PlxLogger;

public abstract class SimulationDataProcessor {
	private static final PlxLogger LOGGER = ThreadLocalData.getLogger(SimulationDataProcessor.class);
	
	private final SimulationData simulationData;
	
	public abstract void process() throws IncompletesDisabledException, SimulationDataFormatException;
	
	public SimulationDataProcessor(Simulator simulator) {
		this.simulationData = simulator.getSimulationData();
	}
	
	protected final boolean setRuleRate(RulePattern pattern, double rate) { 
		ModelRule rule = this.getModel().getRuleByPattern(pattern);
		if (rule != null) {
			rule.setRate(rate);
			return true;
		} else {
			if (LOGGER.isEnabledFor(Level.WARN)) {
				LOGGER.warn("No rule matching pattern '" + pattern + "' is found. Could not set the rate.");
			}
			return false;
		}
	}
	
	protected final boolean setRuleRate(String ruleName, double rate) {
		ModelRule rule = this.getModel().getRuleByName(ruleName);
		if (rule != null) {
			rule.setRate(rate);
			return true;
		} else {
			if (LOGGER.isEnabledFor(Level.WARN)) {
				LOGGER.warn("There's no rule with the name '" + ruleName + "'. Could not set the rate.");
			}
			return false;
		}
	}
	
	protected final boolean incrementRuleRate(RulePattern pattern, double additionalRate) { 
		ModelRule modelRule = this.getModel().getRuleByPattern(pattern);
		if (modelRule != null) {
			modelRule.setRate(modelRule.getRate() + additionalRate);
			return true;
		} else {
			if (LOGGER.isEnabledFor(Level.WARN)) {
				LOGGER.warn("No rule matching pattern '" + pattern + "' is found. Could not increment the rate.");
			}
			return true;
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
	
	public final SimulationData getSimulationData() {
		return simulationData;
	}
	
	
}
