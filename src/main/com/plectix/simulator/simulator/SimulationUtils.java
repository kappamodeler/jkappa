package com.plectix.simulator.simulator;

import java.util.*;

import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.ObservablesConnectedComponent;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.injections.CLiftElement;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.interfaces.IObservablesConnectedComponent;
import com.plectix.simulator.interfaces.IPerturbationExpression;

public class SimulationUtils {

	public final static String getCommandLineString(String[] args) {
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

	public static final String printPartRule(List<IConnectedComponent> ccList,
			boolean isOcamlStyleObsName) {
		String line = new String();
		int[] indexLink = new int[] { 0 };
		int length = 0;
		if (ccList == null)
			return line;
		for (IConnectedComponent cc : ccList)
			length = length + cc.getAgents().size();
		int index = 1;
		for (IConnectedComponent cc : ccList) {
			if (cc.isEmpty())
				return line;
			line += printPartRule(cc, indexLink, isOcamlStyleObsName);
			if (index < ccList.size())
				line += ",";
			index++;

		}
		return line;
	}

	public static final String printPartRule(IConnectedComponent cc,
			int[] index, boolean isOcamlStyleObsName) {
		String line = new String();
		int length = 0;
		if (cc == null)
			return line;
		length = cc.getAgents().size();

		int j = 1;
		if (cc.isEmpty())
			return line;

		List<CAgent> sortedAgents = cc.getAgentsSortedByIdInRule();

		for (CAgent agent : sortedAgents) {
			line = line + agent.getName();
			line = line + "(";

			List<String> sitesList = new ArrayList<String>();

			for (CSite site : agent.getSites()) {
				String siteStr = new String(site.getName());
				// line = line + site.getName();
				if ((site.getInternalState() != null)
						&& (site.getInternalState().getNameId() >= 0)) {
					siteStr = siteStr + "~" + site.getInternalState().getName();
					// line = line + "~" + site.getInternalState().getName();
				}
				switch (site.getLinkState().getStatusLink()) {
				case BOUND: {
					if (site.getLinkState().getStatusLinkRank() == CLinkRank.SEMI_LINK) {
						siteStr = siteStr + "!_";
						// line = line + "!_";
					} else if (site.getAgentLink().getIdInRuleHandside() < ((CSite) site
							.getLinkState().getConnectedSite()).getAgentLink()
							.getIdInRuleHandside()) {
						site.getLinkState().getConnectedSite().getLinkState()
								.setLinkStateID(index[0]);
						siteStr = siteStr + "!" + index[0];
						index[0]++;
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

			line = line
					+ getSitesLine(sortSitesStr(sitesList, isOcamlStyleObsName));
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

	private static final String getSitesLine(List<String> list) {
		String line = new String("");
		if (list.size() == 0)
			return line;
		for (int i = 0; i < list.size() - 1; i++) {
			line = line + list.get(i) + ",";
		}
		line = line + list.get(list.size() - 1);

		return line;
	}

	private static final List<String> sortSitesStr(List<String> list,
			boolean isOcamlStyleObsName) {
		if (isOcamlStyleObsName) {
			Collections.sort(list);
		}

		return list;
	}

	public static final List<IConnectedComponent> buildConnectedComponents(
			Collection<CAgent> listOfAgents) {

		if (listOfAgents == null || listOfAgents.isEmpty()) {
			return null;
		}

		List<CAgent> agents = new ArrayList<CAgent>();
		agents.addAll(listOfAgents);
		List<IConnectedComponent> result = new ArrayList<IConnectedComponent>();

		int index = 1;
		for (CAgent agent : agents)
			agent.setIdInRuleSide(index++);

		while (!agents.isEmpty()) {

			List<CAgent> connectedAgents = new ArrayList<CAgent>();

			findConnectedComponent(agents.get(0), agents, connectedAgents);

			// It needs recursive tree search of connected component
			result.add(new CConnectedComponent(connectedAgents));
		}

		return result;
	}

	private static final void findConnectedComponent(CAgent rootAgent,
			List<CAgent> hsRulesList, List<CAgent> agentsList) {
		agentsList.add(rootAgent);
		rootAgent.setIdInConnectedComponent(agentsList.size() - 1);
		removeAgent(hsRulesList, rootAgent);
		// hsRulesList.remove(rootAgent);
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

	private static final boolean isAgentInList(List<CAgent> list, CAgent agent) {
		for (CAgent lagent : list) {
			if (lagent == agent) {
				return true;
			}
		}
		return false;
	}

	private static final CAgent findLink(List<CAgent> agents, int linkIndex) {
		for (CAgent tmp : agents) {
			for (CSite s : tmp.getSites()) {
				if (s.getLinkIndex() == linkIndex) {
					return tmp;
				}
			}
		}
		return null;
	}

	private static final void removeAgent(List<CAgent> agents, CAgent agent) {
		int i = 0;
		for (i = 0; i < agents.size(); i++) {
			if (agents.get(i) == agent)
				break;
		}
		agents.remove(i);
	}

//	public static final CRule buildRule(List<CAgent> left, List<CAgent> right,
//			String name, ConstraintData activity, int ruleID, boolean isStorify) {
//		return new CRule(buildConnectedComponents(left),
//				buildConnectedComponents(right), name, activity, ruleID,
//				isStorify);
//	}
	
	public static final CRule buildRule(List<CAgent> left, List<CAgent> right,
			String name, double activity, int ruleID, boolean isStorify) {
		return new CRule(buildConnectedComponents(left),
				buildConnectedComponents(right), name, activity, ruleID,
				isStorify);
	}
			
	public final static String[] changeArguments(String[] args) {
		String[] argsNew = new String[args.length];
		int i = 0;
		for (String st : args)
			if (st.startsWith("-"))
				argsNew[i++] = st.substring(0, 2)
						+ st.substring(2).replaceAll("-", "_");
			else
				argsNew[i++] = st;
		return argsNew;
	}

	public final static void addToAgentList(List<CAgent> list, CAgent agent) {

		// if (list.contains(agent)) {
		if (agent.includedInCollection(list)) {
			return;
		}
		list.add(agent);
	}

	public final static void doNegativeUpdate(List<CInjection> injectionsList) {
		for (CInjection injection : injectionsList) {
			if (injection != CInjection.EMPTY_INJECTION) {
				for (CSite site : injection.getChangedSites()) {
					site.getAgentLink().getDefaultSite()
							.clearIncomingInjections(injection);
					site.getAgentLink().getDefaultSite().clearLiftList();
					site.clearIncomingInjections(injection);
					site.clearLiftList();
				}
				if (injection.getChangedSites().size() != 0) {
					for (CSite site : injection.getSiteList()) {
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
	
	public final static void doNegativeUpdateForContactMap(List<CInjection> injectionsList, CRule rule) {
		for (CInjection injection : injectionsList) {
			if (injection != CInjection.EMPTY_INJECTION) {
				for (CSite site : injection.getChangedSites()) {
					site.getAgentLink().getDefaultSite()
							.clearIncomingInjections(injection);
					site.getAgentLink().getDefaultSite().clearLiftList();
					site.clearIncomingInjections(injection);
					site.clearLiftList();
				}
				if (injection.getChangedSites().size() != 0) {
					for (CSite site : injection.getSiteList()) {
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

	public final static List<CAgent> doNegativeUpdateForDeletedAgents(
			CRule rule, List<CInjection> injectionsList) {
		List<CAgent> freeAgents = new ArrayList<CAgent>();
		for (CInjection injection : injectionsList) {
			for (CSite checkedSite : rule.getSitesConnectedWithDeleted()) {
				if (!injection.checkSiteExistanceAmongChangedSites(checkedSite)) {

					CAgent checkedAgent = checkedSite.getAgentLink();
					addToAgentList(freeAgents, checkedAgent);
					for (CLiftElement lift : checkedAgent.getDefaultSite()
							.getLift()) {
						lift.getConnectedComponent().removeInjection(
								lift.getInjection());
					}
					checkedAgent.getDefaultSite().clearLiftList();
					for (CLiftElement lift : checkedSite.getLift()) {

						for (CSite site : lift.getInjection().getSiteList()) {
							if (site != checkedSite)
								site.removeInjectionFromLift(lift
										.getInjection());
						}

						lift.getConnectedComponent().removeInjection(
								lift.getInjection());
					}
					checkedSite.clearLiftList();
				}
			}
		}
		for (CSite checkedSite : rule.getSitesConnectedWithBroken()) {
			CAgent checkedAgent = checkedSite.getAgentLink();
			addToAgentList(freeAgents, checkedAgent);
		}
		return freeAgents;
	}

	public final static String perturbationParametersToString(
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

	public final static void positiveUpdate(List<CRule> rulesList,
			List<IObservablesConnectedComponent> list, CRule rule) {
		for (CRule rules : rulesList) {
			// if(rules!=rule)
			for (IConnectedComponent cc : rules.getLeftHandSide()) {
				cc.doPositiveUpdate(rule.getRightHandSide());
			}
		}
		for (IObservablesConnectedComponent oCC : list) {
			if (oCC.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX)
				oCC.doPositiveUpdate(rule.getRightHandSide());
		}
	}

	public final static void positiveUpdateForContactMap(List<CRule> rulesList,
			CRule rule, List<CRule> invokedRulesList) {
		for (CRule rules : rulesList) {
		//	if (rule != rules)
			int g=0;
				for (IConnectedComponent cc : rules.getLeftHandSide()) {
					cc.doPositiveUpdate(rule.getRightHandSide());
				}
			
			if (rules.canBeApplied()
					&& !rules.includedInCollection(invokedRulesList)) {
				invokedRulesList.add(rules);
			}
		}
	}

}