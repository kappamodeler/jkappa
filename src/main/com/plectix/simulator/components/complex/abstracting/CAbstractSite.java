package com.plectix.simulator.components.complex.abstracting;

import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.simulator.ThreadLocalData;

/**
 * This method implements abstract site.
 * 
 * @author avokhmin
 * 
 */
public class CAbstractSite {
	private final int nameId;
	private CAbstractLinkState linkState;
	private CInternalState internalState = CInternalState.EMPTY_STATE;
	private CAbstractAgent linkAgent = null;

	/**
	 * Constructor of CContactMapAbstractSite
	 * 
	 * @param site
	 *            given site for abstraction
	 * @param agent
	 *            "parent" agent
	 */
	public CAbstractSite(CSite site, CAbstractAgent agent) {
		this.nameId = site.getNameId();
		this.linkAgent = agent;
		if (site.getInternalState() != CInternalState.EMPTY_STATE)
			this.internalState = new CInternalState(site.getInternalState()
					.getNameId());
		this.linkState = new CAbstractLinkState(site.getLinkState());
	}

	public CAbstractSite(CAbstractAgent agent, int nameId) {
		this.nameId = nameId;
		this.linkAgent = agent;
		this.linkState = new CAbstractLinkState();
	}

	/**
	 * Constructor of CContactMapAbstractSite
	 * 
	 * @param site
	 *            given site for abstraction
	 */
	public CAbstractSite(CSite site) {
		this.nameId = site.getNameId();
		this.linkState = new CAbstractLinkState();
	}

	/**
	 * Constructor of CContactMapAbstractSite
	 * 
	 * @param site
	 *            given abstract site
	 */
	private CAbstractSite(CAbstractSite site) {
		this.nameId = site.getNameId();
		this.linkAgent = site.getAgentLink();
		if (site.getInternalState() != CInternalState.EMPTY_STATE)
			this.internalState = new CInternalState(site.getInternalState()
					.getNameId());
		this.linkState = new CAbstractLinkState(site.getLinkState());
	}

	public CAbstractSite clone() {
		CAbstractSite siteOut = new CAbstractSite(this);
		return siteOut;
	}

	/**
	 * This method returns internal state for current site.
	 * 
	 * @return internal state for current site.
	 */
	public CInternalState getInternalState() {
		return internalState;
	}

	/**
	 * Returns link state of this site.
	 * 
	 * @return link state of this site.
	 */
	public CAbstractLinkState getLinkState() {
		return linkState;
	}

	/**
	 * This method returns name of this site
	 * 
	 * @see com.plectix.simulator.util.NameDictionary NameDictionary
	 * @return name of this agent
	 */
	public String getName() {
		if (nameId == CSite.NO_INDEX)
			return "EMPTY_SITE";
		return ThreadLocalData.getNameDictionary().getName(nameId);
	}

	/**
	 * This method returns name-id of this site
	 * 
	 * @return name-id of this site
	 */
	public int getNameId() {
		return nameId;
	}

	/**
	 * This method returns agent, which is parent for this site
	 * 
	 * @return agent, which is parent for this site
	 */
	public final CAbstractAgent getAgentLink() {
		return linkAgent;
	}

	/**
	 * This method sets link to the "parent" agent.
	 * 
	 * @param linkAgent
	 *            "parent" agent
	 */
	public final void setAgentLink(CAbstractAgent linkAgent) {
		this.linkAgent = linkAgent;
	}

	/**
	 * This method returns <tt>true</tt>, if current site equals to given site
	 * (by nameId, internal and link state), otherwise <tt>false</tt>.
	 * 
	 * @param site
	 *            given site
	 * @return <tt>true</tt>, if current site equals to given site (by nameId,
	 *         internal and link state), otherwise <tt>false</tt>.
	 */
	public final boolean equalz(CAbstractSite site) {
		if (this == site) {
			return true;
		}

		if (site == null) {
			return false;
		}

		if (nameId != site.nameId)
			return false;

		if (internalState.getNameId() != site.getInternalState().getNameId())
			return false;

		if (!linkState.equalz(site.getLinkState()))
			return false;

		return true;
	}

	/**
	 * This method returns <tt>true</tt> if <b>nameId</b> current site equals
	 * <b>nameId</b> given site, otherwise <tt>false</tt>.
	 * 
	 * @param site
	 *            given site
	 * @return <tt>true</tt> if <b>nameId</b> current site equals <b>nameId</b>
	 *         given site, otherwise <tt>false</tt>.
	 */
	public final boolean equalByName(CAbstractSite site) {
		if (this == site) {
			return true;
		}

		if (site == null) {
			return false;
		}

		if (nameId != site.nameId)
			return false;

		return true;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("site = " + getName());
		sb.append(" from agent = " + linkAgent.getName());
		// TODO seems that we haven't got this case anytime
		if (nameId == -1)
			return sb.toString();

		if (internalState.getNameId() != -1)
			sb.append(" internal state = " + internalState.getName());
		if (linkState != null && linkState.getLinkSiteNameID() != -1) {
			sb.append(" link agent = "
					+ ThreadLocalData.getNameDictionary().getName(
							linkState.getAgentNameID()));
			sb.append(" link site = "
					+ ThreadLocalData.getNameDictionary().getName(
							linkState.getLinkSiteNameID()));
			// if (linkState.getInternalStateNameID() != -1)
			// sb.append(" link istate = "
			// + ThreadLocalData.getNameDictionary().getName(
			// linkState.getInternalStateNameID()));
		}
		return sb.toString();
	}

	/**
	 * This method returns <tt>true</tt> if current site does fit to given site,
	 * otherwise <tt>false</tt>.
	 * 
	 * @param s
	 *            given site
	 * @return <tt>true</tt> if current site does fit to given site, otherwise
	 *         <tt>false</tt>.
	 */
	public boolean isFit(CAbstractSite s) {
		if (nameId == CSite.NO_INDEX)
			return true;
		if (!internalState.compareInternalStates(s.getInternalState()))
			return false;
		if (!linkState.compareLinkStates(s.getLinkState()))
			return false;

		return true;
	}

	public void addStates(CSite site) {
		if (site == null)
			return;
		if (site.getInternalState() != CInternalState.EMPTY_STATE)
			this.internalState = new CInternalState(site.getInternalState()
					.getNameId());
		this.linkState = new CAbstractLinkState(site.getLinkState());
	}

	public void addStates(CAbstractSite site) {
		if (site == null)
			return;
		if (site.getInternalState() != CInternalState.EMPTY_STATE)
			this.internalState = new CInternalState(site.getInternalState()
					.getNameId());
		if (site.getLinkState().getStatusLinkRank() != CLinkRank.SEMI_LINK
				&& site.getLinkState().getStatusLinkRank() != CLinkRank.BOUND_OR_FREE)
			this.linkState = new CAbstractLinkState(site.getLinkState());
	}

	
	public void setLinkState(CAbstractLinkState newState){
		this.linkState = newState;
		
	}
}
