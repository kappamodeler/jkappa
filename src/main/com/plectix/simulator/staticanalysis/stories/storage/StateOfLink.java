package com.plectix.simulator.staticanalysis.stories.storage;

import com.plectix.simulator.staticanalysis.Site;
import com.plectix.simulator.util.NameDictionary;

public final class StateOfLink {
	private final long agentId;
	private final String siteName;

	public StateOfLink() {
		agentId = -1;
		siteName = Site.DEFAULT_NAME;
	}

	public StateOfLink(long agentId, String siteName) {
		this.agentId = agentId;
		this.siteName = siteName;
	}

	public final long getAgentId() {
		return agentId;
	}

	public final String getSiteName() {
		return siteName;
	}

	public final boolean isFree() {
		if (agentId == -1 && NameDictionary.isDefaultSiteName(siteName))
			return true;
		return false;
	}

//	public final void setState(StateOfLink newState) {
//		this.agentId = newState.getAgentId();
//		this.siteName = newState.getSiteName();
//	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null || !(obj instanceof StateOfLink))
			return false;
		StateOfLink statein = (StateOfLink) obj;
		return agentId == statein.agentId && siteName.equals(statein.siteName);
	}

	private static final int getResult(int result, Object constant) {
		return 37 * result + constant.hashCode();
	}

	@Override
	public final int hashCode() {
		int result = 101;
		result = getResult(result, (int) (agentId ^ (agentId >>> 32)));
		result = getResult(result, siteName);
		return result;
	}

	@Override
	public final String toString() {
		String str;
		if (agentId != -1)
			str = "agentId=" + agentId + " siteId=" + siteName;
		else
			str = "agentId=FREE" + " siteId=FREE";
		return str;
	}
}
