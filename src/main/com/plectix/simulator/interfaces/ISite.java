package com.plectix.simulator.interfaces;

import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLinkState;


public interface ISite {

	public String getName();
	
	public CLinkState getLinkState();
	
	public boolean isChanged();
	
	public IAgent getAgentLink();

	public CInternalState getInternalState();
	
}
