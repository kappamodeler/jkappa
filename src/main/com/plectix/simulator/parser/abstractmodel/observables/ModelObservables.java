package com.plectix.simulator.parser.abstractmodel.observables;

import java.util.LinkedList;
import java.util.List;

import com.plectix.simulator.parser.abstractmodel.ModelAgent;

public final class ModelObservables {
	private final List<ObservableComponentLineData> componentObservables 
			= new LinkedList<ObservableComponentLineData>();
	private final List<ObservableRuleLineData> ruleObservables 
			= new LinkedList<ObservableRuleLineData>();
	
	public final void addComponent(List<ModelAgent> agents, String name, String line, int observableId) {
		componentObservables.add(new ObservableComponentLineData(agents, name, line, observableId));
	}

	public final void addRuleName(String ruleName, int ruleId) {
		ruleObservables.add(new ObservableRuleLineData(ruleName, ruleId));
	}
	
	public final List<ObservableRuleLineData> getRuleNames() {
		return ruleObservables;
	}
	
	public final List<ObservableComponentLineData> getComponents() {
		return componentObservables;
	}
	
	@Override
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		for (ObservableRuleLineData ruleData : ruleObservables) {
			sb.append("%obs: " + ruleData + "\n");
		}
		for (ObservableComponentLineData componentData : componentObservables) {
			sb.append("%obs: " + componentData + "\n");
		}
		return sb.toString();
	}
}
