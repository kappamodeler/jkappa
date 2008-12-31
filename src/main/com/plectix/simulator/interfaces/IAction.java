package com.plectix.simulator.interfaces;

import java.util.Collection;

import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;

public interface IAction {

	public IConnectedComponent getLeftCComponent();

	public void doAction(IInjection injection, INetworkNotation netNotation,  SimulationData simulationData);

	public ISite getSiteFrom();

	public IAgent getAgentFrom();

	public IAgent getAgentTo();

	public ISite getSiteTo();

	public int getTypeId();

	public Collection<IAction> createAtomicActions();
}
