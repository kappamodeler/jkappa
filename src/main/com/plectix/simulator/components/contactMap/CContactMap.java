package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.action.CAddAction;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IAction;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationUtils;

public class CContactMap {
	public enum ContactMapMode {
		MODEL, AGENT_OR_RULE;
	}

	private ContactMapMode mode = ContactMapMode.MODEL;

	public ContactMapMode getMode() {
		return mode;
	}

	public void setMode(ContactMapMode mode) {
		this.mode = mode;
	}

	private List<IRule> reachableRules;
	private SimulationData simulationData;
	private ISolution solution;

	private CContactMapAbstractSolution abstractSolution;
	private List<CContactMapAbstractRule> abstractReachableRules;

	private Map<Integer, IAgent> agentsFromSolution;
	private HashMap<Integer, Map<Integer, CContactMapChangedSite>> agentsInContactMap;
	private HashMap<Integer, Map<Integer, List<CContactMapEdge>>> edgesInContactMap;
	private List<IConnectedComponent> unreachableCC;
	private IRule focusRule;
	private List<IAgent> agentsFromFocusedRule;

	public void setSimulationData(SimulationData simulationData) {
		this.simulationData = simulationData;
	}

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
		case MODEL:
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
			if (agent.equalz(checkingAgent))
				// if (agent.equals(checkingAgent))
				return true;
			for (ISite site : ((CAgent) checkingAgent).getSites()) {
				ISite linkSite = site.getLinkState().getSite();
				if (linkSite != null)
					if (linkSite.getAgentLink().equalz(agent))
						// if (linkSite.getAgentLink().equals(agent))
						return true;

			}
		}
		return false;
	}

	private void addToAgentsInContactMap(IAgent agent, IRule rule, boolean isLHS) {
		if (mode == ContactMapMode.AGENT_OR_RULE
				&& !checkConnectionWithFocused(agent)) {
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
		if (mode == ContactMapMode.AGENT_OR_RULE
				&& !checkConnectionWithFocused(agent)) {
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

	public void constructAbstractContactMap() {
		// TODO
		boolean isEnd = false;
		while (!isEnd) {
			isEnd = true;
			for (CContactMapAbstractRule rule : abstractReachableRules) {
				List<IContactMapAbstractSite> newData = rule.getNewData();
				if (abstractSolution.addNewData(newData))
					isEnd = false;
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
	Map<Integer, List<List<IInjection>>> checkedMapOfInjections;

	private boolean addReachableRule(List<IRule> rules, List<IRule> invokedRules) {
		boolean added = false;

		List<IRule> newRules = new ArrayList<IRule>();
		// for (IRule rule : rules) {
		// int injCounter = 0;
		// List<IInjection> injList = new ArrayList<IInjection>();
		// for (IConnectedComponent cc : rule.getLeftHandSide()) {
		// if (cc != CRule.EMPTY_LHS_CC
		// && cc.getInjectionsList().size() != 0) {
		// injCounter++;
		// injList.add(cc.getInjectionsList().iterator().next());
		// } else if (cc == CRule.EMPTY_LHS_CC) {
		// injCounter++;
		// }
		//
		// // else if (!unreachableCC.contains(cc))
		// // unreachableCC.add(cc);
		// }
		// if (injCounter == rule.getLeftHandSide().size()) {
		// // if (!reachableRules.contains(rule)) {
		// if (checkedMap.containsKey(rule.getRuleID())) {
		// reachableRules.add(rule);
		// }
		// newRules.add(rule);
		// }
		// }

		List<List<IInjection>> commonInjList = new ArrayList<List<IInjection>>();
		List<IRule> rulesToApply = new ArrayList<IRule>();

		for (IRule rule : invokedRules) {
			// if (!checkedRules.contains(rule)) {
			// if (!((CRule) rule).includedInCollection(checkedRules)) {

			// if (commonCheckedInjList == null) {
			// commonCheckedInjList = new ArrayList<List<IInjection>>();
			// checkedMap.put(rule.getRuleID(), commonCheckedInjList);
			// }
			if (rule.getLeftHandSide().size() == 2) {
				Collection<IInjection> injectionsMap1 = rule.getLeftHandSide()
						.get(0).getInjectionsList();
				List<IInjection> injList1 = new ArrayList<IInjection>();
				injList1.addAll(injectionsMap1);

				Collection<IInjection> injectionsMap2 = rule.getLeftHandSide()
						.get(1).getInjectionsList();
				List<IInjection> injList2 = new ArrayList<IInjection>();
				injList2.addAll(injectionsMap2);

				for (IInjection inj1 : injList1) {
					// if (!commonCheckedInjList.contains(inj1)) {
					for (IInjection inj2 : injList2) {
						List<IInjection> oldInjList = new ArrayList<IInjection>();
						oldInjList.add(inj1);
						// if (!commonCheckedInjList.contains(inj2)) {
						oldInjList.add(inj2);
						if (!rule.isClash(oldInjList)) {
							if (!isInList(oldInjList, rule)) {
								rulesToApply.add(rule);
								List<IInjection> newInjList = addNewElementsToSolution(
										oldInjList, rule);
								commonInjList.add(newInjList);
								if (!rule.includedInCollection(reachableRules))
									reachableRules.add(rule);
								added = true;
							}
							// commonCheckedInjList.addAll(oldInjList);
							// commonCheckedInjList.addAll(newInjList);
							// oldInjList.clear();
						}
						// oldInjList.remove(inj2);
						// }
						// }
					}
				}
			} else if (rule.getLeftHandSide().get(0) != CRule.EMPTY_LHS_CC) {
				Collection<IInjection> injectionsMap = rule.getLeftHandSide()
						.get(0).getInjectionsList();
				List<IInjection> injList = new ArrayList<IInjection>();
				injList.addAll(injectionsMap);

				for (IInjection inj : injList) {
					// if (!commonCheckedInjList.contains(inj)) {
					List<IInjection> oldInjList = new ArrayList<IInjection>();
					oldInjList.add(inj);

					if (!isInList(oldInjList, rule)) {
						rulesToApply.add(rule);
						List<IInjection> newInjList = addNewElementsToSolution(
								oldInjList, rule);
						commonInjList.add(newInjList);
						if (!rule.includedInCollection(reachableRules))
							reachableRules.add(rule);
						added = true;
					}
					// commonCheckedInjList.addAll(oldInjList);
					// commonCheckedInjList.addAll(newInjList);
					// oldInjList.clear();
				}
				// }
			}

			if (rule.getLeftHandSide().get(0) == CRule.EMPTY_LHS_CC) {
				List<IAgent> addAgent = new ArrayList<IAgent>();
				for (IAction action : rule.getActionList()) {
					if (action.getTypeId() == CActionType.ADD.getId()) {
						addAgent.add(((CAddAction) action).getAgentTo());
					}
				}

				if (!isInListEmpty(rule)) {
					rulesToApply.add(rule);
					List<IInjection> newInjList = addNewAgentsToSolution(
							addAgent, rule);
					commonInjList.add(newInjList);
					added = true;
				}
				// rulesToApply.add(rule);
				// commonInjList.add(newInjList);
				// commonCheckedInjList.addAll(newInjList);
				// added = true;
			}
		}

		if (added) {
			// invokedRules = new ArrayList<IRule>();
			for (int i = 0; i < rulesToApply.size(); i++) {
				IRule rule = rulesToApply.get(i);
				List<IInjection> newInjList = commonInjList.get(i);
				rule.applyRule(newInjList, simulationData);
				List<IRule> currentInvokedRules = new ArrayList<IRule>();
				SimulationUtils.doNegativeUpdate(newInjList);
				simulationData.getKappaSystem().doPositiveUpdateForContactMap(
						rule, newInjList, invokedRules);
				// addToInvokedRules(invokedRules, currentInvokedRules);

			}
		}

		return added;
	}

	// private void printInjections(IRule rule) {
	// System.out.println("ruleID = " + rule.getRuleID());
	// if(rule.getLeftHandSide().get(0) != CRule.EMPTY_LHS_CC)
	// for (IConnectedComponent cc : rule.getLeftHandSide()) {
	// List<IInjection> injList = new ArrayList<IInjection>();
	// injList.addAll(cc.getInjectionsList());
	//			
	// System.out.println("injList size = "+injList.size());
	// for(IInjection inj : injList){
	// for(IAgentLink al : inj.getAgentLinkList())
	// System.out.println(al.getAgentTo().toString());
	// }
	// }
	// }

	private void addToInvokedRules(List<IRule> rules, List<IRule> rulesToAdd) {
		for (IRule rule : rulesToAdd) {
			if (!rule.includedInCollection(rules))
				rules.add(rule);
		}
	}

	private boolean isInListEmpty(IRule rule) {
		Integer key = rule.getRuleID();

		List<List<IInjection>> commonCheckedInjList = checkedMapOfInjections
				.get(key);

		if (commonCheckedInjList == null) {
			commonCheckedInjList = new ArrayList<List<IInjection>>();
			checkedMapOfInjections.put(key, commonCheckedInjList);
			List<IInjection> injEmptyList = new ArrayList<IInjection>();
			injEmptyList.add(CInjection.EMPTY_INJECTION);
			commonCheckedInjList.add(injEmptyList);
			return false;
		}

		return true;
	}

	private boolean isInList(List<IInjection> injList, IRule rule) {
		Integer key = rule.getRuleID();

		List<List<IInjection>> commonCheckedInjList = checkedMapOfInjections
				.get(key);

		if (commonCheckedInjList == null) {
			commonCheckedInjList = new ArrayList<List<IInjection>>();
			checkedMapOfInjections.put(key, commonCheckedInjList);
		}

		IAgent agentToCheck = injList.get(0).getAgentLinkList().get(0)
				.getAgentTo();
		IConnectedComponent ccToCheck = solution
				.getConnectedComponent(agentToCheck);
		ccToCheck.initSpanningTreeMap();

		for (List<IInjection> injConsideredList : commonCheckedInjList) {
			IAgent agent = injConsideredList.get(0).getAgentLinkList().get(0)
					.getAgentTo();
			if (ccToCheck.isAutomorphism(agent))
				return true;
		}
		commonCheckedInjList.add(injList);
		return false;
	}

	public void constructFirstRules(List<IRule> rules, List<IRule> invokedRules) {
		for (IRule rule : rules) {
			int injCounter = 0;
			List<IInjection> injList = new ArrayList<IInjection>();
			for (IConnectedComponent cc : rule.getLeftHandSide()) {
				if (cc != CRule.EMPTY_LHS_CC
						&& cc.getInjectionsList().size() != 0) {
					injCounter++;
					injList.add(cc.getInjectionsList().iterator().next());
				} else if (cc == CRule.EMPTY_LHS_CC) {
					injCounter++;
				}
			}
			if (injCounter == rule.getLeftHandSide().size()) {
				// List<List<IInjection>> injConsideredList = checkedInjMap
				// .get(rule.getRuleID());
				// if (injConsideredList == null) {
				// injConsideredList = new ArrayList<List<IInjection>>();
				// checkedInjMap.put(rule.getRuleID(), injConsideredList);
				// }

				// if (!isInList(injConsideredList, injList)) {
				reachableRules.add(rule);
				invokedRules.add(rule);
				// }
			}
		}
	}

	public final void initAbstractSolution() {
		this.abstractSolution = new CContactMapAbstractSolution(simulationData);
	}

	public void constructAbstractReachableRules(List<IRule> rules) {
		// TODO constructAbstractReachableRules // ContactMap
		List<CContactMapAbstractRule> listAbstractRules = new ArrayList<CContactMapAbstractRule>();
		for (IRule rule : rules) {
			CContactMapAbstractRule abstractRule = new CContactMapAbstractRule(
					abstractSolution, rule);
			abstractRule.initAbstractRule();
			listAbstractRules.add(abstractRule);
		}
		this.abstractReachableRules = listAbstractRules;
	}

	public void constructReachableRules(List<IRule> rules) {
		checkedMapOfInjections = new HashMap<Integer, List<List<IInjection>>>();
		switch (mode) {
		case MODEL:
			boolean added = true;
			List<IRule> checkedRules = new ArrayList<IRule>();
			List<IRule> invokedRules = new ArrayList<IRule>();
			Map<Integer, List<IInjection>> checkedMap = new TreeMap<Integer, List<IInjection>>();
			HashMap<Integer, List<IRule>> checkedRulesMap = new HashMap<Integer, List<IRule>>();
			Map<Integer, List<List<IInjection>>> checkedInjMap = new HashMap<Integer, List<List<IInjection>>>();

			constructFirstRules(rules, invokedRules);

			while (added) {
				// added = addReachableRule(rules, checkedRules);
				// added = addReachableRule(rules, checkedMap);
				added = addReachableRule(rules, invokedRules);
			}

			for (IRule rule : rules) {
				// if (rule.getLeftHandSide().get(0) == CRule.EMPTY_LHS_CC)
				// //reachableRules.add(rule);
				// else
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
		case AGENT_OR_RULE:
			fillAgentsFromRule((CRule) this.focusRule,
					this.agentsFromFocusedRule);
			for (IRule rule : rules) {
				List<IAgent> agentsFromRule = new ArrayList<IAgent>();
				fillAgentsFromRule((CRule) rule, agentsFromRule);

				for (IAgent agent : this.agentsFromFocusedRule)
					if (agent.includedInCollection(agentsFromRule)) {
						// if (agentsFromRule.contains(agent)) {
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

	private List<IInjection> addNewElementsToSolution(
			List<IInjection> oldInjList, IRule rule) {
		List<IAgent> oldAgents = new ArrayList<IAgent>();

		for (IInjection inj : oldInjList) {
			IAgent agent = inj.getAgentLinkList().get(0).getAgentTo();
			IConnectedComponent cc = solution.getConnectedComponent(agent);
			if (cc != null)
				oldAgents.addAll(cc.getAgents());
		}

		return addNewAgentsToSolution(oldAgents, rule);
	}

	private List<IInjection> addNewAgentsToSolution(List<IAgent> oldAgents,
			IRule rule) {
		List<IAgent> newAgents = new ArrayList<IAgent>();

		newAgents = solution.cloneAgentsList(oldAgents, simulationData
				.getKappaSystem());
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

		if (rule.getLeftHandSide().get(0) == CRule.EMPTY_LHS_CC) {
			newInjList.add(CInjection.EMPTY_INJECTION);
		}

		return newInjList;
	}

	public void setSolution(ISolution solution) {
		this.solution = solution;
	}

	private void addAgentsToListFromRule(List<IAgent> agents,
			List<IAgent> agentsForAdding) {
		for (IAgent agent : agents) {
			if (!agent.includedInCollection(agentsForAdding)) {
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
