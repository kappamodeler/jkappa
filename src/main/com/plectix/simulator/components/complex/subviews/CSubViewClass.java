package com.plectix.simulator.components.complex.subviews;

import java.util.HashSet;
import java.util.Set;

public class CSubViewClass {
	private int agentTypeId;
	private Set<Integer> sitesId;

	public CSubViewClass(int agentTypeId, int siteId) {
		this.agentTypeId = agentTypeId;
		sitesId = new HashSet<Integer>();
		sitesId.add(Integer.valueOf(siteId));
	}

	public int getAgentTypeId() {
		return agentTypeId;
	}

	public Set<Integer> getSitesId() {
		return sitesId;
	}
	
	public void addSite(int site){
		sitesId.add(site);
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof CSubViewClass))
			return false;
		CSubViewClass inClass = (CSubViewClass)obj;
		
		if(agentTypeId != inClass.agentTypeId)
			return false;
		if(!sitesId.equals(inClass.sitesId))
			return false;
		return true;
	}
}
