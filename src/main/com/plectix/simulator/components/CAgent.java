package com.plectix.simulator.components;

import java.io.Serializable;
import java.util.*;

import com.plectix.simulator.components.injections.*;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.ThreadLocalData;

/**
 * This class implements Agent entity.
 * @author avokhmin
 *
 */
@SuppressWarnings("serial")
public final class CAgent implements Comparable<CAgent>, Serializable {
	/**
	 * idInConnectedComponent is the unique id in ConnectedComponent id is an
	 * unique id for agent
	 */
	public static final int UNMARKED = -1;

	private int idInConnectedComponent;
	private int idInRuleSide = UNMARKED;
	private int nameId = -1;
	private long id = -1;

	// TODO: is this field static or not???
	private final CSite myDefaultSite = new CSite(CSite.NO_INDEX, this);
	private Map<Integer, CSite> siteMap = new TreeMap<Integer, CSite>();

	/**
	 * Constructor of Agent.
	 * @param nameId name id of the new agent
	 * @param agentID unique id of the new agent
	 */
	public CAgent(int nameId, long agentID) {
		id = agentID;
		this.nameId = nameId;
	}
	
	/**
	 * This constructor is easier to use
	 * @param name
	 * @param agentID
	 */
	public CAgent(String name, long agentID) {
		id = agentID;
		this.nameId = ThreadLocalData.getNameDictionary().getId(name);
	}
	
	/**
	 * Empty Agent constructor
	 */
	public CAgent() {
		id = -1;
		this.nameId = -1;
	}

	/**
	 * This method returns default site from current agent
	 * @return default site from current agent
	 */
	public CSite getDefaultSite() {
		return myDefaultSite;
	}

	/**
	 * This method returns <tt>true</tt>, if this agent already has injection equaivalent 
	 * to the given one, <tt>false</tt>
	 * @param injection given injection
	 * @return <tt>true</tt>, if this agent already has injection equivalent 
	 * to the given one, <tt>false</tt>
	 */
	public boolean hasSimilarInjection(CInjection injection) {
		IConnectedComponent cc = injection.getConnectedComponent();
		if (checkSites(this.getDefaultSite(), injection, cc))
			return true;
		for (CSite site : siteMap.values()) {
			if (checkSites(site.getAgentLink().getDefaultSite(), injection, cc))
				return true;
			if (checkSites(site, injection, cc))
				return true;
		}
		return false;
	}

	/**
	 * This is utility for finding similar injections, used in {@link #hasSimilarInjection(CInjection)}
	 */
	private boolean checkSites(CSite site, CInjection injection,
			IConnectedComponent cc) {
		List<CInjection> sitesInjections = site.getInjectionFromLift(cc);
		if (sitesInjections.size() != 0) {
			if (injection.compareInjectedLists(sitesInjections))
				return true;
		}
		return false;
	}

	/**
	 * 
	 * This method finds and agent, which is connected with this through the given site-collection 
	 * @param agent given agent 
	 * @param siteCollection collection of sites
	 * @return agent (if there's one), which connected with current agent through the given sites,
	 * otherwise <tt>null</tt>
	 */
	public final CAgent findLinkAgent(CAgent agent, List<CSite> siteCollection) {
		if (agent == null || siteCollection.size() == 0)
			return null;
		CAgent imageAgent = (CAgent) this.getSiteByNameId(siteCollection.get(0).getNameId())
				.getLinkState().getConnectedSite().getAgentLink();
		for (CSite siteF : siteCollection) {
			CAgent agent2 = (CAgent) this.getSiteByNameId(siteF.getNameId())
					.getLinkState().getConnectedSite().getAgentLink();
			if (imageAgent != agent2)
				return null;
		}
		if (imageAgent.equalz(agent))
			return imageAgent;

		return null;
	}

	
	/**
	 * This method adds site to this agent
	 * @param site site we want to add
	 */
	public final void addSite(CSite site) {
		site.setAgentLink(this);
		siteMap.put(site.getNameId(), site);
	}
	
	/**
	 * This method indicates whether name ids of given agent is equal to name id of current one.
	 * @param agent given agent
	 */
	public final boolean equalz(CAgent agent) {
		if (this == agent) {
			return true;
		}
		
		if (agent == null) {
			return false;
		}
		
		return nameId == agent.nameId;
	}
	
	/**
	 * This method is some kind of override {@link Collection#contains(Object) contains}.
	 * We need it just because we haven't override default {@link Object#equals(Object) equals},
	 * but we use our own {@link CAgent#equalz(CAgent) equalz}. So we had to create util
	 * method for checking current agent in given collection 
	 * @param collection given collection
	 */
	public boolean includedInCollection(Collection<CAgent> collection) {
		for (CAgent agent : collection) {
			if (this.equalz(agent)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method returns <tt>true</tt>, if sites from current agent are 
	 * {@link com.plectix.simulator.components.CSite#equalz(CSite) equal} to sites 
	 * of the given agent, otherwise <tt>false</tt>
	 * @param agent given agent
	 * @return <tt>true</tt>, if sites from current agent are 
	 * {@link com.plectix.simulator.components.CSite#equalz(CSite) equal} to sites 
	 * of the given agent, otherwise <tt>false</tt>
	 */
	public boolean siteMapsAreEqual(CAgent agent) {
		if (agent == null) {
			return false;
		}
		
		Set<CSite> listThis = new LinkedHashSet<CSite>();
		Set<CSite> listThat = new LinkedHashSet<CSite>();
		
		listThis.addAll(siteMap.values());
		listThat.addAll(agent.getSites());
		
		for (CSite siteThis : siteMap.values()) {
			boolean containsCurrent = false;
			CSite foundedSiteThat = null;
			for (CSite siteThat : listThat) {
				if (siteThis.equalz(siteThat)) {
					foundedSiteThat = siteThat;
					containsCurrent = true;
					break;
				}
			}
			
			if (!containsCurrent) {
				return false;
			} else {
				listThis.remove(siteThis);
				listThat.remove(foundedSiteThat);
			}
		}
		return listThis.isEmpty() && listThat.isEmpty();
	}
	
	//-------------------------GETTERS AND SETTERS---------------------------------------
	
	/**
	 * This method returns this agent's ordering number in left/right handside of rule
	 * (if there is one), use only for create atomic actions for Rule.
	 * @return ordering number in rule's handside, if there is such rule, or
	 * -1 if there isn't
	 */
	public int getIdInRuleHandside() {
		return idInRuleSide;
	}

	public void setIdInRuleSide(int idInRuleSide) {
		this.idInRuleSide = idInRuleSide;
	}
	
	/**
	 * This method searches site with similar id as given in current agent 
	 * @param siteNameId site id to search
	 * @return site, that has similar id as given, or null, if there's no such
	 */
	public final CSite getSiteByNameId(int siteNameId) {
		return siteMap.get(siteNameId);
	}

	/**
	 * This method returns name-id of this agent
	 * @see com.plectix.simulator.util.NameDictionary NameDictionary
	 * @return name-id of this agent
	 */
	public final int getNameId() {
		return nameId;
	}

	/**
	 * This method returns name of this agent
	 * @see com.plectix.simulator.util.NameDictionary NameDictionary
	 * @return name of this agent
	 */
	public final String getName() {
		if (nameId == -1) {
			return "EMPTY";
		}
		return ThreadLocalData.getNameDictionary().getName(nameId);
	}

	/**
	 * This method returns order number of this agent in connected component, 
	 * used only for create atomic actions for rule.
	 */
	public final int getIdInConnectedComponent() {
		return idInConnectedComponent;
	}

	/**
	 * This method sets order number of this agent in connected component, 
	 * used only for create atomic actions for rule.
	 * @param index new value
	 */
	public final void setIdInConnectedComponent(int index) {
		idInConnectedComponent = index;
	}

	/**
	 * This method returns collection of current agent's sites
	 */
	public final Collection<CSite> getSites() {
		return Collections.unmodifiableCollection(siteMap.values());
	}

	/**
	 * This method returns unique id of current agent
	 * @return id of current agent
	 */
	public final long getId() {
		return id;
	}

	/**
	 * This method returns something like a hashCode for the agent.
	 * We use it to keep agents in more convenient storage, which we can remove from fast enough. 
	 * @return hash code of this agent
	 */
	public long getHash() {
		return id;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer(getName() + "(");
		boolean first = true;
		for (CSite site : siteMap.values()) {
			if (!first) {
				sb.append(", ");
			} else {
				first = false;
			}
			sb.append(site.getName());
			if (site.getInternalState().getNameId() != CSite.NO_INDEX) {
				sb.append("~" + site.getInternalState().getName());
			}
			if (site.getLinkState().getStatusLinkRank() == CLinkRank.SEMI_LINK) {
				sb.append("!_");
			} else if (site.getLinkIndex() != -1) {
//				sb.append("!" + site.getLinkIndex());
				sb.append("!");
				sb.append(site.getLinkState().getConnectedSite().getName());
				sb.append(site.getLinkIndex());
			} else if (site.getLinkState().getStatusLink() == CLinkStatus.WILDCARD) {
				sb.append("?");
			}
		}
		sb.append(")");
		return sb.toString();
	}

	public String skeletonString() {
		StringBuffer sb = new StringBuffer(getName() + "(");
		boolean first = true;
		for (CSite site : siteMap.values()) {
			if (!first) {
				sb.append(", ");
			} else {
				first = false;
			}
			sb.append(site.getName());
			if (site.getInternalState().getNameId() != CSite.NO_INDEX) {
				sb.append("~" + site.getInternalState().getName());
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
	public int compareTo(CAgent o) {
		return idInRuleSide - o.getIdInRuleHandside();
	}

	public void setId(int newId) {
		id = newId;
		
	}

	public void removeSite(int nameId2) {
		siteMap.remove(nameId2);
		
	}
}
