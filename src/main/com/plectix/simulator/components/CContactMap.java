package com.plectix.simulator.components;

import java.util.*;

import com.plectix.simulator.components.actions.CActionType;
import com.plectix.simulator.components.actions.CAddAction;
import com.plectix.simulator.interfaces.*;

public class CContactMap {
	private List<IRule> reachableRules;
	private List<IRule> unreachableRules;
	private Map<Integer, IAgent> agentsFromSolution;
	private Map<Integer, Map<Integer, ChangedSiteWithRule>> agentsInContactMap;
	private Map<Integer, IAgent> unreachableAgentsFromRules;
	private Map<Integer, IAgent> usefulAgentsFromSolution;
	private List<IConnectedComponent> reachableCC;
	private List<IConnectedComponent> unReachableCC;

	class ChangedSiteWithRule extends ChangedSite {
		List<Integer> usedRuleIDs;

		public ChangedSiteWithRule(ISite site, boolean internalState,
				boolean linkState) {
			super(site, internalState, linkState);
			usedRuleIDs = new ArrayList<Integer>();
		}

		public void addRules(int value) {
			if (!usedRuleIDs.contains(value))
				usedRuleIDs.add(value);
		}

	}

	private void addToAgentsInContactMap(ChangedSiteWithRule site, IRule rule) {
		int agentKey = site.getSite().getAgentLink().getNameId();
		Map<Integer, ChangedSiteWithRule> chSiteMap = agentsInContactMap
				.get(agentKey);
		if (chSiteMap == null) {
			chSiteMap = new HashMap<Integer, ChangedSiteWithRule>();
			chSiteMap.put(site.getSite().getNameId(), site);
			agentsInContactMap.put(agentKey, chSiteMap);
		} else {
			int siteKey = site.getSite().getNameId();
			ChangedSiteWithRule chSite = chSiteMap.get(siteKey);
			if (chSite == null) {
				chSiteMap.put(siteKey, site);
			} else {
				if (!chSite.isInternalState())
					chSite.setInternalState(site.isInternalState());
				if (!chSite.isLinkState())
					chSite.setLinkState(site.isLinkState());
			}
		}
		if (rule != null)
			site.addRules(rule.getRuleID());
	}

	public CContactMap() {
		reachableRules = new ArrayList<IRule>();
		unreachableRules = new ArrayList<IRule>();
		reachableCC = new ArrayList<IConnectedComponent>();
		unReachableCC = new ArrayList<IConnectedComponent>();
		agentsFromSolution = new HashMap<Integer, IAgent>();
		unreachableAgentsFromRules = new HashMap<Integer, IAgent>();
		
		agentsInContactMap = new HashMap<Integer, Map<Integer, ChangedSiteWithRule>>();
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

	private void addToAgentsInContactMap(IAgent agent, IRule rule){
		for (ISite site : agent.getSites()) {
			boolean internalState = false;
			boolean linkState = false;
			if (site.getInternalState().getNameId() != -1)
				internalState = true;
			if (site.getLinkState().getSite() != null)
				linkState = true;
			ChangedSiteWithRule chSite = new ChangedSiteWithRule(
					site, internalState, linkState);
			addToAgentsInContactMap(chSite, rule);
		}
	}
	
	public void constructContactMap() {
		for (IRule rule : reachableRules) {
			if (rule.getRightHandSide() != null) {
				List<IAgent> agentsFromRHS = ((CRule) rule)
						.getAgentsFromConnectedComponent(rule
								.getRightHandSide());
				for (IAgent agent : agentsFromRHS) {
				addToAgentsInContactMap(agent, rule); 	
				}
			}
		}

		Iterator<Integer> iterator = agentsFromSolution.keySet().iterator();
		while (iterator.hasNext()) {
			int key = iterator.next();
			if (agentsInContactMap.get(key) == null) {
				IAgent agent = agentsFromSolution.get(key);
				addToAgentsInContactMap(agent, null);
			}
		}
	}

	public void constructReachableRules(List<IRule> rules) {
		// create reachable rules list
		for (IRule rule : rules) {
			if (rule.getLeftHandSide().get(0) == CRule.EMPTY_LHS_CC) {
				reachableRules.add(rule);
			} else {
				int injCounter = 0;
				for (IConnectedComponent cc : rule.getLeftHandSide()) {
					if (cc.getInjectionsList().size() != 0)
						injCounter++;
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
