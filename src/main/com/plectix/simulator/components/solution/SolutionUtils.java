package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.IConnectedComponent;

/**
 * This class contains some util static methods to work with substances from solution 
 */
// TODO all the methods from here to special util class 
public final class SolutionUtils {
	/**
	 * This method takes an agent and finds all agents which somehow connected to it.
	 * I.e. it finds connected component, containing this agent.
	 * <br> NOTE: this method DOES NOT use link indexes! So we CAN use it during the simulation process
	 * @param agent agent to find component for
	 * @return connected component, containing this agent
	 */
	public static final IConnectedComponent getConnectedComponent(CAgent agent) {
		if (agent != null) {
			Map<Long, CAgent> adjacentAgents = new LinkedHashMap<Long, CAgent>();
			adjacentAgents.put(agent.getId(), agent);
			adjacentAgents = getAdjacentAgents(agent, adjacentAgents);
			int index = 0;
			for (CAgent agentIn : adjacentAgents.values()) {
				// TODO notice the line commented below
				// agentIn.setIdInRuleSide(index);
				agentIn.setIdInConnectedComponent(index++);
			}

			return new CConnectedComponent(adjacentAgents.values());
		} else {
			return null;
		}
	}

	private static final Map<Long, CAgent> getAdjacentAgents(CAgent agent, Map<Long, CAgent> agentList2) {
		Map<Long, CAgent> allAgents = agentList2;
		Set<CAgent> agentAddList = new LinkedHashSet<CAgent>();

		for (CSite site : agent.getSites()) {
			CSite siteLink = site.getLinkState().getConnectedSite();
			if ((siteLink != null)
					&& (!allAgents.keySet().contains(siteLink.getParentAgent().getId()))) {
				CAgent agentLink = siteLink.getParentAgent();
				agentAddList.add(agentLink);
				allAgents.put(agentLink.getId(), agentLink);
			}
		}
		
		for (CAgent agentFromList : agentAddList)
			allAgents = getAdjacentAgents(agentFromList, allAgents);

		return allAgents;
	}
}
