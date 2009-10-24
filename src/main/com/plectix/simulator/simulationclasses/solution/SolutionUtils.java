package com.plectix.simulator.simulationclasses.solution;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.ConnectedComponent;
import com.plectix.simulator.staticanalysis.Site;

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
	public static final ConnectedComponentInterface getConnectedComponent(Agent agent) {
		if (agent != null) {
			Map<Long, Agent> adjacentAgents = new LinkedHashMap<Long, Agent>();
			adjacentAgents.put(agent.getId(), agent);
			adjacentAgents = getAdjacentAgents(agent, adjacentAgents);
			int index = 0;
			for (Agent agentIn : adjacentAgents.values()) {
				// TODO notice the line commented below
				// agentIn.setIdInRuleSide(index);
				agentIn.setIdInConnectedComponent(index++);
			}

			return new ConnectedComponent(adjacentAgents.values());
		} else {
			return null;
		}
	}

	private static final Map<Long, Agent> getAdjacentAgents(Agent agent, Map<Long, Agent> agents) {
		Map<Long, Agent> allAgents = agents;
		Set<Agent> agentAddList = new LinkedHashSet<Agent>();

		for (Site site : agent.getSites()) {
			Site siteLink = site.getLinkState().getConnectedSite();
			if ((siteLink != null)
					&& (!allAgents.keySet().contains(siteLink.getParentAgent().getId()))) {
				Agent agentLink = siteLink.getParentAgent();
				agentAddList.add(agentLink);
				allAgents.put(agentLink.getId(), agentLink);
			}
		}
		
		for (Agent agentFromList : agentAddList)
			allAgents = getAdjacentAgents(agentFromList, allAgents);

		return allAgents;
	}
}
