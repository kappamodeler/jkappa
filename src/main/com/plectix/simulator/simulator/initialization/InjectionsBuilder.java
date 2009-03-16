package com.plectix.simulator.simulator.initialization;

import com.plectix.simulator.components.ObservablesConnectedComponent;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.IObservablesConnectedComponent;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.interfaces.ISolutionComponent;
import com.plectix.simulator.simulator.KappaSystem;

public abstract class InjectionsBuilder {
	private final KappaSystem myKappaSystem;
	public InjectionsBuilder(KappaSystem system) {
		myKappaSystem = system;
	}
	
	public abstract void setInjection(IConnectedComponent component, IAgent solutionAgent);
	
	public abstract void build(); 
	
	public ISolution getSolution() {
		return myKappaSystem.getSolution();
	}

	public void walkInjectingComponents(IAgent solutionAgent) {
		for (IRule rule : myKappaSystem.getRules()) {
			for (IConnectedComponent cc : rule.getLeftHandSide()) {
				if (cc != null) {
					setInjection(cc, solutionAgent);
				}
			}
		}

		for (IObservablesConnectedComponent oCC : myKappaSystem.getObservables()
				.getConnectedComponentList()) {
			if (oCC != null) {
				if (oCC.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX) {
					setInjection(oCC, solutionAgent);
				}
			}
		}
	}
}
