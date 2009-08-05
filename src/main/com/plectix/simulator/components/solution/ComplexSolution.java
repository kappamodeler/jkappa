package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.KappaSystem;

/**
 * This is complex solution class, which means that species can be stored here in two ways:
 * as components in SuperStorage and as agents in StraightStorage.
 * <br>Notice that implementations can physically contain all the species in only one storage 
 * (1 and 4 solution-types).
 */
/*package*/ abstract class ComplexSolution extends SolutionAdapter {
	private final StraightStorage myStraightStorage = new StraightStorage();
	private final SuperStorage mySuperStorage = new SuperStorage(this);
	
	ComplexSolution(KappaSystem system) {
		super(system);
	}

	@Override
	public final StraightStorage getStraightStorage() {
		return myStraightStorage;
	}
	
	@Override
	public final SuperStorage getSuperStorage() {
		return mySuperStorage;
	}
	
	@Override
	public Collection<IConnectedComponent> split() {
		List<IConnectedComponent> list = new ArrayList<IConnectedComponent>();
		if (mySuperStorage != null) {
			list.addAll(mySuperStorage.split());
		}
		if (myStraightStorage != null) {
			list.addAll(myStraightStorage.split());
		}
		return list;
	}
	
	@Override
	public final void clear() {
		myStraightStorage.clear();
		mySuperStorage.clear();
	}
}
