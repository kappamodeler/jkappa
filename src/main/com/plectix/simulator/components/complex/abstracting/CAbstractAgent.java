package com.plectix.simulator.components.complex.abstracting;

import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.simulator.ThreadLocalData;

/**
 * This class implements abstract agent.<br>
 * SubView presented in the some form<br>
 * AbstractAgent include part or all information of real Agent<br>
 * Uses for construct Contact map.
 * 
 * @author avokhmin
 * 
 */
public class CAbstractAgent {
	private final CAbstractSite myDefaultSite = new CAbstractSite(this,CSite.NO_INDEX);
	
	private Map<Integer, CAbstractSite> sitesMap;
	private int nameID = -1;
	private boolean add = false;

	public String toString() {
		String st = getName();
		return st;
	}
	
	/**
	 * This method returns default site from current agent
	 * @return default site from current agent
	 */
	public CAbstractSite getDefaultSite() {
		return myDefaultSite;
	}
	

	/**
	 * Uses for correct adds agent to "focus rule"
	 * 
	 * @return <tt>true</tt> if current agent should add, otherwise
	 *         <tt>false</tt>
	 */
	public boolean isAdd() {
		return add;
	}

	/**
	 * This method determines current agent to add to "focus rule"
	 */
	public void shouldAdd() {
		add = true;
	}

	/**
	 * This method returns character expression of current agent.
	 * 
	 * @return character expression of current agent.
	 */
	public String getKey() {
		return this.toString() + " " + sitesMap.toString();
	}

	/**
	 * This method returns map of sites.
	 * 
	 * @return map of sites.
	 */
	public final Map<Integer, CAbstractSite> getSitesMap() {
		return this.sitesMap;
	}

	/**
	 * Constructor of CContactMapAbstractAgent.<br>
	 * Sets <b>nameID</b> only. No fills sites map.
	 * 
	 * @param agent
	 *            given agent
	 */
	public CAbstractAgent(CAgent agent) {
		this.nameID = agent.getNameId();
		this.sitesMap = new HashMap<Integer, CAbstractSite>();
	}

	public CAbstractAgent(int nameId) {
		this.nameID = nameId;
		sitesMap = new HashMap<Integer, CAbstractSite>();
	}

	/**
	 * Constructor of CContactMapAbstractAgent.<br>
	 * Sets <b>nameID</b>, fills sites map.
	 * 
	 * @param agent
	 *            given agent
	 */
	public CAbstractAgent(CAbstractAgent agent) {
		this.nameID = agent.getNameId();
		this.sitesMap = new HashMap<Integer, CAbstractSite>();

		for (Map.Entry<Integer, CAbstractSite> entry : agent.getSitesMap()
				.entrySet()) {
			CAbstractSite newSite = entry.getValue().clone();
			newSite.setAgentLink(this);
			sitesMap.put(entry.getKey(), newSite);
		}
	}

	/**
	 * This method returns site by given id
	 * 
	 * @param nameID
	 *            given id
	 * @return site from sites map.
	 */
	public final CAbstractSite getSite(int nameID) {
		return this.sitesMap.get(nameID);
	}

	/**
	 * This method adds given site to sites map.
	 * 
	 * @param newSite
	 *            given site
	 */
	public final void addSite(CAbstractSite newSite) {
		this.sitesMap.put(newSite.getNameId(), newSite);
	}

	/**
	 * This method adds sites to sites map by given agent and fills missing
	 * data.
	 * 
	 * @param agent
	 *            given agent
	 * @param agentNameIdToAgent
	 *            map of full state of agent
	 */
	public final void addSites(CAgent agent,
			Map<Integer, CAbstractAgent> agentNameIdToAgent) {

		CAbstractAgent abstractModelAgent = agentNameIdToAgent.get(agent
				.getNameId());

		for (CSite site : agent.getSites()) {
			Integer key = site.getNameId();
			CAbstractSite abstractSite = new CAbstractSite(site, this);
			sitesMap.put(key, abstractSite);
		}

		for (Map.Entry<Integer, CAbstractSite> entry : abstractModelAgent
				.getSitesMap().entrySet()) {
			CAbstractSite abstractSite = this.sitesMap.get(entry.getKey());
			if (abstractSite == null) {
				this.sitesMap.put(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * This method adds given site to sites map, if there hasn't it.
	 * 
	 * @param siteToAdd
	 *            given site
	 */
	public final void addModelSite(CAbstractSite siteToAdd) {
		int nameID = siteToAdd.getNameId();
		CAbstractSite site = this.sitesMap.get(nameID);
		if (site == null) {
			this.sitesMap.put(nameID, siteToAdd);
		}
	}

	/**
	 * This method returns <tt>true</tt> if current agent include in given list
	 * of agents.<br>
	 * Checks by nameId
	 * 
	 * @param collection
	 *            given collections of agents
	 * @return <tt>true</tt> if current agent include in given list of agents,
	 *         otherwise <tt>false</tt>
	 */
	public final boolean includedInCollectionByName(
			Collection<CAbstractAgent> collection) {
		for (CAbstractAgent agent : collection) {
			if (this.getNameId() == agent.getNameId()) {
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
	public final boolean equalz(CAbstractAgent agent) {
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
	 * 
	 * @param sitesMap1
	 *            given site map
	 * @param sitesMap2
	 *            given site map
	 * @return <tt>true</tt> if sites maps are equals, otherwise <tt>false</tt>
	 */
	private boolean isEqualSitesMaps(Map<Integer, CAbstractSite> sitesMap1,
			Map<Integer, CAbstractSite> sitesMap2) {

		for (Map.Entry<Integer, CAbstractSite> entry : sitesMap1.entrySet()) {
			if (!entry.getValue().equalz(sitesMap2.get(entry.getKey())))
				return false;
		}

		return true;
	}

	/**
	 * This method checks possibility create injections.
	 * 
	 * @param agentIn
	 *            given agent for checks
	 * @return <tt>true</tt> if given agent fit to current agent, otherwise
	 *         <tt>false</tt>
	 */
	public final boolean isFit(CAbstractAgent agentIn) {
		if (this.nameID != agentIn.getNameId())
			return false;

		for (CAbstractSite site : sitesMap.values()) {
			CAbstractSite siteIn = agentIn.getSite(site.getNameId());
			if (siteIn == null)
				return false;
			if (!site.isFit(siteIn))
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
	public String getName() {
		if (nameID == -1)
			return "-1";
		return ThreadLocalData.getNameDictionary().getName(nameID);
	}

	/**
	 * This method returns name-id of this agent
	 * 
	 * @return name-id of this agent
	 */
	public int getNameId() {
		return nameID;
	}

	/**
	 * This method clone given agents
	 * 
	 * @param listIn
	 *            list of agents for clone
	 * @return list of agents
	 */
	public static List<CAbstractAgent> cloneAll(List<CAbstractAgent> listIn) {
		List<CAbstractAgent> listOut = new ArrayList<CAbstractAgent>();
		for (CAbstractAgent a : listIn)
			listOut.add(a.clone());
		return listOut;
	}

	/**
	 * This method clones current agent
	 */
	public CAbstractAgent clone() {
		return new CAbstractAgent(this);
	}

	/**
	 * This method checks include current agent in given collections.
	 * 
	 * @param collection
	 *            given collection for checks
	 * @see #equalz(CAbstractAgent)
	 * @return <tt>true</tt> if current agent include in given collection,
	 *         otherwise <tt>false</tt>
	 */
	public final boolean includedInCollection(
			Collection<CAbstractAgent> collection) {
		for (CAbstractAgent agent : collection) {
			if (this.equalz(agent)) {
				return true;
			}
		}
		return false;
	}

	public void addAllStates(CAgent agent) {
		for (CAbstractSite aSite : this.getSitesMap().values())
			aSite.addStates(agent.getSiteByNameId(aSite.getNameId()));
	}

	public void addAllStates(CAbstractAgent agent) {
		for (CAbstractSite aSite : this.getSitesMap().values())
			aSite.addStates(agent.getSite(aSite.getNameId()));
	}

	public String toStringForXML() {
		StringBuffer sb = new StringBuffer(getName() + "(");
		boolean first = true;
		for (CAbstractSite site : sitesMap.values()) {
			if (!first) {
				sb.append(",");
			} else {
				first = false;
			}
			sb.append(site.getName());
			if (site.getInternalState().getNameId() != CSite.NO_INDEX) {
				sb.append("~" + site.getInternalState().getName());
			}
			CAbstractLinkState linkState = site.getLinkState();
			if (linkState.getAgentNameID() != CSite.NO_INDEX) {
				sb.append(ThreadLocalData.getNameDictionary().getName(
						linkState.getAgentNameID()));
				sb.append(".");
				sb.append(ThreadLocalData.getNameDictionary().getName(
						linkState.getLinkSiteNameID()));
			}
		}
		sb.append(")");
		return sb.toString();
	}

	public CAbstractAgent plus(CAbstractAgent view) {
		CAbstractAgent sum = new CAbstractAgent(nameID);

		sum.sitesMap = new HashMap<Integer, CAbstractSite>();

		for (Map.Entry<Integer, CAbstractSite> entry : view.getSitesMap()
				.entrySet()) {
			CAbstractSite newSite = entry.getValue().clone();
			sum.sitesMap.put(entry.getKey(), newSite);
		}

		for (Map.Entry<Integer, CAbstractSite> entry : this.getSitesMap()
				.entrySet()) {
			if (sum.sitesMap.get(entry.getKey()) == null) {
				CAbstractSite newSite = entry.getValue().clone();
				sum.sitesMap.put(entry.getKey(), newSite);
			}
		}

		return sum;

	}

}
