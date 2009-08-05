package com.plectix.simulator.rulecompression;

import com.plectix.simulator.components.CSite;

/*package*/ class SiteInfo {
	private final String siteName;
	private final String agentName;
	
	public SiteInfo(String site, String agent) {
		this.siteName = site;
		this.agentName = agent;
	}
	
	public SiteInfo(CSite site) {
		this.siteName = site.getName();
		this.agentName = site.getParentAgent().getName();
	}
	
	public String getSiteName() {
		return siteName;
	}
	
	public String getAgentName() {
		return agentName;
	}
	
	
	public String toString() {
		return agentName + "." + siteName;
	}
}
