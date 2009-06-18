package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.*;

public class SolutionUtils {
	public static final IConnectedComponent getConnectedComponent(CAgent agent) {
		Map<Long, CAgent> adjacentAgents = new HashMap<Long, CAgent>();
		adjacentAgents.put(agent.getId(), agent);
		adjacentAgents = getAdjacentAgents(agent, adjacentAgents);
		int index = 0;
		for (CAgent agentIn : adjacentAgents.values()) {
			agentIn.setIdInRuleSide(index);
			agentIn.setIdInConnectedComponent(index++);
		}

		return new CConnectedComponent(adjacentAgents.values());
	}

	private static final Map<Long, CAgent> getAdjacentAgents(CAgent agent, Map<Long, CAgent> agentList2) {
		Map<Long, CAgent> allAgents = agentList2;
		List<CAgent> agentAddList = new ArrayList<CAgent>();

		for (CSite site : agent.getSites()) {
			CSite siteLink = site.getLinkState().getConnectedSite();
			if ((site.getLinkState().getConnectedSite() != null)
					&& (!allAgents.keySet().contains(siteLink.getAgentLink().getId()))) {
				CAgent agentLink = siteLink.getAgentLink();
				agentAddList.add(agentLink);
				allAgents.put(agentLink.getId(), agentLink);
			}
		}
		
		for (CAgent agentFromList : agentAddList)
			allAgents = getAdjacentAgents(agentFromList, allAgents);

		return allAgents;
	}
}
