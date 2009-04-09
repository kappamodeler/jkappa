package com.plectix.simulator.components;

import java.io.Serializable;

/**
 * This class implements "link" entity.
 * The main character of link if status. It can be bound, wildcard of free.<br>
 * Also there is site which this link belongs to.<br>
 * "Bound" status means that this site connected with another site.<br>
 * "Free" status means that this site is free from connections.<br>
 * "Wildcard" status means that we no know nothing about this site's connections.<br>
 * 
 * @see CLinkStatus
 * @author evlasov
 *
 */
@SuppressWarnings("serial")
public final class CLink implements Serializable {
	private CLinkStatus statusLink;
	private CSite linkSite = null;
	
	private int linkStateID = NULL_INDEX;
	private static final byte NULL_INDEX = -1;
	
	/**
	 * Constructor
	 * @param statusLink status of new link 
	 */
	public CLink(CLinkStatus statusLink) {
		this.statusLink = statusLink;
	}
	
	/**
	 * Util method using by toString convertation
	 */
	public int getLinkStateID() {
		return linkStateID;
	}

	/**
	 * Util method using by toString convertation
	 */
	public void setLinkStateID(int linkSiteID) {
		this.linkStateID = linkSiteID;
	}

	/**
	 * This method sets this link free
	 */
	public final void setFree() {
		statusLink = CLinkStatus.FREE;
		linkSite = null;
		linkStateID = NULL_INDEX;
	}
	
	/**
	 * This method returns <tt>true</tt> if status of this link is "free"
	 * @return <tt>true</tt> if status of this link is "free", otherwise <tt>false</tt>
	 */
	public final boolean hasFreeStatus() {
		return statusLink == CLinkStatus.FREE;
	}

	/**
	 * This method returns site which connected with the "parent" site of this link (using this link), 
	 * or <tt>null</tt>, if there's no such
	 * @return site which connected with the "parent" site of this link (using this link), 
	 * or <tt>null</tt>, if there's no such
	 */
	public final CSite getConnectedSite() {
		return linkSite;
	}

	/**
	 * This method sets given site as connected with the "parent" one. 
	 * @param site given site
	 */
	public final void connectSite(CSite site) {
		linkSite = site;
		if (linkSite != null)
			statusLink = CLinkStatus.BOUND;
	}

	/**
	 * Sets status of this link to a given one
	 * @param newStatus new value
	 */
	public final void setStatusLink(CLinkStatus newStatus) {
		this.statusLink = newStatus;
	}

	/**
	 * This method returns current status of this link
	 * @return current status of this link
	 */
	public final CLinkStatus getStatusLink() {
		return statusLink;
	}

	/**
	 * Returns the rank of the status link (according to the Simulation Engine
	 * Specification part 2). We use this one to compare links.
	 * @return status-rank of this link
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
	 * This method compares this link with the other one for being equal.
	 * @param otherLink the other link to compare to
	 * @return <tt>true</tt> if this link equals other link, otherwise <tt>false</tt> 
	 */
	public final boolean equalz(CLink otherLink) {
		if (this.getStatusLinkRank() == otherLink
				.getStatusLinkRank()
				&& this.getStatusLinkRank() == CLinkRank.BOUND)
			if (this.getConnectedSite().equalz(otherLink.getConnectedSite()))
				return true;

		if (this.getStatusLinkRank() == otherLink
				.getStatusLinkRank()
				&& this.getStatusLinkRank() != CLinkRank.BOUND)
			return true;
		return false;
	}
	
	/**
	 * This method compares this link with the other one and returns true or false, according
	 * to the fixed order of link-status ranks. 
	 * @see CLinkRank
	 * @param otherLink the other link to compare to
	 * @return <tt>true</tt> if this link's status-rank is "smaller" then status rank of the other link 
	 */
	public boolean compare(CLink otherLink) {
		if (this.hasFreeStatus()
				&& otherLink.statusLink == CLinkStatus.BOUND)
			return false;
		if (statusLink == CLinkStatus.BOUND
				&& otherLink.hasFreeStatus())
			return false;

		if (this.getStatusLinkRank().lessPriority(
				otherLink.getStatusLinkRank()))
			return true;

		if (this.getStatusLinkRank() == otherLink
				.getStatusLinkRank()
				&& this.getStatusLinkRank() == CLinkRank.BOUND)
			if (this.getConnectedSite().equalz(otherLink.getConnectedSite()))
				return true;

		if (this.getStatusLinkRank() == otherLink
				.getStatusLinkRank()
				&& this.getStatusLinkRank() != CLinkRank.BOUND)
			return true;

		return false;
	}
}
