package com.plectix.simulator.action;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.stories.enums.EActionOfAEvent;
import com.plectix.simulator.components.stories.enums.ETypeOfWire;
import com.plectix.simulator.components.stories.storage.CEvent;
import com.plectix.simulator.components.stories.storage.WireHashKey;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.components.CSite;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.ThreadLocalData;

/**
 * Class implements "MODIFY" action type.
 * @author avokhmin
 * @see CActionType
 */
@SuppressWarnings("serial")
public class CModifyAction extends CAction {
	private final CSite mySiteTo;
	private final int myInternalStateNameId;

	/**
	 * Constructor of CModifyAction.<br>
	 * <br>
	 * Example:<br>
	 * <code>A(x~q)->A(x~fi)</code>, creates <code>MODIFY</code> action.<br> 
	 * <code>siteFrom</code> - site "x" from agent "A" from left handSide.<br>
	 * <code>siteTo</code> - site "x" from agent "A" from right handSide.<br>
	 * <code>ccL</code> - connected component "A(x~q)" from left handSide.<br>
	 * <code>ccR</code> - connected component "A(x~fi)" from right handSide.<br>
	 * <code>rule</code> - rule "A(x~q)->A(x~fi)".<br>
	 * 
	 * @param rule  given rule
	 * @param siteFrom given site from left handSide
	 * @param siteTo given site from right handSide
	 * @param ccL given connected component from left handSide
	 * @param ccR given connected component from right handSide
	 */
	public CModifyAction(CRule rule, CSite siteFrom, CSite siteTo,
			IConnectedComponent ccL, IConnectedComponent ccR) {
		super(rule, null, null, ccL, ccR);
		mySiteTo = siteTo;
		setActionApplicationSites(siteFrom, siteTo);
		myInternalStateNameId = siteTo.getInternalState().getNameId();
		setType(CActionType.MODIFY);
	}

	public final void doAction(RuleApplicationPool pool, CInjection injection,
			CEvent eventContainer,
			SimulationData simulationData) {
		/**
		 * Done.
		 */
		int agentIdInCC = getAgentIdInCCBySideId(mySiteTo.getAgentLink());
		CAgent agentFromInSolution = injection.getAgentFromImageById(agentIdInCC);

		// /////////////////////////////////////////////
		CSite injectedSite = agentFromInSolution.getSiteByNameId(mySiteTo
				.getNameId());
//		addToNetworkNotation(StateType.BEFORE,
//				netNotation, injectedSite);
//		addRuleSitesToNetworkNotation(false, netNotation, injectedSite);

		addSiteToEventContainer(eventContainer, injectedSite, CEvent.BEFORE_STATE);
		injectedSite.getInternalState().setNameId(myInternalStateNameId);
		injection.addToChangedSites(injectedSite);

//		addToNetworkNotation(StateType.AFTER,
//				netNotation, injectedSite);
		addSiteToEventContainer(eventContainer, injectedSite, CEvent.AFTER_STATE);
		// /////////////////////////////////////////////
	}

	private static void addSiteToEventContainer(CEvent eventContainer,
			CSite site, boolean state) {
		if (eventContainer == null)
			return;
		ThreadLocalData.getTypeById().setTypeOfAgent(site.getAgentLink().getId(), site.getAgentLink().getNameId());

		eventContainer.addAtomicEvent(new WireHashKey(site.getAgentLink().getId(), site
				.getNameId(), ETypeOfWire.INTERNAL_STATE), site,
				EActionOfAEvent.MODIFICATION, state);
	}

}
