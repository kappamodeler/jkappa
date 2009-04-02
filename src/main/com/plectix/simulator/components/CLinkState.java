package com.plectix.simulator.components;

import java.io.Serializable;

import com.plectix.simulator.interfaces.*;

/**
 * @see CSite
 * @see CLinkStatus
 * @author avokhmin
 *
 */
public final class CLinkState extends CState implements ILinkState, Serializable {

	/**
	 * No connections to LinkState.
	 */
	public static final byte NULL_INDEX = -1;

	/**
	 * {@link CLinkStatus} value - status current LinkState.
	 */
	private CLinkStatus statusLink;
	
	/**
	 * <code>{@link CSite}</code> value - Site connects to current LinkState.
	 */
	private CSite linkSite = null;
	
	/**
	 * Id of connection in ConnectedComponent.
	 */
	private int linkStateID = NULL_INDEX;
	
	/**
	 * Constructor of LinkState.
	 * @param site - <code>{@link CSite}</code> value - Site connects to current LinkState.
	 * @param statusLink - <code>{@link CLinkStatus}</code> value - status current LinkState.
	 * @see CLinkStatus
	 */
	public CLinkState(CSite site, CLinkStatus statusLink) {
		linkSite = site;
		this.statusLink = statusLink;
	}

	/**
	 * Constructor of LinkState.
	 * @param statusLink - <code>{@link CLinkStatus}</code> value - status current LinkState.
	 */
	public CLinkState(CLinkStatus statusLink) {
		this.statusLink = statusLink;
	}
	
	/**
	 * Returns <code>{@link Integer}</code> value - id of connection in ConnectedComponent.
	 */
	public int getLinkStateID() {
		return linkStateID;
	}

	/**
	 * Sets id of connection in ConnectedComponent.
	 * @param linkSiteID - <code>{@link Integer}</code> value.
	 */
	public void setLinkStateID(int linkSiteID) {
		this.linkStateID = linkSiteID;
	}

	/**
	 * Sets free LinkState:
	 * <p>
	 * <b>statusLink</b> - sets <code>CLinkStatus.FREE</code>;<br>
	 * <b>linkSite</b> - sets "null";<br>
	 * <b>linkStateID</b> - sets "NULL_INDEX".
	 */
	public final void setFreeLinkState() {
		statusLink = CLinkStatus.FREE;
		linkSite = null;
		linkStateID = NULL_INDEX;
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

	/**
	 * Returns <code>{@link CSite}</code> value - site, connects to current LinkState.
	 */
	public final CSite getSite() {
		return linkSite;
	}

	/**
	 * Sets to connect with <code>{@link CSite site}</code>.
	 * @param site - <code>{@link CSite}</code> value.
	 */
	public final void setSite(CSite site) {
		linkSite = site;
		if (linkSite != null)
			statusLink = CLinkStatus.BOUND;
	}

	/**
	 * Sets <code>{@link CLinkStatus statusLink}</code> current LinkState.
	 * @param statusLink - <code>{@link CLinkStatus}</code> value.
	 */
	public final void setStatusLink(CLinkStatus statusLink) {
		this.statusLink = statusLink;
	}

	/**
	 * Returns <code>{@link CLinkStatus}</code> value current LinkState.
	 */
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

	/**
	 * Returns "null", doesn't implement.
	 */
	public final String getName() {
		return null;
	}
	
	// TODO create comments
	public final boolean fullEqualityLinkStates(ILinkState solutionLinkState) {
		if (this.getStatusLinkRank() == solutionLinkState
				.getStatusLinkRank()
				&& this.getStatusLinkRank() == CLinkRank.BOUND)
			if (this.getSite().equalz(solutionLinkState.getSite()))
				return true;

		if (this.getStatusLinkRank() == solutionLinkState
				.getStatusLinkRank()
				&& this.getStatusLinkRank() != CLinkRank.BOUND)
			return true;
		return false;
	}
	
	// TODO create comments
	public boolean compareLinkStates(ILinkState solutionLinkState) {
		if (this.isLeftBranchStatus()
				&& solutionLinkState.isRightBranchStatus())
			return false;
		if (this.isRightBranchStatus()
				&& solutionLinkState.isLeftBranchStatus())
			return false;

		if (this.getStatusLinkRank().smaller(
				solutionLinkState.getStatusLinkRank()))
			return true;

		if (this.getStatusLinkRank() == solutionLinkState
				.getStatusLinkRank()
				&& this.getStatusLinkRank() == CLinkRank.BOUND)
			if (this.getSite().equalz(solutionLinkState.getSite()))
				return true;

		if (this.getStatusLinkRank() == solutionLinkState
				.getStatusLinkRank()
				&& this.getStatusLinkRank() != CLinkRank.BOUND)
			return true;

		return false;
	}
}
