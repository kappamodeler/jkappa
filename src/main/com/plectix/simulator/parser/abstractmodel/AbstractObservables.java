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
	private final List<ObservableComponentLineData> myComponents 
						= new LinkedList<ObservableComponentLineData>();
	private final List<ObservableRuleLineData> myRules = new LinkedList<ObservableRuleLineData>();
	
	public AbstractObservables() {
	}
	
	public void addComponent(
			List<AbstractAgent> list, String name, int obsId) {//, String line, boolean ocamlStyleObsName) {

		myComponents.add(new ObservableComponentLineData(list, name, obsId));
	}

	public void addRuleName(String ruleName, int obsId) {
		myRules.add(new ObservableRuleLineData(ruleName, obsId));
	}
	
	public List<ObservableRuleLineData> getRuleNames() {
		return myRules;
	}
	
	public List<ObservableComponentLineData> getComponents() {
		return myComponents;
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
	
}
