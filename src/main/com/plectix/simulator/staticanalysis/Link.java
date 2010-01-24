package com.plectix.simulator.staticanalysis;


/**
 * This class implements "link" entity.
 * The main character of link if status. It can be bound, wildcard of free.<br>
 * Also there is site which this link belongs to.<br>
 * "Bound" status means that this site connected with another site.<br>
 * "Free" status means that this site is free from connections.<br>
 * "Wildcard" status means that we no know nothing about this site's connections.<br>
 * 
 * @see LinkStatus
 * @author evlasov
 *
 */
public final class Link {
	private LinkStatus statusLink;	
	private Site linkSite = null;
	
	private int linkStateId = NULL_INDEX;
	private static final byte NULL_INDEX = -1;
	
	/**
	 * Constructor
	 * @param statusLink status of new link 
	 */
	public Link() {
		this.statusLink = LinkStatus.FREE;
	}
	
	/**
	 * Util method using by toString convertation
	 */
	public final int getLinkStateId() {
		return linkStateId;
	}

	/**
	 * Util method using by toString convertation
	 */
	public final void setLinkStateId(int linkStateId) {
		this.linkStateId = linkStateId;
	}

	/**
	 * This method sets this link free
	 */
	public final void setFree() {
		statusLink = LinkStatus.FREE;
		linkSite = null;
		linkStateId = NULL_INDEX;
	}
	
	public final void setWildLinkState(){
		statusLink = LinkStatus.WILDCARD;
		
		linkSite = null;
		linkStateId = NULL_INDEX;
	}
	
	public final void setSemiLink() {
		statusLink = LinkStatus.BOUND;
		linkSite = null;
		linkStateId = NULL_INDEX;
	}	
	
	/**
	 * This method returns <tt>true</tt> if status of this link is "free"
	 * @return <tt>true</tt> if status of this link is "free", otherwise <tt>false</tt>
	 */
	final boolean hasFreeStatus() {
		return statusLink == LinkStatus.FREE;
	}

	/**
	 * This method returns site which connected with the "parent" site of this link (using this link), 
	 * or <tt>null</tt>, if there's no such
	 * @return site which connected with the "parent" site of this link (using this link), 
	 * or <tt>null</tt>, if there's no such
	 */
	public final Site getConnectedSite() {
		return linkSite;
	}

	/**
	 * This method sets given site as connected with the "parent" one. 
	 * @param site given site
	 */
	public final void connectSite(Site site) {
		linkSite = site;
		if (linkSite != null)
			statusLink = LinkStatus.BOUND;
	}

	/**
	 * Sets status of this link to a given one
	 * @param newStatus new value
	 */
	public final void setStatusLink(LinkStatus newStatus) {
		this.statusLink = newStatus;
	}

	/**
	 * This method returns current status of this link
	 * @return current status of this link
	 */
	public final LinkStatus getStatusLink() {
		return statusLink;
	}

	/**
	 * Returns the rank of the status link (according to the Simulation Engine
	 * Specification part 2). We use this one to compare links.
	 * @return status-rank of this link
	 */
	public final LinkRank getStatusLinkRank() {
		switch (statusLink) {
		case BOUND:
			if (linkSite != null)
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
	 * This method compares this link with the other one for being equal.
	 * @param link the other link to compare to
	 * @return <tt>true</tt> if this link equals other link, otherwise <tt>false</tt> 
	 */
	public final boolean equalz(Link link) {
		if (this.getStatusLinkRank() == link
				.getStatusLinkRank()
				&& this.getStatusLinkRank() == LinkRank.BOUND)
			if (this.getConnectedSite().equalz(link.getConnectedSite()))
				return true;

		if (this.getStatusLinkRank() == link
				.getStatusLinkRank()
				&& this.getStatusLinkRank() != LinkRank.BOUND)
			return true;
		return false;
	}
	
	/**
	 * This method compares this link with the other one and returns true or false, according
	 * to the fixed order of link-status ranks. 
	 * @see LinkRank
	 * @param link the other link to compare to
	 * @return <tt>true</tt> if this link's status-rank is "smaller" then status rank of the other link 
	 */
	public final boolean compare(Link link) {
		if (this.hasFreeStatus()
				&& link.statusLink == LinkStatus.BOUND)
			return false;
		if (statusLink == LinkStatus.BOUND
				&& link.hasFreeStatus())
			return false;

		if (this.getStatusLinkRank().lessPriority(
				link.getStatusLinkRank()))
			return true;

		if (this.getStatusLinkRank() == link
				.getStatusLinkRank()
				&& this.getStatusLinkRank() == LinkRank.BOUND)
			if (this.getConnectedSite().equalz(link.getConnectedSite()))
				return true;

		if (this.getStatusLinkRank() == link
				.getStatusLinkRank()
				&& this.getStatusLinkRank() != LinkRank.BOUND)
			return true;

		return false;
	}
}
