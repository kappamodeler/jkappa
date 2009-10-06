package com.plectix.simulator.parser.builders;

import java.util.List;

import com.plectix.simulator.component.Agent;
import com.plectix.simulator.component.Observables;
import com.plectix.simulator.component.Rule;
import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.parser.abstractmodel.observables.ModelObservables;
import com.plectix.simulator.parser.abstractmodel.observables.ObservableComponentLineData;
import com.plectix.simulator.parser.abstractmodel.observables.ObservableRuleLineData;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationUtils;

public final class ObservablesBuilder {
	private final Observables existingObservables;
	private final SubstanceBuilder substanceBuilder;
	
	public ObservablesBuilder(SimulationData simulationData) {
		KappaSystem kappaSystem = simulationData.getKappaSystem();
		this.existingObservables = kappaSystem.getObservables();
		this.substanceBuilder = new SubstanceBuilder(kappaSystem);
	}
	
	public final Observables build(ModelObservables abstractObservables, List<Rule> rules) {
		Observables observables = existingObservables;
		for (ObservableComponentLineData componentData : abstractObservables.getComponents()) {
			List<Agent> agentsList = substanceBuilder.buildAgents(componentData.getAgents());
			String obsName = componentData.getName();
			int id = componentData.getId();
			List<ConnectedComponentInterface> listCC = SimulationUtils
										.buildConnectedComponents(agentsList);
			observables.addConnectedComponents(listCC, obsName, componentData.getLine(), id);
		}
		
		for (ObservableRuleLineData obsRule : abstractObservables.getRuleNames()) {
			String ruleName = obsRule.getRuleName();
			int id = obsRule.getId();
			observables.addRulesName(ruleName, id, rules);
		}
		return observables;
	}
}
