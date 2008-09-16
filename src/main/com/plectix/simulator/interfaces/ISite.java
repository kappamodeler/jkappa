package com.plectix.simulator.interfaces;

import java.util.List;

public interface ISite {

	//TODO specify details
	public List<IState> getSatesList();
	
	public IInternalState getInternalState();
	
	public ILinkState getLinkState();
	
	public boolean isChanged();
}
