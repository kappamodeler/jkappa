package com.plectix.simulator.interfaces;

import com.plectix.simulator.components.stories.CStoriesSiteStates.StateType;

public interface IStoriesSiteStates {

	public void addInformation(StateType index, IStoriesSiteStates siteStates);

	public IStates getBeforeState();

	public IStates getAfterState();
	
	public boolean isEqualsAfterState(IStoriesSiteStates checkSS);

}
