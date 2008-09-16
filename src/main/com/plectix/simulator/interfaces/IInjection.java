package com.plectix.simulator.interfaces;

import java.util.List;

public interface IInjection {
	
	//TODO specify details

	public List<IAgent> getAgents();//returns list of Agents of injection.
	// maximum number of agent = 2 (it depends of number of 
	//conected components of some rule);
	
	public void setAgents(List<IAgent> agents);//returns list of Agents of injection.

}
