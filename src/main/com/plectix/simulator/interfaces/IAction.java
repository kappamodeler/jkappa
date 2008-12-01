package com.plectix.simulator.interfaces;

import java.util.Collection;

public interface IAction {

	public IConnectedComponent getLeftCComponent();

	public void doAction(IInjection injection, INetworkNotation netNotation);

	public ISite getSiteFrom();

	public IAgent getAgentFrom();

	public IAgent getAgentTo();

	public ISite getSiteTo();

	public int getTypeId();

	public Collection<IAction> createAtomicActions();
}
