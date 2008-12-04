package com.plectix.simulator.components;

import java.util.*;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.interfaces.*;

public final class CSolution implements ISolution {
	private final HashMap<Long, IAgent> agentMap;
	private final List<SolutionLines> solutionLines;

	public CSolution() {
		agentMap = new HashMap<Long, IAgent>();
		solutionLines = new ArrayList<SolutionLines>();
	}

	private final void depthSearch(IAgent agent2, List<IAgent> agentsList) {
		for (ISite site : agent2.getSites()) {
			ISite linkSite = site.getLinkState().getSite();
			if (linkSite != null) {
				IAgent agent = linkSite.getAgentLink();
				if (!(agentsList.contains(agent))) {
					agentsList.add(agent);
					depthSearch(agent, agentsList);
				}
			}
		}
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

	public final List<IAgent> getConnectedAgents(IAgent inAgent) {
		List<IAgent> agentsList = new ArrayList<IAgent>();
		agentsList.add(inAgent);
		depthSearch(inAgent, agentsList);
		return agentsList;
	}

	public final Map<Long, IAgent> getAgents() {
		return Collections.unmodifiableMap(agentMap);
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

	public final void clearSolutionLines() {
		solutionLines.clear();
	}

}
