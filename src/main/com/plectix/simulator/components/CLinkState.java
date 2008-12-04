package com.plectix.simulator.components;

import com.plectix.simulator.interfaces.*;

public final class CLinkState extends CState implements ILinkState {

	public static final byte STATUS_LINK_BOUND = 0x01;
	public static final byte STATUS_LINK_WILDCARD = 0x02;
	public static final byte STATUS_LINK_FREE = 0x04;

	public static final byte RANK_BOUND_OR_FREE = 0x01;
	public static final byte RANK_SEMI_LINK = 0x02;
	public static final byte RANK_BOUND = 0x03;
	public static final byte RANK_FREE = 0x04;
	
	public static final byte NULL_INDEX = -1;

	private byte statusLink;
	private ISite linkSite = null;
	private int linkStateID = NULL_INDEX;
	
	public CLinkState(ISite site, byte statusLink) {
		linkSite = site;
		this.statusLink = statusLink;
	}

	public CLinkState(byte statusLink) {
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
		if (statusLink == STATUS_LINK_WILDCARD)
			return true;
		else
			return false;
	}

	public final boolean isLeftBranchStatus() {
		return (statusLink == STATUS_LINK_FREE) ? true : false;
	}

	public final boolean isRightBranchStatus() {
		return (statusLink == STATUS_LINK_BOUND) ? true : false;
	}

	public final ISite getSite() {
		return linkSite;
	}

	public final void setSite(ISite site) {
		linkSite = site;
		if (linkSite != null)
			statusLink = STATUS_LINK_BOUND;
	}

	public final void setStatusLink(byte statusLink) {
		this.statusLink = statusLink;
	}

	public final byte getStatusLink() {
		return statusLink;
	}

	/**
	 * Returns the rank of the status link (according to the Simulation Engine
	 * Specification part 2) Used to compare states.
	 */
	public final byte getStatusLinkRank() {
		switch (statusLink) {
		case STATUS_LINK_BOUND:
			if (linkSite != null)
				return RANK_BOUND;
			else
				return RANK_SEMI_LINK;
		case STATUS_LINK_WILDCARD:
			return RANK_BOUND_OR_FREE;
		default:
			return RANK_FREE;
		}
	}

	public final String getName() {
		return null;
	}
}
