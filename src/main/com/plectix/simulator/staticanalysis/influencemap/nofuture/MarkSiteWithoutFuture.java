package com.plectix.simulator.staticanalysis.influencemap.nofuture;

import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.abstracting.AbstractSite;

/*package*/ final class MarkSiteWithoutFuture {
	private final AbstractSite site;
	private final Quark type;

	public MarkSiteWithoutFuture(AbstractSite site, Quark type) {
		this.site = site.clone();
		this.type = type;
	}

	public MarkSiteWithoutFuture(AbstractAgent agent) {
		this.type = Quark.LINK_STATE_QUARK;
		this.site = agent.getSitesMap().values().iterator().next();
	}
	
	public final AbstractSite getSite() {
		return site;
	}

	public final Quark getType() {
		return type;
	}
}
