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
	private HashMap<String, List<IAgent>> map;

	public CSolution() {
		map = new HashMap<String, List<IAgent>>();
	}

	public HashMap<String, List<IAgent>> getMap() {
		return map;
	}

	public void addAgents(List<IAgent> agents) {
		if (agents.isEmpty())
			return;
		for (IAgent agentAdd : agents) {
			String agentName = agentAdd.getName();
			List<IAgent> list = map.get(agentName);
			if (list == null) {
				list = new ArrayList<IAgent>();
				map.put(agentName, list);
			}
			list.add(agentAdd);
		}
	}

	@Override
	public void add(ISolution solution) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<IAgent> apply(IRule rule, IInjection injection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, IAgent> getAgents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConnectedComponent getConnectedComponent(IAgent agent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<ISite, IAgent> getLinks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFullyInstatiated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void multiply(int N) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean satisfy(IConstraint constraint, IInjection injection) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<IConnectedComponent> split() {
		// TODO Auto-generated method stub
		return null;
	}

}
