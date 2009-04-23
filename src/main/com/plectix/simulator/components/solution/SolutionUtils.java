package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.*;

public class SolutionUtils {
	public static final IConnectedComponent getConnectedComponent(CAgent agent) {
		Map<Long, CAgent> agentList = new HashMap<Long, CAgent>();
		agentList.put(agent.getId(), agent);
		agentList = getAdjacentAgents(agent, agentList);
		int index = 0;
		for (CAgent agentIn : agentList.values()) {
			agentIn.setIdInRuleSide(index);
			agentIn.setIdInConnectedComponent(index++);
		}

		return new CConnectedComponent(agentList.values());
	}

	private static final Map<Long, CAgent> getAdjacentAgents(CAgent agent, Map<Long, CAgent> agentList2) {
		Map<Long, CAgent> agentList = agentList2;
		List<CAgent> agentAddList = new ArrayList<CAgent>();

		for (CSite site : agent.getSites()) {
			CSite siteLink = site.getLinkState().getConnectedSite();
			if ((site.getLinkState().getConnectedSite() != null)
					&& (!agentList.keySet().contains(siteLink.getAgentLink().getId()))) {
				CAgent agentLink = siteLink.getAgentLink();
				agentAddList.add(agentLink);
				agentList.put(agentLink.getId(), agentLink);
			}
		}

		for (CAgent agentFromList : agentAddList)
			agentList = getAdjacentAgents(agentFromList, agentList);

		return agentList;
	}
}
