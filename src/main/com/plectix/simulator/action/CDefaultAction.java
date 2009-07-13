package com.plectix.simulator.action;

import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.stories.enums.EActionOfAEvent;
import com.plectix.simulator.components.stories.storage.CEvent;
import com.plectix.simulator.components.stories.storage.WireHashKey;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;

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
			CEvent eventContainer,
			SimulationData simulationData) {
		int agentIdInCC = getAgentIdInCCBySideId(myToAgent);
		CAgent agentFromInSolution = injection
				.getAgentFromImageById(agentIdInCC);
		getRightCComponent().addAgentFromSolutionForRHS(agentFromInSolution);
		addToEventContainer(eventContainer, agentFromInSolution,EActionOfAEvent.TEST);
	}

//	private void addToEventContainer(CEventContainer eventContainer,
//			CAgent agentFromInSolution) {
//		if (eventContainer == null)
//			return;
//		// AGENT
//		eventContainer.addEvent(new WireHashKey(agentFromInSolution.getId(),
//				EKeyOfState.AGENT), null, ECheck.TEST, CEventContainer.BEFORE_STATE);
//		for (CSite s : getAgentFrom().getSites()) {
//			CSite site = agentFromInSolution.getSiteById(s.getNameId());
//			CLinkRank linkRank = s.getLinkState().getStatusLinkRank();
//			if (linkRank != CLinkRank.BOUND_OR_FREE) {
//				// FREE/BOUND
//				eventContainer.addEvent(new WireHashKey(agentFromInSolution
//						.getId(), site.getNameId(), EKeyOfState.BOUND_FREE),
//						site, ECheck.TEST, CEventContainer.BEFORE_STATE);
//
//				if (linkRank != CLinkRank.SEMI_LINK) {
//					eventContainer.addEvent(
//							new WireHashKey(agentFromInSolution.getId(), site
//									.getNameId(), EKeyOfState.LINK_STATE),
//							site, ECheck.TEST, CEventContainer.BEFORE_STATE);
//				}
//			}
//
//			eventContainer.addEvent(new WireHashKey(agentFromInSolution.getId(),
//					site.getNameId(), EKeyOfState.INTERNAL_STATE), site,
//					ECheck.TEST, CEventContainer.BEFORE_STATE);
//		}
//	}
}
