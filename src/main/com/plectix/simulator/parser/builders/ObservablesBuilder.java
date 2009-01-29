package com.plectix.simulator.parser.builders;

import java.util.*;

import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.interfaces.IObservables;
import com.plectix.simulator.interfaces.IObservablesComponent;
import com.plectix.simulator.interfaces.IObservablesConnectedComponent;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.parser.abstractmodel.AbstractObservableRule;
import com.plectix.simulator.parser.abstractmodel.AbstractObservables;

public class ObservablesBuilder {
	public IObservables build(AbstractObservables arg, List<IRule> rules) {
		CObservables observables = new CObservables();
		List<IObservablesConnectedComponent> connectedComponentList = arg.getConnectedComponentList();
		List<IObservablesComponent> componentList = arg.getComponentList();
		observables.setComponentList(componentList);
		observables.setConnectedComponentList(connectedComponentList);
		
		for (AbstractObservableRule obsRule : arg.getRuleNames()) {
			String ruleName = obsRule.getName();
			int id = obsRule.getObsId();
			observables.addRulesName(ruleName, id, rules);
		}
		return observables;
	}
}
