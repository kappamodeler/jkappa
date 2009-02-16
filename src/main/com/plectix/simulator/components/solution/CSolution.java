package com.plectix.simulator.components.solution;

import java.io.Serializable;
import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.KappaSystem;

@SuppressWarnings("serial")
public final class CSolution implements ISolution, Serializable {
	private final HashMap<Long, IAgent> agentMap;
	private final List<SolutionLines> solutionLines;

	public CSolution() {
		agentMap = new HashMap<Long, IAgent>();
		solutionLines = new ArrayList<SolutionLines>();
	}

	public final void removeAgent(IAgent agent) {
		if (agent == null) {
			return;
		}
		agentMap.remove(agent.getHash());
	}

	public final void addAgent(IAgent agent) {
		if (agent != null) {
			long key = agent.getHash();
			agentMap.put(key, agent);
		}
	}

	public final void addAgents(List<IAgent> agents) {
		if (agents == null || agents.isEmpty())
			return;
		for (IAgent agentAdd : agents) {
			addAgent(agentAdd);
		}
	}

	public final Collection<IAgent> getAgents() {
		return Collections.unmodifiableCollection(agentMap.values());
	}

	public final void clearAgents() {
		agentMap.clear();
	}
	
	public final IConnectedComponent getConnectedComponent(IAgent agent) {
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

	private final List<IAgent> getAdjacentAgents(IAgent agent, List<IAgent> agentList2) {
		List<IAgent> agentList = agentList2;
		List<IAgent> agentAddList = new ArrayList<IAgent>();

		for (ISite site : agent.getSites()) {
			ISite siteLink = site.getLinkState().getSite();
			if ((site.getLinkState().getSite() != null)
					&& (!isAgentInList(siteLink.getAgentLink(), agentList))) {
				agentAddList.add(siteLink.getAgentLink());
				agentList.add(siteLink.getAgentLink());

			}
		}

		for (IAgent agentFromList : agentAddList)
			agentList = getAdjacentAgents(agentFromList, agentList);

		return agentList;
	}

	private final boolean isAgentInList(IAgent agent, List<IAgent> agentList) {
		for (IAgent agents : agentList)
			if (agent.getId() == agents.getId())
				return true;
		return false;
	}

	
	public final List<IConnectedComponent> split() {

		BitSet bitset = new BitSet(1024);

		List<IConnectedComponent> ccList = new ArrayList<IConnectedComponent>();

		for (IAgent agent : agentMap.values()) {
			int index = (int) agent.getId();
			if (!bitset.get(index)) {
				IConnectedComponent cc = getConnectedComponent(agent);
				for (IAgent agentCC : cc.getAgents()) {
					bitset.set((int) agentCC.getId(), true);
				}
				ccList.add(cc);
			}
		}

		return ccList;
	}

	public final List<SolutionLines> getSolutionLines() {
		return Collections.unmodifiableList(solutionLines);
	}

	//TODO REMOVE
	public final void checkSolutionLinesAndAdd(String line, long count) {
		line = line.replaceAll("[ 	]", "");
		while (line.indexOf("(") == 0) {
			line = line.substring(1);
			line = line.substring(0, line.length() - 1);
		}
		for (SolutionLines sl : solutionLines) {
			if (sl.getLine().equals(line)) {
				sl.setCount(sl.getCount() + count);
				return;
			}
		}
		solutionLines.add(new SolutionLines(line, count));

	}
	
	public List<IAgent> cloneAgentsList(List<IAgent> agentList, KappaSystem system) {
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

		return Collections.unmodifiableList(newAgentsList);
	}

	public final void clearSolutionLines() {
		solutionLines.clear();
	}

}
