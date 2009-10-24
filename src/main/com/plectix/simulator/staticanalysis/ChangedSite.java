package com.plectix.simulator.staticanalysis;

import java.io.Serializable;

@SuppressWarnings("serial")
/*package*/ final class ChangedSite implements Serializable {
	private final Site site;
	private boolean hasLinkState;
	private boolean hasInternalState;

	public ChangedSite(Site site, boolean internalState, boolean linkState) {
		this.site = site;
		this.hasLinkState = linkState;
		this.hasInternalState = internalState;
	}
	
	public final Site getSite() {
		return site;
	}

	public final boolean hasLinkState() {
		return hasLinkState;
	}

	public final boolean hasInternalState() {
		return hasInternalState;
	}

	public final void setLinkState(boolean linkState) {
		this.hasLinkState = linkState;
	}

	public final void setInternalState(boolean internalState) {
		this.hasInternalState = internalState;
	}
}
