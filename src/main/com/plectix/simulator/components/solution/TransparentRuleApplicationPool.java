package com.plectix.simulator.components.solution;

import com.plectix.simulator.interfaces.IAgent;

/*package*/class TransparentRuleApplicationPool extends RuleApplicationPool {
	private final StraightStorage myStorage;
	
	public TransparentRuleApplicationPool(StraightStorage storage) {
		myStorage = storage;
	}
	
	public StraightStorage getStorage() {
		return myStorage;
	}

	public void removeAgent(IAgent agent) {
		myStorage.removeAgent(agent);
	}

	public void addAgent(IAgent agent) {
		myStorage.addAgent(agent);
	}
}
