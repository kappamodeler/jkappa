package com.plectix.simulator.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.ConnectedComponent;
import com.plectix.simulator.staticanalysis.Site;

public final class SpeciesManager {
	/**
	 * This method takes a set of properly connected agents and "regroup" them
	 * so each group forms connected component.    
	 * @param agents collection of somehow connected agents
	 * @return resulting connected components
	 */
	public static final List<ConnectedComponentInterface> formConnectedComponents(
			Collection<Agent> agents) {
		if (agents == null || agents.isEmpty()) {
			return null;
		}

		List<Agent> agentsCopy = new ArrayList<Agent>(agents);
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
					if (!agents.contains(linkedAgent));
						findConnectedComponent(linkedAgent, agentsFromRules,
								agents);
				}
			}
		}
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
}