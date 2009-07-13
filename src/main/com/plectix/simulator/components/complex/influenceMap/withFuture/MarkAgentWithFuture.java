package com.plectix.simulator.components.complex.influenceMap.withFuture;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;

class MarkAgentWithFuture {
	private CAbstractAgent agent;
	private int siteId = CSite.NO_INDEX;
	private CActionType type = CActionType.NONE;

	public MarkAgentWithFuture(CAbstractAgent agent, CAbstractSite site, CActionType type) {
		this.type = type;
		CAbstractAgent agentNew = new CAbstractAgent(agent.getNameId());
		this.agent = agentNew;
		if (site != null) {
			CAbstractSite siteNew = site.clone();
			siteNew.setAgentLink(agentNew);
			agentNew.addSite(siteNew);
			siteId = siteNew.getNameId();
		}
	}

	public MarkAgentWithFuture(CAbstractAgent agent, CActionType type) {
		this.type = type;
		CAbstractAgent agentNew = new CAbstractAgent(agent.getNameId());
		this.agent = agentNew;
	}

	public CActionType getType() {
		return type;
	}

	public void setType(CActionType type) {
		this.type = type;
	}

	public CAbstractAgent getAgent() {
		return agent;
	}
	
	public int getSiteId(){
		return siteId;
	}
	
	public CAbstractSite getSite(){
		return agent.getSite(siteId);
	}
	
}
