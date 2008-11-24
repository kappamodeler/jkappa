package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IConstraint;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.interfaces.ISolution;

public class CSolution implements ISolution {
	private HashMap<Long, CAgent> agentMap;

	private List<SolutionLines> solutionLines;

	public CSolution() {
		agentMap = new HashMap<Long, CAgent>();
		solutionLines = new ArrayList<SolutionLines>();
	}

	private final void depthSearch(CAgent rootAgent, List<CAgent> agentsList) {
		for (CSite site : rootAgent.getSites()) {
			CSite linkSite = (CSite) site.getLinkState().getSite();
			if (linkSite != null) {
				CAgent agent = linkSite.getAgentLink();
				if (!(agentsList.contains(agent))) {
					agentsList.add(agent);
					depthSearch(agent, agentsList);
				}
			}
		}
	}

	// TODO check
	public final void removeAgent(CAgent agent) {
		if (agent == null) {
			return;
		}

		agentMap.remove(agent.getHash());
	}

	public final void addAgent(CAgent agent) {
		if (agent != null) {
			long key = agent.getHash();
			agentMap.put(key, agent);
		}
	}

	public final void addAgents(List<CAgent> agents) {
		if (agents == null || agents.isEmpty())
			return;
		for (CAgent agentAdd : agents) {
			addAgent(agentAdd);
		}
	}

	public final List<CAgent> getConnectedAgents(CAgent inAgent) {
		List<CAgent> agentsList = new ArrayList<CAgent>();
		agentsList.add(inAgent);
		depthSearch(inAgent, agentsList);
		return agentsList;
	}

	public final Map<Long, CAgent> getAgents() {
		return Collections.unmodifiableMap(agentMap);
	}

	public void clearAgents() {
		agentMap.clear();
	}

	
	public final void add(ISolution solution) {
		// TODO Auto-generated method stub

	}

	
	public final List<IAgent> apply(IRule rule, IInjection injection) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public final CConnectedComponent getConnectedComponent(CAgent agent) {
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

	private final List<CAgent> getAdjacentAgents(CAgent agent, List<CAgent> list) {
		List<CAgent> agentList = list;
		List<CAgent> agentAddList = new ArrayList<CAgent>();

		for (CSite site : agent.getSites()) {
			CSite siteLink = (CSite) site.getLinkState().getSite();
			if ((site.getLinkState().getSite() != null)
					&& (!isAgentInList(siteLink.getAgentLink(), agentList))) {
				agentAddList.add(siteLink.getAgentLink());
				agentList.add(siteLink.getAgentLink());

			}
		}

		for (CAgent agentFromList : agentAddList)
			agentList = getAdjacentAgents(agentFromList, agentList);

		return agentList;
	}

	private final boolean isAgentInList(CAgent agent, List<CAgent> agentList) {
		for (CAgent agents : agentList)
			if (agent.getId() == agents.getId())
				return true;
		return false;
	}

	
	public final Map<ISite, IAgent> getLinks() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public final boolean isFullyInstatiated() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public final void multiply(int N) {
		// TODO Auto-generated method stub

	}

	
	public final boolean satisfy(IConstraint constraint, IInjection injection) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public final List<CConnectedComponent> split() {
		List<Boolean> indexList = new ArrayList<Boolean>();

		for (int i = 0; i < SimulationMain.getSimulationManager()
				.getAgentIdGenerator(); i++) {
			indexList.add(false);
		}

		List<CConnectedComponent> ccList = new ArrayList<CConnectedComponent>();

		for (CAgent agent : agentMap.values()) {
			int index = (int) agent.getId();
			if (!indexList.get(index)) {
				CConnectedComponent cc = getConnectedComponent(agent);
				for (CAgent agentCC : cc.getAgents()) {
					indexList.set((int) agentCC.getId(), true);
				}
				ccList.add(cc);
			}
		}

		return ccList;
	}

	public List<SolutionLines> getSolutionLines() {
		return solutionLines;
	}

	public void checkSolutionLinesAndAdd(String line, long count) {
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

}
