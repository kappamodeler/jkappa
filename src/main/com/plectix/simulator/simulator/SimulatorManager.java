package com.plectix.simulator.simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CInjection;
import com.plectix.simulator.components.CLinkState;
import com.plectix.simulator.components.CPerturbation;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.components.NameDictionary;
import com.plectix.simulator.components.ObservablesConnectedComponent;
import com.plectix.simulator.components.SolutionLines;
import com.plectix.simulator.components.CRule.Action;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.util.TimerSimulation;

public class SimulatorManager {

	private SimulationData simulationData = new SimulationData();

	private TimerSimulation timer;

	private int agentIdGenerator = 0;

	private NameDictionary nameDictionary = new NameDictionary();

	public SimulatorManager() {
	}

	public final List<IConnectedComponent> buildConnectedComponents(
			List<IAgent> agents) {

		if (agents == null || agents.isEmpty())
			return null;

		List<IConnectedComponent> result = new ArrayList<IConnectedComponent>();

		int index = 1;
		for (IAgent agent : agents)
			agent.setIdInRuleSide(index++);

		while (!agents.isEmpty()) {

			List<IAgent> connectedAgents = new ArrayList<IAgent>();

			findConnectedComponent(agents.get(0), agents, connectedAgents);

			// It needs recursive tree search of connected component
			result.add(new CConnectedComponent(connectedAgents));
		}

		return result;
	}

	private final void findConnectedComponent(IAgent rootAgent,
			List<IAgent> hsRulesList, List<IAgent> agentsList) {
		agentsList.add(rootAgent);
		rootAgent.setIdInConnectedComponent(agentsList.size() - 1);
		removeAgent(hsRulesList, rootAgent);
		for (ISite site : rootAgent.getSites()) {
			if (site.getLinkIndex() != CSite.NO_INDEX) {
				IAgent linkedAgent = findLink(hsRulesList, site.getLinkIndex());
				if (linkedAgent != null) {
					if (!isAgentInList(agentsList, linkedAgent))
						findConnectedComponent(linkedAgent, hsRulesList,
								agentsList);
				}
			}
		}
	}

	private final boolean isAgentInList(List<IAgent> list, IAgent agent) {
		for (IAgent lagent : list) {
			if (lagent == agent)
				return true;
		}
		return false;
	}

	private final IAgent findLink(List<IAgent> agents, int linkIndex) {
		for (IAgent tmp : agents) {
			for (ISite s : tmp.getSites()) {
				if (s.getLinkIndex() == linkIndex) {
					return tmp;
				}
			}
		}
		return null;
	}

	private final void removeAgent(List<IAgent> agents, IAgent agent) {
		int i = 0;
		for (i = 0; i < agents.size(); i++) {
			if (agents.get(i) == agent)
				break;
		}
		agents.remove(i);
	}

	public final IRule buildRule(List<IAgent> left, List<IAgent> right,
			String name, double activity, int ruleID) {
		return new CRule(buildConnectedComponents(left),
				buildConnectedComponents(right), name, activity, ruleID);
	}

	public final void setRules(List<IRule> rules) {
		simulationData.setRules(rules);
	}

	public final List<IRule> getRules() {
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
				simulationData.getInitialTime(), simulationData.getEvent(),
				simulationData.getPoints(), simulationData.isTime());
		CSolution solution = (CSolution) simulationData.getSolution();
		List<IRule> rules = simulationData.getRules();
		Iterator<IAgent> iterator = solution.getAgents().values().iterator();
		simulationData.getObservables().checkAutomorphisms();

		if (simulationData.isActivationMap()) {
			for (IRule rule : rules) {
				rule.createActivatedRulesList(rules);
				rule.createActivatedObservablesList(simulationData
						.getObservables());
			}
		}

		while (iterator.hasNext()) {
			IAgent agent = iterator.next();
			for (IRule rule : rules) {
				for (IConnectedComponent cc : rule.getLeftHandSide()) {
					if (cc != null) {
						IInjection inj = cc.getInjection(agent);
						if (inj != null) {
							if (!agent.isAgentHaveLinkToConnectedComponent(cc,
									inj))
								cc.setInjection(inj);
						}
					}
				}
			}

			for (IObservablesConnectedComponent oCC : simulationData
					.getObservables().getConnectedComponentList())
				if (oCC != null)
					if (oCC.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX) {
						IInjection inj = oCC.getInjection(agent);
						if (inj != null) {
							if (!agent.isAgentHaveLinkToConnectedComponent(oCC,
									inj))
								oCC.setInjection(inj);
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

		for (CPerturbation perturbation : simulationData.getPerturbations()) {
			System.out.println(perturbationToString(perturbation));
		}

	}

	private final String perturbationToString(CPerturbation perturbation) {
		String st = "-";
		String greater;
		if (perturbation.getGreater())
			greater = "> ";
		else
			greater = "< ";

		switch (perturbation.getType()) {
		case CPerturbation.TYPE_TIME: {
			st += "Whenever current time ";
			st += greater;
			st += perturbation.getTimeCondition();
			break;
		}
		case CPerturbation.TYPE_NUMBER: {
			st += "Whenever [";
			st += simulationData.getObservables().getComponentList().get(
					perturbation.getObsNameID()).getName();
			st += "] ";
			st += greater;
			st += perturbationParametersToString(perturbation
					.getLHSParametersList());
			break;
		}
		}
		st += " do kin(";
		st += perturbation.getPerturbationRule().getName();
		st += "):=";
		st += perturbationParametersToString(perturbation
				.getRHSParametersList());

		return st;
	}

	private final String perturbationParametersToString(
			List<IPerturbationExpression> sumParameters) {
		String st = new String();

		int index = 1;
		for (IPerturbationExpression parameters : sumParameters) {
			st += parameters.getValueToString();
			if (parameters.getName() != null) {
				st += "*[";
				st += parameters.getName();
				st += "]";
			}
			if (index < sumParameters.size())
				st += " + ";
			index++;
		}

		return st;
	}

	private static int indexLink = 0;

	public static final String printPartRule(List<IConnectedComponent> ccList) {
		String line = new String();
		indexLink = 0;
		int length = 0;
		if (ccList == null)
			return line;
		for (IConnectedComponent cc : ccList)
			length = length + cc.getAgents().size();
		int index = 1;
		for (IConnectedComponent cc : ccList) {
			if (cc == CRule.EMPTY_LHS_CC)
				return line;
			line += printPartRule(cc, indexLink);
			if (index < ccList.size())
				line += ",";
			index++;

		}
		return line;
	}

	public static final String printPartRule(IConnectedComponent cc, int index) {
		String line = new String();
		indexLink = index;
		int length = 0;
		if (cc == null)
			return line;
		length = cc.getAgents().size();

		int j = 1;
		if (cc == CRule.EMPTY_LHS_CC)
			return line;

		List<IAgent> sortedAgents = cc.getAgentsSortedByIdInRule();
		
		for (IAgent agent : sortedAgents) {
			line = line + agent.getName();
			line = line + "(";

			List<String> sitesList = new ArrayList<String>();

			int i = 1;
			for (ISite site : agent.getSites()) {
				String siteStr = new String(site.getName());
				// line = line + site.getName();
				if ((site.getInternalState() != null)
						&& (site.getInternalState().getNameId() >= 0)) {
					siteStr = siteStr + "~" + site.getInternalState().getName();
					// line = line + "~" + site.getInternalState().getName();
				}
				switch (site.getLinkState().getStatusLink()) {
				case CLinkState.STATUS_LINK_BOUND: {
					if (site.getLinkState().getStatusLinkRank() == CLinkState.RANK_SEMI_LINK) {
						siteStr = siteStr + "!_";
						// line = line + "!_";
					} else if (site.getAgentLink().getIdInRuleSide() < ((ISite) site
							.getLinkState().getSite()).getAgentLink()
							.getIdInRuleSide()) {
						((ISite) site.getLinkState().getSite()).getLinkState()
								.setLinkStateID(indexLink);
						siteStr = siteStr + "!" + indexLink;
						indexLink++;
						// line = line + "!" + indexLink++;
					} else {
						siteStr = siteStr + "!"
								+ site.getLinkState().getLinkStateID();
						// line = line + "!"
						// + site.getLinkState().getLinkStateID();
						site.getLinkState().setLinkStateID(-1);
					}

					break;
				}
				case CLinkState.STATUS_LINK_WILDCARD: {
					siteStr = siteStr + "?";
					// line = line + "?";
					break;
				}
				}

				// if (agent.getSites().size() > i++)
				// line = line + ",";
				sitesList.add(siteStr);
			}

			line = line + getSitesLine(sortSitesStr(sitesList));
			if (length > j) {
				line = line + "),";
			} else {
				line = line + ")";
			}
			sitesList.clear();
			j++;
		}

		return line;
	}

	private final static String getSitesLine(List<String> list) {
		String line = new String("");
		if (list.size() == 0)
			return line;
		for (int i = 0; i < list.size() - 1; i++) {
			line = line + list.get(i) + ",";
		}
		line = line + list.get(list.size() - 1);

		return line;
	}

	private final static List<String> sortSitesStr(List<String> list) {
		if (ObservablesConnectedComponent.isOcamlStyleObsName()) {
			Collections.sort(list);
		}
		
		return list;
	}


	private final void outputRules() {
		for (IRule rule : getRules()) {
			int countAgentsInLHS = rule.getCountAgentsLHS();
			int indexNewAgent = countAgentsInLHS;

			for (IAction action : rule.getActionList()) {
				switch (action.getAction()) {
				case Action.ACTION_BRK: {
					ISite siteTo = ((ISite) action.getSiteFrom().getLinkState()
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
					for (ISite site : action.getToAgent().getSites()) {
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
					ISite siteTo = ((ISite) action.getSiteFrom().getLinkState()
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
		timer = new TimerSimulation();
		timer.startTimer();
	}

	public final String getTimerMess() {
		return timer.getTimerMess();
	}

	public final TimerSimulation getTimer() {
		return timer;
	}

	public int getAgentIdGenerator() {
		return agentIdGenerator;
	}
}
