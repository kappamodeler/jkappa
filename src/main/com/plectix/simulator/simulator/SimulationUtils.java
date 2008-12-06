package com.plectix.simulator.simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CLinkState;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISite;

public class SimulationUtils {

	public static final String printPartRule(List<IConnectedComponent> ccList, boolean isOcamlStyleObsName) {
		String line = new String();
		int[] indexLink = new int[] {0};
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

	public static final String printPartRule(IConnectedComponent cc, int[] index,
			boolean isOcamlStyleObsName) {
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
			
					line = line + getSitesLine(sortSitesStr(sitesList, isOcamlStyleObsName));
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

	private static final List<String> sortSitesStr(List<String> list, boolean isOcamlStyleObsName) {
		if (isOcamlStyleObsName) {
			Collections.sort(list);
		}
	
		return list;
	}

	public static final List<IConnectedComponent> buildConnectedComponents(List<IAgent> agents) {
	
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

	private static final void findConnectedComponent(IAgent rootAgent,
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

	private static final boolean isAgentInList(List<IAgent> list, IAgent agent) {
		for (IAgent lagent : list) {
			if (lagent == agent)
				return true;
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

	public static final IRule buildRule(List<IAgent> left, List<IAgent> right,
			String name, double activity, int ruleID, boolean isStorify) {
				return new CRule(buildConnectedComponents(left),
						buildConnectedComponents(right), name, activity, ruleID, isStorify);
			}

}