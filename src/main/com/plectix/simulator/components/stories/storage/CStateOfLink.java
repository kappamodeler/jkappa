package com.plectix.simulator.components.stories.storage;

public class CStateOfLink {
	public final static byte FREE = -1;
	private long agentId;
	private int siteId;

	public CStateOfLink() {
		agentId = FREE;
		siteId = FREE;
	}

	public CStateOfLink(long agentId, int siteId) {
		this.agentId = agentId;
		this.siteId = siteId;
	}

	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}

	public long getAgentId() {
		return agentId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public int getSiteId() {
		return siteId;
	}

	public boolean isFree() {
		if (agentId == FREE && siteId == FREE)
			return true;
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof CStateOfLink))
			return false;
		CStateOfLink statein = (CStateOfLink) obj;
		if (agentId == statein.agentId && siteId == statein.siteId)
			return true;
		return false;
	}

	
	@Override
	public int hashCode() {
		int result = 101;
		result = getResult(result, (int) (agentId ^ (agentId >>> 32)));
		result = getResult(result, siteId);
		return result;
	}

	private static int getResult(int result, int c) {
		return 37 * result + c;
	}
	@Override
	public String toString() {
		String str;
		if (agentId != FREE)
			str = "agentId=" + agentId + " siteId=" + siteId;
		else
			str = "agentId=FREE" + " siteId=FREE";
		return str;
	}
}
