package com.plectix.simulator.components;

import java.io.Serializable;

/**
 * Auxiliary class used for easier work with injection. For each injection we
 * have set of corresponding pairs (agent, agent), which shows us detailed injection structure.
 * This class implements a pair of corresponding agents.
 *  
 * @see CAgent
 * @author avokhmin
 */
@SuppressWarnings("serial")
public final class CAgentLink implements Serializable {
	private final int idAgentFrom;
	private final CAgent agentTo;

	/**
	 * Constructor. Creates CAgentLink with 2 existing agents
	 * @param agentFromId id of the first agent, the one that parent injection built from
	 * @param agentTo second agent, the one that parent injection points to
	 */
	public CAgentLink(int agentFromId, CAgent agentTo) {
		this.idAgentFrom = agentFromId;
		this.agentTo = agentTo;
	}
	
	/**
	 * This method returns id of agent which parent injection built from
	 * @return id of agent which parent injection built from
	 */
	public final int getIdAgentFrom() {
		return idAgentFrom;
	}

	/**
	 * This method returns agent which parent injection points to
	 * @return agent which parent injection points to
	 */
	public final CAgent getAgentTo() {
		return agentTo;
	}
}