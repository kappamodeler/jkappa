package com.plectix.simulator.component.complex.contactmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.component.Agent;
import com.plectix.simulator.component.Rule;
import com.plectix.simulator.component.Site;
import com.plectix.simulator.component.complex.abstracting.AbstractAgent;
import com.plectix.simulator.component.complex.abstracting.AbstractLinkState;
import com.plectix.simulator.component.complex.abstracting.AbstractSite;
import com.plectix.simulator.component.complex.subviews.base.AbstractionRule;
import com.plectix.simulator.component.complex.subviews.storage.SubViewsInterface;
import com.plectix.simulator.component.solution.SuperSubstance;
import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.interfaces.SolutionInterface;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.util.NameDictionary;

public final class ContactMapAbstractSolution {
	private final Map<String, AbstractAgent> agentNameToAgent;
	private final Map<String, List<AbstractAgent>> agentNameToAgents;
	private final Map<String, Boolean> agentsMap;
	private final Map<String, Map<String, List<ContactMapAbstractEdge>>> edgesInContactMap;
	private final Map<String, Map<String, ContactMapChangedSite>> agentsInContactMap;
	private final KappaSystem kappaSystem;

	public ContactMapAbstractSolution(KappaSystem kappaSystem) {
		this.agentNameToAgent = new LinkedHashMap<String, AbstractAgent>();
		this.agentNameToAgents = new LinkedHashMap<String, List<AbstractAgent>>();
		this.agentsMap = new LinkedHashMap<String, Boolean>();
		this.edgesInContactMap = new LinkedHashMap<String, Map<String, List<ContactMapAbstractEdge>>>();
		this.agentsInContactMap = new LinkedHashMap<String, Map<String, ContactMapChangedSite>>();
		this.kappaSystem = kappaSystem;
		Collection<Agent> agents = getSolutionAgents();
		fillModelMapOfAgents(agents);
		fillAgentMap(agents);
	}

	private final Collection<Agent> getSolutionAgents() {
		Collection<Agent> agents = new ArrayList<Agent>();
		SolutionInterface solution = kappaSystem.getSolution();
		if (solution.getStraightStorage() != null) {
			agents.addAll(solution.getStraightStorage().getAgents());
		}
		if (solution.getSuperStorage() != null) {
			for (SuperSubstance substance : solution.getSuperStorage()
					.getComponents()) {
				agents.addAll(substance.getComponent().getAgents());
			}
		}
		return agents;

	}

	private final void addToEdgesAndAgentsMap(AbstractAgent agent) {
		String agentKey = agent.getName();

		Map<String, ContactMapChangedSite> sitesMap = this.agentsInContactMap
				.get(agentKey);

		fillMaps(null, agent, agentKey, sitesMap);

		if (agent.getSitesMap().isEmpty()) {
			if (sitesMap == null) {
				sitesMap = new LinkedHashMap<String, ContactMapChangedSite>();
				ContactMapChangedSite changedSite = new ContactMapChangedSite(
						agent.getDefaultSite());
				sitesMap.put(Site.DEFAULT_NAME, changedSite);
				this.agentsInContactMap.put(agentKey, sitesMap);
			}
		}
	}

	private final void addToEdgesAndAgentsMap(Integer ruleId,
			AbstractAgent agent) {
		String agentKey = agent.getName();

		Map<String, ContactMapChangedSite> sitesMap = this.agentsInContactMap
				.get(agentKey);

		fillMaps(ruleId, agent, agentKey, sitesMap);
	}

	private void fillMaps(Integer ruleId, AbstractAgent agent, String agentKey,
			Map<String, ContactMapChangedSite> sitesMap) {
		Map<String, List<ContactMapAbstractEdge>> edgesMap = this.edgesInContactMap
				.get(agentKey);
		for (AbstractSite site : agent.getSitesMap().values()) {
			String targetSiteName = site.getLinkState().getConnectedSiteName();
			String siteKey = site.getName();
			if (!NameDictionary.isDefaultSiteName(targetSiteName)) {
				edgesMap = putToEdgesInContactMap(ruleId, agentKey, edgesMap,
						site, siteKey);

			}
			ContactMapChangedSite changedSite;
			if (sitesMap == null) {
				sitesMap = new LinkedHashMap<String, ContactMapChangedSite>();
				changedSite = new ContactMapChangedSite(site);
				sitesMap.put(siteKey, changedSite);
				this.agentsInContactMap.put(agentKey, sitesMap);
			} else {
				changedSite = sitesMap.get(siteKey);
				if (changedSite == null) {
					changedSite = new ContactMapChangedSite(site);
					sitesMap.put(siteKey, changedSite);
				} else {
					changedSite.setInternalState(site);
					changedSite.setLinkState(site);
				}
			}
			if (ruleId != null) {
				changedSite.addRules(ruleId);
			}
		}
	}

	private Map<String, List<ContactMapAbstractEdge>> putToEdgesInContactMap(
			Integer ruleId, String agentKey,
			Map<String, List<ContactMapAbstractEdge>> edgesMap,
			AbstractSite site, String siteKey) {
		if (edgesMap == null) {
			edgesMap = new LinkedHashMap<String, List<ContactMapAbstractEdge>>();
			List<ContactMapAbstractEdge> edgeList = new ArrayList<ContactMapAbstractEdge>();
			ContactMapAbstractEdge edge = new ContactMapAbstractEdge(site);
			edgeList.add(edge);
			edgesMap.put(siteKey, edgeList);
			this.edgesInContactMap.put(agentKey, edgesMap);
			if (ruleId != null) {
				edge.addRules(ruleId);
			}
		} else {
			List<ContactMapAbstractEdge> edgeList = edgesMap.get(siteKey);
			if (edgeList == null) {
				edgeList = new ArrayList<ContactMapAbstractEdge>();
				edgesMap.put(siteKey, edgeList);
			}

			ContactMapAbstractEdge edge = new ContactMapAbstractEdge(site);
			boolean wasInList = false;
			for (ContactMapAbstractEdge checkedEdge : edgeList) {
				if (edge.equalz(checkedEdge)) {
					if (ruleId != null) {
						checkedEdge.addRules(ruleId);
					}
					wasInList = true;
					break;
				}
			}
			if (!wasInList) {
				edgeList.add(edge);
				if (ruleId != null) {
					edge.addRules(ruleId);
				}
			}
		}
		return edgesMap;
	}

	private final void fillModelMapOfAgents(Collection<Agent> agents) {
		fillModelMapByAgentList(agents);
		for (Rule rule : kappaSystem.getRules()) {
			for (ConnectedComponentInterface cc : rule.getLeftHandSide())
				fillModelMapByAgentList(cc.getAgents());
			if (rule.getRightHandSide() != null)
				for (ConnectedComponentInterface cc : rule.getRightHandSide())
					fillModelMapByAgentList(cc.getAgents());
		}
	}

	private final void fillModelMapByAgentList(Collection<Agent> agents) {
		for (Agent agent : agents) {
			AbstractAgent modelAgent = agentNameToAgent.get(agent.getName());
			if (modelAgent == null) {
				modelAgent = new AbstractAgent(agent.getName());
				agentNameToAgent.put(agent.getName(), modelAgent);
			}

			for (Site s : agent.getSites()) {
				AbstractSite as = new AbstractSite(s);
				as.setParentAgent(modelAgent);
				modelAgent.addModelSite(as);
			}
		}
	}

	private final void fillAgentMap(Collection<Agent> agents) {
		for (Agent agent : agents) {
			AbstractAgent abstractAgent = new AbstractAgent(agent,
					this.agentNameToAgent.get(agent.getName()));
			addAgentToAgentsMap(abstractAgent);
		}
	}

	public final boolean addAgentToAgentsMap(AbstractAgent abstractAgent) {
		String key = abstractAgent.getKey();

		if (agentsMap.get(key) == null) {
			List<AbstractAgent> agentsFromSolution = agentNameToAgents
					.get(abstractAgent.getName());
			if (agentsFromSolution == null) {
				agentsFromSolution = new ArrayList<AbstractAgent>();
				agentNameToAgents.put(abstractAgent.getName(),
						agentsFromSolution);
			}
			agentsFromSolution.add(abstractAgent);
			agentsMap.put(key, true);
		} else
			return false;

		addToEdgesAndAgentsMap(abstractAgent);
		return true;
	}

	public final void addAgentsBoundedWithFocusedAgent(AbstractAgent agent,
			List<AbstractAgent> agentsFromRule) {
		for (AbstractAgent ruleAgent : agentsFromRule) {
			Map<String, AbstractSite> sitesMapFromRule = ruleAgent
					.getSitesMap();
			for (AbstractSite siteFromRule : sitesMapFromRule.values()) {
				AbstractLinkState ls = siteFromRule.getLinkState();
				if (ls.getAgentName().equals(agent.getName())
						|| ruleAgent.getName().equals(agent.getName()))
					addAgentToAgentsMap(ruleAgent);
			}
		}

	}

	public final void addData(List<SubViewsInterface> subViews) {
		for (SubViewsInterface subView : subViews) {
			List<AbstractAgent> list = subView.getAllSubViews();
			LinkedHashSet<Integer> listOfRules = subView.getSubViewClass()
					.getRulesId();
			for (AbstractAgent a : list) {
				if (addAgentToAgentsMap(a)) {
					for (Integer ruleId : listOfRules) {
						addToEdgesAndAgentsMap(ruleId, a);
					}
				}
			}
		}
	}

	/**
	 * Util method. Construct abstract contact map by given rules for guiven
	 * agents
	 * 
	 * @param rules
	 *            given rules
	 * @param addAgentList
	 *            given agents
	 */
	final void constructAbstractCard(List<Rule> rules,
			Collection<AbstractAgent> addAgentList) {
		if (addAgentList == null) {
			addAgentList = getAgentNameToAgent().values();
		}
		for (AbstractAgent agent : addAgentList)
			addAgentToAgentsMap(agent);

		for (Rule rule : rules) {
			List<AbstractAgent> agentsFromRule = fillAgentsFromRule(rule);
			for (AbstractAgent agent : addAgentList)
				if (agent.includedInCollectionByName(agentsFromRule)) {
					addAgentsBoundedWithFocusedAgent(agent, agentsFromRule);
				}
		}

	}

	/**
	 * Util method. Clears unnecessary information in contact map.
	 * 
	 * @param agentNames
	 */
	public final void clearCard(Collection<String> agentNames) {
		List<String> namesOfAgentsToDelete = new ArrayList<String>();

		for (String key : agentNameToAgents.keySet()) {
			if (!agentNames.contains(key)) {
				namesOfAgentsToDelete.add(key);
				edgesInContactMap.remove(key);
				agentsInContactMap.remove(key);
			}
		}

		for (String name : namesOfAgentsToDelete) {
			agentNameToAgents.remove(name);
		}
	}

	/**
	 * Util method. Fills given agents to given rule.
	 * 
	 * @param rule
	 *            given rule
	 * @param agents
	 *            given agents
	 * @return
	 */
	private final List<AbstractAgent> fillAgentsFromRule(Rule rule) {
		List<AbstractAgent> agents = new ArrayList<AbstractAgent>();
		AbstractionRule abstractRule = new AbstractionRule(rule);
		// TODO hren
		if (!rule.getLeftHandSide().get(0).isEmpty()) {
			for (AbstractAgent agent : abstractRule.getLeftHandSideAgents()) {
				if (!agent.includedInCollection(agents)) {
					agents.add(agent);
				}
			}

		}
		for (AbstractAgent agent : abstractRule.getRightHandSideAgents()) {
			if (!agent.includedInCollection(agents)) {
				agents.add(agent);
			}
		}
		return agents;
	}

	public void addAllRules(List<Rule> rules) {
		for (Rule r : rules) {
			for (AbstractAgent agent : fillAgentsFromRule(r)) {
				if (addAgentToAgentsMap(agent)) {
					addToEdgesAndAgentsMap(r.getRuleId(), agent);
				}

			}
		}

	}

	// =====================================
	// getters and setters

	public final Map<String, Map<String, List<ContactMapAbstractEdge>>> getEdgesInContactMap() {
		return edgesInContactMap;
	}

	public final Map<String, AbstractAgent> getAgentNameToAgent() {
		return agentNameToAgent;
	}

	public final Map<String, Map<String, ContactMapChangedSite>> getAgentsInContactMap() {
		return agentsInContactMap;
	}

	public final Map<String, List<AbstractAgent>> getAgentNameToAgentsList() {
		return agentNameToAgents;
	}

}
