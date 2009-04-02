package com.plectix.simulator.components.solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.interfaces.ISolutionComponent;
import com.plectix.simulator.simulator.KappaSystem;

/*package*/ abstract class ComplexSolution extends SolutionAdapter {
	private final StraightStorage myStraightStorage;
	private final SuperStorage mySuperStorage;
	
	public ComplexSolution(KappaSystem system) {
		super(system);
		myStraightStorage = new StraightStorage();
		mySuperStorage = new SuperStorage(this);
	}

	public StraightStorage getStraightStorage() {
		return myStraightStorage;
	}
	
	public SuperStorage getSuperStorage() {
		return mySuperStorage;
	}
	
	public List<IConnectedComponent> split() {
		List<IConnectedComponent> list = new ArrayList<IConnectedComponent>();
		if (mySuperStorage != null) {
			list.addAll(mySuperStorage.split());
		}
		if (myStraightStorage != null) {
			list.addAll(myStraightStorage.split());
		}
		return list;
	}
	
	public void clear() {
		myStraightStorage.clear();
		mySuperStorage.clear();
	}
}
