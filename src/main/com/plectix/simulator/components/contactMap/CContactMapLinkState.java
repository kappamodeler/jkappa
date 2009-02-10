package com.plectix.simulator.components.contactMap;

import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.ILinkState;

public class CContactMapLinkState {
	private CLinkRank statusLinkRank;
	private int linkSiteNameID = CSite.NO_INDEX;
	private int agentNameID = CSite.NO_INDEX;
	private int internalStateNameID = CSite.NO_INDEX;

	public CLinkRank getStatusLinkRank() {
		return statusLinkRank;
	}

	public int getLinkSiteNameID() {
		return linkSiteNameID;
	}

	public int getAgentNameID() {
		return agentNameID;
	}

	public int getInternalStateNameID() {
		return internalStateNameID;
	}

	public CContactMapLinkState(ILinkState linkState) {
		if (linkState.getSite() != null) {
			this.agentNameID = linkState.getSite().getAgentLink().getNameId();
			this.linkSiteNameID = linkState.getSite().getNameId();
			this.internalStateNameID = linkState.getSite().getInternalState()
					.getNameId();
		}
		this.statusLinkRank = linkState.getStatusLinkRank();
	}

	public boolean equalz(CContactMapLinkState linkState) {
		if (this == linkState) {
			return true;
		}

		if (linkState == null) {
			return false;
		}

		if (this.statusLinkRank != linkState.getStatusLinkRank())
			return false;

		if (this.agentNameID != linkState.getAgentNameID())
			return false;

		if (this.internalStateNameID != linkState.getInternalStateNameID())
			return false;

		if (this.linkSiteNameID != linkState.getLinkSiteNameID())
			return false;

		return true;
	}
}
