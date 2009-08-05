package com.plectix.simulator.components.solution;

import com.plectix.simulator.components.CAgent;

/**
 * This one is the standard rule application pool which is the honest one.
 * It means that it really contains a little StraightStorage object, which
 * really keeps substances.
 */
/*package*/ final class StandardRuleApplicationPool implements RuleApplicationPool {
	// We think that this collection is not so big
	private final StraightStorage myTempStorage;
	
	StandardRuleApplicationPool() {
		myTempStorage = new StraightStorage();
	}

	@Override
	public final void addAgent(CAgent agent) {
		myTempStorage.addAgent(agent);
	}

	@Override
	public final void removeAgent(CAgent agent) {
		myTempStorage.removeAgent(agent);
	}

	@Override
	public final StraightStorage getStorage() {
		return myTempStorage;
	}

	@Override
	public final void clear() {
		myTempStorage.clear();
	}
}
