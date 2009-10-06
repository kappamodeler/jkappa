package com.plectix.simulator.simulator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.plectix.simulator.component.Agent;
import com.plectix.simulator.component.ConnectedComponent;
import com.plectix.simulator.component.LinkRank;
import com.plectix.simulator.component.ObservableConnectedComponent;
import com.plectix.simulator.component.Rule;
import com.plectix.simulator.component.Site;
import com.plectix.simulator.component.injections.Injection;
import com.plectix.simulator.component.injections.LiftElement;
import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.interfaces.ObservableConnectedComponentInterface;
import com.plectix.simulator.interfaces.PerturbationExpressionInterface;

public final class SimulationUtils {

	public static final String getCommandLineString(String[] args) {
		if (args.length == 0) {
			return null;
		}
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			stringBuffer.append(args[i] + " ");
		}
		stringBuffer.deleteCharAt(stringBuffer.length() - 1);
		return stringBuffer.toString();
	}

	public static final String printPartRule(
			List<ConnectedComponentInterface> components, 
			boolean ocamlStyleNaming) {
		StringBuffer sb = new StringBuffer();
		int[] indexLink = new int[] { 0 };
		int length = 0;
		if (components == null)
			return sb.toString();
		for (ConnectedComponentInterface cc : components)
			length = length + cc.getAgents().size();
		int index = 1;
		for (ConnectedComponentInterface cc : components) {
			if (cc.isEmpty())
				return sb.toString();
			sb.append(printPartRule(cc, indexLink, ocamlStyleNaming));
			if (index < components.size())
				sb.append(",");
			index++;

		}
		return sb.toString();
	}

	public static final String printPartRule(
			ConnectedComponentInterface component, int[] index, boolean ocamlStyleNaming) {
		StringBuffer sb = new StringBuffer();
		int length = 0;
		if (component == null)
			return sb.toString();
		length = component.getAgents().size();

		int j = 1;
		if (component.isEmpty())
			return sb.toString();

		List<Agent> sortedAgents = component.getAgentsSortedByIdInRule();

		for (Agent agent : sortedAgents) {
			sb.append(agent.getName());
			sb.append("(");

			List<String> sitesList = new ArrayList<String>();

			for (Site site : agent.getSites()) {
				String siteStr = new String(site.getName());
				// line = line + site.getName();
				if ((site.getInternalState() != null)
						&& (!site.getInternalState().hasDefaultName())) {
					siteStr = siteStr + "~" + site.getInternalState().getName();
					// line = line + "~" + site.getInternalState().getName();
				}
				switch (site.getLinkState().getStatusLink()) {
				case BOUND: {
					if (site.getLinkState().getStatusLinkRank() == LinkRank.SEMI_LINK) {
						siteStr = siteStr + "!_";
						// line = line + "!_";
					} else if (site.getParentAgent().getIdInRuleHandside() < ((Site) site
							.getLinkState().getConnectedSite()).getParentAgent()
							.getIdInRuleHandside()) {
						site.getLinkState().getConnectedSite().getLinkState()
								.setLinkStateId(index[0]);
						siteStr = siteStr + "!" + index[0];
						index[0]++;
						// line = line + "!" + indexLink++;
					} else {
						siteStr = siteStr + "!"
								+ site.getLinkState().getLinkStateId();
						// line = line + "!"
						// + site.getLinkState().getLinkStateID();
						site.getLinkState().setLinkStateId(-1);
					}

					break;
				}
				case WILDCARD: {
					siteStr = siteStr + "?";
					// line = line + "?";
					break;
				}
				}

				// if (agent.getSites().size() > i++)
				// line = line + ",";
				sitesList.add(siteStr);
			}

			sb.append(prepareSiteDescription(sortSiteLines(sitesList, ocamlStyleNaming)));
			sb.append((length > j) ? "),":")");
			sitesList.clear();
			j++;
		}

		return sb.toString();
	}

	private static final String prepareSiteDescription(List<String> siteLines) {
		StringBuffer sb = new StringBuffer();
		if (siteLines.size() == 0)
			return sb.toString();
		for (int i = 0; i < siteLines.size() - 1; i++) {
			sb.append(siteLines.get(i) + ",");
		}
		sb.append(siteLines.get(siteLines.size() - 1));

		return sb.toString();
	}

	private static final List<String> sortSiteLines(List<String> siteLines,
			boolean isOcamlStyleObsName) {
		if (isOcamlStyleObsName) {
			Collections.sort(siteLines);
		}
		return siteLines;
	}

	public static final List<ConnectedComponentInterface> buildConnectedComponents(
			Collection<Agent> agents) {

		if (agents == null || agents.isEmpty()) {
			return null;
		}

		List<Agent> agentsCopy = new ArrayList<Agent>();
		agentsCopy.addAll(agents);
		List<ConnectedComponentInterface> result = new ArrayList<ConnectedComponentInterface>();

		int index = 1;
		for (Agent agent : agentsCopy)
			agent.setIdInRuleSide(index++);

		while (!agentsCopy.isEmpty()) {

			List<Agent> connectedAgents = new ArrayList<Agent>();

			findConnectedComponent(agentsCopy.get(0), agentsCopy, connectedAgents);

			// It needs recursive tree search of connected component
			result.add(new ConnectedComponent(connectedAgents));
		}

		return result;
	}

	private static final void findConnectedComponent(Agent rootAgent,
			List<Agent> agentsFromRules, List<Agent> agents) {
		agents.add(rootAgent);
		rootAgent.setIdInConnectedComponent(agents.size() - 1);
		agentsFromRules.remove(rootAgent);
		// hsRulesList.remove(rootAgent);
		for (Site site : rootAgent.getSites()) {
			if (site.getLinkIndex() != -1) {
				Agent linkedAgent = findAgentByLinkIndex(agentsFromRules, site.getLinkIndex());
				if (linkedAgent != null) {
					if (!isAgentInList(agents, linkedAgent))
						findConnectedComponent(linkedAgent, agentsFromRules,
								agents);
				}
			}
		}
	}

	private static final boolean isAgentInList(List<Agent> agentsList, Agent agent) {
		for (Agent lagent : agentsList) {
			if (lagent == agent) {
				return true;
			}
		}
		return false;
	}

	private static final Agent findAgentByLinkIndex(List<Agent> agents, int linkIndex) {
		for (Agent tmp : agents) {
			for (Site s : tmp.getSites()) {
				if (s.getLinkIndex() == linkIndex) {
					return tmp;
				}
			}
		}
		return null;
	}

	public static final Rule buildRule(List<Agent> leftHandSideAgents, List<Agent> rightHandSideAgents,
			String name, double activity, int id, boolean isStorify) {
		return new Rule(buildConnectedComponents(leftHandSideAgents),
				buildConnectedComponents(rightHandSideAgents), name, activity, id,
				isStorify);
	}
			
	public static final String[] changeArguments(String[] commandLineArguments) {
		String[] argsNew = new String[commandLineArguments.length];
		int i = 0;
		for (String st : commandLineArguments)
			if (st.startsWith("-"))
				argsNew[i++] = st.substring(0, 2)
						+ st.substring(2).replaceAll("-", "_");
			else
				argsNew[i++] = st;
		return argsNew;
	}

	public static final void addToAgentList(List<Agent> list, Agent agent) {
		if (agent.includedInCollection(list)) {
			return;
		}
		list.add(agent);
	}

	public static final void doNegativeUpdate(List<Injection> injections) {
		for (Injection injection : injections) {
			if (injection != ThreadLocalData.getEmptyInjection()) {
				for (Site site : injection.getChangedSites()) {
					site.getParentAgent().getDefaultSite()
							.clearIncomingInjections(injection);
					site.getParentAgent().getDefaultSite().clearLifts();
					site.clearIncomingInjections(injection);
					site.clearLifts();
				}
				if (injection.getChangedSites().size() != 0) {
					for (Site site : injection.getSiteList()) {
						if (!injection
								.checkSiteExistanceAmongChangedSites(site)) {
							site.removeInjectionFromLift(injection);
						}
					}
					injection.getConnectedComponent()
							.removeInjection(injection);
				}
			}
		}
	}
	
	public static final void doNegativeUpdate(Injection injection) {
		if (injection != ThreadLocalData.getEmptyInjection()) {
			for (Site site : injection.getChangedSites()) {
				site.getParentAgent().getDefaultSite().clearIncomingInjections(injection);
				site.getParentAgent().getDefaultSite().clearLifts();
				site.clearIncomingInjections(injection);
				site.clearLifts();
			}
			for (Site site : injection.getSiteList()) {
				site.removeInjectionFromLift(injection);
			}
			injection.getConnectedComponent().removeInjection(injection);
		}
	}
	
	public static final List<Agent> doNegativeUpdateForDeletedAgents(
			Rule rule, List<Injection> injections) {
		List<Agent> freeAgents = new ArrayList<Agent>();
		for (Injection injection : injections) {
			for (Site checkedSite : rule.getSitesConnectedWithDeleted()) {
				if (!injection.checkSiteExistanceAmongChangedSites(checkedSite)) {

					Agent checkedAgent = checkedSite.getParentAgent();
					addToAgentList(freeAgents, checkedAgent);
					for (LiftElement lift : checkedAgent.getDefaultSite()
							.getLift()) {
						lift.getConnectedComponent().removeInjection(
								lift.getInjection());
					}
					checkedAgent.getDefaultSite().clearLifts();
					for (LiftElement lift : checkedSite.getLift()) {

						for (Site site : lift.getInjection().getSiteList()) {
							if (site != checkedSite)
								site.removeInjectionFromLift(lift
										.getInjection());
						}

						lift.getConnectedComponent().removeInjection(
								lift.getInjection());
					}
					checkedSite.clearLifts();
				}
			}
		}
		for (Site checkedSite : rule.getSitesConnectedWithBroken()) {
			Agent checkedAgent = checkedSite.getParentAgent();
			addToAgentList(freeAgents, checkedAgent);
		}
		return freeAgents;
	}

	public static final String perturbationParametersToString(
			List<PerturbationExpressionInterface> perturbationExpressions) {
		StringBuffer sb = new StringBuffer();

		int index = 1;
		for (PerturbationExpressionInterface expression : perturbationExpressions) {
			sb.append(expression.getValueToString());
			if (expression.getName() != null) {
				sb.append("*[");
				sb.append(expression.getName());
				sb.append("]");
			}
			if (index < perturbationExpressions.size())
				sb.append(" + ");
			index++;
		}

		return sb.toString();
	}

	public static final void positiveUpdate(List<Rule> rulesList,
			List<ObservableConnectedComponentInterface> rules, Rule rule) {
		for (Rule ruleFromList : rulesList) {
			// if(rules!=rule)
			for (ConnectedComponentInterface cc : ruleFromList.getLeftHandSide()) {
				cc.doPositiveUpdate(rule.getRightHandSide());
			}
		}
		for (ObservableConnectedComponentInterface oCC : rules) {
			if (oCC.getMainAutomorphismNumber() == ObservableConnectedComponent.NO_INDEX)
				oCC.doPositiveUpdate(rule.getRightHandSide());
		}
	}

	/**
	 * current
	 * @param currentAgent
	 * @param solutionAgent
	 * @return
	 */
	// currentAgent is the biggest one. solutionAgent should be contained in it.
	public static final boolean justCompareAgents(Agent solutionAgent, Agent currentAgent) {
		if (currentAgent == null || solutionAgent == null)
			return false;
		for (Site site : currentAgent.getSites()) {
			Site solutionSite = solutionAgent.getSiteByName(site.getName());
			if (solutionSite == null)
				return false;
			if (!site.expandedEqualz(solutionSite, false))
				return false;
		}
		return true;
	}
	
	public static final List<ConnectedComponentInterface> splitAndCopy(KappaSystem ks, Collection<Agent> agents) {
		List<ConnectedComponentInterface> set = new LinkedList<ConnectedComponentInterface>();
		Collection<ConnectedComponentInterface> split = split(agents);
		if (split == null) {
			set.add(new ConnectedComponent());
			return set;
		}
		for (ConnectedComponentInterface cc : split) {
			set.add(ks.getSolution().cloneConnectedComponent(cc));
		}
		if (set.isEmpty()) {
			return null;
		}
		return set;
	}
	
	private static final Collection<ConnectedComponentInterface> split(Collection<Agent> agents){
		return SimulationUtils.buildConnectedComponents(agents);
	}
}