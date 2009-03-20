package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.interfaces.*;

public interface IStorage {

	public void addConnectedComponent(IConnectedComponent component);
	
//	public void removeConnectedComponent(IConnectedComponent component);
	
//	public Collection<IAgent> getAgents();

	public List<IConnectedComponent> split();

	public IConnectedComponent extractComponent(IInjection inj);
	
	public void clear();

	void applyRule(RuleApplicationPool pool);
}
