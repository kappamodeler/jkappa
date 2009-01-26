package com.plectix.simulator.simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CInjection;
import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CLinkState;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.ConstraintData;
import com.plectix.simulator.components.ObservablesConnectedComponent;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.ILiftElement;
import com.plectix.simulator.interfaces.IObservablesConnectedComponent;
import com.plectix.simulator.interfaces.IPerturbationExpression;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISite;

public class SimulationUtils {

	public final static String getCommandLineString(String[] args) {
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
			if (cc == CRule.EMPTY_LHS_CC)
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
		if (cc == CRule.EMPTY_LHS_CC)
			return line;

		List<IAgent> sortedAgents = cc.getAgentsSortedByIdInRule();

		for (IAgent agent : sortedAgents) {
			line = line + agent.getName();
			line = line + "(";

			List<String> sitesList = new ArrayList<String>();

			for (ISite site : agent.getSites()) {
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
					} else if (site.getAgentLink().getIdInRuleSide() < ((ISite) site
							.getLinkState().getSite()).getAgentLink()
							.getIdInRuleSide()) {
						((ISite) site.getLinkState().getSite()).getLinkState()
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
			List<IAgent> agents) {

		if (agents == null || agents.isEmpty()) {
			return null;
		}

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

	private static final void findConnectedComponent(IAgent rootAgent,
			List<IAgent> hsRulesList, List<IAgent> agentsList) {
		agentsList.add(rootAgent);
		rootAgent.setIdInConnectedComponent(agentsList.size() - 1);
		removeAgent(hsRulesList, rootAgent);
		// hsRulesList.remove(rootAgent);
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

	private static final boolean isAgentInList(List<IAgent> list, IAgent agent) {
		for (IAgent lagent : list) {
			if (lagent == agent) {
				return true;
			}
		}
		return false;
	}

	private static final IAgent findLink(List<IAgent> agents, int linkIndex) {
		for (IAgent tmp : agents) {
			for (ISite s : tmp.getSites()) {
				if (s.getLinkIndex() == linkIndex) {
					return tmp;
				}
			}
		}
		return null;
	}

	private static final void removeAgent(List<IAgent> agents, IAgent agent) {
		int i = 0;
		for (i = 0; i < agents.size(); i++) {
			if (agents.get(i) == agent)
				break;
		}
		agents.remove(i);
	}

	/**
	 * @deprecated
	 * @param left
	 * @param right
	 * @param name
	 * @param activity
	 * @param ruleID
	 * @param isStorify
	 * @return
	 */
	public static final IRule buildRule(List<IAgent> left, List<IAgent> right,
			String name, ConstraintData activity, int ruleID, boolean isStorify) {
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

	public final static void addToAgentList(List<IAgent> list, IAgent agent) {

		// if (list.contains(agent)) {
		if (agent.includedInCollection(list)) {
			return;
		}
		list.add(agent);
	}

	public final static void doNegativeUpdate(List<IInjection> injectionsList) {
		for (IInjection injection : injectionsList) {
			if (injection != CInjection.EMPTY_INJECTION) {
				for (ISite site : injection.getChangedSites()) {
					site.getAgentLink().getEmptySite()
							.removeInjectionsFromCCToSite(injection);
					site.getAgentLink().getEmptySite().clearLiftList();
					site.removeInjectionsFromCCToSite(injection);
					site.clearLiftList();
				}
				if (injection.getChangedSites().size() != 0) {
					for (ISite site : injection.getSiteList()) {
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
	
	public final static void doNegativeUpdateForContactMap(List<IInjection> injectionsList, IRule rule) {
		for (IInjection injection : injectionsList) {
			if (injection != CInjection.EMPTY_INJECTION) {
				for (ISite site : injection.getChangedSites()) {
					site.getAgentLink().getEmptySite()
							.removeInjectionsFromCCToSite(injection);
					site.getAgentLink().getEmptySite().clearLiftList();
					site.removeInjectionsFromCCToSite(injection);
					site.clearLiftList();
				}
				if (injection.getChangedSites().size() != 0) {
					for (ISite site : injection.getSiteList()) {
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

	public final static List<IAgent> doNegativeUpdateForDeletedAgents(
			IRule rule, List<IInjection> injectionsList) {
		List<IAgent> freeAgents = new ArrayList<IAgent>();
		for (IInjection injection : injectionsList) {
			for (ISite checkedSite : rule.getSitesConnectedWithDeleted()) {
				if (!injection.checkSiteExistanceAmongChangedSites(checkedSite)) {

					IAgent checkedAgent = checkedSite.getAgentLink();
					addToAgentList(freeAgents, checkedAgent);
					for (ILiftElement lift : checkedAgent.getEmptySite()
							.getLift()) {
						lift.getConnectedComponent().removeInjection(
								lift.getInjection());
					}
					checkedAgent.getEmptySite().clearLiftList();
					for (ILiftElement lift : checkedSite.getLift()) {

						for (ISite site : lift.getInjection().getSiteList()) {
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
		for (ISite checkedSite : rule.getSitesConnectedWithBroken()) {
			IAgent checkedAgent = checkedSite.getAgentLink();
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

	public final static void positiveUpdate(List<IRule> rulesList,
			List<IObservablesConnectedComponent> list, IRule rule) {
		for (IRule rules : rulesList) {
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

	public final static void positiveUpdateForContactMap(List<IRule> rulesList,
			IRule rule, List<IRule> invokedRulesList) {
		for (IRule rules : rulesList) {
		//	if (rule != rules)
			int g=0;
				for (IConnectedComponent cc : rules.getLeftHandSide()) {
					cc.doPositiveUpdate(rule.getRightHandSide());
				}
			
			if (rules.isInvokedRule()
					&& !rules.includedInCollection(invokedRulesList)) {
				invokedRulesList.add(rules);
			}
		}
	}

}