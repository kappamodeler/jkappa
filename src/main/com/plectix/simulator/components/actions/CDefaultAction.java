package com.plectix.simulator.components.actions;

import com.plectix.simulator.components.CNetworkNotation;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.INetworkNotation;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.simulator.SimulationData;

public class CDefaultAction extends CAction {
	private final IAgent myToAgent;
	
	public CDefaultAction(CRule rule, IAgent fromAgent, IAgent toAgent,
			IConnectedComponent ccL, IConnectedComponent ccR) {
		super(rule, fromAgent, toAgent, ccL, ccR);
		myToAgent = toAgent;
		setType(CActionType.NONE);
	}
	
	public final void doAction(IInjection injection, INetworkNotation netNotation, SimulationData simulationData) {
		int agentIdInCC = getAgentIdInCCBySideId(myToAgent);
		IAgent agentFromInSolution = getLeftCComponent()
				.getAgentByIdFromSolution(agentIdInCC, injection);
		getRightCComponent().addAgentFromSolutionForRHS(agentFromInSolution);
	}
	
	public final void addRuleSitesToNetworkNotation(boolean existInRule,
			INetworkNotation netNotation, ISite site) {
		if (netNotation != null) {
			byte agentMode = CNetworkNotation.MODE_NONE;
			byte linkStateMode = CNetworkNotation.MODE_NONE;
			byte internalStateMode = CNetworkNotation.MODE_NONE;
			netNotation.addToAgentsFromRules(site, agentMode,
					internalStateMode, linkStateMode);
		}
	}
	
	protected final void addToNetworkNotation(int index,
			INetworkNotation netNotation, ISite site) {
	}
}
