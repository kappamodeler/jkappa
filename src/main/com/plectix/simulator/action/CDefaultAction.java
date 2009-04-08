package com.plectix.simulator.action;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.stories.CNetworkNotation.NetworkNotationMode;
import com.plectix.simulator.components.stories.CStoriesSiteStates.StateType;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.interfaces.INetworkNotation;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.simulator.SimulationData;

/**
 * Class implements "NONE" action type.
 * @author avokhmin
 * @see CActionType
 */
@SuppressWarnings("serial")
public class CDefaultAction extends CAction {
	private final CAgent myToAgent;

	/**
	 * Constructor of CDefaultAction.<br>
	 * <br>
	 * Example:<br>
	 * <code>A(x)->A(x)</code>, creates <code>NONE</code> action.<br> 
	 * <code>siteFrom</code> - site "x" from agent "A" from left handSide.<br>
	 * <code>siteTo</code> - site "x" from agent "A" from right handSide.<br>
	 * <code>ccL</code> - connected component "A(x)" from left handSide.<br>
	 * <code>ccR</code> - connected component "A(x)" from right handSide.<br>
	 * <code>rule</code> - rule "A(x)->A(x)".<br>
	 * 
	 */
	public CDefaultAction(CRule rule, CAgent fromAgent, CAgent toAgent,
			IConnectedComponent ccL, IConnectedComponent ccR) {
		super(rule, fromAgent, toAgent, ccL, ccR);
		myToAgent = toAgent;
		setType(CActionType.NONE);
	}
	
	public final void doAction(RuleApplicationPool pool, CInjection injection, 
			INetworkNotation netNotation, SimulationData simulationData) {
		int agentIdInCC = getAgentIdInCCBySideId(myToAgent);
		CAgent agentFromInSolution = injection.getAgentFromImageById(agentIdInCC);
		getRightCComponent().addAgentFromSolutionForRHS(agentFromInSolution);
	}
	
	protected final void addRuleSitesToNetworkNotation(boolean existInRule,
			INetworkNotation netNotation, CSite site) {
		if (netNotation != null) {
			NetworkNotationMode agentMode = NetworkNotationMode.NONE;
			NetworkNotationMode linkStateMode = NetworkNotationMode.NONE;
			NetworkNotationMode internalStateMode = NetworkNotationMode.NONE;
			netNotation.addToAgentsFromRules(site, agentMode,
					internalStateMode, linkStateMode);
		}
	}
	
	protected final void addToNetworkNotation(StateType index,
			INetworkNotation netNotation, CSite site) {
	}
}
