package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.simulator.SimulationData;

/**
 * Class implements contact map.
 * @author avokhmin
 *
 */
public class CContactMap {
	private EContactMapMode mode = EContactMapMode.MODEL;
	private SimulationData simulationData;
	private CContactMapAbstractSolution abstractSolution;
	private CRule focusRule;
	private List<CContactMapAbstractRule> abstractRules;
	private List<CContactMapAbstractAgent> agentsFromFocusedRule;

	/**
	 * This method sets mode of create contact map.
	 * @param mode given mode
	 * @see EContactMapMode
	 */
	public void setMode(EContactMapMode mode) {
		this.mode = mode;
	}

	/**
	 * This method returns abstract solution.
	 * @return abstract solution.
	 */
	public CContactMapAbstractSolution getAbstractSolution() {
		return abstractSolution;
	}

	/**
	 * This method sets simulation data.
	 * @param simulationData given simulation data
	 */
	public void setSimulationData(SimulationData simulationData) {
		this.simulationData = simulationData;
	}

	/**
	 * This method sets "focus rule".
	 * @param focusRule given rule
	 * @see EContactMapMode
	 */
	public void setFocusRule(CRule focusRule) {
		this.focusRule = focusRule;
	}

	/**
	 * Default constructor of contact map. 
	 */
	public CContactMap() {
		agentsFromFocusedRule = new ArrayList<CContactMapAbstractAgent>();
	}

	/**
	 * This method creates abstract contact map
	 * (need uses with create contact map by <code>MODEL</code>).
	 */
	public void constructAbstractContactMap() {
		// TODO
		switch (mode) {
		case MODEL:
			boolean isEnd = false;
			while (!isEnd) {
				isEnd = true;
				for (CContactMapAbstractRule rule : abstractRules) {
					List<CContactMapAbstractAgent> newData = rule.getNewData();
					if (abstractSolution.addNewData(newData, rule)) {
						isEnd = false;
						// clear();
					}
				}
			}
			break;

		case AGENT_OR_RULE:
			// TODO add edges to contact map for agents from
			// agentNameIdToAgentsList which are not in focus;
			break;
		}

	}

	/**
	 * This method initializes abstract solution.
	 */
	public final void initAbstractSolution() {
		this.abstractSolution = new CContactMapAbstractSolution(simulationData);
	}

	/**
	 * This method initializes abstract rules.<br>
	 * For <code>AGENT_OR_RULE</code> mode, creates abstract contact map. 
	 * @param rules given rules
	 */
	public void constructAbstractRules(List<CRule> rules) {
		switch (mode) {
		case MODEL:
			List<CContactMapAbstractRule> listAbstractRules = new ArrayList<CContactMapAbstractRule>();
			for (CRule rule : rules) {
				CContactMapAbstractRule abstractRule = new CContactMapAbstractRule(
						abstractSolution, rule);
				abstractRule.initAbstractRule();
				listAbstractRules.add(abstractRule);
			}
			this.abstractRules = listAbstractRules;
			break;

		case AGENT_OR_RULE:
//					CContactMapAbstractRule abstractRule = fillFocusAgentsFromRule(
			fillFocusAgentsFromRule(
					(CRule) this.focusRule, this.agentsFromFocusedRule);
			constructAbstractCard(rules, agentsFromFocusedRule);

//			TODO wait and remove!
//			Iterator<Integer> iterator = abstractSolution
//					.getAgentNameIdToAgent().keySet().iterator();
//			while (iterator.hasNext()) {
//				Integer key = iterator.next();
//				addAgentList.add(abstractSolution.getAgentNameIdToAgent().get(
//						key));
//			}
			List<CContactMapAbstractAgent> addAgentList = new ArrayList<CContactMapAbstractAgent>();
			addAgentList.addAll(abstractSolution.getAgentNameIdToAgent().values());

			List<Integer> agentNameIdList = new ArrayList<Integer>();
			agentNameIdList.addAll(abstractSolution.getAgentNameIdToAgentsList().keySet());

			constructAbstractCard(rules, addAgentList);

			clearCard(agentNameIdList);
			break;
		}
	}

	/**
	 * Util method. Clears unnecessary information in contact map.
	 * @param agentNameIdList
	 */
	private void clearCard(List<Integer> agentNameIdList) {
		List<Integer> namesOfAgentsToDelete = new ArrayList<Integer>();

		for (Integer key : abstractSolution.getAgentNameIdToAgentsList().keySet()) {
			if (!agentNameIdList.contains(key))
				namesOfAgentsToDelete.add(key);
		}
		
		for (Integer i : namesOfAgentsToDelete) {
			abstractSolution.getAgentNameIdToAgentsList().remove(i);
			abstractSolution.getEdgesInContactMap().remove(i);
			abstractSolution.getAgentsInContactMap().remove(i);
		}
	}

	/**
	 * Util method. Construct abstract contact map by given rules for guiven agents 
	 * @param rules given rules
	 * @param addAgentList given agents
	 */
	private void constructAbstractCard(List<CRule> rules,
			List<CContactMapAbstractAgent> addAgentList) {
		for (CRule rule : rules) {
			List<CContactMapAbstractAgent> agentsFromRule = new ArrayList<CContactMapAbstractAgent>();
			fillAgentsFromRule(rule, agentsFromRule);
			for (CContactMapAbstractAgent agent : addAgentList)
				if (agent.includedInCollectionByName(agentsFromRule)) {
					abstractSolution.addAgentToAgentsMap(agent);
					abstractSolution.addAgentsBoundedWithFocusedAgent(agent,
							agentsFromRule);
					// break;
				}
		}
	}

	/**
	 * Util method. Fills given agents to given "focus rule"
	 * @param rule given rule
	 * @param agentsList given agents
	 */
	private void fillFocusAgentsFromRule(CRule rule,
			List<CContactMapAbstractAgent> agentsList) {
		CContactMapAbstractRule abstractRule = new CContactMapAbstractRule(rule);
		abstractRule.initAbstractRule();
		List<CContactMapAbstractAgent> list = abstractRule.getFocusedAgents();
		addAgentsToListFromRule(list, agentsList);
//		return abstractRule;
	}

	/**
	 * Util method. Fills given agents to given rule.
	 * @param rule given rule
	 * @param agentsList given agents
	 */
	private void fillAgentsFromRule(CRule rule,
			List<CContactMapAbstractAgent> agentsList) {
		CContactMapAbstractRule abstractRule = new CContactMapAbstractRule(rule);
		List<CContactMapAbstractAgent> agents = new ArrayList<CContactMapAbstractAgent>();
		if (rule.getLeftHandSide().get(0).isEmpty()) {
			agents = abstractRule.getLhsAgents();
			addAgentsToListFromRule(agents, agentsList);
		}
		agents = abstractRule.getRhsAgents();
		addAgentsToListFromRule(agents, agentsList);
	}

	/**
	 * Util method. Find sets difference and add to <b>agentsForAdding</b>
	 * @param agents given agents for checks
	 * @param agentsForAdding given agents for adds
	 */
	private void addAgentsToListFromRule(List<CContactMapAbstractAgent> agents,
			List<CContactMapAbstractAgent> agentsForAdding) {
		for (CContactMapAbstractAgent agent : agents) {
			if (!agent.includedInCollection(agentsForAdding)) {
				agentsForAdding.add(agent);
			}
		}
	}
}
