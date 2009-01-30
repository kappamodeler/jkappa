package com.plectix.simulator.parser.builders;

import java.util.*;

import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.ObservablesConnectedComponent;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IObservables;
import com.plectix.simulator.interfaces.IObservablesComponent;
import com.plectix.simulator.interfaces.IObservablesConnectedComponent;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.parser.abstractmodel.AbstractAgent;
import com.plectix.simulator.parser.abstractmodel.AbstractObservableRule;
import com.plectix.simulator.parser.abstractmodel.AbstractObservables;
import com.plectix.simulator.parser.abstractmodel.ObservableComponentLineData;
import com.plectix.simulator.parser.abstractmodel.ObservableRuleLineData;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationUtils;
import com.plectix.simulator.util.Converter;

public class ObservablesBuilder {
	private final SimulationArguments myArguments;
	private IObservables done;
	private final SubstanceBuilder mySubstanceBuilder;
	
	public ObservablesBuilder(SimulationData data, SimulationArguments args) {
		myArguments = args;
		done = data.getObservables();
		mySubstanceBuilder = new SubstanceBuilder(data);
	}
	
	public IObservables build(AbstractObservables arg, List<IRule> rules) {
//		CObservables observables = new CObservables();
		IObservables observables = done;
		
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
