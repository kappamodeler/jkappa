package com.plectix.simulator.component.complex.abstracting;

import com.plectix.simulator.component.Agent;
import com.plectix.simulator.component.Link;
import com.plectix.simulator.component.LinkRank;
import com.plectix.simulator.component.LinkStatus;
import com.plectix.simulator.component.Site;
/**
 * This class implements link state. Uses in Contact map.
 * @author avokhmin
 *
 */
public final class AbstractLinkState {
	private LinkRank statusLinkRank;
	private LinkStatus statusLink;
	private String connectedSiteName = Site.DEFAULT_NAME;
	private String agentName = Agent.DEFAULT_NAME;

	/**
	 * Constructor of CContactMapLinkState
	 */
	public AbstractLinkState() {
		setFreeLinkState();
	}

	/**
	 * Constructor of CContactMapLinkState.
	 * @param linkState given link state for abstraction
	 */
	public AbstractLinkState(Link linkState) {
		if (linkState.getConnectedSite() != null) {
			this.agentName = linkState.getConnectedSite().getParentAgent().getName();
			this.connectedSiteName = linkState.getConnectedSite().getName();
		}
		this.statusLinkRank = linkState.getStatusLinkRank();
		this.statusLink = linkState.getStatusLink();
	}

	/**
	 * Constructor of CContactMapLinkState
	 * @param linkState given link state
	 */
	public AbstractLinkState(AbstractLinkState linkState) {
		if (!linkState.getConnectedSiteName().equals(Site.DEFAULT_NAME)) {
			this.agentName = linkState.getAgentName();
			this.connectedSiteName = linkState.getConnectedSiteName();
		}
		this.statusLink = linkState.getStatusLink();
		this.statusLinkRank = linkState.getStatusLinkRank();
	}

	/**
	 * Sets status of this link to a given one
	 * @param status new value
	 */
	public final void setStatusLink(LinkStatus status) {
		this.statusLink = status;
		if (status == LinkStatus.BOUND)
			statusLinkRank = LinkRank.BOUND;
		else
			statusLinkRank = LinkRank.FREE;
	}

	/**
	 * This method sets id of link site
	 * @param id given id
	 */
	public final void setLinkSiteName(String name) {
		this.connectedSiteName = name;
	}

	/**
	 * This method sets id of link agent
	 * @param id given id
	 */
	public final void setAgentName(String name) {
		this.agentName = name;
	}

	/**
	 * Returns the rank of the status link (according to the Simulation Engine
	 * Specification part 2). We use this one to compare links.
	 * @return status-rank of this link
	 */
	public final LinkRank getStatusLinkRank() {
		switch (statusLink) {
		case BOUND:
			if (!connectedSiteName.equals(Site.DEFAULT_NAME))
				return LinkRank.BOUND;
			else
				return LinkRank.SEMI_LINK;
		case WILDCARD:
			return LinkRank.BOUND_OR_FREE;
		default:
			return LinkRank.FREE;
		}
	}

	/**
	 * This method returns current status of this link
	 * @return current status of this link
	 */
	public final LinkStatus getStatusLink(){
		return statusLink;
	}

	/**
	 * This method returns id of link site
	 * @return id of link site
	 */
	public final String getConnectedSiteName() {
		return connectedSiteName;
	}

	/**
	 * This method returns id of link agent
	 * @return id of link agent
	 */
	public final String getAgentName() {
		return agentName;
	}

	private final void setDefaultNames() {
		connectedSiteName = Site.DEFAULT_NAME;
		agentName = Agent.DEFAULT_NAME;
	}
	/**
	 * This method sets this link free
	 */
	public final void setFreeLinkState() {
		statusLink = LinkStatus.FREE;
		statusLinkRank = LinkRank.FREE;
		setDefaultNames();
	}
	
	public final void setWildLinkState(){
		statusLink = LinkStatus.WILDCARD;
		statusLinkRank = LinkRank.BOUND_OR_FREE;
		setDefaultNames();
	}
	
	public final void setSemiLink() {
		statusLink = LinkStatus.BOUND;
		statusLinkRank = LinkRank.SEMI_LINK;
		setDefaultNames();
	}	

	/**
	 * This method returns <tt>true</tt> if current link state equals to given link state, otherwise <tt>false</tt> <br>
	 * Equals by:
	 * <li><b>statusLinkRank</b></li>
	 * <li>link agent name</li>
	 * <li>link site name</li>
	 * <li>internal state from link site<br>
	 *  if internal state from current/given link state does "EMPTY" returns <tt>true</tt>, otherwise compare their id.
	 * </li>
	 * @param linkState given state for checks
	 * @return <tt>true</tt> if current state equals to given state, otherwise <tt>false</tt>
	 */
	public final boolean equalz(AbstractLinkState linkState) {
		if (this == linkState) {
			return true;
		}

		if (linkState == null) {
			return false;
		}

		if (this.statusLinkRank != linkState.getStatusLinkRank())
			return false;

		if (!this.agentName.equals(linkState.getAgentName()))
			return false;

		if (!this.connectedSiteName.equals(linkState.getConnectedSiteName()))
			return false;

		return true;
	}

	/**
	 * This method compares this link with the other one and returns true or false, according
	 * to the fixed order of link-status ranks. 
	 * @see LinkRank
	 * @param solutionLinkState the other link to compare to
	 * @return <tt>true</tt> if this link's status-rank is "smaller" then status rank of the other link 
	 */
	public final boolean compareLinkStates(
			AbstractLinkState solutionLinkState) {
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
				&& this.getStatusLinkRank() == LinkRank.BOUND)
			if (this.equalz(solutionLinkState))
				return true;

		if (this.getStatusLinkRank() == solutionLinkState.getStatusLinkRank()
				&& this.getStatusLinkRank() != LinkRank.BOUND)
			return true;

		return false;
	}

	private final boolean isLeftBranchStatus() {
		return (statusLink == LinkStatus.FREE) ? true : false;
	}

	private final boolean isRightBranchStatus() {
		return (statusLink == LinkStatus.BOUND) ? true : false;
	}
}
