package com.plectix.simulator.components;

import java.util.*;

import org.apache.log4j.jmx.Agent;

import com.plectix.simulator.components.actions.CActionType;
import com.plectix.simulator.components.actions.CAddAction;
import com.plectix.simulator.components.actions.CModifyAction;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.Simulator;

public class CContactMap {
	public static final byte MODE_MODEL = 0;
	public static final byte MODE_AGENT_OR_RULE = 1;

	private byte mode = MODE_MODEL;

	public byte getMode() {
		return mode;
	}

	public void setMode(byte mode) {
		this.mode = mode;
	}

	private List<IRule> reachableRules;
	private Simulator simulator;
	private ISolution solution;

	public void setSimulator(Simulator simulator) {
		this.simulator = simulator;
	}

	private Map<Integer, IAgent> agentsFromSolution;
	private HashMap<Integer, Map<Integer, CContactMapChangedSite>> agentsInContactMap;
	private HashMap<Integer, Map<Integer, List<CContactMapEdge>>> edgesInContactMap;
	private List<IConnectedComponent> unreachableCC;
	private IRule focusRule;
	private List<IAgent> agentsFromFocusedRule;

	public IRule getFocusRule() {
		return focusRule;
	}

	public void setFocusRule(IRule focusRule) {
		this.focusRule = focusRule;
	}

	public Map<Integer, Map<Integer, CContactMapChangedSite>> getAgentsInContactMap() {
		return agentsInContactMap;
	}

	public Map<Integer, Map<Integer, List<CContactMapEdge>>> getBondsInContactMap() {
		return edgesInContactMap;
	}

	public CContactMap() {
		reachableRules = new ArrayList<IRule>();
		unreachableCC = new ArrayList<IConnectedComponent>();
		agentsFromSolution = new HashMap<Integer, IAgent>();
		edgesInContactMap = new HashMap<Integer, Map<Integer, List<CContactMapEdge>>>();
		agentsInContactMap = new HashMap<Integer, Map<Integer, CContactMapChangedSite>>();
		agentsFromFocusedRule = new ArrayList<IAgent>();
	}

	public void addCreatedAgentsToSolution(ISolution solution, List<IRule> rules) {
		this.solution = solution;
		switch (mode) {
		case MODE_MODEL:
			for (IRule rule : rules) {
				for (IAction action : rule.getActionList()) {
					if (action.getTypeId() == CActionType.ADD.getId()) {
						IAgent addAgent = ((CAddAction) action).getAgentTo();
						addAgentFromSolution(addAgent, agentsFromSolution);
						solution.addAgent(addAgent);
					}
				}
			}
			break;
		default:
			break;
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

	private boolean checkConnectionWithFocused(IAgent checkingAgent) {
		for (IAgent agent : agentsFromFocusedRule) {
			if (agent.equals(checkingAgent))
				return true;
			for (ISite site : ((CAgent) checkingAgent).getSites()) {
				ISite linkSite = site.getLinkState().getSite();
				if (linkSite != null)
					if (linkSite.getAgentLink().equals(agent))
						return true;

			}
		}
		return false;
	}

	private void addToAgentsInContactMap(IAgent agent, IRule rule, boolean isLHS) {
		if (mode == MODE_AGENT_OR_RULE && !checkConnectionWithFocused(agent)) {
			return;
		}
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
		if (mode == MODE_AGENT_OR_RULE && !checkConnectionWithFocused(agent)) {
			return;
		}

		int agentKey = agent.getNameId();
		Map<Integer, List<CContactMapEdge>> edgesMap = edgesInContactMap
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
					edgesInContactMap.put(agentKey, edgesMap);
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
						edgesInContactMap.put(agentKey, edgesMap);
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
							edgesInContactMap.put(agentKey, edgesMap);
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
							edgesInContactMap.put(agentKey, edgesMap);
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
				IAgent agent = agentsFromSolution.get(key);
				addToAgentsInContactMap(agent, null, false);
				addToEdgesInContactMap(agent, null);
		}

		for (IConnectedComponent cc : unreachableCC) {
			for (IAgent agent : cc.getAgents()) {
				if (!agentsFromSolution.containsKey(agent.getNameId())) {
					addToAgentsInContactMap(agent, null, true);
				}
			}

		}

	}

	// private void addToRuleList(List<IRule>rulesList, IRule rule){
	// if()
	// }

	private boolean addReachableRule(List<IRule> rules, List<IRule> chekedRules) {
		boolean added = false;
		for (IRule rule : rules) {
			int injCounter = 0;
			List<IInjection> injList = new ArrayList<IInjection>();
			for (IConnectedComponent cc : rule.getLeftHandSide()) {
				if (cc != CRule.EMPTY_LHS_CC
						&& cc.getInjectionsList().size() != 0) {
					injCounter++;
					injList.add(cc.getInjectionsList().iterator().next());
				} 
//				else if (!unreachableCC.contains(cc))
//					unreachableCC.add(cc);
			}
			if (injCounter == rule.getLeftHandSide().size())
				if (!reachableRules.contains(rule)) {
					reachableRules.add(rule);
				}
		}

		for (IRule rule : reachableRules) {
			if (!chekedRules.contains(rule)) {

				List<IInjection> oldInjList = new ArrayList<IInjection>();
				if (rule.getLeftHandSide().size() == 2) {
					Collection<IInjection> injectionsMap1 = rule
							.getLeftHandSide().get(0).getInjectionsList();
					List<IInjection> injList1 = new ArrayList<IInjection>();
					injList1.addAll(injectionsMap1);

					Collection<IInjection> injectionsMap2 = rule
							.getLeftHandSide().get(1).getInjectionsList();
					List<IInjection> injList2 = new ArrayList<IInjection>();
					injList2.addAll(injectionsMap2);

					for (IInjection inj1 : injList1) {
						oldInjList.add(inj1);
						for (IInjection inj2 : injList2) {
							oldInjList.add(inj2);
							addNewElementsToSolution(oldInjList, rule);
							oldInjList.remove(1);
						}
						oldInjList.clear();
					}
				} else if (rule.getLeftHandSide().get(0) != CRule.EMPTY_LHS_CC) {
					Collection<IInjection> injectionsMap = rule
							.getLeftHandSide().get(0).getInjectionsList();
					List<IInjection> injList = new ArrayList<IInjection>();
					injList.addAll(injectionsMap);

					for (IInjection inj : injList) {
						oldInjList.add(inj);
						addNewElementsToSolution(oldInjList, rule);
						oldInjList.clear();
					}
				}
				if (rule.getLeftHandSide().get(0) != CRule.EMPTY_LHS_CC) {
					chekedRules.add(rule);
					added = true;
				}
			}
		}

		return added;
	}

	private void addNewElementsToSolution(IRule rule) {
		List<Collection<IInjection>> listColls = new ArrayList<Collection<IInjection>>();
		List<Integer> listCollsNumbers = new ArrayList<Integer>();
		List<IInjection> oldInjList = new ArrayList<IInjection>();

		for (IConnectedComponent cc : rule.getLeftHandSide()) {
			Collection<IInjection> injCollection = cc.getInjectionsList();
			listColls.add(injCollection);
			listCollsNumbers.add(injCollection.size());
		}

		// for(IInjection inj)

	}

	private void addNewElementsToSolution(List<IInjection> oldInjList,
			IRule rule) {
		List<IAgent> newAgents = new ArrayList<IAgent>();
		List<IAgent> oldAgents = new ArrayList<IAgent>();

		for (IInjection inj : oldInjList) {
			IAgent agent = inj.getAgentLinkList().get(0).getAgentTo();
			oldAgents.addAll(solution.getConnectedComponent(agent).getAgents());
		}

		newAgents = solution.cloneAgentsList(oldAgents, simulator);
		solution.addAgents(newAgents);
		List<IInjection> newInjList = new ArrayList<IInjection>();

		for (IAgent agent : newAgents)
			for (IConnectedComponent cc : rule.getLeftHandSide()) {
				if (cc != null) {
					IInjection inj = cc.getInjection(agent);
					if (inj != null) {
						if (!agent.isAgentHaveLinkToConnectedComponent(cc, inj)) {
							cc.setInjection(inj);
							newInjList.add(inj);
						}

					}
				}
			}

		rule.applyRule(newInjList, simulator);
		simulator.getSimulationData().doPositiveUpdate(rule, newInjList);
	}

	public void constructReachableRules(List<IRule> rules) {
		switch (mode) {
		case MODE_MODEL:
			boolean added = true;
			List<IRule> checkedRules = new ArrayList<IRule>();

			while (added) {
				added = addReachableRule(rules, checkedRules);
			}

			for (IRule rule : rules) {
				if (rule.getLeftHandSide().get(0) == CRule.EMPTY_LHS_CC)
					reachableRules.add(rule);
				else
				for (IConnectedComponent cc : rule.getLeftHandSide()) {
					if (cc != CRule.EMPTY_LHS_CC
							&& cc.getInjectionsList().size() == 0) {
						unreachableCC.add(cc);
					}
				}
				// else {
				// int injCounter = 0;
				// for (IConnectedComponent cc : rule.getLeftHandSide()) {
				// if (cc.getInjectionsList().size() != 0)
				// injCounter++;
				// else
				// unreachableCC.add(cc);
				// }
				// if (injCounter == rule.getLeftHandSide().size())
				// reachableRules.add(rule);
				//
				// }
			}
			break;
		case MODE_AGENT_OR_RULE:
			fillAgentsFromRule((CRule) this.focusRule,
					this.agentsFromFocusedRule);
			for (IRule rule : rules) {
				List<IAgent> agentsFromRule = new ArrayList<IAgent>();
				fillAgentsFromRule((CRule) rule, agentsFromRule);

				for (IAgent agent : this.agentsFromFocusedRule)
					if (agentsFromRule.contains(agent)) {
						reachableRules.add(rule);
						break;
					}
			}
			break;
		}
	}

	private void fillAgentsFromRule(CRule rule, List<IAgent> agentsForAdding) {
		List<IAgent> agents;
		if (this.focusRule.getLeftHandSide().size() > 0) {
			agents = rule.getAgentsFromConnectedComponent(this.focusRule
					.getLeftHandSide());
			addAgentsToListFromRule(agents, agentsForAdding);
		}
		if (this.focusRule.getRightHandSide() != null) {

			agents = rule.getAgentsFromConnectedComponent(this.focusRule
					.getRightHandSide());
			addAgentsToListFromRule(agents, agentsForAdding);
		}
	}

	private void addAgentsToListFromRule(List<IAgent> agents,
			List<IAgent> agentsForAdding) {
		for (IAgent agent : agents) {
			if (!agentsForAdding.contains(agent)) {
				agentsForAdding.add(agent);
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
