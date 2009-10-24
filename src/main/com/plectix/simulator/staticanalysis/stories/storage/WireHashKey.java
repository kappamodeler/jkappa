package com.plectix.simulator.staticanalysis.stories.storage;

import com.plectix.simulator.staticanalysis.Site;
import com.plectix.simulator.staticanalysis.stories.TypeOfWire;

public final class WireHashKey {
	private final long agentId;

	// = Site.DEFAULT_NAME if the type of wire == agent
	private final String siteName;

	// AGENT(1),
	// INTERNAL_STATE(2),
	// LINK_STATE(3),
	// BOUND_FREE(4);
	private final TypeOfWire typeOfWire;

	
	// number of unresolved modify events on this wire
	// may be need tested events
	
	public WireHashKey(long agentId, String siteName, TypeOfWire state) {
		this.agentId = agentId;
		this.siteName = siteName;
		this.typeOfWire = state;
	}

	// for agent_test_existence wires
	public WireHashKey(long agentId, TypeOfWire state) {
		this.agentId = agentId;
		this.siteName = Site.DEFAULT_NAME;
		this.typeOfWire = state;
	}

	public final TypeOfWire getTypeOfWire() {
		return typeOfWire;
	}
	
	public final String getSiteName() {
		return siteName;
	}
	
	public final long getAgentId() {
		return agentId;
	}

	public final int getSmallHash() {
		int result = 101;
		result = getResult(result, siteName);
		result = getResult(result, typeOfWire.getId());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof WireHashKey))
			return false;

		WireHashKey in = (WireHashKey) obj;
		if (this.agentId == in.agentId && this.siteName.equals(in.siteName)
				&& this.typeOfWire == in.typeOfWire)
			return true;
		return false;
	}

	@Override
	public final int hashCode() {
		int result = 101;
		result = getResult(result, (int) (agentId ^ (agentId >>> 32)));
		result = getResult(result, siteName);
		result = getResult(result, typeOfWire.getId());
		return result;
	}

	private static final int getResult(int result, int constant) {
		return 37 * result + constant;
	}

	private static final int getResult(int result, String constant) {
		return 37 * result + constant.hashCode();
	}

	@Override
	public String toString() {
		String str;
		if (typeOfWire == TypeOfWire.AGENT)
			str = "agentId= " + agentId + " type= " + typeOfWire.toString();
		else
			str = "agentId=" + agentId + " siteId=" + siteName + " type="
					+ typeOfWire.toString();

		return str;
	}
}
