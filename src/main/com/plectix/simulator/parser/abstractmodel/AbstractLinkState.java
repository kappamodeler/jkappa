package com.plectix.simulator.parser.abstractmodel;

import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CLinkStatus;

public class AbstractLinkState {
	private CLinkStatus myLinkStatus;
	private AbstractSite myLinkedSite = null;
	
	public AbstractLinkState(CLinkStatus status) {
		myLinkStatus = status;
	}

	public void setStatusLink(CLinkStatus status) {
		myLinkStatus = status;
	}

	public CLinkStatus getStatusLink() {
		return myLinkStatus;
	}

	public void setSite(AbstractSite csite) {
		myLinkedSite = csite;
		if (myLinkedSite != null)
			myLinkStatus = CLinkStatus.BOUND;
	}

	public CLinkRank getStatusLinkRank() {
		switch (myLinkStatus) {
		case BOUND:
			if (myLinkedSite != null)
				return CLinkRank.BOUND;
			else
				return CLinkRank.SEMI_LINK;
		case WILDCARD:
			return CLinkRank.BOUND_OR_FREE;
		default:
			return CLinkRank.FREE;
		}
	}
}
