package com.plectix.simulator.interfaces;

import java.util.*;

import com.plectix.simulator.simulator.Simulator;

public interface ISolution {

	public Map<Long, IAgent> getAgents();
	
	public IConnectedComponent getConnectedComponent (IAgent agent);
	
	public List<IConnectedComponent> split();
	
	public void addAgents(List<IAgent> agents);

	public void removeAgent(IAgent agent);

	public void addAgent(IAgent agent);

	public void clearAgents();

	public void clearSolutionLines();
	
	public List<IAgent> cloneAgentsList(List<IAgent> agentList, Simulator simulator);
	
}
