package com.plectix.simulator.components.stories.newVersion;

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

	public void setSiteId(int siteId) {
		this.siteId = siteId;
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
	public String toString() {
		String str;
		if (agentId != FREE)
			str = "agentId=" + agentId + " siteId=" + siteId;
		else
			str = "agentId=FREE" + " siteId=FREE";
		return str;
	}
}
