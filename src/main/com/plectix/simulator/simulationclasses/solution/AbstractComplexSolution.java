package com.plectix.simulator.simulationclasses.solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulator.KappaSystem;

/**
 * This is complex solution class, which means that species can be stored here in two ways:
 * as components in SuperStorage and as agents in StraightStorage.
 * <br>Notice that implementations can physically contain all the species in only one storage 
 * (1 and 4 solution-types).
 */
/*package*/ abstract class AbstractComplexSolution extends SolutionAdapter {
	private final StraightStorage straightStorage = new StraightStorage();
	private final SuperStorage superStorage = new SuperStorage(this);
	
	AbstractComplexSolution(KappaSystem system) {
		super(system);
	}

	@Override
	public final StraightStorage getStraightStorage() {
		return straightStorage;
	}
	
	@Override
	public final SuperStorage getSuperStorage() {
		return superStorage;
	}
	
	@Override
	public Collection<ConnectedComponentInterface> split() {
		List<ConnectedComponentInterface> list = new ArrayList<ConnectedComponentInterface>();
		if (superStorage != null) {
			list.addAll(superStorage.split());
		}
		if (straightStorage != null) {
			list.addAll(straightStorage.split());
		}
		return list;
	}
	
	@Override
	public final void clear() {
		straightStorage.clear();
		superStorage.clear();
	}
}
