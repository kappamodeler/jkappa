package com.plectix.simulator.parser.builders;

import java.util.List;

import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.parser.abstractmodel.observables.*;
import com.plectix.simulator.parser.util.IdGenerator;
import com.plectix.simulator.simulator.*;

public class ObservablesBuilder {
	private IObservables myExistingObservables;
	private final SubstanceBuilder mySubstanceBuilder;
	
	public ObservablesBuilder(SimulationData data) {
		KappaSystem system = data.getKappaSystem();
		myExistingObservables = system.getObservables();
		mySubstanceBuilder = new SubstanceBuilder(system);
	}
	
	public IObservables build(AbstractObservables arg, List<IRule> rules) {
		IObservables observables = myExistingObservables;
		
		for (ObservableComponentLineData componentData : arg.getComponents()) {
			List<IAgent> agentsList = mySubstanceBuilder.buildAgents(componentData.getAgents());
			String obsName = componentData.getName();
			int id = componentData.getId();
			
			List<IConnectedComponent> listCC = SimulationUtils
										.buildConnectedComponents(agentsList);
			
			observables.addConnectedComponents(listCC, obsName, componentData.getLine(), id);
		}
		
		for (ObservableRuleLineData obsRule : arg.getRuleNames()) {
			String ruleName = obsRule.getRuleName();
			int id = obsRule.getId();
			observables.addRulesName(ruleName, id, rules);
		}
		return observables;
	}
}
