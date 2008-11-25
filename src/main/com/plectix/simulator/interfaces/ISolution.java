package com.plectix.simulator.interfaces;

import java.util.*;

public interface ISolution {

	public Map<Long, IAgent> getAgents();
	
	public List<IAgent> apply(IRule rule, IInjection injection);
	//returns list of root agents after applying rule - new CCs
	
	public IConnectedComponent getConnectedComponent (IAgent agent);
	
	public List<IConnectedComponent> split();
	
	public void add(ISolution solution);
	
	public void addAgents(List<IAgent> agents);
	
}
