package com.plectix.simulator.components.stories.events;

class CStateOfLinkState {
	private final long idLinkAgent;
	private final int idLinkSite;

	public CStateOfLinkState(long idLinkAgent, int inLinkSite) {
		this.idLinkAgent = idLinkAgent;
		this.idLinkSite = inLinkSite;
	}

	public long getIdLinkAgent() {
		return idLinkAgent;
	}

	public int getIdLinkSite() {
		return idLinkSite;
	}

}
