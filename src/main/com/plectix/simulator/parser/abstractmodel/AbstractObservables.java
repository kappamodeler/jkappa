package com.plectix.simulator.parser.abstractmodel;

import java.util.*;

import com.plectix.simulator.components.ObservablesConnectedComponent;
import com.plectix.simulator.components.ObservablesRuleComponent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IObservables;
import com.plectix.simulator.interfaces.IObservablesComponent;
import com.plectix.simulator.interfaces.IObservablesConnectedComponent;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.simulator.SimulationUtils;

//copypaste =(
public class AbstractObservables implements IAbstractComponent {
	private final List<IObservablesConnectedComponent> connectedComponentList;
	private final List<IObservablesComponent> componentList;
	private final List<AbstractObservableRule> myObservableRules; 
	
	public AbstractObservables() {
		connectedComponentList = new ArrayList<IObservablesConnectedComponent>();
		componentList = new ArrayList<IObservablesComponent>();
		myObservableRules = new ArrayList<AbstractObservableRule>();
	}
	
	public void addConnectedComponents(
			List<IConnectedComponent> list, String name,
			String line, int nameID, boolean ocamlStyleObsName) {
		boolean unique;
		if (list.size() > 1)
			unique = false;
		else
			unique = true;
		if (ocamlStyleObsName) {
			line = SimulationUtils.printPartRule(list, ocamlStyleObsName);
		}

		for (IConnectedComponent component : list) {
			IObservablesConnectedComponent oCC = new ObservablesConnectedComponent(
					component.getAgents(), name, line, nameID, unique);
			oCC.initSpanningTreeMap();
			connectedComponentList.add(oCC);
			componentList.add(oCC);
		}
	}

	public void addRuleName(int observableId, String ruleName) {
		myObservableRules.add(new AbstractObservableRule(observableId, ruleName));
	}
	
	public List<AbstractObservableRule> getRuleNames() {
		return myObservableRules;
	}
	
//	public void addRulesName(String name, int obsRuleNameID, Collection<AbstractRule> rules) {
//		for (AbstractRule rule : rules) {
//			if ((rule.getName() != null) && (rule.getName().equals(name))) {
//				ObservablesRuleComponent obsRC = new ObservablesRuleComponent(
//						rule, obsRuleNameID);
//				componentList.add(obsRC);
//			}
//		}
//	}

	public List<IObservablesComponent> getComponentList() {
		return componentList;
	}
	
	public List<IObservablesConnectedComponent> getConnectedComponentList() {
		return connectedComponentList;
	}

	
}
