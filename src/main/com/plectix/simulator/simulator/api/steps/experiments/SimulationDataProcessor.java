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

/**
 * This class provides convenience methods to make changes to the initial Kappa Model.
 * <br><br>
 * One can implement <code>updateInitialModel</code> method in order to make changes
 * to the initial Kappa Model using these convenience methods.
 * 
 */
public abstract class SimulationDataProcessor {
	private static final PlxLogger LOGGER = ThreadLocalData.getLogger(SimulationDataProcessor.class);
	
	private final SimulationData simulationData;
	
	public SimulationDataProcessor(Simulator simulator) {
		this.simulationData = simulator.getSimulationData();
	}

	public abstract void updateInitialModel() throws IncompletesDisabledException, SimulationDataFormatException;
	
	protected final boolean setRuleRate(RulePattern pattern, double rate) { 
		ModelRule modelRule = this.getInitialModel().getRuleByPattern(pattern);
		if (setRuleRate(modelRule, rate) == false) {
			if (LOGGER.isEnabledFor(Level.WARN)) {
				LOGGER.warn("No rule matching pattern '" + pattern + "' is found. Could not set the rate.");
			}
			return false;
		}
		return true;
	}
	
	protected final boolean setRuleRate(String ruleName, double rate) {
		ModelRule modelRule = this.getInitialModel().getRuleByName(ruleName);
		if (setRuleRate(modelRule, rate) == false) {
			if (LOGGER.isEnabledFor(Level.WARN)) {
				LOGGER.warn("There's no rule with the name '" + ruleName + "'. Could not set the rate.");
			}
			return false;
		}
		return true;
	}
	
	private final boolean setRuleRate(ModelRule modelRule, double rate) {
		if (modelRule == null) {
			return false;
		}
		modelRule.setRate(rate);
		return true;
	}
	
	protected final boolean incrementRuleRate(RulePattern pattern, double additionalRate) { 
		ModelRule modelRule = this.getInitialModel().getRuleByPattern(pattern);
		if (incrementRuleRate(modelRule, additionalRate) == false) {
			if (LOGGER.isEnabledFor(Level.WARN)) {
				LOGGER.warn("No rule matching pattern '" + pattern + "' is found. Could not increment the rate.");
			}
			return false;
		}
		return true;
	}

	protected final boolean incrementRuleRate(String ruleName, double additionalRate) { 
		ModelRule modelRule = this.getInitialModel().getRuleByName(ruleName);
		if (incrementRuleRate(modelRule, additionalRate) == false) {
			if (LOGGER.isEnabledFor(Level.WARN)) {
				LOGGER.warn("There's no rule with the name '" + ruleName + "'. Could not increment the rate.");
			}
			return false;
		}
		return true;
	}
	
	private final boolean incrementRuleRate(ModelRule modelRule, double additionalRate) { 
		if (modelRule == null) {
			return false;
		}
		modelRule.setRate(modelRule.getRate() + additionalRate);
		return true;
	}
	
	
	protected final void removeInitialCondition(String connectedComponents) {
		ModelSolution solution = this.getInitialModel().getSolution();
		ModelSolutionManager solutionManager = new ModelSolutionManager(solution);
		solutionManager.removeSubstance(connectedComponents);
	}
	
	protected final void addInitialCondition(String connectedComponent, int count) 
				throws IncompletesDisabledException, SimulationDataFormatException {
		List<ModelAgent> agents = ModelParseHelper.readAgents(true, connectedComponent);
		this.getInitialModel().getSolution().addAgents(count, agents);
	}
	
	protected final void changeInitialCondition(String connectedComponents, int newCount) 
				throws IncompletesDisabledException, SimulationDataFormatException {
		this.removeInitialCondition(connectedComponents);
		this.addInitialCondition(connectedComponents, newCount);
	}
	
	private final KappaModel getInitialModel() {
		return simulationData.getInitialModel();
	}
	
	public final SimulationData getSimulationData() {
		return simulationData;
	}
	
	
}
