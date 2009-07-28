package com.plectix.simulator.components.complex.contactMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractLinkState;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;
import com.plectix.simulator.components.complex.subviews.storage.ISubViews;
import com.plectix.simulator.components.solution.SuperSubstance;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.simulator.SimulationData;

public class CContactMapAbstractSolution {
	private Map<Integer, CAbstractAgent> agentNameIdToAgent;
	private Map<Integer, List<CAbstractAgent>> agentNameIdToAgentsList;
	private Map<String, CAbstractAgent> agentsMap;
	private Map<Integer, Map<Integer, List<CContactMapAbstractEdge>>> edgesInContactMap;
	private Map<Integer, Map<Integer, CContactMapChangedSite>> agentsInContactMap;
	private final SimulationData simulationData;

	public Map<Integer, Map<Integer, List<CContactMapAbstractEdge>>> getEdgesInContactMap() {
		return edgesInContactMap;
	}

	public Map<Integer, CAbstractAgent> getAgentNameIdToAgent() {
		return agentNameIdToAgent;
	}

	public CContactMapAbstractSolution(SimulationData simulationData) {
		this.agentNameIdToAgent = new LinkedHashMap<Integer, CAbstractAgent>();
		this.agentNameIdToAgentsList = new LinkedHashMap<Integer, List<CAbstractAgent>>();
		this.agentsMap = new LinkedHashMap<String, CAbstractAgent>();
		this.edgesInContactMap = new LinkedHashMap<Integer, Map<Integer, List<CContactMapAbstractEdge>>>();
		this.agentsInContactMap = new LinkedHashMap<Integer, Map<Integer, CContactMapChangedSite>>();
		this.simulationData = simulationData;
		Collection<CAgent> agents = prepareSolutionAgents();
		fillModelMapOfAgents(agents);
		fillAgentMap(agents);
	}

	private Collection<CAgent> prepareSolutionAgents() {
		Collection<CAgent> agents = new ArrayList<CAgent>();
		ISolution solution = simulationData.getKappaSystem().getSolution();
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

	public Map<Integer, Map<Integer, CContactMapChangedSite>> getAgentsInContactMap() {
		return agentsInContactMap;
	}

	public Map<Integer, List<CAbstractAgent>> getAgentNameIdToAgentsList() {
		return agentNameIdToAgentsList;
	}

	public Map<String, CAbstractAgent> getAgentsMap() {
		return agentsMap;
	}

	private void addToEdgesAndAgentsMap(CContactMapAbstractRule rule,
			CAbstractAgent agent) {
		int agentKey = agent.getNameId();
		Map<Integer, List<CContactMapAbstractEdge>> edgesMap = this.edgesInContactMap
				.get(agentKey);

		Map<Integer, CContactMapChangedSite> sitesMap = this.agentsInContactMap
				.get(agentKey);

		for (CAbstractSite site : agent.getSitesMap().values()) {
			int siteToNameID = site.getLinkState().getLinkSiteNameID();
			int siteKey = site.getNameId();
			if (siteToNameID != CSite.NO_INDEX) {
				if (edgesMap == null) {
					edgesMap = new LinkedHashMap<Integer, List<CContactMapAbstractEdge>>();
					List<CContactMapAbstractEdge> edgeList = new ArrayList<CContactMapAbstractEdge>();
					CContactMapAbstractEdge edge = new CContactMapAbstractEdge(
							site);
					edgeList.add(edge);
					edgesMap.put(siteKey, edgeList);
					this.edgesInContactMap.put(agentKey, edgesMap);
					edge.addRules(rule);
				} else {
					List<CContactMapAbstractEdge> edgeList = edgesMap
							.get(siteKey);
					if (edgeList == null) {
						edgeList = new ArrayList<CContactMapAbstractEdge>();
						edgesMap.put(siteKey, edgeList);
					}

					CContactMapAbstractEdge edge = new CContactMapAbstractEdge(
							site);
					boolean wasInList = false;
					for (CContactMapAbstractEdge checkedEdge : edgeList) {
						if (edge.equalz(checkedEdge)) {
							checkedEdge.addRules(rule);
							wasInList = true;
							break;
						}
					}
					if (!wasInList) {
						edgeList.add(edge);
						edge.addRules(rule);
					}
				}

			}
			CContactMapChangedSite changedSite;
			if (sitesMap == null) {
				sitesMap = new LinkedHashMap<Integer, CContactMapChangedSite>();
				changedSite = new CContactMapChangedSite(site);
				sitesMap.put(siteKey, changedSite);
				this.agentsInContactMap.put(agentKey, sitesMap);
			} else {
				changedSite = sitesMap.get(siteKey);
				if (changedSite == null) {
					changedSite = new CContactMapChangedSite(site);
					sitesMap.put(siteKey, changedSite);
				} else {
					changedSite.setInternalState(site);
					changedSite.setLinkState(site);
				}
			}
			changedSite.addRules(rule);
		}

		if (agent.getSitesMap().isEmpty()){
			CContactMapChangedSite changedSite = null;
			if (sitesMap == null) {
				sitesMap = new LinkedHashMap<Integer, CContactMapChangedSite>();
				changedSite = new CContactMapChangedSite(agent.getDefaultSite());
				sitesMap.put(-1, changedSite);
				this.agentsInContactMap.put(agentKey, sitesMap);
			} else changedSite = sitesMap.get(-1);
			changedSite.addRules(rule);
		}
	}

	private void addToEdgesAndAgentsMap(int ruleId, CAbstractAgent agent) {
		int agentKey = agent.getNameId();
		Map<Integer, List<CContactMapAbstractEdge>> edgesMap = this.edgesInContactMap
				.get(agentKey);

		Map<Integer, CContactMapChangedSite> sitesMap = this.agentsInContactMap
				.get(agentKey);

		for (CAbstractSite site : agent.getSitesMap().values()) {
			int siteToNameID = site.getLinkState().getLinkSiteNameID();
			int siteKey = site.getNameId();
			if (siteToNameID != CSite.NO_INDEX) {
				if (edgesMap == null) {
					edgesMap = new LinkedHashMap<Integer, List<CContactMapAbstractEdge>>();
					List<CContactMapAbstractEdge> edgeList = new ArrayList<CContactMapAbstractEdge>();
					CContactMapAbstractEdge edge = new CContactMapAbstractEdge(
							site);
					edgeList.add(edge);
					edgesMap.put(siteKey, edgeList);
					this.edgesInContactMap.put(agentKey, edgesMap);
					edge.addRules(ruleId);
				} else {
					List<CContactMapAbstractEdge> edgeList = edgesMap
							.get(siteKey);
					if (edgeList == null) {
						edgeList = new ArrayList<CContactMapAbstractEdge>();
						edgesMap.put(siteKey, edgeList);
					}

					CContactMapAbstractEdge edge = new CContactMapAbstractEdge(
							site);
					boolean wasInList = false;
					for (CContactMapAbstractEdge checkedEdge : edgeList) {
						if (edge.equalz(checkedEdge)) {
							checkedEdge.addRules(ruleId);
							wasInList = true;
							break;
						}
					}
					if (!wasInList) {
						edgeList.add(edge);
						edge.addRules(ruleId);
					}
				}

			}
			CContactMapChangedSite changedSite;
			if (sitesMap == null) {
				sitesMap = new LinkedHashMap<Integer, CContactMapChangedSite>();
				changedSite = new CContactMapChangedSite(site);
				sitesMap.put(siteKey, changedSite);
				this.agentsInContactMap.put(agentKey, sitesMap);
			} else {
				changedSite = sitesMap.get(siteKey);
				if (changedSite == null) {
					changedSite = new CContactMapChangedSite(site);
					sitesMap.put(siteKey, changedSite);
				} else {
					changedSite.setInternalState(site);
					changedSite.setLinkState(site);
				}
			}
			changedSite.addRules(ruleId);
		}
	}

	public boolean addNewData(List<CAbstractAgent> listIn,
			CContactMapAbstractRule rule) {
		if (listIn == null)
			return false;

		boolean isAdd = false;
		for (CAbstractAgent a : listIn) {
			if (addAgentToAgentsMap(a)) {
				isAdd = true;
				addToEdgesAndAgentsMap(rule, a);
			}
		}
		// listIn.clear();
		return isAdd;
	}

	private void fillModelMapOfAgents(Collection<CAgent> agents) {
		fillModelMapByAgentList(agents);

		for (CRule rule : simulationData.getKappaSystem().getRules()) {
			for (IConnectedComponent cc : rule.getLeftHandSide())
				fillModelMapByAgentList(cc.getAgents());
			if (rule.getRightHandSide() != null)
				for (IConnectedComponent cc : rule.getRightHandSide())
					fillModelMapByAgentList(cc.getAgents());
		}
	}

	private void fillModelMapByAgentList(Collection<CAgent> listIn) {
		for (CAgent a : listIn) {
			CAbstractAgent modelAgent = agentNameIdToAgent.get(a.getNameId());
			if (modelAgent == null) {
				modelAgent = new CAbstractAgent(a);
				agentNameIdToAgent.put(a.getNameId(), modelAgent);
			}

			for (CSite s : a.getSites()) {
				CAbstractSite as = new CAbstractSite(s);
				as.setAgentLink(modelAgent);
				modelAgent.addModelSite(as);
			}
		}
	}

	private void fillAgentMap(Collection<CAgent> agents) {

		for (CAgent agent : agents) {
			CAbstractAgent abstractAgent = new CAbstractAgent(agent);
			abstractAgent.addSites(agent, this.agentNameIdToAgent);
			addAgentToAgentsMap(abstractAgent);
		}
	}

	public boolean addAgentToAgentsMap(CAbstractAgent abstractAgent) {
		String key = abstractAgent.getKey();
		CAbstractAgent ag = agentsMap.get(key);
		if (ag == null) {
			List<CAbstractAgent> agentsFromSolution = agentNameIdToAgentsList
					.get(abstractAgent.getNameId());
			if (agentsFromSolution == null) {
				agentsFromSolution = new ArrayList<CAbstractAgent>();
				agentNameIdToAgentsList.put(abstractAgent.getNameId(),
						agentsFromSolution);
			}

			agentsFromSolution.add(abstractAgent);
			agentsMap.put(key, abstractAgent);
		} else
			return false;

		addToEdgesAndAgentsMap(null, abstractAgent);
		return true;
	}

	public void addAgentsBoundedWithFocusedAgent(CAbstractAgent agent,
			List<CAbstractAgent> agentsFromRule) {
		for (CAbstractAgent agentFromRule : agentsFromRule) {
			Map<Integer, CAbstractSite> sitesMapFromRule = agentFromRule
					.getSitesMap();
			for (CAbstractSite siteFromRule : sitesMapFromRule.values()) {
				CAbstractLinkState ls = siteFromRule.getLinkState();
				if (ls.getAgentNameID() == agent.getNameId()
						|| agentFromRule.getNameId() == agent.getNameId())
					// if((agentNameIdList == null) ||
					// (agentNameIdList.contains(agentFromRule.getNameId())))
					addAgentToAgentsMap(agentFromRule);
			}
		}

	}

	public final List<CAbstractAgent> getListOfAgentsByNameID(int nameID) {
		List<CAbstractAgent> listAgents = agentNameIdToAgentsList.get(Integer
				.valueOf(nameID));
		if (listAgents == null)
			return null;
		return listAgents;
	}

	public void addData(List<ISubViews> listOfSubViews) {
		for (ISubViews views : listOfSubViews) {
			// List<CAbstractAgent> list = views.getAllSubViews(null);
			List<CAbstractAgent> list = views.getAllSubViews();
			LinkedHashSet<Integer> listOfRules = views.getSubViewClass().getRulesId();
			for (CAbstractAgent a : list) {
				if (addAgentToAgentsMap(a)) {
					for (int ruleId : listOfRules) {
						addToEdgesAndAgentsMap(ruleId, a);
					}
				}
			}
		}
	}

}
