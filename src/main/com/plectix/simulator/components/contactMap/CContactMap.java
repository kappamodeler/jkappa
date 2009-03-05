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
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.SolutionUtils;
import com.plectix.simulator.interfaces.IAbstractSite;
import com.plectix.simulator.interfaces.IAction;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IContactMapAbstractAgent;
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
	private SimulationData simulationData;
	private CContactMapAbstractSolution abstractSolution;
	private IRule focusRule;
	private List<CContactMapAbstractRule> abstractRules;
	private List<IContactMapAbstractAgent> agentsFromFocusedRule;

	public ContactMapMode getMode() {
		return mode;
	}

	public void setMode(ContactMapMode mode) {
		this.mode = mode;
	}

	public CContactMapAbstractSolution getAbstractSolution() {
		return abstractSolution;
	}

	public void setSimulationData(SimulationData simulationData) {
		this.simulationData = simulationData;
	}

	public IRule getFocusRule() {
		return focusRule;
	}

	public void setFocusRule(IRule focusRule) {
		this.focusRule = focusRule;
	}

	public CContactMap() {
		agentsFromFocusedRule = new ArrayList<IContactMapAbstractAgent>();
	}

	private boolean checkConnectionWithFocused(
			IContactMapAbstractAgent checkingAgent) {
		// TODO
		for (IContactMapAbstractAgent agent : agentsFromFocusedRule) {
			if (agent.equalz(checkingAgent))
				return true;
			// for (ISite site : ((CAgent) checkingAgent).getSites()) {
			// ISite linkSite = site.getLinkState().getSite();
			// if (linkSite != null)
			// if (linkSite.getAgentLink().equalz(agent))
			// // if (linkSite.getAgentLink().equals(agent))
			// return true;
			//
			// }
		}
		return false;
	}

	private void addToAgentsInContactMap(IContactMapAbstractAgent agent,
			IRule rule, boolean isLHS) {
		// TODO
		if (mode == ContactMapMode.AGENT_OR_RULE
				&& !checkConnectionWithFocused(agent)) {
			return;
		}
		// for (ISite site : agent.getSites()) {
		// boolean internalState = false;
		// boolean linkState = false;
		// if (!isLHS) {
		// if (site.getInternalState().getNameId() != -1)
		// internalState = true;
		// if (site.getLinkState().getSite() != null)
		// linkState = true;
		// }
		// CContactMapChangedSite chSite = new CContactMapChangedSite(site,
		// internalState, linkState);
		// addToAgentsInContactMap(chSite, rule);
		// }
	}

	public void constructAbstractContactMap() {
		// TODO
		switch (mode) {
		case MODEL:
			boolean isEnd = false;
			while (!isEnd) {
				isEnd = true;
				for (CContactMapAbstractRule rule : abstractRules) {
					List<IContactMapAbstractAgent> newData = rule.getNewData();
					if (abstractSolution.addNewData(newData, rule))
						isEnd = false;
				}
			}
			break;

		case AGENT_OR_RULE:
			//TODO add edges to contact map for agents from agentNameIdToAgentsList which are not in focus; 
			break;
		}
		

		Map<Integer, List<IContactMapAbstractAgent>> abstractAgentMap = abstractSolution
				.getAgentNameIdToAgentsList();
	}

	public void constructContactMap() {
	}

	public final void initAbstractSolution() {
		this.abstractSolution = new CContactMapAbstractSolution(simulationData);
	}

	public void constructAbstractRules(List<IRule> rules) {
		switch (mode) {
		case MODEL:
			List<CContactMapAbstractRule> listAbstractRules = new ArrayList<CContactMapAbstractRule>();
			for (IRule rule : rules) {
				CContactMapAbstractRule abstractRule = new CContactMapAbstractRule(
						abstractSolution, rule);
				abstractRule.initAbstractRule();
				listAbstractRules.add(abstractRule);
			}
			this.abstractRules = listAbstractRules;
			break;

		case AGENT_OR_RULE:
			fillAgentsFromRule((CRule) this.focusRule,
					this.agentsFromFocusedRule);
			constructAbstractCard(rules, agentsFromFocusedRule);
//			for (IRule rule : rules) {
//				List<IContactMapAbstractAgent> agentsFromRule = new ArrayList<IContactMapAbstractAgent>();
//				fillAgentsFromRule(rule, agentsFromRule);
//				for (IContactMapAbstractAgent agent : this.agentsFromFocusedRule)
//					if (agent.includedInCollectionByName(agentsFromRule)) {
//						abstractSolution.addAgentToAgentsMap(agent);
//						abstractSolution.addAgentsBoundedWithFocusedAgent(agent,agentsFromRule);
//						break;
//					}
//			}
			List<IContactMapAbstractAgent> addAgentList = new ArrayList<IContactMapAbstractAgent>();
			Iterator<Integer> iterator  = abstractSolution.getAgentNameIdToAgent().keySet().iterator();
			while(iterator.hasNext()){
				Integer key = iterator.next();
				addAgentList.add(abstractSolution.getAgentNameIdToAgent().get(key));				
			}
			
			List<Integer> agentNameIdList = new ArrayList<Integer>(); 
			iterator = abstractSolution.getAgentNameIdToAgentsList().keySet().iterator();
			while(iterator.hasNext())
				agentNameIdList.add(iterator.next());
			
			constructAbstractCard(rules, addAgentList);
			
			clearCard(agentNameIdList);
			break;
		}
	}
	
	private void clearCard(List<Integer> agentNameIdList){
		Iterator<Integer> iterator  = abstractSolution.getAgentNameIdToAgentsList().keySet().iterator();
		List<Integer> listToDell = new ArrayList<Integer>();
		while(iterator.hasNext()){
			Integer key = iterator.next();
			if(!agentNameIdList.contains(key))
				listToDell.add(key);
		}
		for(Integer i : listToDell){
			abstractSolution.getAgentNameIdToAgentsList().remove(i);
			abstractSolution.getEdgesInContactMap().remove(i);
			abstractSolution.getAgentsInContactMap().remove(i);
		}
	}

	private void constructAbstractCard(List<IRule> rules,List<IContactMapAbstractAgent> addAgentList){
		for(IRule rule : rules){
			if(rule.getRuleID() == 99)
				System.out.println();
			List<IContactMapAbstractAgent> agentsFromRule = new ArrayList<IContactMapAbstractAgent>();
			fillAgentsFromRule(rule, agentsFromRule);
			for (IContactMapAbstractAgent agent : addAgentList)
				if (agent.includedInCollectionByName(agentsFromRule)) {
					abstractSolution.addAgentToAgentsMap(agent);
					abstractSolution.addAgentsBoundedWithFocusedAgent(agent,agentsFromRule);
//					break;
				}
		}
	}

	private void fillAgentsFromRule(IRule rule,
			List<IContactMapAbstractAgent> agentsList) {
		CContactMapAbstractRule abstractRule = new CContactMapAbstractRule(rule);
		List<IContactMapAbstractAgent> agents = new ArrayList<IContactMapAbstractAgent>();
		if (rule.getLeftHandSide().get(0)!=CRule.EMPTY_LHS_CC) {
			agents = abstractRule.getLhsAgents();
			addAgentsToListFromRule(agents, agentsList);
		}
		agents = abstractRule.getRhsAgents();
		addAgentsToListFromRule(agents, agentsList);
	}

	private void addAgentsToListFromRule(List<IContactMapAbstractAgent> agents,
			List<IContactMapAbstractAgent> agentsForAdding) {
		for (IContactMapAbstractAgent agent : agents) {
			if (!agent.includedInCollection(agentsForAdding)) {
				agentsForAdding.add(agent);
			}
		}
	}
}
