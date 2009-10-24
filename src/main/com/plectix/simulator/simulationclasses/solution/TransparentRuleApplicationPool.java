package com.plectix.simulator.simulationclasses.solution;

import com.plectix.simulator.interfaces.SolutionInterface;
import com.plectix.simulator.staticanalysis.Agent;

/**
 * This is the transparent rule application pool. And this is the tricky one.
 * It contains no local storage and uses solution's StraightStorage directly.
 * So, when rule says "change agent", we take the agent from solution and modify it
 * directly there.
 */
/*package*/ final class TransparentRuleApplicationPool implements RuleApplicationPoolInterface {
	private final StraightStorage temporaryStorage;
	
	TransparentRuleApplicationPool(SolutionInterface solution) {
		temporaryStorage = solution.getStraightStorage();
	}
	
	@Override
	public final StraightStorage getStorage() {
		return temporaryStorage;
	}

	@Override
	public final void removeAgent(Agent agent) {
		temporaryStorage.removeAgent(agent);
	}

	@Override
	public final void addAgent(Agent agent) {
		temporaryStorage.addAgent(agent);
	}
	
	@Override
	public final void clear() {
		// we have nothing to clear!
	}
}
