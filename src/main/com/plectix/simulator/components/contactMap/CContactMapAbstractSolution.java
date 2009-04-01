package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.solution.SuperSubstance;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IContactMapAbstractAgent;
import com.plectix.simulator.interfaces.IContactMapAbstractRule;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;

import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.simulator.SimulationData;

public class CContactMapAbstractSolution {
	private Map<Integer, IContactMapAbstractAgent> agentNameIdToAgent;
	private Map<Integer, List<IContactMapAbstractAgent>> agentNameIdToAgentsList;
	private Map<String, IContactMapAbstractAgent> agentsMap;
	private Map<Integer, Map<Integer, List<CContactMapAbstractEdge>>> edgesInContactMap;
	private Map<Integer, Map<Integer, CContactMapChangedSite>> agentsInContactMap;
	private final SimulationData simulationData;

	public Map<Integer, Map<Integer, List<CContactMapAbstractEdge>>> getEdgesInContactMap() {
		return edgesInContactMap;
	}

	public Map<Integer, IContactMapAbstractAgent> getAgentNameIdToAgent() {
		return agentNameIdToAgent;
	}

	public CContactMapAbstractSolution(SimulationData simulationData) {
		this.agentNameIdToAgent = new HashMap<Integer, IContactMapAbstractAgent>();
		this.agentNameIdToAgentsList = new HashMap<Integer, List<IContactMapAbstractAgent>>();
		this.agentsMap = new HashMap<String, IContactMapAbstractAgent>();
		this.edgesInContactMap = new HashMap<Integer, Map<Integer, List<CContactMapAbstractEdge>>>();
		this.agentsInContactMap = new HashMap<Integer, Map<Integer, CContactMapChangedSite>>();
		this.simulationData = simulationData;
		Collection<IAgent> agents = prepareSolutionAgents();
		fillModelMapOfAgents(agents);
		fillAgentMap(agents);
	}

	private Collection<IAgent> prepareSolutionAgents() {
		Collection<IAgent> agents = new ArrayList<IAgent>();
		ISolution solution = simulationData.getKappaSystem().getSolution();
		if (solution.getStraightStorage() != null) {
			agents.addAll(solution.getStraightStorage().getAgents());
		}
		if (solution.getSuperStorage() != null) {
			for (SuperSubstance substance : solution.getSuperStorage().getComponents()) {
				agents.addAll(substance.getComponent().getAgents());
			}
		}
		return agents;
		
	}
	
	public Map<Integer, Map<Integer, CContactMapChangedSite>> getAgentsInContactMap() {
		return agentsInContactMap;
	}

	public Map<Integer, List<IContactMapAbstractAgent>> getAgentNameIdToAgentsList() {
		return agentNameIdToAgentsList;
	}

	public Map<String, IContactMapAbstractAgent> getAgentsMap() {
		return agentsMap;
	}

	private void addToEdgesAndAgentsMap(IContactMapAbstractRule rule,
			IContactMapAbstractAgent agent) {
		int agentKey = agent.getNameId();
		Map<Integer, List<CContactMapAbstractEdge>> edgesMap = this.edgesInContactMap
				.get(agentKey);

		Iterator<Integer> siteIterator = agent.getSitesMap().keySet()
				.iterator();
		Map<Integer, CContactMapChangedSite> sitesMap = this.agentsInContactMap
				.get(agentKey);

		while (siteIterator.hasNext()) {
			int siteNameID = siteIterator.next();
			IContactMapAbstractSite site = agent.getSitesMap().get(siteNameID);
			int siteToNameID = site.getLinkState().getLinkSiteNameID();
			int siteKey = site.getNameId();
			if (siteToNameID != CSite.NO_INDEX) {
				if (edgesMap == null) {
					edgesMap = new HashMap<Integer, List<CContactMapAbstractEdge>>();
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
				sitesMap = new HashMap<Integer, CContactMapChangedSite>();
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
	}

	public boolean addNewData(List<IContactMapAbstractAgent> listIn,
			IContactMapAbstractRule rule) {
		if (listIn == null)
			return false;

		boolean isAdd = false;
		for (IContactMapAbstractAgent a : listIn) {
			if (addAgentToAgentsMap(a)) {
				isAdd = true;
				addToEdgesAndAgentsMap(rule, a);
			}
		}
		// listIn.clear();
		return isAdd;
	}

	private void fillModelMapOfAgents(Collection<IAgent> agents) {
		fillModelMapByAgentList(agents);

		for (CRule rule : simulationData.getKappaSystem().getRules()) {
			for (IConnectedComponent cc : rule.getLeftHandSide())
				fillModelMapByAgentList(cc.getAgents());
			if (rule.getRightHandSide() != null)
				for (IConnectedComponent cc : rule.getRightHandSide())
					fillModelMapByAgentList(cc.getAgents());
		}
	}

	private void fillModelMapByAgentList(Collection<IAgent> listIn) {
		for (IAgent a : listIn) {
			IContactMapAbstractAgent modelAgent = agentNameIdToAgent.get(a
					.getNameId());
			if (modelAgent == null) {
				modelAgent = new CContactMapAbstractAgent(a);
				agentNameIdToAgent.put(a.getNameId(), modelAgent);
			}

			for (ISite s : a.getSites()) {
				IContactMapAbstractSite as = new CContactMapAbstractSite(s);
				as.setAgentLink(modelAgent);
				modelAgent.addModelSite(as);
			}
		}
	}

	private void fillAgentMap(Collection<IAgent> agents) {

		for (IAgent agent : agents) {
			IContactMapAbstractAgent abstractAgent = new CContactMapAbstractAgent(
					agent);
			abstractAgent.addSites(agent, this.agentNameIdToAgent);
			addAgentToAgentsMap(abstractAgent);
		}
	}

	public boolean addAgentToAgentsMap(IContactMapAbstractAgent abstractAgent) {
		String key = abstractAgent.getKey();
		IContactMapAbstractAgent ag = agentsMap.get(key);
		if (ag == null) {
			List<IContactMapAbstractAgent> agentsFromSolution = agentNameIdToAgentsList
					.get(abstractAgent.getNameId());
			if(agentsFromSolution == null){
				agentsFromSolution = new ArrayList<IContactMapAbstractAgent>();
				agentNameIdToAgentsList.put(abstractAgent.getNameId(), agentsFromSolution);
			}
				
			agentsFromSolution.add(abstractAgent);
			agentsMap.put(key, abstractAgent);
		} else
			return false;

		addToEdgesAndAgentsMap(null, abstractAgent);
		return true;
	}

	public final void addAgentsToAgentsMap(
			List<IContactMapAbstractAgent> agentsFromRule) {
		for (IContactMapAbstractAgent agent : agentsFromRule)
			addAgentToAgentsMap(agent);

	}

	public void addAgentsBoundedWithFocusedAgent(
			IContactMapAbstractAgent agent,
			List<IContactMapAbstractAgent> agentsFromRule) {
		for (IContactMapAbstractAgent agentFromRule : agentsFromRule) {
			Map<Integer, IContactMapAbstractSite> sitesMapFromRule = agentFromRule
					.getSitesMap();
			Iterator<Integer> iterator = sitesMapFromRule.keySet().iterator();
			while (iterator.hasNext()) {
				int key = iterator.next();
				IContactMapAbstractSite siteFromRule = sitesMapFromRule
						.get(key);
				CContactMapLinkState ls = siteFromRule.getLinkState();
				if (ls.getAgentNameID() == agent.getNameId()
						|| agentFromRule.getNameId() == agent.getNameId())
					// if((agentNameIdList == null) ||
					// (agentNameIdList.contains(agentFromRule.getNameId())))
					addAgentToAgentsMap(agentFromRule);
			}
		}

	}

	public final List<IContactMapAbstractAgent> getListOfAgentsByNameID(
			int nameID) {
		List<IContactMapAbstractAgent> listAgents = agentNameIdToAgentsList
				.get(Integer.valueOf(nameID));
		if (listAgents == null)
			return null;
		return listAgents;
	}

	public IContactMapAbstractSite findSite(Integer agentId, Integer siteId,
			int internalStateId, int agentLinkId, int siteLinkId,
			int internalStateLinkId) {
		// IContactMapAbstractAgent agent = abstractAgentMap.get(agentId);
		// if(agent == null)
		// return null;
		// List<IContactMapAbstractSite> list = agent.getSiteMap().get(siteId);
		// if(list == null)
		// return null;
		// for(IContactMapAbstractSite s : list){
		// if(s.getInternalState().getNameId() != internalStateId)
		// continue;
		// CContactMapLinkState linkState = s.getLinkState();
		// if(linkState.getAgentNameID() != agentLinkId)
		// continue;
		// if(linkState.getInternalStateNameID() != internalStateLinkId)
		// continue;
		// if(linkState.getLinkSiteNameID() != siteLinkId)
		// continue;
		// return s;
		// }
		return null;
	}

	public void print() {
		// Iterator<Integer> aIter = abstractAgentMapOld.keySet().iterator();
		// while (aIter.hasNext()) {
		// Integer aKey = aIter.next();
		// IContactMapAbstractAgent cMAA = abstractAgentMapOld.get(aKey);
		// ((CContactMapAbstractAgent) cMAA).print();
		// }
		// System.out
		// .println(
		// "***************************************************************************"
		// );
	}

}
