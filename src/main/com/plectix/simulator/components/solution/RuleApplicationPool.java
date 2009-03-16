package com.plectix.simulator.components.solution;

import com.plectix.simulator.interfaces.IAgent;

public abstract class RuleApplicationPool {
	public abstract void addAgent(IAgent agent);

	public abstract void removeAgent(IAgent agent);
}
