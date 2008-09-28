package com.plectix.simulator.components;

public class CAgentLink {
	public CAgentLink(int idAgentFrom, CAgent agentTo) {
		this.idAgentFrom = idAgentFrom;
		this.agentTo = agentTo;
	}

	int idAgentFrom;
	CAgent agentTo;

	public int getIdAgentFrom() {
		return idAgentFrom;
	}

	public void setIdAgentFrom(int idAgentFrom) {
		this.idAgentFrom = idAgentFrom;
	}

	public CAgent getAgentTo() {
		return agentTo;
	}

	public void setAgentTo(CAgent agentTo) {
		this.agentTo = agentTo;
	}
}