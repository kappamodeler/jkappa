package com.plectix.simulator.simulator.initialization;

import java.util.Collection;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.interfaces.ObservableConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.solution.SuperSubstance;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.ObservableConnectedComponent;
import com.plectix.simulator.staticanalysis.Rule;

public final class InjectionsBuilder {
	private final KappaSystem kappaSystem;
	
	public InjectionsBuilder(KappaSystem system) {
		kappaSystem = system;
	}
	
	private final void walkInjectingComponents(InjectionSettingStrategy strategy, Agent solutionAgent) {
		for (Rule rule : kappaSystem.getRules()) {
			for (ConnectedComponentInterface cc : rule.getLeftHandSide()) {
				if (cc != null) {
					strategy.process(cc, solutionAgent);
				}
			}
		}

		for (ObservableConnectedComponentInterface oCC : kappaSystem.getObservables().getConnectedComponentList()) {
			if (oCC != null) {
				if (oCC.getMainAutomorphismNumber() == ObservableConnectedComponent.NO_INDEX) {
					strategy.process(oCC, solutionAgent);
				}
			}
		}
	}
	
	public final void build() {
		InjectionSettingStrategy strategy = new StraightInjectionSettingStrategy();
		for (Agent agent : kappaSystem.getSolution().getStraightStorage().getAgents()) {
			this.walkInjectingComponents(strategy, agent);
		}
		for (SuperSubstance substance : kappaSystem.getSolution().getSuperStorage().getComponents()) {
			strategy = new SuperInjectionSettingStrategy(substance);  
			for (Agent agent : substance.getComponent().getAgents()) {
				this.walkInjectingComponents(strategy, agent);
			}
		}
	}
	
	public final void build(Collection<Agent> agents) {
		InjectionSettingStrategy strategy = new StraightInjectionSettingStrategy();
		for (Agent agent : agents) {
			walkInjectingComponents(strategy, agent);
		}
	}
	
	public final void build(SuperSubstance substance) {
		InjectionSettingStrategy strategy = new SuperInjectionSettingStrategy(substance);
		for (Agent agent : substance.getComponent().getAgents()) {
			walkInjectingComponents(strategy, agent);
		}
	}
}
