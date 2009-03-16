package com.plectix.simulator.components.solution;

import java.util.Collection;
import java.util.List;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.ISolutionComponent;
import com.plectix.simulator.simulator.KappaSystem;

/*package*/ abstract class ComplexSolution extends SolutionAdapter {
	private final StraightStorage myStraightStorage;
	private final SuperStorage mySuperStorage;
	
	public ComplexSolution(KappaSystem system) {
		super(system);
		myStraightStorage = new StraightStorage();
		mySuperStorage = new SuperStorage();
	}

	protected StraightStorage getStraightStorage() {
		return myStraightStorage;
	}
	
	protected SuperStorage getSuperStorage() {
		return mySuperStorage;
	}
	
	public void clear() {
		myStraightStorage.clear();
		mySuperStorage.clear();
	}
}
