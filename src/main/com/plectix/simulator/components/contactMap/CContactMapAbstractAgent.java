package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.simulator.ThreadLocalData;

/**
 * This class implements abstract agent.<br>
 * Uses for construct Contact map.
 * @author avokhmin
 *
 */
public class CContactMapAbstractAgent{
	private Map<Integer, CContactMapAbstractSite> sitesMap;
	private int nameID = -1;
	private boolean add = false;

	@Override
	public String toString() {
		String st = getName();
		return st;
	}

	/**
	 * Uses for correct adds agent to "focus rule"
	 * @return <tt>true</tt> if current agent should add, otherwise <tt>false</tt>
	 */
	public boolean isAdd(){
		return add;
	}

	/**
	 * This method determines current agent to add to "focus rule"
	 */
	public void shouldAdd(){
		add = true;
	}

	/**
	 * This method returns character expression of current agent.
	 * @return character expression of current agent.
	 */
	public String getKey(){
		return this.toString()+" "+sitesMap.toString();
	}

	/**
	 * This method returns map of sites.
	 * @return map of sites.
	 */
	public final Map<Integer, CContactMapAbstractSite> getSitesMap() {
		return this.sitesMap;
	}

	/**
	 * Constructor of CContactMapAbstractAgent.<br>
	 * Sets <b>nameID</b> only. No fills sites map.
	 * @param agent given agent
	 */
	public CContactMapAbstractAgent(CAgent agent) {
		this.nameID = agent.getNameId();
		this.sitesMap = new HashMap<Integer, CContactMapAbstractSite>();
	}

	/**
	 * Constructor of CContactMapAbstractAgent.<br>
	 * Sets <b>nameID</b>, fills sites map.
	 * @param agentLink given agent
	 */
	public CContactMapAbstractAgent(CContactMapAbstractAgent agentLink) {
		this.nameID = agentLink.getNameId();
		this.sitesMap = new HashMap<Integer, CContactMapAbstractSite>();

		Iterator<Integer> iterator = agentLink.getSitesMap().keySet()
				.iterator();
		while (iterator.hasNext()) {
			int key = iterator.next();
			CContactMapAbstractSite abstractSite = agentLink.getSitesMap().get(
					key);
			CContactMapAbstractSite newSite = abstractSite.clone();
			newSite.setAgentLink(this);
			sitesMap.put(key, newSite);
		}
	}

	/**
	 * This method returns site by given id
	 * @param nameID given id
	 * @return site from sites map.
	 */
	public final CContactMapAbstractSite getSite(int nameID) {
		return this.sitesMap.get(nameID);
	}

	/**
	 * This method adds given site to sites map.
	 * @param newSite given site
	 */
	public final void addSite(CContactMapAbstractSite newSite) {
		this.sitesMap.put(newSite.getNameId(), newSite);
	}

	/**
	 * This method adds sites to sites map by given agent
	 * and fills missing data. 
	 * @param agent given agent
	 * @param agentNameIdToAgent map of full state of agent
	 */
	public final void addSites(CAgent agent,
			Map<Integer, CContactMapAbstractAgent> agentNameIdToAgent) {

		CContactMapAbstractAgent abstractModelAgent = agentNameIdToAgent
				.get(agent.getNameId());

		for (CSite site : agent.getSites()) {
			Integer key = site.getNameId();
			CContactMapAbstractSite abstractSite = new CContactMapAbstractSite(
					site, this);
			sitesMap.put(key, abstractSite);
		}

		Iterator<Integer> iterator = abstractModelAgent.getSitesMap().keySet()
				.iterator();
		while (iterator.hasNext()) {
			int key = iterator.next();
			CContactMapAbstractSite abstractSite = this.sitesMap.get(key);
			if (abstractSite == null) {
				CContactMapAbstractSite modelSite = abstractModelAgent
						.getSitesMap().get(key);
				this.sitesMap.put(key, modelSite);
			}
		}
	}

	/**
	 * This method adds given site to sites map, if there hasn't it.
	 * @param siteToAdd given site
	 */
	public final void addModelSite(CContactMapAbstractSite siteToAdd) {
		int nameID = siteToAdd.getNameId();
		CContactMapAbstractSite site = this.sitesMap.get(nameID);
		if (site == null) {
			this.sitesMap.put(nameID, siteToAdd);
		}
	}

	/**
	 * This method returns <tt>true</tt> if current agent include in given list of agents.<br>
	 * Checks by nameId
	 * @param collection given collections of agents
	 * @return <tt>true</tt> if current agent include in given list of agents, otherwise <tt>false</tt>
	 */
	public final boolean includedInCollectionByName(
			Collection<CContactMapAbstractAgent> collection) {
		for (CContactMapAbstractAgent agent : collection) {
			if (this.getNameId() == agent.getNameId()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method returns <tt>true</tt> if given agent equals to current agent, otherwise <tt>false</tt>
	 * @param agent given agent
	 * @return <tt>true</tt> if given agent equals to current agent, otherwise <tt>false</tt>
	 */
	public final boolean equalz(CContactMapAbstractAgent agent) {
		if (this == agent) {
			return true;
		}

		if (agent == null) {
			return false;
		}

		if (nameID != agent.nameID)
			return false;

		if (this.sitesMap.size() != agent.getSitesMap().size())
			return false;

		if (!isEqualSitesMaps(sitesMap, agent.getSitesMap()))
			return false;
		return true;
	}

	/**
	 * Util method. Uses for compare given sites maps.
	 * @param sitesMap1 given site map
	 * @param sitesMap2 given site map
	 * @return <tt>true</tt> if sites maps are equals, otherwise <tt>false</tt>
	 */
	private boolean isEqualSitesMaps(
			Map<Integer, CContactMapAbstractSite> sitesMap1,
			Map<Integer, CContactMapAbstractSite> sitesMap2) {

		Iterator<Integer> iterator = sitesMap1.keySet().iterator();
		while (iterator.hasNext()) {
			int siteKey = iterator.next();
			CContactMapAbstractSite site1 = sitesMap1.get(siteKey);
			CContactMapAbstractSite site2 = sitesMap2.get(siteKey);
			if (site2 == null)
				return false;
			if (!site1.equalz(site2))
				return false;
		}

		return true;
	}

	/**
	 * This method checks possibility create injections.
	 * @param agentIn given agent for checks
	 * @return <tt>true</tt> if given agent fit to current agent, otherwise <tt>false</tt>
	 */
	public final boolean isFit(CContactMapAbstractAgent agentIn) {
		if (this.nameID != agentIn.getNameId())
			return false;

		Iterator<Integer> sitesIterator = this.sitesMap.keySet().iterator();

		while (sitesIterator.hasNext()) {
			int key = sitesIterator.next();
			CContactMapAbstractSite site = this.sitesMap.get(key);
			CContactMapAbstractSite siteIn = agentIn.getSite(site.getNameId());
			if (siteIn == null)
				return false;
			if (!site.isFit(siteIn))
				return false;
		}

		return true;
	}

	/**
	 * This method returns name of this agent
	 * @see com.plectix.simulator.util.NameDictionary NameDictionary
	 * @return name of this agent
	 */
	public String getName() {
		if (nameID == -1)
			return "-1";
		return ThreadLocalData.getNameDictionary().getName(nameID);
	}

	/**
	 * This method returns name-id of this agent
	 * @return name-id of this agent
	 */
	public int getNameId() {
		return nameID;
	}

	/**
	 * This method clone given agents
	 * @param listIn list of agents for clone
	 * @return list of agents
	 */
	public static List<CContactMapAbstractAgent> cloneAll(
			List<CContactMapAbstractAgent> listIn) {
		List<CContactMapAbstractAgent> listOut = new ArrayList<CContactMapAbstractAgent>();
		for (CContactMapAbstractAgent a : listIn)
			listOut.add(a.clone());
		return listOut;
	}

	/**
	 * This method clones current agent
	 */
	public CContactMapAbstractAgent clone() {
		return new CContactMapAbstractAgent(this);
	}

	/**
	 * This method checks include current agent in given collections.
	 * @param collection given collection for checks
	 * @see #equalz(CContactMapAbstractAgent)
	 * @return <tt>true</tt> if current agent include in given collection, otherwise <tt>false</tt>
	 */
	public final boolean includedInCollection(
			Collection<CContactMapAbstractAgent> collection) {
		for (CContactMapAbstractAgent agent : collection) {
			if (this.equalz(agent)) {
				return true;
			}
		}
		return false;
	}

}
