package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.util.Converter;

public class SolutionUtils {
	public static final IConnectedComponent getConnectedComponent(IAgent agent) {
		List<IAgent> agentList = new ArrayList<IAgent>();
		agentList.add(agent);
		agentList = getAdjacentAgents(agent, agentList);
		int index = 0;
		for (IAgent agentIn : agentList) {
			agentIn.setIdInRuleSide(index);
			agentIn.setIdInConnectedComponent(index++);
		}

		return new CConnectedComponent(agentList);
	}

	private static final List<IAgent> getAdjacentAgents(IAgent agent, List<IAgent> agentList2) {
		List<IAgent> agentList = agentList2;
		List<IAgent> agentAddList = new ArrayList<IAgent>();

		for (ISite site : agent.getSites()) {
			ISite siteLink = site.getLinkState().getSite();
			if ((site.getLinkState().getSite() != null)
					&& (!SolutionUtils.agentListContains(agentList, siteLink.getAgentLink()))) {
				agentAddList.add(siteLink.getAgentLink());
				agentList.add(siteLink.getAgentLink());

			}
		}

		for (IAgent agentFromList : agentAddList)
			agentList = getAdjacentAgents(agentFromList, agentList);

		return agentList;
	}
	
	public static final boolean agentListContains(List<IAgent> agentList, IAgent agent) {
		for (IAgent agents : agentList)
			if (agent.getId() == agents.getId())
				return true;
		return false;
	}
}
