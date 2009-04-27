package com.plectix.simulator.components.contactMap;

import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.simulator.ThreadLocalData;

/**
 * This method implements abstract site.
 * @author avokhmin
 *
 */
public class CContactMapAbstractSite{
	public static final int NO_INDEX = -1;
	private final int nameId;
	private CContactMapLinkState linkState;
	private CInternalState internalState = CInternalState.EMPTY_STATE;
	private CContactMapAbstractAgent linkAgent = null;

	/**
	 * Constructor of CContactMapAbstractSite
	 * @param site given site for abstraction
	 * @param agent "parent" agent
	 */
	public CContactMapAbstractSite(CSite site, CContactMapAbstractAgent agent) {
		this.nameId = site.getNameId();
		this.linkAgent = agent;
		if (site.getInternalState() != CInternalState.EMPTY_STATE)
			this.internalState = new CInternalState(site.getInternalState()
					.getNameId());
		this.linkState = new CContactMapLinkState(site.getLinkState());
	}

	/**
	 * Constructor of CContactMapAbstractSite
	 * @param site given site for abstraction
	 */
	public CContactMapAbstractSite(CSite site) {
		this.nameId = site.getNameId();
		this.linkState = new CContactMapLinkState();
	}

	/**
	 * Constructor of CContactMapAbstractSite
	 * @param site given abstract site
	 */
	private CContactMapAbstractSite(CContactMapAbstractSite site) {
		this.nameId = site.getNameId();
		this.linkAgent = site.getAgentLink();
		if (site.getInternalState() != CInternalState.EMPTY_STATE)
			this.internalState = new CInternalState(site.getInternalState()
					.getNameId());
		this.linkState = new CContactMapLinkState(site.getLinkState());
	}

	public CContactMapAbstractSite clone() {
		CContactMapAbstractSite siteOut = new CContactMapAbstractSite(this);
		return siteOut;
	}

	/**
	 * This method returns internal state for current site.
	 * @return internal state for current site.
	 */
	public CInternalState getInternalState() {
		return internalState;
	}

	/**
	 * Returns link state of this site.
	 * @return link state of this site.
	 */
	public CContactMapLinkState getLinkState() {
		return linkState;
	}

	/**
	 * This method returns name of this site
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
	 * @return name-id of this site
	 */
	public int getNameId() {
		return nameId;
	}

	/**
	 * This method returns agent, which is parent for this site
	 * @return agent, which is parent for this site 
	 */
	public final CContactMapAbstractAgent getAgentLink() {
		return linkAgent;
	}

	/**
	 * This method sets link to the "parent" agent. 
	 * @param linkAgent "parent" agent
	 */
	public final void setAgentLink(CContactMapAbstractAgent linkAgent) {
		this.linkAgent = linkAgent;
	}

	/**
	 * This method returns <tt>true</tt>, if current site equals 
	 * to given site (by nameId, internal and link state), otherwise <tt>false</tt>.
	 * @param site given site
	 * @return <tt>true</tt>, if current site equals 
	 * to given site (by nameId, internal and link state), otherwise <tt>false</tt>.
	 */
	public final boolean equalz(CContactMapAbstractSite site) {
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
	 * This method returns <tt>true</tt> if <b>nameId</b> current site equals <b>nameId</b> given site, otherwise <tt>false</tt>.
	 * @param site given site
	 * @return <tt>true</tt> if <b>nameId</b> current site equals <b>nameId</b> given site, otherwise <tt>false</tt>.
	 */
	public final boolean equalByName(CContactMapAbstractSite site) {
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
		if (nameId == NO_INDEX)
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
			if (linkState.getInternalStateNameID() != -1)
				sb.append(" link istate = "
						+ ThreadLocalData.getNameDictionary().getName(
								linkState.getInternalStateNameID()));
		}
		return sb.toString();
	}

	/**
	 * This method returns <tt>true</tt> if current site does fit to given site, otherwise <tt>false</tt>.
	 * @param s given site
	 * @return <tt>true</tt> if current site does fit to given site, otherwise <tt>false</tt>.
	 */
	public boolean isFit(CContactMapAbstractSite s) {
		if (nameId == CSite.NO_INDEX)
			return true;
		if (!internalState.compareInternalStates(s.getInternalState()))
			return false;
		if (!linkState.compareLinkStates(s.getLinkState()))
			return false;

		return true;
	}

}
