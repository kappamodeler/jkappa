package com.plectix.simulator.components.solution;

import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.KappaSystem;

/**
 * This type of solution is very similar to the third one.
 * Distinctive feature of the 5th solution, that it adds component 
 * to SuperStorage if and only if this component isn't too long.
 * 
 * Critical length of the component in SuperSolution defined in SimulatorOptions.
 * @author evlasov
 *
 */
public class CFifthSolution extends CThirdSolution {
	private final SuperStorage mySuperStorage;
	private final StraightStorage myStraightStorage;
	
	public CFifthSolution(KappaSystem system) {
		super(system);
		mySuperStorage = getSuperStorage();
		myStraightStorage = getStraightStorage();
	}

	@Override
	protected final void addConnectedComponent(IConnectedComponent component) {
		if (component.getAgentsQuantity() <= mySuperStorage.getAgentsLimit()) {
			if (!mySuperStorage.tryIncrement(component)) { 
				mySuperStorage.addNewSuperSubstance(component);
			}
		} else {
			myStraightStorage.addConnectedComponent(component);
		}
	}
}
