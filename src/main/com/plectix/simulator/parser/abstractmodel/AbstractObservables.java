package com.plectix.simulator.parser.abstractmodel;

import java.util.*;

public class AbstractObservables implements IAbstractComponent {
	private final List<ObservableComponentLineData> myComponents 
						= new LinkedList<ObservableComponentLineData>();
	private final List<ObservableRuleLineData> myRules = new LinkedList<ObservableRuleLineData>();
	
	public AbstractObservables() {
	}
	
	public void addComponent(
			List<AbstractAgent> list, String name, String line,  int obsId) {//, String line, boolean ocamlStyleObsName) {

		myComponents.add(new ObservableComponentLineData(list, name, line, obsId));
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
	
	//-------------------toString-------------------------
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		for (ObservableRuleLineData ruleData : myRules) {
			sb.append("%obs: " + ruleData + "\n");
		}
		for (ObservableComponentLineData componentData : myComponents) {
			sb.append("%obs: " + componentData + "\n");
		}
		return sb.toString();
	}
}
