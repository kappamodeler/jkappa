package com.plectix.simulator.component.complex.influencemap.nofuture;

import com.plectix.simulator.component.complex.abstracting.AbstractAgent;
import com.plectix.simulator.component.complex.abstracting.AbstractSite;

/*package*/ final class MarkSiteWithoutFuture {
	private final AbstractSite site;
	private final Action type;

	public MarkSiteWithoutFuture(AbstractSite site, Action type) {
		this.site = site.clone();
		this.type = type;
	}

	public MarkSiteWithoutFuture(AbstractAgent agent, Action type) {
		this.type = type;
		this.site = agent.getSitesMap().values().iterator().next();
	}
	
	public final AbstractSite getSite() {
		return site;
	}

	public final Action getType() {
		return type;
	}
}