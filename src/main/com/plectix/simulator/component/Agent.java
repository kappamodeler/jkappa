package com.plectix.simulator.component;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.plectix.simulator.component.injections.Injection;
import com.plectix.simulator.interfaces.ConnectedComponentInterface;

/**
 * This class implements Agent entity.
 * @author avokhmin
 *
 */
@SuppressWarnings("serial")
public class Agent extends NamedEntity implements Comparable<Agent>, Serializable {
	/**
	 * idInConnectedComponent is the unique id in ConnectedComponent id is an
	 * unique id for agent
	 */
	public static final int UNMARKED = -1;
	public static final String DEFAULT_NAME = "AGENT_DEFAULT_NAME";

	private int idInConnectedComponent;
	private int idInRuleSide = UNMARKED;
	private final String name;
	private long id = -1;

	// TODO: is this field static or not???
	private final Site defaultSite = new Site(Site.DEFAULT_NAME, this);
	private Map<String, Site> siteMap = new TreeMap<String, Site>();

	/**
	 * Constructor of Agent.
	 * @param name name id of the new agent
	 * @param agentId unique id of the new agent
	 */
	public Agent(String name, long agentId) {
		id = agentId;
		this.name = name.intern();
	}
	
	/**
	 * Empty Agent constructor
	 */
	public Agent() {
		id = -1;
		this.name = DEFAULT_NAME;
	}

	/**
	 * This method returns default site from current agent
	 * @return default site from current agent
	 */
	public final Site getDefaultSite() {
		return defaultSite;
	}

	/**
	 * This method returns <tt>true</tt>, if this agent already has injection equaivalent 
	 * to the given one, <tt>false</tt>
	 * @param injection given injection
	 * @return <tt>true</tt>, if this agent already has injection equivalent 
	 * to the given one, <tt>false</tt>
	 */
	public final boolean hasSimilarInjection(Injection injection) {
		ConnectedComponentInterface cc = injection.getConnectedComponent();
		if (checkSites(this.getDefaultSite(), injection, cc))
			return true;
		for (Site site : siteMap.values()) {
			if (checkSites(site.getParentAgent().getDefaultSite(), injection, cc))
				return true;
			if (checkSites(site, injection, cc))
				return true;
		}
		return false;
	}

	/**
	 * This is utility for finding similar injections, used in {@link #hasSimilarInjection(Injection)}
	 */
	private final boolean checkSites(Site site, Injection injection,
			ConnectedComponentInterface cc) {
		List<Injection> sitesInjections = site.getInjectionFromLift(cc);
		if (sitesInjections.size() != 0) {
			if (injection.findInCollection(sitesInjections))
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
	public final Agent findLinkAgent(Agent agent, List<Site> siteCollection) {
		if (agent == null || siteCollection.size() == 0)
			return null;
		Agent imageAgent = (Agent) this.getSiteByName(siteCollection.get(0).getName())
				.getLinkState().getConnectedSite().getParentAgent();
		for (Site siteF : siteCollection) {
			Agent agent2 = (Agent) this.getSiteByName(siteF.getName())
					.getLinkState().getConnectedSite().getParentAgent();
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
	public final void addSite(Site site) {
		site.setParentAgent(this);
		siteMap.put(site.getName(), site);
	}
	
	/**
	 * This method indicates whether name ids of given agent is equal to name id of current one.
	 * @param agent given agent
	 */
	public final boolean equalz(Agent agent) {
		if (this == agent) {
			return true;
		}
		
		if (agent == null) {
			return false;
		}
		
		return name.equals(agent.name);
	}
	
	/**
	 * This method is some kind of override {@link Collection#contains(Object) contains}.
	 * We need it just because we haven't override default {@link Object#equals(Object) equals},
	 * but we use our own {@link Agent#equalz(Agent) equalz}. So we had to create util
	 * method for checking current agent in given collection 
	 * @param collection given collection
	 */
	public final boolean includedInCollection(Collection<Agent> collection) {
		for (Agent agent : collection) {
			if (this.equalz(agent)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method returns <tt>true</tt>, if sites from current agent are 
	 * {@link com.plectix.simulator.component.Site#equalz(Site) equal} to sites 
	 * of the given agent, otherwise <tt>false</tt>
	 * @param agent given agent
	 * @return <tt>true</tt>, if sites from current agent are 
	 * {@link com.plectix.simulator.component.Site#equalz(Site) equal} to sites 
	 * of the given agent, otherwise <tt>false</tt>
	 */
	public final boolean siteMapsAreEqual(Agent agent) {
		if (agent == null) {
			return false;
		}
		
		Set<Site> listThis = new LinkedHashSet<Site>();
		Set<Site> listThat = new LinkedHashSet<Site>();
		
		listThis.addAll(siteMap.values());
		listThat.addAll(agent.getSites());
		
		for (Site siteThis : siteMap.values()) {
			boolean containsCurrent = false;
			Site foundedSiteThat = null;
			for (Site siteThat : listThat) {
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
	public final int getIdInRuleHandside() {
		return idInRuleSide;
	}

	public final void setIdInRuleSide(int idInRuleSide) {
		this.idInRuleSide = idInRuleSide;
	}
	
	/**
	 * This method searches site with similar id as given in current agent 
	 * @param siteName site id to search
	 * @return site, that has similar id as given, or null, if there's no such
	 */
	public final Site getSiteByName(String siteName) {
		return siteMap.get(siteName);
	}

	/**
	 * This method returns name of this agent
	 * @see com.plectix.simulator.util.NameDictionary NameDictionary
	 * @return name of this agent
	 */
	public final String getName() {
		return name;
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
	public final Collection<Site> getSites() {
		return siteMap.values();
	}

	/**
	 * This method returns unique id of current agent
	 * @return id of current agent
	 */
	public final long getId() {
		return id;
	}

	public final void setId(int id) {
		this.id = id;
	}

	public final void removeSite(String name) {
		siteMap.remove(name);
	}
	
	@Override
	public final int compareTo(Agent o) {
		return idInRuleSide - o.getIdInRuleHandside();
	}

	@Override
	public final Agent clone(){
		Agent agent = new Agent(name, -1);
		for (Site s : siteMap.values()) {
			agent.addSite(s.clone());
		}
		return agent;
	}
	
	@Override
	public final String toString() {
		StringBuffer sb = new StringBuffer(getName() + "(");
		boolean first = true;
		for (Site site : siteMap.values()) {
			if (!first) {
				sb.append(", ");
			} else {
				first = false;
			}
			sb.append(site.getName());
			if (!site.getInternalState().hasDefaultName()) {
				sb.append("~" + site.getInternalState().getName());
			}
			if (site.getLinkState().getStatusLinkRank() == LinkRank.SEMI_LINK) {
				sb.append("!_");
			} else if (site.getLinkIndex() != -1) {
//				sb.append("!" + site.getLinkIndex());
				sb.append("!");
				sb.append(site.getLinkState().getConnectedSite().getName());
				sb.append(site.getLinkIndex());
			} else if (site.getLinkState().getStatusLink() == LinkStatus.WILDCARD) {
				sb.append("?");
			}
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	protected String getDefaultName() {
		return DEFAULT_NAME;
	}
}
