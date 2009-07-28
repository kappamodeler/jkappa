package com.plectix.simulator.components.stories.storage;

import com.plectix.simulator.components.stories.enums.ETypeOfWire;

public class WireHashKey {
	private final long agentId;

	// = 0 if the type of wire == agent
	private final int siteId;

	// AGENT(1),
	// INTERNAL_STATE(2),
	// LINK_STATE(3),
	// BOUND_FREE(4);
	private final ETypeOfWire typeOfWire;

	
	// number of unresolved modify events on this wire
	// may be need tested events
	//private int numberOfUnresolvedEventOnWire;
	
	public WireHashKey(long agentId, int siteId, ETypeOfWire state) {
		this.agentId = agentId;
		this.siteId = siteId;
		this.typeOfWire = state;
	}

	// for agent_test_existence wires
	public WireHashKey(long agentId, ETypeOfWire state) {
		this.agentId = agentId;
		this.siteId = 0;
		this.typeOfWire = state;
	}

	public ETypeOfWire getTypeOfWire() {
		return typeOfWire;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof WireHashKey))
			return false;

		WireHashKey in = (WireHashKey) obj;
		if (this.agentId == in.agentId && this.siteId == in.siteId
				&& this.typeOfWire == in.typeOfWire)
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		int result = 101;
		result = getResult(result, (int) (agentId ^ (agentId >>> 32)));
		result = getResult(result, siteId);
		result = getResult(result, typeOfWire.getId());
		return result;
	}

	private static int getResult(int result, int c) {
		return 37 * result + c;
	}

	@Override
	public String toString() {
		String str;
		if (typeOfWire == ETypeOfWire.AGENT)
			str = "agentId= " + agentId + " type= " + typeOfWire.toString();
		else
			str = "agentId=" + agentId + " siteId=" + siteId + " type="
					+ typeOfWire.toString();

		return str;
	}

	public Integer getSiteId() {
		return siteId;
	}
	
	public Long getAgentId() {
		return agentId;
	}

	public Integer getSmallHash() {
		int result = 101;
		result = getResult(result, siteId);
		result = getResult(result, typeOfWire.getId());
		return result;
	}

	
}
