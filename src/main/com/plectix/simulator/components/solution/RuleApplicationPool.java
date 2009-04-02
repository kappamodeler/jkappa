package com.plectix.simulator.components.solution;

import com.plectix.simulator.components.CAgent;

public abstract class RuleApplicationPool {
	public abstract void addAgent(CAgent agent);

	public abstract void removeAgent(CAgent agent);
	
	public abstract StraightStorage getStorage();
}
