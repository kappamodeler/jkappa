package com.plectix.simulator.interfaces;

import java.util.Collection;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.solution.StandardRuleApplicationPool;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;

public interface IAction {

	public IConnectedComponent getLeftCComponent();

	public void doAction(RuleApplicationPool pool, CInjection injection, 
			INetworkNotation netNotation,  SimulationData simulationData);

	public CSite getSiteFrom();

	public CAgent getAgentFrom();

	public CAgent getAgentTo();

	public CSite getSiteTo();

	public int getTypeId();

	public Collection<IAction> createAtomicActions();
}
