package com.plectix.simulator.components.complex.abstracting;

import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CLink;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CSite;
/**
 * This class implements link state. Uses in Contact map.
 * @author avokhmin
 *
 */
public class CAbstractLinkState {
	private CLinkRank statusLinkRank;
	private CLinkStatus statusLink;
	private int linkSiteNameID = CSite.NO_INDEX;
	private int agentNameID = CSite.NO_INDEX;
//	private int internalStateNameID = CSite.NO_INDEX;

	/**
	 * Constructor of CContactMapLinkState.
	 * @param linkState given link state for abstraction
	 */
	public CAbstractLinkState(CLink linkState) {
		if (linkState.getConnectedSite() != null) {
			this.agentNameID = linkState.getConnectedSite().getAgentLink().getNameId();
			this.linkSiteNameID = linkState.getConnectedSite().getNameId();
//			this.internalStateNameID = linkState.getConnectedSite().getInternalState()
//					.getNameId();
		}
		this.statusLinkRank = linkState.getStatusLinkRank();
		this.statusLink = linkState.getStatusLink();
	}

	/**
	 * Constructor of CContactMapLinkState
	 * @param linkState given link state
	 */
	public CAbstractLinkState(CAbstractLinkState linkState) {
		if (linkState.getLinkSiteNameID() != -1) {
			this.agentNameID = linkState.getAgentNameID();
			this.linkSiteNameID = linkState.getLinkSiteNameID();
//			this.internalStateNameID = linkState.getInternalStateNameID();
		}
		this.statusLink = linkState.getStatusLink();
		this.statusLinkRank = linkState.getStatusLinkRank();
	}

	/**
	 * Sets status of this link to a given one
	 * @param status new value
	 */
	public void setStatusLink(CLinkStatus status) {
		this.statusLink = status;
		if (status == CLinkStatus.BOUND)
			statusLinkRank = CLinkRank.BOUND;
		else
			statusLinkRank = CLinkRank.FREE;
	}

	/**
	 * This method sets id of link site
	 * @param id given id
	 */
	public void setLinkSiteNameID(int id) {
		this.linkSiteNameID = id;
	}

	/**
	 * This method sets id of link agent
	 * @param id given id
	 */
	public void setAgentNameID(int id) {
		this.agentNameID = id;
	}

	/**
	 * Returns the rank of the status link (according to the Simulation Engine
	 * Specification part 2). We use this one to compare links.
	 * @return status-rank of this link
	 */
	public final CLinkRank getStatusLinkRank() {
		switch (statusLink) {
		case BOUND:
			if (linkSiteNameID != CSite.NO_INDEX)
				return CLinkRank.BOUND;
			else
				return CLinkRank.SEMI_LINK;
		case WILDCARD:
			return CLinkRank.BOUND_OR_FREE;
		default:
			return CLinkRank.FREE;
		}
	}

	/**
	 * This method returns current status of this link
	 * @return current status of this link
	 */
	public CLinkStatus getStatusLink(){
		return statusLink;
	}

	/**
	 * This method returns id of link site
	 * @return id of link site
	 */
	public int getLinkSiteNameID() {
		return linkSiteNameID;
	}

	/**
	 * This method returns id of link agent
	 * @return id of link agent
	 */
	public int getAgentNameID() {
		return agentNameID;
	}

//	/**
//	 * This method returns id of internal state link site
//	 * @return id of internal state link site
//	 */
//	public int getInternalStateNameID() {
//		return internalStateNameID;
//	}
//
//	/**
//	 * This method sets id of internal state link site
//	 * @param id given id
//	 */
//	public void setInternalStateNameID(int id) {
//		this.internalStateNameID = id;
//	}

	/**
	 * This method sets this link free
	 */
	public final void setFreeLinkState() {
		statusLink = CLinkStatus.FREE;
		statusLinkRank = CLinkRank.FREE;
		linkSiteNameID = CSite.NO_INDEX;
		agentNameID = CSite.NO_INDEX;
//		internalStateNameID = CSite.NO_INDEX;
	}

	/**
	 * Constructor of CContactMapLinkState
	 */
	public CAbstractLinkState() {
		setFreeLinkState();
	}

	/**
	 * This method returns <tt>true</tt> if current link state equals to given link state, otherwise <tt>false</tt> <br>
	 * Equals by:
	 * <li><b>statusLinkRank</b></li>
	 * <li>link agent nameId</li>
	 * <li>link site nameId</li>
	 * <li>internal state from link site<br>
	 *  if internal state from current/given link state does "EMPTY" returns <tt>true</tt>, otherwise compare their id.
	 * </li>
	 * @param linkState given state for checks
	 * @return <tt>true</tt> if current state equals to given state, otherwise <tt>false</tt>
	 */
	public boolean equalz(CAbstractLinkState linkState) {
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

		if (this.linkSiteNameID != linkState.getLinkSiteNameID())
			return false;

//		if (internalStateNameID == CSite.NO_INDEX
//				|| linkState.getInternalStateNameID() == CSite.NO_INDEX)
//			return true;
//
//		if (this.internalStateNameID != linkState.getInternalStateNameID())
//			return false;

		return true;
	}

	/**
	 * This method compares this link with the other one and returns true or false, according
	 * to the fixed order of link-status ranks. 
	 * @see CLinkRank
	 * @param solutionLinkState the other link to compare to
	 * @return <tt>true</tt> if this link's status-rank is "smaller" then status rank of the other link 
	 */
	public final boolean compareLinkStates(
			CAbstractLinkState solutionLinkState) {
		if (this.isLeftBranchStatus()
				&& solutionLinkState.isRightBranchStatus())
			return false;
		if (this.isRightBranchStatus()
				&& solutionLinkState.isLeftBranchStatus())
			return false;

		if (this.getStatusLinkRank().lessPriority(
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

	private final boolean isLeftBranchStatus() {
		return (statusLink == CLinkStatus.FREE) ? true : false;
	}

	private final boolean isRightBranchStatus() {
		return (statusLink == CLinkStatus.BOUND) ? true : false;
	}

}
