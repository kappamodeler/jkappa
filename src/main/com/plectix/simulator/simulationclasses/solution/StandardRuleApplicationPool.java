package com.plectix.simulator.simulationclasses.solution;

import com.plectix.simulator.staticanalysis.Agent;

/**
 * This one is the standard rule application pool which is the honest one.
 * It means that it really contains a little StraightStorage object, which
 * really keeps substances.
 */
/*package*/ final class StandardRuleApplicationPool implements RuleApplicationPoolInterface {
	// We think that this collection is not so big
	private final StraightStorage temporaryStorage;
	
	StandardRuleApplicationPool() {
		temporaryStorage = new StraightStorage();
	}

	@Override
	public final void addAgent(Agent agent) {
		temporaryStorage.addAgent(agent);
	}

	@Override
	public final void removeAgent(Agent agent) {
		temporaryStorage.removeAgent(agent);
	}

	@Override
	public final StraightStorage getStorage() {
		return temporaryStorage;
	}

	@Override
	public final void clear() {
		temporaryStorage.clear();
	}
}
