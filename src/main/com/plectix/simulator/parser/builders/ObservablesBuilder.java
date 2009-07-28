package com.plectix.simulator.parser.builders;

import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.parser.abstractmodel.observables.AbstractObservables;
import com.plectix.simulator.parser.abstractmodel.observables.ObservableComponentLineData;
import com.plectix.simulator.parser.abstractmodel.observables.ObservableRuleLineData;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationUtils;

public class ObservablesBuilder {
	private CObservables myExistingObservables;
	private final SubstanceBuilder mySubstanceBuilder;
	
	public ObservablesBuilder(SimulationData data) {
		KappaSystem system = data.getKappaSystem();
		myExistingObservables = system.getObservables();
		mySubstanceBuilder = new SubstanceBuilder(system);
	}
	
	public CObservables build(AbstractObservables arg, List<CRule> rules) {
		CObservables observables = myExistingObservables;
		
		for (ObservableComponentLineData componentData : arg.getComponents()) {
			List<CAgent> agentsList = mySubstanceBuilder.buildAgents(componentData.getAgents());
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
