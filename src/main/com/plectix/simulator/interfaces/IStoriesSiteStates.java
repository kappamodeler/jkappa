package com.plectix.simulator.interfaces;

public interface IStoriesSiteStates {

	public void addInformation(int index, IStoriesSiteStates siteStates);

	public IStates getLastState();

	public IStates getCurrentState();

}
