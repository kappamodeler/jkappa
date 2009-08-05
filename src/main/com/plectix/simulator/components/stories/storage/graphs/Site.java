package com.plectix.simulator.components.stories.storage.graphs;

import com.plectix.simulator.simulator.ThreadLocalData;

public class Site {
	
	private Integer siteId;
	private Integer linkState;
	private Integer internalState;
	
	
	public Site(Integer _siteId, Integer _internalState) {
		siteId = _siteId;
		linkState = -1;
		internalState = _internalState;
	}
	public Site(Integer _siteId, Integer _linkState, Integer _internalState) {
		siteId = _siteId;
		linkState = _linkState;
		internalState = _internalState;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public Integer getLinkState() {
		return linkState;
	}

	public Integer getInternalState() {
		return internalState;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Site))
			return false;

		Site in = (Site) obj;
		if (this.siteId == in.siteId && this.linkState == in.linkState
				&& this.internalState == in.internalState)
			return true;
		return false;
	}
	
	@Override
	public int hashCode() {
		int result = 101;
		result = getResult(result, siteId);
		result = getResult(result, linkState);
		result = getResult(result, internalState);
		return result;
	}

	private static int getResult(int result, int c) {
		return 37 * result + c;
	}
	
	
	@Override
	public String toString() {
		StringBuffer sb  = new StringBuffer();
		sb.append(ThreadLocalData.getNameDictionary().getName(siteId));
		if (internalState!=-1){
			sb.append("~" + ThreadLocalData.getNameDictionary().getName(internalState));
		}
		
		if (linkState!=-1){
			sb.append("!" + ThreadLocalData.getNameDictionary().getName(linkState));
		}
		
		return sb.toString();
	}

}
