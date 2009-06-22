package com.plectix.simulator.components.complex.subviews.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;
import com.plectix.simulator.components.complex.subviews.CSubViews;
import com.plectix.simulator.components.complex.subviews.IAllSubViewsOfAllAgents;
import com.plectix.simulator.components.complex.subviews.base.AbstractClassSubViewBuilder;
import com.plectix.simulator.components.complex.subviews.base.SubViewsRule;
import com.plectix.simulator.components.solution.SuperSubstance;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.simulator.ThreadLocalData;

public class CMockSubViews extends AbstractClassSubViewBuilder implements IAllSubViewsOfAllAgents {
	private Map<Integer, CAbstractAgent> agentNameIdToAgent;
	private Map<Integer, List<CAbstractAgent>> agentNameIdToAgentsList;
	private Map<String, CAbstractAgent> agentsMap;
	private Map<Integer, Map<Integer, List<MockEdge>>> edgesInContactMap;
	private Map<Integer, Map<Integer, MockChangedSite>> agentsInContactMap;
	private List<MockRule> abstractRules;

	public CMockSubViews() {
		super();
		this.agentNameIdToAgent = new HashMap<Integer, CAbstractAgent>();
		this.agentNameIdToAgentsList = new HashMap<Integer, List<CAbstractAgent>>();
		this.agentsMap = new HashMap<String, CAbstractAgent>();
		this.edgesInContactMap = new HashMap<Integer, Map<Integer, List<MockEdge>>>();
		this.agentsInContactMap = new HashMap<Integer, Map<Integer, MockChangedSite>>();
	}

	private Collection<CAgent> prepareSolutionAgents(ISolution solution) {
		Collection<CAgent> agents = new ArrayList<CAgent>();
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

	public Map<Integer, List<CAbstractAgent>> getAgentNameIdToAgentsList() {
		return agentNameIdToAgentsList;
	}

	public Map<String, CAbstractAgent> getAgentsMap() {
		return agentsMap;
	}

	private void addToEdgesAndAgentsMap(MockRule rule, CAbstractAgent agent) {
		int agentKey = agent.getNameId();
		Map<Integer, List<MockEdge>> edgesMap = this.edgesInContactMap
				.get(agentKey);

		Map<Integer, MockChangedSite> sitesMap = this.agentsInContactMap
				.get(agentKey);

		for (CAbstractSite site : agent.getSitesMap().values()) {
			int siteToNameID = site.getLinkState().getLinkSiteNameID();
			int siteKey = site.getNameId();
			if (siteToNameID != CSite.NO_INDEX) {
				if (edgesMap == null) {
					edgesMap = new HashMap<Integer, List<MockEdge>>();
					List<MockEdge> edgeList = new ArrayList<MockEdge>();
					MockEdge edge = new MockEdge(site);
					edgeList.add(edge);
					edgesMap.put(siteKey, edgeList);
					this.edgesInContactMap.put(agentKey, edgesMap);
					edge.addRules(rule);
				} else {
					List<MockEdge> edgeList = edgesMap.get(siteKey);
					if (edgeList == null) {
						edgeList = new ArrayList<MockEdge>();
						edgesMap.put(siteKey, edgeList);
					}

					MockEdge edge = new MockEdge(site);
					boolean wasInList = false;
					for (MockEdge checkedEdge : edgeList) {
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
			MockChangedSite changedSite;
			if (sitesMap == null) {
				sitesMap = new HashMap<Integer, MockChangedSite>();
				changedSite = new MockChangedSite(site);
				sitesMap.put(siteKey, changedSite);
				this.agentsInContactMap.put(agentKey, sitesMap);
			} else {
				changedSite = sitesMap.get(siteKey);
				if (changedSite == null) {
					changedSite = new MockChangedSite(site);
					sitesMap.put(siteKey, changedSite);
				} else {
					changedSite.setInternalState(site);
					changedSite.setLinkState(site);
				}
			}
			changedSite.addRules(rule);
		}
	}

	private boolean addNewData(List<CAbstractAgent> listIn, MockRule rule) {
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

	private void fillModelMapOfAgents(Collection<CAgent> agents,
			List<CRule> rules) {
		fillModelMapByAgentList(agents);

		for (CRule rule : rules) {
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

	private boolean addAgentToAgentsMap(CAbstractAgent abstractAgent) {
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

	/**
	 * This method creates abstract contact map (need uses with create contact
	 * map by <code>MODEL</code>).
	 */
	private void constructAbstractContactMap() {
		boolean isEnd = false;
		while (!isEnd) {
			isEnd = true;
			for (MockRule rule : abstractRules) {
				List<CAbstractAgent> newData = rule.getNewData();
				if (addNewData(newData, rule))
					isEnd = false;
			}
		}
	}

	/**
	 * This method initializes abstract rules.<br>
	 * For <code>AGENT_OR_RULE</code> mode, creates abstract contact map.
	 * 
	 * @param rules
	 *            given rules
	 */
	private void constructAbstractRules(List<CRule> rules) {
		List<MockRule> listAbstractRules = new ArrayList<MockRule>();
		for (CRule rule : rules) {
			MockRule abstractRule = new MockRule(this, rule);
			listAbstractRules.add(abstractRule);
		}
		this.abstractRules = listAbstractRules;
	}

	public void build(ISolution solution, List<CRule> rules) {
		Collection<CAgent> agents = prepareSolutionAgents(solution);
		fillModelMapOfAgents(agents, rules);
		fillAgentMap(agents);
		constructAbstractRules(rules);
		constructClasses(abstractRules, agentNameIdToAgent);
		constructAbstractContactMap();
	}

	private void constructClasses(List<MockRule> abstractRules,
			Map<Integer, CAbstractAgent> agentNameIdToAgent) {
		List<SubViewsRule> list = new LinkedList<SubViewsRule>();
		for (MockRule mr : abstractRules)
			list.add(mr);
		constructClassesSubViews(list, agentNameIdToAgent);

	}

	public Iterator<Integer> getAllTypesIdOfAgents() {
		return agentNameIdToAgent.keySet().iterator();
	}

	public List<String> getAllTypesOfAgents() {
		List<String> outlist = new LinkedList<String>();
		for (Integer id : agentNameIdToAgent.keySet())
			outlist.add(ThreadLocalData.getNameDictionary().getName(id));
		return outlist;
	}

	public List<CAbstractAgent> getAllAgentsByTypeId(int type) {
		List<CAbstractAgent> listAgents = agentNameIdToAgentsList.get(Integer
				.valueOf(type));
		if (listAgents == null)
			return null;
		return listAgents;
	}

	public List<CSubViews> getAllSubViewsByType(String type) {
		return subViewsMap.get(Integer.valueOf(ThreadLocalData
				.getNameDictionary().getId(type)));
	}

	public List<CSubViews> getAllSubViewsByTypeId(int type) {
		return subViewsMap.get(Integer.valueOf(type));
	}

}
