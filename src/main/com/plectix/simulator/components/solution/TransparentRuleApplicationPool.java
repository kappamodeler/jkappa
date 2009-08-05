package com.plectix.simulator.components.solution;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.ISolution;

/**
 * This is the transparent rule application pool. And this is the tricky one.
 * It contains no local storage and uses solution's StraightStorage directly.
 * So, when rule says "change agent", we take the agent from solution and modify it
 * directly there.
 */
/*package*/ final class TransparentRuleApplicationPool implements RuleApplicationPool {
	private final StraightStorage myStorage;
	
	TransparentRuleApplicationPool(ISolution solution) {
		myStorage = solution.getStraightStorage();
	}
	
	@Override
	public final StraightStorage getStorage() {
		return myStorage;
	}

	@Override
	public final void removeAgent(CAgent agent) {
		myStorage.removeAgent(agent);
	}

	@Override
	public final void addAgent(CAgent agent) {
		myStorage.addAgent(agent);
	}
	
	@Override
	public final void clear() {
		// we have nothing to clear!
	}
}
