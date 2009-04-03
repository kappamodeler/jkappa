package com.plectix.simulator.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.injections.CLiftElement;
import com.plectix.simulator.components.solution.SuperSubstance;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CAgentLink;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.interfaces.IRandom;

import com.plectix.simulator.components.CSite;

/**
 * Basic ConnectedComponent class.
 * @author avokhmin
 *
 */
public class CConnectedComponent implements IConnectedComponent, Serializable {
	private static final long serialVersionUID = -2233812055480299501L;
	
	/**
	 * "EMPTY" ConnectedComponent i.e. without Agents.
	 */
	public static CConnectedComponent EMPTY = new CConnectedComponent();

	/**
	 * agents generates ConnectedComponent.
	 */
	private final List<CAgent> agentList;

	/**
	 * Spanning Tree Map.<br>
	 * <b>key</b> - id agent's in ConnectedComponent<br>
	 * <b>List of {@link CSpanningTree}</b> - list for check.
	 */
	private Map<Integer, List<CSpanningTree>> spanningTreeMap;
	private List<CAgent> agentFromSolutionForRHS;
	
	/**
	 * Util. Uses for create {@link CInjection}
	 */
	private List<CAgentLink> agentLinkList;
	private final Map<Integer, CInjection> injectionsList;

	/**
	 * Util. Uses for create {@link CInjection}
	 */
	private List<CSite> injectedSites;

	/**
	 * counter {@link CInjection} current ConnectedComponent.
	 */
	private int maxId = -1;

	/**
	 * Link to rule from this ConnectidComponent
	 */
	private CRule rule;
	private SuperSubstance mySubstance = null;

	/**
	 * Empty ConnectedComponent constructor
	 */
	private CConnectedComponent() {
		agentList = new ArrayList<CAgent>();
		agentList.add(new CAgent());
		injectionsList = new TreeMap<Integer, CInjection>();
		addInjection(CInjection.EMPTY_INJECTION, 0);
		agentFromSolutionForRHS = new ArrayList<CAgent>();
	}

	/**
	 * Constructor ConnectedComponent with <b>connectedAgents</b> agents.
	 * @param connectedAgents - <code>List of {@link CAgent}</code> value - agents generates ConnectedComponent. 
	 */
	public CConnectedComponent(List<CAgent> connectedAgents) {
		agentList = connectedAgents;
		injectionsList = new TreeMap<Integer, CInjection>();
		agentFromSolutionForRHS = new ArrayList<CAgent>();
	}

	/**
	 * Returns <tt>true</tt>, if this ConnectedComponent does "EMPTY", otherwise <tt>false</tt>.
	 */
	public boolean isEmpty() {
		return this == EMPTY;
	}
	
	public void setSuperSubstance(SuperSubstance substance) {
		mySubstance = substance;
	}

	public SuperSubstance getSubstance() {
		return mySubstance;
	}

	/**
	 * Adds injection to current ConnectedComponent.
	 * @param inj - new injection
	 * @param id - id of <b>inj</b>
	 */
	private final void addInjection(CInjection inj, int id) {
		if (inj != null) {
			maxId = Math.max(maxId, id);
			inj.setId(id);
			injectionsList.put(id, inj);
		}
	}

	public final void addAgentFromSolutionForRHS(CAgent agentFromSolutionForRHS) {
		this.agentFromSolutionForRHS.add(agentFromSolutionForRHS);
	}

	public final void clearAgentsFromSolutionForRHS() {
		agentFromSolutionForRHS.clear();
	}

	public final List<CAgent> getAgentFromSolutionForRHS() {
		return Collections.unmodifiableList(agentFromSolutionForRHS);
	}

	/**
	 * Removes <b>injection</b> from current ConnectedComponent.
	 * @param injection - Injection for removes.
	 */
	public final void removeInjection(CInjection injection) {
		if (injection == null) {
			return;
		}

		int id = injection.getId();

		if (injectionsList.get(id) != null) {
			if (injection != injectionsList.get(id)) {
				return;
			}
			CInjection inj = injectionsList.remove(maxId);
			if (id != maxId) {
				addInjection(inj, id);
			}
			maxId--;
		}
	}

	/**
	 * Initializing Spanning Tree Map.
	 */
	public final void initSpanningTreeMap() {
		CSpanningTree spTree;
		spanningTreeMap = new HashMap<Integer, List<CSpanningTree>>();
		if (agentList.isEmpty())
			return;

		for (CAgent agentAdd : agentList) {
			spTree = new CSpanningTree(agentList.size(), agentAdd);
			List<CSpanningTree> list = spanningTreeMap
					.get(agentAdd.getNameId());
			if (list == null) {
				list = new ArrayList<CSpanningTree>();
				spanningTreeMap.put(agentAdd.getNameId(), list);
			}
			list.add(spTree);
		}
	}

	private final void addLiftsToCurrentChangedStates(CInjection injection) {
		for (CSite changedSite : injectedSites) {
			changedSite.addToLift(new CLiftElement(this, injection));
		}
	}

	public final void setInjection(CInjection inj) {
		addInjection(inj, maxId + 1);
		addLiftsToCurrentChangedStates(inj);
	}

	public final CInjection createInjection(CAgent agent) {
		if (unify(agent)) {
			CInjection injection = new CInjection(this, injectedSites,
					agentLinkList);
			return injection;
		}
		return null;
	}

	public final void doPositiveUpdate(
			List<IConnectedComponent> connectedComponentList) {
		if (connectedComponentList == null)
			return;
		for (IConnectedComponent cc : connectedComponentList) {
			for (CAgent agent : cc.getAgentFromSolutionForRHS()) {
				CInjection inj = this.createInjection(agent);
				if (inj != null) {
					if (!agent.hasSimilarInjection(inj))
						setInjection(inj);
				}
			}
		}
	}

	public final List<CAgent> getAgents() {
		return Collections.unmodifiableList(agentList);
	}

	public final boolean unify(CAgent agent) {
		injectedSites = new ArrayList<CSite>();
		agentLinkList = new ArrayList<CAgentLink>();

		if (spanningTreeMap == null)
			return false;

		List<CSpanningTree> spList = spanningTreeMap.get(agent.getNameId());

		if (spList == null) {
			return false;
		}

		for (CSpanningTree tree : spList) {
			injectedSites.clear();
			agentLinkList.clear();
			if (tree != null) {
				tree.resetNewVertex();
				if (agentList.get(tree.getRootIndex()).getSites().isEmpty()) {
					injectedSites.add(agent.getDefaultSite());
					agentLinkList.add(new CAgentLink(0, agent));
					return true;
				} else {
					if (compareAgents(agentList.get(tree.getRootIndex()), agent))
						if (spanningTreeViewer(agent, tree,
								tree.getRootIndex(), false))
							if (this.getAgents().size() == agentLinkList.size())
								return true;
							else {
								injectedSites.clear();
								agentLinkList.clear();
							}
				}

			}
		}
		return false;
	}

	public final boolean isAutomorphism(CAgent agent) {
		if (spanningTreeMap == null)
			return false;

		List<CSpanningTree> spList = spanningTreeMap.get(agent.getNameId());

		if (spList == null)
			return false;

		for (CSpanningTree tree : spList) {
			if (tree != null) {
				tree.resetNewVertex();
				if (agentList.get(tree.getRootIndex()).getSites().isEmpty()
						&& agent.getSites().isEmpty()) {
					return true;
				} else {
					if (fullEqualityOfAgents(
							agentList.get(tree.getRootIndex()), agent))
						if (spanningTreeViewer(agent, tree,
								tree.getRootIndex(), true))
							return true;
				}

			}
		}
		return false;
	}

	private final boolean fullEqualityOfAgents(CAgent cc1Agent, CAgent cc2Agent) {
		if (cc1Agent == null || cc2Agent == null)
			return false;
		if (cc1Agent.getSites().size() != cc2Agent.getSites().size())
			return false;

		for (CSite cc1Site : cc1Agent.getSites()) {
			CSite cc2Site = cc2Agent.getSiteById(cc1Site.getNameId());
			if (cc2Site == null)
				return false;
			if (!cc1Site.expandedEqualz(cc2Site, true))
				return false;
		}
		return true;
	}

	private final boolean compareAgents(CAgent currentAgent,
			CAgent solutionAgent) {
		if (currentAgent == null || solutionAgent == null)
			return false;
		for (CSite site : currentAgent.getSites()) {
			CSite solutionSite = solutionAgent.getSiteById(site.getNameId());
			if (solutionSite == null)
				return false;
			if (!site.expandedEqualz(solutionSite, false))
				return false;
			injectedSites.add(solutionSite);
		}
		agentLinkList.add(new CAgentLink(currentAgent
				.getIdInConnectedComponent(), solutionAgent));
		return true;
	}

	private final List<CSite> getConnectedSite(CAgent agentFrom, CAgent agentTo) {
		List<CSite> siteList = new ArrayList<CSite>();

		for (CSite sF : agentFrom.getSites()) {
			for (CSite sT : agentTo.getSites()) {
				if (sF == sT.getLinkState().getConnectedSite()) {
					siteList.add(sF);
				}
			}
		}

		return siteList;
	}

	// is there injection or not and create lifts
	private final boolean spanningTreeViewer(CAgent agent,
			CSpanningTree spTree, int rootVertex, boolean fullEquality) {
		spTree.setTrue(rootVertex);
		for (Integer v : spTree.getVertexes()[rootVertex]) {
			CAgent cAgent = agentList.get(v);// get next agent from spanning
			if (!(spTree
					.getNewVertexElement(cAgent.getIdInConnectedComponent()))) {
				List<CSite> sitesFrom = getConnectedSite(agentList
						.get(rootVertex), agentList.get(v));
				CAgent sAgent = agent.findLinkAgent(cAgent, sitesFrom);
				if (fullEquality && !(fullEqualityOfAgents(cAgent, sAgent)))
					return false;
				if (!fullEquality && !compareAgents(cAgent, sAgent))
					return false;
				spanningTreeViewer(sAgent, spTree, v, fullEquality);
			}
		}
		return true;
	}

	public final CRule getRule() {
		return rule;
	}

	public final void setRule(CRule rule) {
		this.rule = rule;
	}

	public final Collection<CInjection> getInjectionsList() {
		return Collections.unmodifiableCollection(injectionsList.values());
	}

	public final CInjection getRandomInjection(IRandom random) {
		int index;
		CInjection inj = null;
		index = random.getInteger(maxId + 1);
		inj = injectionsList.get(index);
		return inj;
	}

	public final CInjection getFirstInjection() {
		return injectionsList.get(0);
	}

	public final List<CAgent> getAgentsSortedByIdInRule() {
		List<CAgent> temp = new ArrayList<CAgent>();
		temp.addAll(agentList);
		Collections.sort(temp);
		return Collections.unmodifiableList(temp);
	}

	// -----------------------hash, toString,
	// equals-----------------------------

	// TODO we can do it better! =)
	/**
	 * this methods takes agentNames in alphabetical order as a String, then
	 * allsiteNames in this order as String too and then concatenates these
	 * Strings.
	 */
	public String getHash() {
		List<String> siteNames = new ArrayList<String>();
		// TreeMap means this collection sorted by key anytime
		Map<String, String> agentStrings = new TreeMap<String, String>();
		for (CAgent agent : agentList) {
			StringBuffer sb = new StringBuffer();
			for (CSite site : agent.getSites()) {
				siteNames.add(site.getName());
			}
			Collections.sort(siteNames);
			for (CSite site : agent.getSites()) {
				sb.append(site.getName());
			}
			agentStrings.put(agent.getName(), sb.toString());
		}
		StringBuffer sb = new StringBuffer();
		for (String key : agentStrings.keySet()) {
			sb.append(key + agentStrings.get(key));
		}

		// TODO write connections here
		return sb.toString();
	}
}