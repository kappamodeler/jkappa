package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.KappaSystem;

public class SolutionUtils {
	public static List<IAgent> cloneAgentsList(List<IAgent> agentList, KappaSystem system) {
		List<IAgent> newAgentsList = new ArrayList<IAgent>();
		for (IAgent agent : agentList) {
			IAgent newAgent = new CAgent(agent.getNameId(), system.generateNextAgentId());
			for (ISite site : agent.getSites()) {
				CSite newSite = new CSite(site.getNameId(), newAgent);
				newSite.setLinkIndex(site.getLinkIndex());
				newSite.setInternalState(new CInternalState(site
						.getInternalState().getNameId()));
				// newSite.getInternalState().setNameId(
				// site.getInternalState().getNameId());
				newAgent.addSite(newSite);
			}
			newAgentsList.add(newAgent);
		}
		for (int i = 0; i < newAgentsList.size(); i++) {
			for (ISite siteNew : newAgentsList.get(i).getSites()) {
				ILinkState lsNew = siteNew.getLinkState();
				ILinkState lsOld = agentList.get(i)
						.getSite(siteNew.getNameId()).getLinkState();
				lsNew.setStatusLink(lsOld.getStatusLink());
				if (lsOld.getSite() != null) {
					CSite siteOldLink = (CSite) lsOld.getSite();
					int j = 0;
					for (j = 0; j < agentList.size(); j++) {
						if (agentList.get(j) == siteOldLink.getAgentLink())
							break;
					}
					int index = j;
					lsNew.setSite(newAgentsList.get(index).getSite(
							siteOldLink.getNameId()));
				}

			}

		}

		return newAgentsList;
	}
	
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
