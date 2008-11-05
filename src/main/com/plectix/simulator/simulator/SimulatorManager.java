package com.plectix.simulator.simulator;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CLinkState;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.ObservablesConnectedComponent;
import com.plectix.simulator.components.SolutionLines;
import com.plectix.simulator.components.CRule.Action;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.components.NameDictionary;
import com.plectix.simulator.interfaces.IObservablesComponent;

public class SimulatorManager {

	private SimulationData simulationData = new SimulationData();

	private int agentIdGenerator = 0;

	private long timeStart;

	private long timeStartThread;

	private NameDictionary nameDictionary = new NameDictionary();

	public SimulatorManager() {
	}

	public final List<CConnectedComponent> buildConnectedComponents(
			List<CAgent> agents) {

		if (agents == null || agents.isEmpty())
			return null;

		List<CConnectedComponent> result = new ArrayList<CConnectedComponent>();

		while (!agents.isEmpty()) {

			List<CAgent> connectedAgents = new ArrayList<CAgent>();

			findConnectedComponent(agents.get(0), agents, connectedAgents);

			// It needs recursive tree search of connected component
			result.add(new CConnectedComponent(connectedAgents));
		}

		return result;
	}

	private final void findConnectedComponent(CAgent rootAgent,
			List<CAgent> hsRulesList, List<CAgent> agentsList) {
		agentsList.add(rootAgent);
		rootAgent.setIdInConnectedComponent(agentsList.size() - 1);
		removeAgent(hsRulesList, rootAgent);
		for (CSite site : rootAgent.getSites()) {
			if (site.getLinkIndex() != CSite.NO_INDEX) {
				CAgent linkedAgent = findLink(hsRulesList, site.getLinkIndex());
				if (linkedAgent != null) {
					if (!isAgentInList(agentsList, linkedAgent))
						findConnectedComponent(linkedAgent, hsRulesList,
								agentsList);
				}
			}
		}
	}

	private final boolean isAgentInList(List<CAgent> list, CAgent agent) {
		for (CAgent lagent : list) {
			if (lagent == agent)
				return true;
		}
		return false;
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

	private final void removeAgent(List<CAgent> agents, CAgent agent) {
		int i = 0;
		for (i = 0; i < agents.size(); i++) {
			if (agents.get(i) == agent)
				break;
		}
		agents.remove(i);
	}

	public final CRule buildRule(List<CAgent> left, List<CAgent> right,
			String name, double activity, int ruleID) {
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
		simulationData.getObservables().init(simulationData.getTimeLength(),
				simulationData.getInitialTime(), simulationData.getPoints());
		CSolution solution = (CSolution) simulationData.getSolution();
		List<CRule> rules = simulationData.getRules();
		Iterator<List<CAgent>> iterator = solution.getAgentMap().values()
				.iterator();
		simulationData.getObservables().checkAutomorphisms();

		for (CRule rule : rules) {
			rule.createActivatedRulesList(rules);
			rule
					.createActivatedObservablesList(simulationData
							.getObservables());
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
							if (oCC.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX)
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

	private static int indexLink = 0;

	public static final String printPartRule(List<CConnectedComponent> ccList) {
		String line = new String();
		indexLink = 0;
		int length = 0;
		if (ccList == null)
			return line;
		for (CConnectedComponent cc : ccList)
			length = length + cc.getAgents().size();
		for (CConnectedComponent cc : ccList) {
			if (cc == CRule.EMPTY_LHS_CC)
				return line;
			line += printPartRule(cc, indexLink);

		}
		return line;
	}

	public static final String printPartRule(CConnectedComponent cc, int index) {
		String line = new String();
		indexLink = index;
		int length = 0;
		if (cc == null)
			return line;
		length = cc.getAgents().size();

		int j = 1;
		if (cc == CRule.EMPTY_LHS_CC)
			return line;
		for (CAgent agent : cc.getAgents()) {
			line = line + agent.getName();
			line = line + "(";
			int i = 1;
			for (CSite site : agent.getSites()) {
				line = line + site.getName();
				if ((site.getInternalState() != null)
						&& (site.getInternalState().getNameId() >= 0))
					line = line + "~" + site.getInternalState().getName();
				switch (site.getLinkState().getStatusLink()) {
				case CLinkState.STATUS_LINK_BOUND: {
					if (site.getLinkState().getStatusLinkRank() == CLinkState.RANK_SEMI_LINK)
						line = line + "!_";
					else if (site.getAgentLink().getIdInConnectedComponent() < ((CSite) site
							.getLinkState().getSite()).getAgentLink()
							.getIdInConnectedComponent()) {
						((CSite) site.getLinkState().getSite()).getLinkState()
								.setLinkStateID(indexLink);
						line = line + "!" + indexLink++;
					} else {
						line = line + "!"
								+ site.getLinkState().getLinkStateID();
						site.getLinkState().setLinkStateID(-1);
					}

					break;
				}
				case CLinkState.STATUS_LINK_WILDCARD: {
					line = line + "?";
					break;
				}
				}

				if (agent.getSites().size() > i++)
					line = line + ",";
			}
			if (length > j)
				line = line + "),";
			else
				line = line + ")";
			j++;
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
			line = line + "->";
			line = line + printPartRule(rule.getRightHandSide());
			String ch = new String();
			for (int j = 0; j < line.length(); j++)
				ch = ch + "-";

			System.out.println(ch);
			if (rule.getName() != null) {
				System.out.print(rule.getName());
				System.out.print(": ");
			}
			System.out.print(line);
			System.out.println();
			System.out.println(ch);
			System.out.println();
			System.out.println();
		}
	}

	public final void startTimer() {
		timeStart = System.currentTimeMillis();
		timeStartThread = ManagementFactory.getThreadMXBean()
				.getCurrentThreadCpuTime();
	}

	public final String getTimer() {
		double wallClockTimeInSeconds = 1.0E-3 * (System.currentTimeMillis() - timeStart);
		double threadTimeInSeconds = 1.0E-9 * (ManagementFactory
				.getThreadMXBean().getCurrentThreadCpuTime() - timeStartThread);
		return "WallClock=" + wallClockTimeInSeconds + " ThreadTime="
				+ threadTimeInSeconds;
	}

	public int getAgentIdGenerator() {
		return agentIdGenerator;
	}
}
