package com.plectix.simulator.simulator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CLinkState;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.SolutionLines;
import com.plectix.simulator.components.CRule.Action;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.components.NameDictionary;
import com.plectix.simulator.components.CObservables.ObservablesConnectedComponent;

public class SimulatorManager {

	private SimulationData simulationData = new SimulationData();

	private int agentIdGenerator = 0;

	private NameDictionary nameDictionary = new NameDictionary();

	public SimulatorManager() {
	}

	public final List<CConnectedComponent> buildConnectedComponents(
			List<CAgent> agents) {

		if (agents == null || agents.isEmpty())
			return null;

		List<CConnectedComponent> result = new ArrayList<CConnectedComponent>();

		while (!agents.isEmpty()) {
			// int index = 0;

			List<CAgent> connectedAgents = new ArrayList<CAgent>();

			findConnectedComponent(agents.get(0), agents, connectedAgents);
			/*
			 * CAgent agent = agents.remove(0); connectedAgents.add(agent);
			 * 
			 * agent.setIdInConnectedComponent(index);
			 * 
			 * Collection<CSite> sites = agent.getSites();
			 * 
			 * for (CSite site : sites) { if (site.getLinkIndex() !=
			 * CSite.NO_INDEX) { CAgent linkedAgent = findLink(agents,
			 * site.getLinkIndex()); if (linkedAgent != null) { //if
			 * (!(connectedAgents.contains(linkedAgent))) {
			 * connectedAgents.add(linkedAgent); //} agents.remove(linkedAgent);
			 * linkedAgent.setIdInConnectedComponent(++index); } } }
			 */
			// It needs recursive tree search of connected component
			result.add(new CConnectedComponent(connectedAgents));
		}

		return result;
	}

	private final void findConnectedComponent(CAgent rootAgent,
			List<CAgent> hsRulesList, List<CAgent> agentsList) {
		// newVertex[rootAgent.getIdInConnectedComponent()] = false;
		agentsList.add(rootAgent);
		rootAgent.setIdInConnectedComponent(agentsList.size() - 1);
		hsRulesList.remove(rootAgent);
		for (CSite site : rootAgent.getSites()) {
			if (site.getLinkIndex() != CSite.NO_INDEX) {
				CAgent linkedAgent = findLink(hsRulesList, site.getLinkIndex());
				if (linkedAgent != null) {
					if (!(agentsList.contains(linkedAgent)))
						findConnectedComponent(linkedAgent, hsRulesList,
								agentsList);
					// connectedAgents.add(linkedAgent);
					// agents.remove(linkedAgent);
					// linkedAgent.setIdInConnectedComponent(++index);
				}
			}
		}
	}

	private final CAgent findLink(List<CAgent> agents, int linkIndex) {
		for (CAgent tmp : agents) {
			for (CSite s : tmp.getSites()) {
				if (s.getLinkIndex() == linkIndex) {
					return tmp;
				}
			}
		}
		return null;
	}

	public final CRule buildRule(List<CAgent> left, List<CAgent> right,
			String name, Double activity, int ruleID) {
		return new CRule(buildConnectedComponents(left),
				buildConnectedComponents(right), name, activity, ruleID);
	}

	public final void setRules(List<CRule> rules) {
		simulationData.setRules(rules);
	}

	public final List<CRule> getRules() {
		return simulationData.getRules();
	}

	public final SimulationData getSimulationData() {
		return simulationData;
	}

	public final synchronized long generateNextAgentId() {
		return agentIdGenerator++;
	}

	public final NameDictionary getNameDictionary() {
		return nameDictionary;
	}

	public void initialize() {
		CSolution solution = (CSolution) simulationData.getSolution();
		List<CRule> rules = simulationData.getRules();
		Iterator<List<CAgent>> iterator = solution.getAgentMap().values()
				.iterator();
		for (CRule rule : rules) {
			rule.createActivatedRulesList(rules);
		}

		while (iterator.hasNext()) {
			for (CAgent agent : iterator.next()) {
				for (CRule rule : rules) {
					for (CConnectedComponent cc : rule.getLeftHandSide()) {
						if (cc != null) {
							if (!agent.isAgentHaveLinkToConnectedComponent(cc)) {
								cc.setInjections(agent);
							}
						}
					}
				}

				for (ObservablesConnectedComponent oCC : simulationData
						.getObservables().getConnectedComponentList())
					if (oCC != null)
						if (!agent.isAgentHaveLinkToConnectedComponent(oCC)) {
							oCC.setInjections(agent);
						}

			}
		}

	}

	public final void outputData() {

		outputRules();

		outputPertubation();
		outputSolution();
	}

	private final void outputSolution() {
		System.out.println("INITIAL SOLUTION:");
		for (SolutionLines sl : ((CSolution) simulationData.getSolution())
				.getSolutionLines()) {
			System.out.print("-");
			System.out.print(sl.getCount());
			System.out.print("*[");
			System.out.print(sl.getLine());
			System.out.println("]");
		}
	}

	private final void outputPertubation() {

		System.out.println("PERTURBATIONS:");
	}

	public static final String printPartRule(List<CConnectedComponent> ccList) {
		String line = new String();
		int indexLink = 0;
		int length = 0;
		for (CConnectedComponent cc : ccList)
			length = length + cc.getAgents().size();
		int j = 1;
		for (CConnectedComponent cc : ccList) {
			if (cc == CRule.EMPTY_LHS_CC)
				return line;
			for (CAgent agent : cc.getAgents()) {
				line = line + agent.getName();
				// System.out.print(agent.getName());
				line = line + "(";
				// System.out.print("(");
				int i = 1;
				for (CSite site : agent.getSites()) {
					line = line + site.getName();
					// System.out.print(site.getName());
					if ((site.getInternalState() != null)
							&& (site.getInternalState().getNameId() >= 0))
						line = line + "~" + site.getInternalState().getName();
					// System.out.print("~"
					// + site.getInternalState().getName());
					switch (site.getLinkState().getStatusLink()) {
					case CLinkState.STATUS_LINK_BOUND: {
						if (site.getLinkState() == null)
							line = line + "!_";
						// System.out.print("!_");

						else if (site.getAgentLink().getIdInRuleSide() < ((CSite) site
								.getLinkState().getSite()).getAgentLink()
								.getIdInRuleSide()) {
							line = line + "!" + indexLink;
							// System.out.print("!" + indexLink);
						} else {
							line = line + "!" + indexLink;
							// System.out.print("!" + indexLink);
							indexLink++;
						}

						break;
					}
					case CLinkState.STATUS_LINK_WILDCARD: {
						line = line + "?";
						// System.out.print("?");
						break;
					}
					}

					if (agent.getSites().size() > i++)
						line = line + ",";
					// System.out.print(",");
				}
				if (length > j)
					line = line + "),";
				// System.out.print("),");
				else
					line = line + ")";
				// System.out.print(")");
				j++;
			}

		}
		return line;
	}

	private final void outputRules() {
		for (CRule rule : getRules()) {
			int countAgentsInLHS = rule.getCountAgentsLHS();
			int indexNewAgent = countAgentsInLHS;

			for (Action action : rule.getActionList()) {
				switch (action.getAction()) {
				case Action.ACTION_BRK: {
					CSite siteTo = ((CSite) action.getSiteFrom().getLinkState()
							.getSite());
					if (action.getSiteFrom().getAgentLink().getIdInRuleSide() < siteTo
							.getAgentLink().getIdInRuleSide()) {
						// BRK (#0,a) (#1,x)
						System.out.print("BRK (#");
						System.out.print(action.getSiteFrom().getAgentLink()
								.getIdInRuleSide() - 1);
						System.out.print(",");
						System.out.print(action.getSiteFrom().getName());
						System.out.print(") ");
						System.out.print("(#");
						System.out.print(siteTo.getAgentLink()
								.getIdInRuleSide() - 1);
						System.out.print(",");
						System.out.print(siteTo.getName());
						System.out.print(") ");
						System.out.println();
					}
					break;
				}
				case Action.ACTION_DEL: {
					// DEL #0
					System.out.print("DEL #");
					System.out
							.println(action.getFromAgent().getIdInRuleSide() - 1);
					break;
				}
				case Action.ACTION_ADD: {
					// ADD a#0(x)
					System.out.print("ADD " + action.getToAgent().getName()
							+ "#");

					System.out.print(action.getToAgent().getIdInRuleSide() - 1);
					System.out.print("(");
					int i = 1;
					for (CSite site : action.getToAgent().getSites()) {
						System.out.print(site.getName());
						if ((site.getInternalState() != null)
								&& (site.getInternalState().getNameId() >= 0))
							System.out.print("~"
									+ site.getInternalState().getName());
						if (action.getToAgent().getSites().size() > i++)
							System.out.print(",");
					}
					System.out.println(") ");

					break;
				}
				case Action.ACTION_BND: {
					// BND (#1,x) (#0,a)
					CSite siteTo = ((CSite) action.getSiteFrom().getLinkState()
							.getSite());
					if (action.getSiteFrom().getAgentLink().getIdInRuleSide() > siteTo
							.getAgentLink().getIdInRuleSide()) {
						System.out.print("BND (#");
						System.out.print(action.getSiteFrom().getAgentLink()
								.getIdInRuleSide() - 1);
						System.out.print(",");
						System.out.print(action.getSiteFrom().getName());
						System.out.print(") ");
						System.out.print("(#");
						System.out.print(action.getSiteTo().getAgentLink()
								.getIdInRuleSide() - 1);
						System.out.print(",");
						System.out.print(siteTo.getName());
						System.out.print(") ");
						System.out.println();
					}
					break;
				}
				case Action.ACTION_MOD: {
					// MOD (#1,x) with p
					System.out.print("MOD (#");
					System.out.print(action.getSiteFrom().getAgentLink()
							.getIdInRuleSide() - 1);
					System.out.print(",");
					System.out.print(action.getSiteFrom().getName());
					System.out.print(") with ");
					System.out.print(action.getSiteTo().getInternalState()
							.getName());
					System.out.println();
					break;
				}
				}

			}

			String line = printPartRule(rule.getLeftHandSide());
			// System.out.print("->");
			line = line + "->";
			line = line + printPartRule(rule.getRightHandSide());
			String ch = new String();
			for (int j = 0; j < line.length(); j++)
				ch = ch + "-";

			System.out.println(ch);
			// System.out.println("--------------------------------");
			if (rule.getName() != null) {
				System.out.print(rule.getName());
				System.out.print(": ");
			}
			System.out.print(line);
			System.out.println();
			System.out.println(ch);
			// System.out.println("--------------------------------");
			System.out.println();
			System.out.println();
		}
	}

}
