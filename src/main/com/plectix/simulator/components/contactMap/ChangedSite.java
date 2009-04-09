package com.plectix.simulator.components.contactMap;
import java.io.Serializable;

import com.plectix.simulator.components.CSite;

@SuppressWarnings("serial")
public class ChangedSite implements Serializable{
	private final CSite site;
	private boolean linkState;
	private boolean internalState;

	public CSite getSite() {
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

	public ChangedSite(CSite site) {
		this.site = site;
		this.linkState = false;
		this.internalState = false;
	}

	public ChangedSite(CSite site, boolean internalState, boolean linkState) {
		this.site = site;
		this.linkState = linkState;
		this.internalState = internalState;
	}
}
