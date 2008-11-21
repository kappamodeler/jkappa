package com.plectix.simulator.interfaces;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CConnectedComponent;

public interface ISolution {

	public Map<Long, CAgent> getAgents();
	
	public Map<ISite, IAgent> getLinks();
	
	public boolean isFullyInstatiated();
	
	public List<IAgent> apply(IRule rule, IInjection injection);
	//returns list of root agents after applying rule - new CCs
	
	public CConnectedComponent getConnectedComponent (CAgent agent);
	
	public List<CConnectedComponent> split();
	
	public boolean satisfy(IConstraint constraint, IInjection injection);
	
	public void add(ISolution solution);
	
	public void addAgents(List<CAgent> agents);
	
	public void multiply(int N);
}
