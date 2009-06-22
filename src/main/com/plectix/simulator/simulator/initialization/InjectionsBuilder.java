package com.plectix.simulator.simulator.initialization;

import java.util.Collection;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.ObservablesConnectedComponent;
import com.plectix.simulator.components.solution.SuperSubstance;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.interfaces.IObservablesConnectedComponent;

import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.simulator.KappaSystem;

public class InjectionsBuilder {
	private final KappaSystem kappaSystem;
	
	public InjectionsBuilder(KappaSystem system) {
		kappaSystem = system;
	}
	
	private final void walkInjectingComponents(InjectionSettingStrategy strategy, CAgent solutionAgent) {
		for (CRule rule : kappaSystem.getRules()) {
			for (IConnectedComponent cc : rule.getLeftHandSide()) {
				if (cc != null) {
					strategy.process(cc, solutionAgent);
				}
			}
		}

		for (IObservablesConnectedComponent oCC : kappaSystem.getObservables().getConnectedComponentList()) {
			if (oCC != null) {
				if (oCC.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX) {
					strategy.process(oCC, solutionAgent);
				}
			}
		}
	}
	
	public final void build() {
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
	
	public final void build(SuperSubstance substance) {
		if (substance.getQuantity() != 0) {
			InjectionSettingStrategy strategy = new SuperInjectionSettingStrategy(substance);
			for (CAgent agent : substance.getComponent().getAgents()) {
				walkInjectingComponents(strategy, agent);
			}
		}
	}
	
	public final void build(Collection<CAgent> agents) {
		InjectionSettingStrategy strategy = new StraightInjectionSettingStrategy();
		for (CAgent agent : agents) {
			walkInjectingComponents(strategy, agent);
		}
	}

	public final ISolution getSolution() {
		return kappaSystem.getSolution();
	}
}
