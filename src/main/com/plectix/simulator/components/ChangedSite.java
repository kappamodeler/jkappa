package com.plectix.simulator.components;
import com.plectix.simulator.interfaces.ISite;

public class ChangedSite {
	private final ISite site;
	private boolean linkState;
	private boolean internalState;

	public ISite getSite() {
		return site;
	}

	public boolean isLinkState() {
		return linkState;
	}

	public boolean isInternalState() {
		return internalState;
	}

	public final void setLinkState(boolean linkState) {
		this.linkState = linkState;
	}

	public final void setInternalState(boolean internalState) {
		this.internalState = internalState;
	}

	public ChangedSite(ISite site) {
		this.site = site;
		this.linkState = false;
		this.internalState = false;
	}

	public ChangedSite(ISite site, boolean internalState, boolean linkState) {
		this.site = site;
		this.linkState = linkState;
		this.internalState = internalState;
	}
}
