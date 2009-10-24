package com.plectix.simulator.staticanalysis.abstracting;

import com.plectix.simulator.staticanalysis.InternalState;
import com.plectix.simulator.staticanalysis.LinkRank;
import com.plectix.simulator.staticanalysis.NamedEntity;
import com.plectix.simulator.staticanalysis.Site;

/**
 * This method implements abstract site.
 * 
 * @author avokhmin
 * 
 */
public final class AbstractSite extends NamedEntity {
	private static final String DEFAULT_NAME = Site.DEFAULT_NAME;
	private final String name;
	private AbstractLinkState linkState;
	private InternalState internalState = InternalState.EMPTY_STATE;
	private AbstractAgent parentAgent = null;

	/**
	 * Constructor of CContactMapAbstractSite
	 * 
	 * @param site
	 *            given site for abstraction
	 * @param agent
	 *            "parent" agent
	 */
	public AbstractSite(Site site, AbstractAgent agent) {
		this.name = site.getName();
		this.parentAgent = agent;
		if (site.getInternalState() != InternalState.EMPTY_STATE)
			this.internalState = new InternalState(site.getInternalState().getName());
		this.linkState = new AbstractLinkState(site.getLinkState());
	}

	public AbstractSite(AbstractAgent agent, String name) {
		this.name = name;
		this.parentAgent = agent;
		this.linkState = new AbstractLinkState();
	}

	/**
	 * Constructor of CContactMapAbstractSite
	 * 
	 * @param site
	 *            given site for abstraction
	 */
	public AbstractSite(Site site) {
		this.name = site.getName();
		this.linkState = new AbstractLinkState();
	}

	/**
	 * Constructor of CContactMapAbstractSite
	 * 
	 * @param site
	 *            given abstract site
	 */
	public AbstractSite(AbstractSite site) {
		this.name = site.getName();
		this.parentAgent = site.getParentAgent();
		if (site.getInternalState() != InternalState.EMPTY_STATE)
			this.internalState = new InternalState(site.getInternalState().getName());
		this.linkState = new AbstractLinkState(site.getLinkState());
	}

	/**
	 * This method returns internal state for current site.
	 * 
	 * @return internal state for current site.
	 */
	public final InternalState getInternalState() {
		return internalState;
	}

	/**
	 * Returns link state of this site.
	 * 
	 * @return link state of this site.
	 */
	public final AbstractLinkState getLinkState() {
		return linkState;
	}

	/**
	 * This method returns name of this site
	 * 
	 * @see com.plectix.simulator.util.NameDictionary NameDictionary
	 * @return name of this agent
	 */
	public final String getName() {
		return name;
	}

	/**
	 * This method returns agent, which is parent for this site
	 * 
	 * @return agent, which is parent for this site
	 */
	public final AbstractAgent getParentAgent() {
		return parentAgent;
	}

	/**
	 * This method sets link to the "parent" agent.
	 * 
	 * @param parentAgent
	 *            "parent" agent
	 */
	public final void setParentAgent(AbstractAgent parentAgent) {
		this.parentAgent = parentAgent;
	}

	/**
	 * This method returns <tt>true</tt>, if current site equals to given site
	 * (by name, internal and link state), otherwise <tt>false</tt>.
	 * 
	 * @param site
	 *            given site
	 * @return <tt>true</tt>, if current site equals to given site (by name,
	 *         internal and link state), otherwise <tt>false</tt>.
	 */
	public final boolean equalz(AbstractSite site) {
		if (this == site) {
			return true;
		}

		if (site == null) {
			return false;
		}

		if (!this.hasSimilarName(site))
			return false;

		if (!internalState.hasSimilarName(site.getInternalState()))
			return false;

		if (!linkState.equalz(site.getLinkState()))
			return false;

		return true;
	}

	/**
	 * This method returns <tt>true</tt> if current site does fit to given site,
	 * otherwise <tt>false</tt>.
	 * 
	 * @param site
	 *            given site
	 * @return <tt>true</tt> if current site does fit to given site, otherwise
	 *         <tt>false</tt>.
	 */
	public final boolean isFit(AbstractSite site) {
		if (this.hasDefaultName())
			return true;
		if (!internalState.compareInternalStates(site.getInternalState()))
			return false;
		if (!linkState.compareLinkStates(site.getLinkState()))
			return false;

		return true;
	}

	public final void addStates(Site site) {
		if (site == null)
			return;
		if (site.getInternalState() != InternalState.EMPTY_STATE)
			this.internalState = new InternalState(site.getInternalState()
					.getName());
		this.linkState = new AbstractLinkState(site.getLinkState());
	}

	public final void addStates(AbstractSite site) {
		if (site == null)
			return;
		if (site.getInternalState() != InternalState.EMPTY_STATE)
			this.internalState = new InternalState(site.getInternalState()
					.getName());
		if (site.getLinkState().getStatusLinkRank() != LinkRank.SEMI_LINK
				&& site.getLinkState().getStatusLinkRank() != LinkRank.BOUND_OR_FREE)
			this.linkState = new AbstractLinkState(site.getLinkState());
	}
	
	public final void setLinkState(AbstractLinkState newState){
		this.linkState = newState;		
	}
	
	@Override
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("site = " + getName());
		sb.append(" from agent = " + parentAgent.getName());
		// TODO seems that we haven't got this case anytime
		if (this.hasDefaultName())
			return sb.toString();

		if (!internalState.hasDefaultName())
			sb.append(" internal state = " + internalState.getName());
		if (linkState != null && !linkState.getConnectedSiteName().equals(Site.DEFAULT_NAME)) {
			sb.append(" bound with agent = "
					+ linkState.getAgentName());
			sb.append(" through site = "
					+ linkState.getConnectedSiteName());
			// if (linkState.getInternalStateNameID() != -1)
			// sb.append(" link istate = "
			// + ThreadLocalData.getNameDictionary().getName(
			// linkState.getInternalStateNameID()));
		}
		return sb.toString();
	}
	
	@Override
	public final AbstractSite clone() {
		AbstractSite siteOut = new AbstractSite(this);
		return siteOut;
	}

	@Override
	protected String getDefaultName() {
		return DEFAULT_NAME;
	}
}
