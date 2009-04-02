package com.plectix.simulator.components;

import java.io.Serializable;

import com.plectix.simulator.interfaces.*;

/**
 * Auxiliary class, uses for more easy interpretation {@link CInjection} ("Atomic" injection).
 * @see CInjection
 * @see CAgent
 * @author avokhmin
 */
public final class CAgentLink implements Serializable {
	/**
	 * {@link Integer} value - id of Agent from injection.
	 */
	private final int idAgentFrom;

	/**
	 * {@link CAgent} value - agent from solution.
	 */
	private final CAgent agentTo;
	
	/**
	 * Standard constructor.
	 * @param idAgentFrom - {@link Integer} value - id of Agent from injection.
	 * @param agentTo - {@link CAgent} value - agent from solution.
	 */
	public CAgentLink(int idAgentFrom, CAgent agentTo) {
		this.idAgentFrom = idAgentFrom;
		this.agentTo = agentTo;
	}
	
	/**
	 * Returns {@link Integer} value - id of Agent from injection.
	 */
	public final int getIdAgentFrom() {
		return idAgentFrom;
	}

	/**
	 * Returns {@link CAgent} value - agent from solution.
	 */
	public final CAgent getAgentTo() {
		return agentTo;
	}
}