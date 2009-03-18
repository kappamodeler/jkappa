package com.plectix.simulator.components.contactMap;

import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;
import com.plectix.simulator.interfaces.ILinkState;
import com.plectix.simulator.interfaces.ISite;

public class CContactMapLinkState {
	private CLinkRank statusLinkRank;
	private CLinkStatus statusLink;
	private int linkSiteNameID = CSite.NO_INDEX;
	private int agentNameID = CSite.NO_INDEX;
//	private int agentID = CSite.NO_INDEX;
	private int internalStateNameID = CSite.NO_INDEX;

//	public int getAgentID() {
//		return agentID;
//	}
//
//	public void setAgentID(int agentID) {
//		this.agentID = agentID;
//	}
	
	public void setStatusLink(CLinkStatus status){
		this.statusLink= status;
		if(status == CLinkStatus.BOUND)
			statusLinkRank = CLinkRank.BOUND;
		else
			statusLinkRank = CLinkRank.FREE;
	}
	
	public void setLinkSiteNameID(int id){
		this.linkSiteNameID = id;
	}
	
	public void setAgentNameID(int id){
		this.agentNameID = id;
	}
	
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
	
	public void setInternalStateNameID(int id){
		this.internalStateNameID = id;
	}

	public final void setFreeLinkState() {
		statusLink = CLinkStatus.FREE;
		statusLinkRank = CLinkRank.FREE;
		linkSiteNameID = CSite.NO_INDEX;
		agentNameID = CSite.NO_INDEX;
		internalStateNameID = CSite.NO_INDEX;
	}

	public CContactMapLinkState() {
		setFreeLinkState();
	}

	public CContactMapLinkState(ILinkState linkState) {
		if (linkState.getSite() != null) {
			this.agentNameID = linkState.getSite().getAgentLink().getNameId();
			this.linkSiteNameID = linkState.getSite().getNameId();
			this.internalStateNameID = linkState.getSite().getInternalState()
					.getNameId();
		}
		this.statusLinkRank = linkState.getStatusLinkRank();
		this.statusLink = linkState.getStatusLink();
	}

	public CContactMapLinkState(CContactMapLinkState linkState) {
		if (linkState.getLinkSiteNameID() != -1) {
			this.agentNameID = linkState.getAgentNameID();
			this.linkSiteNameID = linkState.getLinkSiteNameID();
			this.internalStateNameID = linkState.getInternalStateNameID();
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

	public final boolean compareLinkStates(
			CContactMapLinkState solutionLinkState) {
		if (this.isLeftBranchStatus()
				&& solutionLinkState.isRightBranchStatus())
			return false;
		if (this.isRightBranchStatus()
				&& solutionLinkState.isLeftBranchStatus())
			return false;

		if (this.getStatusLinkRank().smaller(
				solutionLinkState.getStatusLinkRank()))
			return true;

		if (this.getStatusLinkRank() == solutionLinkState.getStatusLinkRank()
				&& this.getStatusLinkRank() == CLinkRank.BOUND)
			if (this.equalz(solutionLinkState))
				return true;

		if (this.getStatusLinkRank() == solutionLinkState.getStatusLinkRank()
				&& this.getStatusLinkRank() != CLinkRank.BOUND)
			return true;

		return false;
	}

	public final boolean isLeftBranchStatus() {
		return (statusLink == CLinkStatus.FREE) ? true : false;
	}

	public final boolean isRightBranchStatus() {
		return (statusLink == CLinkStatus.BOUND) ? true : false;
	}

	protected CContactMapLinkState clone() {
		return new CContactMapLinkState(this);
	}
}
