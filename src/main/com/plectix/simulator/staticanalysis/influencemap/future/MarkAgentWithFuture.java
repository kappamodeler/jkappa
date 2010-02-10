package com.plectix.simulator.staticanalysis.influencemap.future;

import com.plectix.simulator.simulationclasses.action.ActionType;
import com.plectix.simulator.staticanalysis.Site;
import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.abstracting.AbstractSite;

// TODO rename this class
/*package*/ final class MarkAgentWithFuture {
	private final AbstractAgent agent;
	private String siteName = Site.DEFAULT_NAME;
	private ActionType type = ActionType.NONE;

	public MarkAgentWithFuture(AbstractAgent agent, AbstractSite site, ActionType type) {
		this.type = type;
		AbstractAgent agentNew = new AbstractAgent(agent.getName());
		this.agent = agentNew;
		if (site != null) {
			AbstractSite siteNew = site.clone();
			siteNew.setParentAgent(agentNew);
			agentNew.addSite(siteNew);
			siteName = siteNew.getName();
		}
	}

	public MarkAgentWithFuture(AbstractAgent agent) {
		this.type = ActionType.BREAK;
		AbstractAgent agentNew = new AbstractAgent(agent.getName());
		this.agent = agentNew;
		for(AbstractSite site : agent.getSitesMap().values()){
			AbstractSite siteNew = site.clone();
			siteNew.setParentAgent(agentNew);
			agentNew.addSite(siteNew);
			siteName = siteNew.getName();
		}
	}

	public final ActionType getType() {
		return type;
	}

	public final AbstractAgent getAgent() {
		return agent;
	}
	
	public final AbstractSite getSite(){
		return agent.getSiteByName(siteName);
	}
}
