package com.plectix.simulator.interfaces;

import java.util.List;
import java.util.Map;

public interface ISolution {

	public Map<String, IAgent> getAgents();
	
	public Map<ISite, IAgent> getLinks();
	
	public boolean isFullyInstatiated();
	
	public List<IAgent> apply(IRule rule, IInjection injection);
	//returns list of root agents after applying rule - new CCs
	
	public IConnectedComponent getConnectedComponent (IAgent agent);
	
	public List<IConnectedComponent> split();
	
	public boolean satisfy(IConstraint constraint, IInjection injection);
	
	public void add(ISolution solution);
	
	public void multiply(int N);
}
