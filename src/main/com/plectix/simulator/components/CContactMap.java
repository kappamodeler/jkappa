package com.plectix.simulator.components;

import java.util.*;

import com.plectix.simulator.components.actions.CActionType;
import com.plectix.simulator.components.actions.CAddAction;
import com.plectix.simulator.interfaces.*;

public class CContactMap {
	private List<IRule> reachableRules;
	private Map<Integer, IAgent> agentsFromSolution;
	private HashMap<Integer, Map<Integer, CContactMapChangedSite>> agentsInContactMap;
	private HashMap<Integer, Map<Integer, List<CContactMapEdge>>> bondsInContactMap;
	private List<IConnectedComponent> unreachableCC;

	public Map<Integer, Map<Integer, CContactMapChangedSite>> getAgentsInContactMap() {
		return agentsInContactMap;
	}

	public Map<Integer, Map<Integer, List<CContactMapEdge>>> getBondsInContactMap() {
		return bondsInContactMap;
	}

	public CContactMap() {
		reachableRules = new ArrayList<IRule>();
		unreachableCC = new ArrayList<IConnectedComponent>();
		agentsFromSolution = new HashMap<Integer, IAgent>();
		bondsInContactMap = new HashMap<Integer, Map<Integer, List<CContactMapEdge>>>();
		agentsInContactMap = new HashMap<Integer, Map<Integer, CContactMapChangedSite>>();
	}

	public void addCreatedAgentsToSolution(ISolution solution, List<IRule> rules) {
		for (IRule rule : rules) {
			for (IAction action : rule.getActionList()) {
				if (action.getTypeId() == CActionType.ADD.getId()) {
					IAgent addAgent = ((CAddAction) action).getAgentTo();
					addAgentFromSolution(addAgent, agentsFromSolution);
					solution.addAgent(addAgent);
				}
			}
		}
	}

	private void addToAgentsInContactMap(CContactMapChangedSite site, IRule rule) {
		int agentKey = site.getSite().getAgentLink().getNameId();
		Map<Integer, CContactMapChangedSite> chSiteMap = agentsInContactMap
				.get(agentKey);
		if (chSiteMap == null) {
			chSiteMap = new HashMap<Integer, CContactMapChangedSite>();
			chSiteMap.put(site.getSite().getNameId(), site);
			agentsInContactMap.put(agentKey, chSiteMap);
		} else {
			int siteKey = site.getSite().getNameId();
			CContactMapChangedSite chSite = chSiteMap.get(siteKey);
			if (chSite == null) {
				chSiteMap.put(siteKey, site);
			} else {
				if (!chSite.isInternalState())
					chSite.setInternalState(site.isInternalState());
				if (!chSite.isLinkState())
					chSite.setLinkState(site.isLinkState());
				if (site.isLinkState())
					chSite.addRules(rule);
				return;
			}
		}
		if (site.isLinkState())
			site.addRules(rule);
	}

	private void addToAgentsInContactMap(IAgent agent, IRule rule, boolean isLHS) {
		for (ISite site : agent.getSites()) {
			boolean internalState = false;
			boolean linkState = false;
			if (!isLHS) {
				if (site.getInternalState().getNameId() != -1)
					internalState = true;
				if (site.getLinkState().getSite() != null)
					linkState = true;
			}
			CContactMapChangedSite chSite = new CContactMapChangedSite(site,
					internalState, linkState);
			addToAgentsInContactMap(chSite, rule);
		}
	}

	private void addToEdgesInContactMap(IAgent agent, IRule rule) {
		int agentKey = agent.getNameId();
		Map<Integer, List<CContactMapEdge>> edgesMap = bondsInContactMap
				.get(agentKey);

		for (ISite site : agent.getSites()) {
			ISite siteTo = site.getLinkState().getSite();
			if (siteTo != null)
				if (edgesMap == null) {
					edgesMap = new HashMap<Integer, List<CContactMapEdge>>();
					List<CContactMapEdge> edgeList = new ArrayList<CContactMapEdge>();
					CContactMapEdge edge = new CContactMapEdge(site, siteTo);
					edgeList.add(edge);
					edgesMap.put(site.getNameId(), edgeList);
					bondsInContactMap.put(agentKey, edgesMap);
					edge.addRules(rule);
				} else {
					int siteKey = site.getNameId();
					ArrayList<CContactMapEdge> edgeList = (ArrayList<CContactMapEdge>) edgesMap
							.get(siteKey);
					if (edgeList == null) {
						CContactMapEdge edge = new CContactMapEdge(site, siteTo);
						edgeList = new ArrayList<CContactMapEdge>();
						edgeList.add(edge);
						edgesMap.put(site.getNameId(), edgeList);
						bondsInContactMap.put(agentKey, edgesMap);
						edge.addRules(rule);
					} else if (edgeList.size() == 1) {
						CContactMapEdge edge = edgeList.get(0);
						if (edge.getVertexTo() == null) {
							edge.setVertexTo(siteTo);
							edge.clearRules();
							edge.addRules(rule);
						} else if (edge.getVertexTo() != null) {
							CContactMapEdge newEdge = new CContactMapEdge(site,
									siteTo);
							edgeList.add(newEdge);
							edgesMap.put(site.getNameId(), edgeList);
							bondsInContactMap.put(agentKey, edgesMap);
							newEdge.addRules(rule);
						}
					} else {
						int counter = 0;
						for (CContactMapEdge edge : edgeList) {
							if (edge.getVertexTo().equals(siteTo)) {
								edge.addRules(rule);
								break;
							}
							counter++;
						}
						if (counter == edgeList.size()) {
							CContactMapEdge edge = new CContactMapEdge(site,
									siteTo);
							edgeList.add(edge);
							edgesMap.put(site.getNameId(), edgeList);
							bondsInContactMap.put(agentKey, edgesMap);
							edge.addRules(rule);
						}
					}
				}
		}
	}

	public void constructContactMap() {
		for (IRule rule : reachableRules) {
			if (rule.getRightHandSide() != null) {
				List<IAgent> agentsFromRHS = ((CRule) rule)
						.getAgentsFromConnectedComponent(rule
								.getRightHandSide());
				for (IAgent agent : agentsFromRHS) {
					addToAgentsInContactMap(agent, rule, false);
					addToEdgesInContactMap(agent, rule);
				}
			}
		}

		Iterator<Integer> iterator = agentsFromSolution.keySet().iterator();
		while (iterator.hasNext()) {
			int key = iterator.next();
			if (!agentsInContactMap.containsKey(key)) {
				IAgent agent = agentsFromSolution.get(key);
				addToAgentsInContactMap(agent, null, false);
				addToEdgesInContactMap(agent, null);
			}
		}

		for (IConnectedComponent cc : unreachableCC) {
			for (IAgent agent : cc.getAgents()) {
				if (!agentsFromSolution.containsKey(agent.getNameId())) {
					addToAgentsInContactMap(agent, null, true);
				}
			}

		}

	}

	public void constructReachableRules(List<IRule> rules) {
		for (IRule rule : rules) {
			if (rule.getLeftHandSide().get(0) == CRule.EMPTY_LHS_CC) {
				reachableRules.add(rule);
			} else {
				int injCounter = 0;
				for (IConnectedComponent cc : rule.getLeftHandSide()) {
					if (cc.getInjectionsList().size() != 0)
						injCounter++;
					else
						unreachableCC.add(cc);
				}
				if (injCounter == rule.getLeftHandSide().size())
					reachableRules.add(rule);

			}
		}
	}

	public void addAgentFromSolution(List<IAgent> agentList) {
		if (agentList == null)
			return;
		for (IAgent agent : agentList)
			addAgentFromSolution(agent, agentsFromSolution);
	}

	public void addAgentFromSolution(IAgent agent, Map<Integer, IAgent> map) {
		if (agent == null)
			return;
		IAgent curAgent = map.get(agent.getNameId());
		if (curAgent == null) {
			map.put(agent.getNameId(), agent);
		}
	}
}
