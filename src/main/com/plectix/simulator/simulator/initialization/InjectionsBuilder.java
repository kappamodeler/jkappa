package com.plectix.simulator.simulator.initialization;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.ObservablesConnectedComponent;
import com.plectix.simulator.components.solution.SuperSubstance;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.interfaces.IObservablesConnectedComponent;

import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.interfaces.ISolutionComponent;
import com.plectix.simulator.simulator.KappaSystem;

public class InjectionsBuilder {
	private final KappaSystem myKappaSystem;
	public InjectionsBuilder(KappaSystem system) {
		myKappaSystem = system;
	}
	
	public ISolution getSolution() {
		return myKappaSystem.getSolution();
	}

	private void walkInjectingComponents(InjectionSettingStrategy strategy, CAgent solutionAgent) {
		for (CRule rule : myKappaSystem.getRules()) {
			for (IConnectedComponent cc : rule.getLeftHandSide()) {
				if (cc != null) {
					strategy.process(cc, solutionAgent);
				}
			}
		}

		for (IObservablesConnectedComponent oCC : myKappaSystem.getObservables()
				.getConnectedComponentList()) {
			if (oCC != null) {
				if (oCC.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX) {
					strategy.process(oCC, solutionAgent);
				}
			}
		}
	}
	
	public void build() {
		InjectionSettingStrategy strategy = new StraightInjectionSettingStrategy();
		for (CAgent agent : getSolution().getStraightStorage().getAgents()) {
			this.walkInjectingComponents(strategy, agent);
		}
		for (SuperSubstance substance : getSolution().getSuperStorage().getComponents()) {
			strategy = new SuperInjectionSettingStrategy(substance);  
			for (CAgent agent : substance.getComponent().getAgents()) {
				this.walkInjectingComponents(strategy, agent);
			}
		}
	}
}
