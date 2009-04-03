package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.*;

public class SolutionUtils {
	public static final IConnectedComponent getConnectedComponent(CAgent agent) {
		List<CAgent> agentList = new ArrayList<CAgent>();
		agentList.add(agent);
		agentList = getAdjacentAgents(agent, agentList);
		int index = 0;
		for (CAgent agentIn : agentList) {
			agentIn.setIdInRuleSide(index);
			agentIn.setIdInConnectedComponent(index++);
		}

		return new CConnectedComponent(agentList);
	}

	private static final List<CAgent> getAdjacentAgents(CAgent agent, List<CAgent> agentList2) {
		List<CAgent> agentList = agentList2;
		List<CAgent> agentAddList = new ArrayList<CAgent>();

		for (CSite site : agent.getSites()) {
			CSite siteLink = site.getLinkState().getConnectedSite();
			if ((site.getLinkState().getConnectedSite() != null)
					&& (!SolutionUtils.agentListContains(agentList, siteLink.getAgentLink()))) {
				agentAddList.add(siteLink.getAgentLink());
				agentList.add(siteLink.getAgentLink());

			}
		}

		for (CAgent agentFromList : agentAddList)
			agentList = getAdjacentAgents(agentFromList, agentList);

		return agentList;
	}
	
	public static final boolean agentListContains(List<CAgent> agentList, CAgent agent) {
		for (CAgent agents : agentList)
			if (agent.getId() == agents.getId())
				return true;
		return false;
	}
}
