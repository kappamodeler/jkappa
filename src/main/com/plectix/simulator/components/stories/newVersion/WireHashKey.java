package com.plectix.simulator.components.stories.newVersion;

public class WireHashKey {
	private final long agentId;
	private final int siteId;
	private final EKeyOfState keyOfState;

	public WireHashKey(long agentId, int siteId, EKeyOfState state) {
		this.agentId = agentId;
		this.siteId = siteId;
		this.keyOfState = state;
	}

	public WireHashKey(long agentId, EKeyOfState state) {
		this.agentId = agentId;
		this.siteId = 0;
		this.keyOfState = state;
	}

	public EKeyOfState getKeyOfState() {
		return keyOfState;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof WireHashKey))
			return false;

		WireHashKey in = (WireHashKey) obj;
		if (this.agentId == in.agentId && this.siteId == in.siteId
				&& this.keyOfState == in.keyOfState)
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = getResult(result, (int) (agentId ^ (agentId >>> 32)));
		result = getResult(result, siteId);
		result = getResult(result, keyOfState.getId());
		return result;
	}

	private static int getResult(int result, int c) {
		return 37 * result + c;
	}

	@Override
	public String toString() {
		String str;
		if (keyOfState == EKeyOfState.AGENT)
			str = "agentId= " + agentId + " type= " + keyOfState.toString();
		else
			str = "agentId=" + agentId + " siteId=" + siteId + " type="
					+ keyOfState.toString();

		return str;
	}

}
