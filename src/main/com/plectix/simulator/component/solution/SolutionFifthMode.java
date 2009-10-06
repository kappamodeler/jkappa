package com.plectix.simulator.component.solution;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulator.KappaSystem;

/**
 * This type of solution is very similar to the third one, so we just extend CThirdSolution.
 * Distinctive feature of the 5th solution is adding component 
 * to SuperStorage if and only if this component isn't too long.
 * <br> Critical length of the component in SuperSolution defined in SimulatorOptions.
 */
/*package*/ final class SolutionFifthMode extends SolutionThirdMode {
	private final SuperStorage superStorage;
	private final StraightStorage straightStorage;
	
	SolutionFifthMode(KappaSystem system) {
		super(system);
		superStorage = getSuperStorage();
		straightStorage = getStraightStorage();
	}

	@Override
	protected final void addConnectedComponent(ConnectedComponentInterface component) {
		if (component.getAgents().size() <= superStorage.getAgentsLimit()) {
			superStorage.addConnectedComponent(component);
		} else {
			straightStorage.addConnectedComponent(component);
		}
	}
}
