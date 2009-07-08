package com.plectix.simulator.components.complex.influenceMap;

import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;

class MarkSite {
	private final CAbstractSite site;
	private final EAction type;

	public MarkSite(CAbstractSite site, EAction type) {
		this.site = site.clone();
		this.type = type;
	}

	public CAbstractSite getSite() {
		return site;
	}

	public EAction getType() {
		return type;
	}

	public MarkSite(CAbstractAgent agent, EAction type) {
		this.type = type;
		this.site = agent.getSitesMap().values().iterator().next();
	}
}
