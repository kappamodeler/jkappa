package com.plectix.simulator.components;

import com.plectix.simulator.interfaces.*;

public final class CAgentLink implements IAgentLink {
	private final int idAgentFrom;
	private final IAgent agentTo;
	
	public CAgentLink(int idAgentFrom, IAgent agentTo) {
		this.idAgentFrom = idAgentFrom;
		this.agentTo = agentTo;
	}
	
	public int getIdAgentFrom() {
		return idAgentFrom;
	}

	public IAgent getAgentTo() {
		return agentTo;
	}
}