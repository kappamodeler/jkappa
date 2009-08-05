package com.plectix.simulator.components.solution;

import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.KappaSystem;

/**
 * This type of solution is very similar to the third one, so we just extend CThirdSolution.
 * Distinctive feature of the 5th solution is adding component 
 * to SuperStorage if and only if this component isn't too long.
 * <br> Critical length of the component in SuperSolution defined in SimulatorOptions.
 */
/*package*/ final class CFifthSolution extends CThirdSolution {
	private final SuperStorage mySuperStorage;
	private final StraightStorage myStraightStorage;
	
	CFifthSolution(KappaSystem system) {
		super(system);
		mySuperStorage = getSuperStorage();
		myStraightStorage = getStraightStorage();
	}

	@Override
	protected final void addConnectedComponent(IConnectedComponent component) {
		if (component.getAgentsQuantity() <= mySuperStorage.getAgentsLimit()) {
			mySuperStorage.addConnectedComponent(component);
		} else {
			myStraightStorage.addConnectedComponent(component);
		}
	}
}
