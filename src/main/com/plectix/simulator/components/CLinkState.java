package com.plectix.simulator.components;

import java.util.List;

import com.plectix.simulator.interfaces.IState;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.interfaces.ILift.LiftElement;

public class CLinkState implements IState {

	public static final byte STATUS_LINK_BOUND = 0x01;
	public static final byte STATUS_LINK_WILDCARD = 0x02;
	public static final byte STATUS_LINK_FREE = 0x04;

	public static final byte RANK_BOUND_OR_FREE = 0x01;
	public static final byte RANK_FREE = 0x02;
	public static final byte RANK_SEMI_LINK = 0x02;
	public static final byte RANK_BOUND = 0x03;

	
	private byte statusLink;
	private ISite linkSite = null;

	public CLinkState(ISite site, byte statusLink) {
		linkSite = site;
		this.statusLink = statusLink;
	}

	public CLinkState(byte statusLink) {
		this.statusLink = statusLink;
	}

	public final boolean isLeftBranchStatus() {
		return ((statusLink == STATUS_LINK_FREE) 
			|| (statusLink == STATUS_LINK_WILDCARD)) ? false
				: true;
	}

	public final boolean isRightBranchStatus() {
		return (statusLink == STATUS_LINK_FREE) ? false : true;
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

	@Override
	public final List<LiftElement> getLift() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final boolean isChanged() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public final void removeLiftElement(LiftElement element) {
		// TODO Auto-generated method stub

	}

	@Override
	public final void setLift(List<LiftElement> lift) {
		// TODO Auto-generated method stub

	}

}
