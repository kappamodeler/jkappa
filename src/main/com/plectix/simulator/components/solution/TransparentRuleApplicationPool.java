package com.plectix.simulator.components.solution;

import com.plectix.simulator.components.CAgent;

/*package*/class TransparentRuleApplicationPool extends RuleApplicationPool {
	private final StraightStorage myStorage;
	
	public TransparentRuleApplicationPool(StraightStorage storage) {
		myStorage = storage;
	}
	
	public StraightStorage getStorage() {
		return myStorage;
	}

	public void removeAgent(CAgent agent) {
		myStorage.removeAgent(agent);
	}

	public void addAgent(CAgent agent) {
		myStorage.addAgent(agent);
	}
	
	@Override
	public void clear() {
		
	}
}
