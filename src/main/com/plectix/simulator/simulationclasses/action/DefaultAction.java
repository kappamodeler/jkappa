package com.plectix.simulator.simulationclasses.action;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulationclasses.solution.RuleApplicationPoolInterface;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.stories.ActionOfAEvent;

/**
 * Class implements "NONE" action type.
 * @author avokhmin
 * @see CActionType
 */
public class DefaultAction extends Action {
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
	public DefaultAction(Rule rule, Agent sourceAgent, Agent targetAgent,
			ConnectedComponentInterface leftHandSideComponent, 
			ConnectedComponentInterface rightHandSideComponent) {
		super(rule, sourceAgent, targetAgent, leftHandSideComponent, rightHandSideComponent);
		setType(ActionType.NONE);
	}

	@Override
	public final void doAction(RuleApplicationPoolInterface ruleApplicationPool, 
			Injection injection, ActionObserverInteface eventContainer, SimulationData simulationData) {
		int agentIdInCC = getAgentIdInCCBySideId(this.getTargetAgent());
		Agent agentFromInSolution = injection
				.getAgentFromImageById(agentIdInCC);
		
		getRightCComponent().addAgentFromSolutionForRHS(agentFromInSolution);
		eventContainer.addToEvent(agentFromInSolution,ActionOfAEvent.TEST,getSourceAgent());
	}
}
