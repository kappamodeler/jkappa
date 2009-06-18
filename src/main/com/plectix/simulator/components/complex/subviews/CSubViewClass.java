package com.plectix.simulator.components.complex.subviews;

import java.util.HashSet;
import java.util.Set;

// Build by initial rules.
public class CSubViewClass {
	private int agentTypeId;
	private Set<Integer> sitesId;

	public CSubViewClass(int agentTypeId) {
		this.agentTypeId = agentTypeId;
		sitesId = new HashSet<Integer>();
	}

	public int getAgentTypeId() {
		return agentTypeId;
	}

	public Set<Integer> getSitesId() {
		return sitesId;
	}
}
