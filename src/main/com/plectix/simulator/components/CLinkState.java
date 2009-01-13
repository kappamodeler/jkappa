package com.plectix.simulator.components;

import java.io.Serializable;

import com.plectix.simulator.interfaces.*;

public final class CLinkState extends CState implements ILinkState, Serializable {

	public static final byte NULL_INDEX = -1;

	private CLinkStatus statusLink;
	private ISite linkSite = null;
	private int linkStateID = NULL_INDEX;
	
	public CLinkState(ISite site, CLinkStatus statusLink) {
		linkSite = site;
		this.statusLink = statusLink;
	}

	public CLinkState(CLinkStatus statusLink) {
		this.statusLink = statusLink;
	}
	
	public int getLinkStateID() {
		return linkStateID;
	}

	public void setLinkStateID(int linkSiteID) {
		this.linkStateID = linkSiteID;
	}

	@Override
	public boolean isRankRoot() {
		if (statusLink == CLinkStatus.WILDCARD)
			return true;
		else
			return false;
	}

	public final boolean isLeftBranchStatus() {
		return (statusLink == CLinkStatus.FREE) ? true : false;
	}

	public final boolean isRightBranchStatus() {
		return (statusLink == CLinkStatus.BOUND) ? true : false;
	}

	public final ISite getSite() {
		return linkSite;
	}

	public final void setSite(ISite site) {
		linkSite = site;
		if (linkSite != null)
			statusLink = CLinkStatus.BOUND;
	}

	public final void setStatusLink(CLinkStatus statusLink) {
		this.statusLink = statusLink;
	}

	public final CLinkStatus getStatusLink() {
		return statusLink;
	}

	/**
	 * Returns the rank of the status link (according to the Simulation Engine
	 * Specification part 2) Used to compare states.
	 */
	public final CLinkRank getStatusLinkRank() {
		switch (statusLink) {
		case BOUND:
			if (linkSite != null)
				return CLinkRank.BOUND;
			else
				return CLinkRank.SEMI_LINK;
		case WILDCARD:
			return CLinkRank.BOUND_OR_FREE;
		default:
			return CLinkRank.FREE;
		}
	}

	public final String getName() {
		return null;
	}
}
