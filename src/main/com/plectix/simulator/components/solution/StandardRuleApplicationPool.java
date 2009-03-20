package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.action.*;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.SimulationUtils;

public class StandardRuleApplicationPool extends RuleApplicationPool {
	// We think that this collection is not so big
	private final StraightStorage myTempStorage;
	
	public StandardRuleApplicationPool(StraightStorage storage) {
		myTempStorage = storage;
	}

	public void addAgent(IAgent agent) {
		myTempStorage.addAgent(agent);
	}

	public void removeAgent(IAgent agent) {
		myTempStorage.removeAgent(agent);
	}

	public StraightStorage getStorage() {
		return myTempStorage;
	}
}
