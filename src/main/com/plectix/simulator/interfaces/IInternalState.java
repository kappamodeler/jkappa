package com.plectix.simulator.interfaces;


public interface IInternalState extends IState{

	public IState getState();
	
	public void setState(IState state);
	
	//TODO specify details
}
