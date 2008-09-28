package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IConstraint;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.interfaces.ISolution;

public class CSolution implements ISolution {
	private HashMap<Integer, List<CAgent>> agentMap;

	public CSolution() {
		agentMap = new HashMap<Integer, List<CAgent>>();
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
	
	public final void addAgent(CAgent agent){
		List<CAgent> list = agentMap.get(agent.getNameId());
		list.add(agent);
	}
	
	public final void removeAgent(CAgent agent){
		List<CAgent> list = agentMap.get(agent.getNameId());
		list.remove(agent);
	}
	
	public final List<CAgent> getConnectedAgents(CAgent inAgent){
		List<CAgent> agentsList = new ArrayList<CAgent>();
		agentsList.add(inAgent);
		depthSearch(inAgent, agentsList);
		return agentsList;
	}
	
	
	public final HashMap<Integer, List<CAgent>> getAgentMap() {
		return agentMap;
	}

	public final void addAgents(List<CAgent> agents) {
		if (agents == null || agents.isEmpty())
			return;
		for (CAgent agentAdd : agents) {
			Integer agentNameId = agentAdd.getNameId();
			List<CAgent> list = agentMap.get(agentNameId);
			if (list == null) {
				list = new ArrayList<CAgent>();
				agentMap.put(agentNameId, list);
			}
			list.add(agentAdd);
		}
	}

	@Override
	public final void add(ISolution solution) {
		// TODO Auto-generated method stub

	}

	@Override
	public final List<IAgent> apply(IRule rule, IInjection injection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final HashMap<Integer, List<CAgent>> getAgents() {
		return agentMap;
	}

	@Override
	public final IConnectedComponent getConnectedComponent(IAgent agent) {
			return null;
	}

	@Override
	public final Map<ISite, IAgent> getLinks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final boolean isFullyInstatiated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public final void multiply(int N) {
		// TODO Auto-generated method stub

	}

	@Override
	public final boolean satisfy(IConstraint constraint, IInjection injection) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public final List<IConnectedComponent> split() {
		// TODO Auto-generated method stub
		return null;
	}

}
