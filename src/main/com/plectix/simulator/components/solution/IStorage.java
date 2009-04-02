package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.*;

public interface IStorage {

	public void addConnectedComponent(IConnectedComponent component);
	
//	public void removeConnectedComponent(IConnectedComponent component);
	
//	public Collection<CAgent> getAgents();

	public List<IConnectedComponent> split();

	public IConnectedComponent extractComponent(CInjection inj);
	
	public void clear();

	void applyRule(RuleApplicationPool pool);
}
