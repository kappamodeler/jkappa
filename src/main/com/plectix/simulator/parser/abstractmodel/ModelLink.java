package com.plectix.simulator.parser.abstractmodel;

import com.plectix.simulator.staticanalysis.LinkRank;
import com.plectix.simulator.staticanalysis.LinkStatus;

public final class ModelLink {
	private LinkStatus linkStatus;
	private ModelSite linkedSite = null;
	
	public ModelLink() {
		linkStatus = LinkStatus.FREE;
	}

	public final void setStatusLink(LinkStatus status) {
		linkStatus = status;
	}

	public final LinkStatus getStatusLink() {
		return linkStatus;
	}

	public final void setSite(ModelSite csite) {
		linkedSite = csite;
		if (linkedSite != null)
			linkStatus = LinkStatus.BOUND;
	}

	public final LinkRank getStatusLinkRank() {
		switch (linkStatus) {
		case BOUND:
			if (linkedSite != null)
				return LinkRank.BOUND;
			else
				return LinkRank.SEMI_LINK;
		case WILDCARD:
			return LinkRank.BOUND_OR_FREE;
		default:
			return LinkRank.FREE;
		}
	}
}
