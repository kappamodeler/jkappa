package com.plectix.simulator.util;

public class BoundContactMap {
	private int agentNameIdFrom;
	private int agentNameIdTo;
	private int siteNameIdFrom;
	private int siteNameIdTo;

	public BoundContactMap(int agentNameIdFrom, int siteNameIdFrom,
			int agentNameIdTo, int siteNameIdTo) {
		this.agentNameIdFrom = agentNameIdFrom;
		this.agentNameIdTo = agentNameIdTo;
		this.siteNameIdFrom = siteNameIdFrom;
		this.siteNameIdTo = siteNameIdTo;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BoundContactMap))
			return false;

		BoundContactMap from = (BoundContactMap) obj;
		if(agentNameIdFrom == from.getAgentNameIdTo() && siteNameIdFrom == from.getSiteNameIdTo() && siteNameIdTo == from.getSiteNameIdFrom())
			return true;
		
//		if (!(((agentNameIdFrom == from.getAgentNameIdFrom()) && (agentNameIdTo == from
//				.getAgentNameIdTo())) || ((agentNameIdFrom == from
//				.getAgentNameIdTo()) && (agentNameIdTo == from
//				.getAgentNameIdFrom()))))
//			return false;		
		return false;
	}

	public int getAgentNameIdFrom() {
		return agentNameIdFrom;
	}

	public int getAgentNameIdTo() {
		return agentNameIdTo;
	}

	public int getSiteNameIdFrom() {
		return siteNameIdFrom;
	}

	public int getSiteNameIdTo() {
		return siteNameIdTo;
	}
}
