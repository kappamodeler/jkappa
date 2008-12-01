package com.plectix.simulator.interfaces;

public interface IAction {

//	public byte getAction();

	public IConnectedComponent getLeftCComponent();

	public void doAction(IInjection injection, INetworkNotation netNotation);

	public ISite getSiteFrom();

	public IAgent getFromAgent();

	public IAgent getToAgent();

	public ISite getSiteTo();

	public int getTypeId();
}
