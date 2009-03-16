package com.plectix.simulator.interfaces;

import java.util.Collection;

import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.solution.StandardRuleApplicationPool;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;

public interface IAction {

	public IConnectedComponent getLeftCComponent();

	public void doAction(RuleApplicationPool pool, IInjection injection, 
			INetworkNotation netNotation,  SimulationData simulationData);

	public ISite getSiteFrom();

	public IAgent getAgentFrom();

	public IAgent getAgentTo();

	public ISite getSiteTo();

	public int getTypeId();

	public Collection<IAction> createAtomicActions();
}
