package com.plectix.simulator.components;

import com.plectix.simulator.interfaces.*;

/*package*/ final class CAgentLink implements IAgentLink {
	private final int idAgentFrom;
	private final IAgent agentTo;
	
	public CAgentLink(int idAgentFrom, IAgent agentTo) {
		this.idAgentFrom = idAgentFrom;
		this.agentTo = agentTo;
	}
	
	public final int getIdAgentFrom() {
		return idAgentFrom;
	}

	public final void storifyAgent(){
		this.agentTo.storifyAgent();
	}
	
	public final IAgent getAgentTo() {
		return agentTo;
	}
}