package com.plectix.simulator.component.complex.abstracting;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.plectix.simulator.component.Agent;
import com.plectix.simulator.component.NamedEntity;
import com.plectix.simulator.component.Site;

/**
 * This class implements abstract agent.<br>
 * SubView presented in the some form<br>
 * AbstractAgent include part or all information of real Agent<br>
 * Uses for construct Contact map.
 * 
 * @author avokhmin
 * 
 */
public final class AbstractAgent extends NamedEntity {
	private static final String DEFAULT_NAME = Agent.DEFAULT_NAME;
	private final AbstractSite defaultSite = new AbstractSite(this, Site.DEFAULT_NAME);
	private final String name;
	
	private Map<String, AbstractSite> sitesMap = new LinkedHashMap<String, AbstractSite>();
	private boolean add = false;

	/**
	 * Constructor of CContactMapAbstractAgent.<br>
	 * Sets <b>nameID</b> only. No fills sites map.
	 * 
	 * @param agent
	 *            given agent
	 */
	public AbstractAgent(Agent agent) {
		this.name = agent.getName();
		for (Site s : agent.getSites()) {
			AbstractSite newSite = new AbstractSite(s, this);
			addSite(newSite);
		}
	}

	public AbstractAgent(String name) {
		this.name = name;
	}

	/**
	 * Constructor of CContactMapAbstractAgent.<br>
	 * Sets <b>nameID</b>, fills sites map.
	 * 
	 * @param agent
	 *            given agent
	 */
	public AbstractAgent(AbstractAgent agent) {
		this.name = agent.getName();
		this.sitesMap = new LinkedHashMap<String, AbstractSite>();

		for (Map.Entry<String, AbstractSite> entry : agent.getSitesMap()
				.entrySet()) {
			AbstractSite newSite = entry.getValue().clone();
			newSite.setParentAgent(this);
			sitesMap.put(entry.getKey(), newSite);
		}
	}

	public AbstractAgent(Agent agent, AbstractAgent modelAgent) {
		this.name = agent.getName();
		this.sitesMap = new LinkedHashMap<String, AbstractSite>();
		for (Site site : agent.getSites()) {
			String key = site.getName();
			AbstractSite abstractSite = new AbstractSite(site, this);
			sitesMap.put(key, abstractSite);
		}

		for (Map.Entry<String, AbstractSite> entry : modelAgent.getSitesMap().entrySet()) {
			AbstractSite abstractSite = this.sitesMap.get(entry.getKey());
			if (abstractSite == null) {
				this.sitesMap.put(entry.getKey(), entry.getValue());
			}
		}

	}

	/**
	 * This method returns default site from current agent
	 * @return default site from current agent
	 */
	public final AbstractSite getDefaultSite() {
		return defaultSite;
	}
	

	/**
	 * This method returns character expression of current agent.
	 * 
	 * @return character expression of current agent.
	 */
	public final String getKey() {
		return this.toString() + " " + sitesMap.toString();
	}

	/**
	 * This method returns map of sites.
	 * 
	 * @return map of sites.
	 */
	public final Map<String, AbstractSite> getSitesMap() {
		return this.sitesMap;
	}

	/**
	 * This method returns site by given id
	 * 
	 * @param name
	 *            given id
	 * @return site from sites map.
	 */
	public final AbstractSite getSiteByName(String name) {
		return this.sitesMap.get(name);
	}

	/**
	 * This method adds given site to sites map.
	 * 
	 * @param newSite
	 *            given site
	 */
	public final void addSite(AbstractSite newSite) {
		this.sitesMap.put(newSite.getName(), newSite);
	}

	/**
	 * This method adds given site to sites map, if there hasn't it.
	 * 
	 * @param siteToAdd
	 *            given site
	 */
	public final void addModelSite(AbstractSite siteToAdd) {
		String name = siteToAdd.getName();
		AbstractSite site = this.sitesMap.get(name);
		if (site == null) {
			this.sitesMap.put(name, siteToAdd);
		}
	}

	/**
	 * This method returns <tt>true</tt> if current agent include in given list
	 * of agents.<br>
	 * Checks by name
	 * 
	 * @param collection
	 *            given collections of agents
	 * @return <tt>true</tt> if current agent include in given list of agents,
	 *         otherwise <tt>false</tt>
	 */
	public final boolean includedInCollectionByName(Collection<AbstractAgent> collection) {
		for (AbstractAgent agent : collection) {
			if (this.getName().equals(agent.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method returns <tt>true</tt> if given agent equals to current agent,
	 * otherwise <tt>false</tt>
	 * 
	 * @param agent
	 *            given agent
	 * @return <tt>true</tt> if given agent equals to current agent, otherwise
	 *         <tt>false</tt>
	 */
	public final boolean equalz(AbstractAgent agent) {
		if (this == agent) {
			return true;
		}

		if (agent == null) {
			return false;
		}

		if (!name.equals(agent.name))
			return false;

		if (this.sitesMap.size() != agent.getSitesMap().size())
			return false;

		if (!compareSites(sitesMap, agent.getSitesMap()))
			return false;
		return true;
	}

	/**
	 * Util method. Uses for compare given sites maps.
	 * 
	 * @param sitesMap1
	 *            given site map
	 * @param sitesMap2
	 *            given site map
	 * @return <tt>true</tt> if sites maps are equals, otherwise <tt>false</tt>
	 */
	private static final boolean compareSites(Map<String, AbstractSite> sitesMap1,
			Map<String, AbstractSite> sitesMap2) {

		for (Map.Entry<String, AbstractSite> entry : sitesMap1.entrySet()) {
			if (!entry.getValue().equalz(sitesMap2.get(entry.getKey())))
				return false;
		}

		return true;
	}

	/**
	 * This method returns name of this agent
	 * 
	 * @see com.plectix.simulator.util.NameDictionary NameDictionary
	 * @return name of this agent
	 */
	public final String getName() {
		return name;
	}

	/**
	 * This method checks include current agent in given collections.
	 * 
	 * @param collection
	 *            given collection for checks
	 * @see #equalz(AbstractAgent)
	 * @return <tt>true</tt> if current agent include in given collection,
	 *         otherwise <tt>false</tt>
	 */
	public final boolean includedInCollection(Collection<AbstractAgent> collection) {
		for (AbstractAgent agent : collection) {
			if (this.equalz(agent)) {
				return true;
			}
		}
		return false;
	}

	public final void addAllStates(Agent agent) {
		for (AbstractSite aSite : this.getSitesMap().values())
			aSite.addStates(agent.getSiteByName(aSite.getName()));
	}

	public final void addAllStates(AbstractAgent agent) {
		for (AbstractSite aSite : this.getSitesMap().values())
			aSite.addStates(agent.getSiteByName(aSite.getName()));
	}

	public final AbstractAgent summon(AbstractAgent agent) {
		AbstractAgent sum = new AbstractAgent(name);

		sum.sitesMap = new LinkedHashMap<String, AbstractSite>();

		for (Map.Entry<String, AbstractSite> entry : agent.getSitesMap()
				.entrySet()) {
			AbstractSite newSite = entry.getValue().clone();
			sum.sitesMap.put(entry.getKey(), newSite);
		}

		for (Map.Entry<String, AbstractSite> entry : this.getSitesMap()
				.entrySet()) {
			if (sum.sitesMap.get(entry.getKey()) == null) {
				AbstractSite newSite = entry.getValue().clone();
				sum.sitesMap.put(entry.getKey(), newSite);
			}
		}

		return sum;
	}

	/**
	 * This method checks possibility create injections.
	 * 
	 * @param agentIn
	 *            given agent for checks
	 * @return <tt>true</tt> if given agent fit to current agent, otherwise
	 *         <tt>false</tt>
	 */
	public final boolean isFit(AbstractAgent agentIn) {
		if (!this.name.equals(agentIn.getName()))
			return false;

		for (AbstractSite site : sitesMap.values()) {
			AbstractSite siteIn = agentIn.getSiteByName(site.getName());
			if (siteIn == null)
				return false;
			if (!site.isFit(siteIn))
				return false;
		}

		return true;
	}

	// TODO Please rename it
	public final boolean isFitTwo(AbstractAgent agentIn) {
		if (!this.name.equals(agentIn.getName()))
			return false;

		for (AbstractSite site : sitesMap.values()) {
			AbstractSite siteIn = agentIn.getSiteByName(site.getName());
			if (siteIn == null)
				return false;
			if (!siteIn.isFit(site))
				return false;
		}

		return true;
	}

	public final String toStringForXML() {
		StringBuffer sb = new StringBuffer(getName() + "(");
		boolean first = true;
		for (AbstractSite site : sitesMap.values()) {
			if (!first) {
				sb.append(",");
			} else {
				first = false;
			}
			sb.append(site.getName());
			if (!site.getInternalState().hasDefaultName()) {
				sb.append("~" + site.getInternalState().getName());
			}
			AbstractLinkState linkState = site.getLinkState();
			if (!linkState.getAgentName().equals(Agent.DEFAULT_NAME)) {
				sb.append("!");
				sb.append(linkState.getAgentName());
				sb.append(".");
				sb.append(linkState.getConnectedSiteName());
			}
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public final String toString() {
		String st = getName();
		return st;
	}

	@Override
	public final AbstractAgent clone() {
		return new AbstractAgent(this);
	}

	@Override
	protected String getDefaultName() {
		return DEFAULT_NAME;
	}

}
