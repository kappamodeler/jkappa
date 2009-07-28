package com.plectix.simulator.components.solution;

import com.plectix.simulator.components.CAgent;

public class StandardRuleApplicationPool extends RuleApplicationPool {
	// We think that this collection is not so big
	private final StraightStorage myTempStorage;
	
	public StandardRuleApplicationPool(StraightStorage storage) {
		myTempStorage = storage;
	}

	public void addAgent(CAgent agent) {
		myTempStorage.addAgent(agent);
	}

	public void removeAgent(CAgent agent) {
		myTempStorage.removeAgent(agent);
	}

	public StraightStorage getStorage() {
		return myTempStorage;
	}
}
