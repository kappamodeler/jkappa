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
	private HashMap<String, List<CAgent>> agentMap;

	public CSolution() {
		agentMap = new HashMap<String, List<CAgent>>();
	}

	public final HashMap<String, List<CAgent>> getAgentMap() {
		return agentMap;
	}

	public final void addAgents(List<CAgent> agents) {
		if (agents.isEmpty())
			return;
		for (CAgent agentAdd : agents) {
			String agentName = agentAdd.getName();
			List<CAgent> list = agentMap.get(agentName);
			if (list == null) {
				list = new ArrayList<CAgent>();
				agentMap.put(agentName, list);
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
	public final Map<String, IAgent> getAgents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final IConnectedComponent getConnectedComponent(IAgent agent) {
		// TODO Auto-generated method stub
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
