package com.plectix.simulator.action;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.stories.CNetworkNotation.NetworkNotationMode;
import com.plectix.simulator.components.stories.CStoriesSiteStates.StateType;
import com.plectix.simulator.components.stories.storage.CEvent;
import com.plectix.simulator.components.stories.storage.ECheck;
import com.plectix.simulator.components.stories.storage.ETypeOfWire;
import com.plectix.simulator.components.stories.storage.WireHashKey;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.interfaces.INetworkNotation;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.simulator.SimulationData;

/**
 * Class implements "BOUND" action type.
 * @author avokhmin
 * @see CActionType
 */
@SuppressWarnings("serial")
public class CBoundAction extends CAction {
	private final CSite mySiteFrom;
	private final CSite mySiteTo;
	private CRule myRule;

	/**
	 * Constructor of CBoundAction.<br>
	 * <br>
	 * Example:<br>
	 * <code>A(x)->A(x!1),B(y!1)</code>, creates 2 <code>BOUND</code> actions
	 * and <code>ADD</code> action.<br>
	 * <li>relative to site "x" from agent "A":<br>
	 * <code>siteFrom</code> - site "x" from agent "A" from right handSide.<br>
	 * <code>siteTo</code> - site "y" from agent "B" from right handSide.<br>
	 * <code>ccL</code> - connected component "A(x)" from left handSide.<br>
	 * <code>ccR</code> - connected component "A(x!1),B(y!1)" from right handSide.<br>
	 * <code>rule</code> - rule "A(x)->A(x!1),B(y!1)".<br>
	 * </li>
	 * <br>
	 * <li>relative to site "y" from agent "B":<br>
	 * <code>siteFrom</code> - site "y" from agent "B" from right handSide.<br>
	 * <code>siteTo</code> - site "x" from agent "A" from right handSide.<br>
	 * <code>ccL</code> - connected component "NULL" from left handSide.<br>
	 * <code>ccR</code> - connected component "A(x!1),B(y!1)" from right handSide.<br>
	 * <code>rule</code> - this rule "A(x)->A(x!1),B(y!1)".<br>
	 * </li>
	 * 
	 * @param rule  given rule
	 * @param siteFrom given site from right handSide
	 * @param siteTo given site from right handSide
	 * @param ccL given connected component from left handSide (may be null)
	 * @param ccR given connected component from right handSide
	 */
	public CBoundAction(CRule rule, CSite siteFrom, CSite siteTo, IConnectedComponent ccL,
			IConnectedComponent ccR) {
		super(rule, null, null, ccL, ccR);
		myRule = rule;
		mySiteFrom = siteFrom;
		mySiteTo = siteTo;
		setActionApplicationSites(mySiteFrom, mySiteTo);
		setType(CActionType.BOUND);
	}

	public final void doAction(RuleApplicationPool pool, CInjection injection,
			INetworkNotation netNotation, CEvent eventContainer,
			SimulationData simulationData) {
		// TODO remove copypaste
		/**
		 * Done.
		 */

		CAgent agentFromInSolution;
		if (mySiteFrom.getAgentLink().getIdInRuleHandside() > myRule.getAgentsFromConnectedComponent(
				myRule.getLeftHandSide()).size()) {
			agentFromInSolution = myRule.getAgentAdd(mySiteFrom.getAgentLink());
		} else {
			int agentIdInCC = getAgentIdInCCBySideId(mySiteFrom.getAgentLink());

			agentFromInSolution = injection.getAgentFromImageById(agentIdInCC);

			// /////////////////////////////////////////////
			CSite injectedSite = agentFromInSolution.getSiteByNameId(mySiteFrom
					.getNameId());
			injection.addToChangedSites(injectedSite);

			addToNetworkNotation(StateType.BEFORE, netNotation,
					injectedSite);
			addRuleSitesToNetworkNotation(false, netNotation, injectedSite);
			// /////////////////////////////////////////////
		}

		CAgent agentToInSolution;
		if (mySiteTo.getAgentLink().getIdInRuleHandside() > myRule.getAgentsFromConnectedComponent(
				myRule.getLeftHandSide()).size()) {
			agentToInSolution = myRule.getAgentAdd(mySiteTo.getAgentLink());
		} else {
			int agentIdInCC = getAgentIdInCCBySideId(mySiteTo.getAgentLink());
			CInjection inj = myRule.getInjectionBySiteToFromLHS(mySiteTo);
			agentToInSolution = inj.getAgentFromImageById(agentIdInCC);
		}

		agentFromInSolution.getSiteByNameId(mySiteFrom.getNameId()).getLinkState()
				.connectSite(agentToInSolution.getSiteByNameId(mySiteTo.getNameId()));

		agentToInSolution.getSiteByNameId(mySiteTo.getNameId()).getLinkState()
		.connectSite(agentFromInSolution.getSiteByNameId(mySiteFrom.getNameId()));
		//======================================================================

		addToNetworkNotation(StateType.AFTER, netNotation,
				agentFromInSolution.getSiteByNameId(mySiteFrom.getNameId()));
		addToEventContainer(eventContainer, agentFromInSolution
				.getSiteByNameId(mySiteFrom.getNameId()));
		addToEventContainer(eventContainer, agentToInSolution
				.getSiteByNameId(mySiteTo.getNameId()));
		//======================================================================

		agentFromInSolution.getSiteByNameId(mySiteFrom.getNameId()).setLinkIndex(
				mySiteFrom.getLinkIndex());
		agentToInSolution.getSiteByNameId(mySiteTo.getNameId()).setLinkIndex(
				mySiteTo.getLinkIndex());

	}

	private static void addToEventContainer(CEvent eventContainer,
			CSite site) {
		if (eventContainer == null)
			return;
		eventContainer.addEvent(new WireHashKey(site.getAgentLink().getId(), site
				.getNameId(), ETypeOfWire.LINK_STATE), site,
				ECheck.MODIFICATION, CEvent.AFTER_STATE);

		// UHashKey key = new
		// UHashKey(site.getAgentLink().getId(),site.getNameId
		// (),EKeyOfState.LINK_STATE);
		// AEvent<CStateOfLink> event = (AEvent<CStateOfLink>)
		// eventContainer.getEvent(key);
		// CSite connectedSite = site.getLinkState().getConnectedSite();
		// event.getState().setAfterState(new
		// CStateOfLink(connectedSite.getAgentLink
		// ().getId(),connectedSite.getNameId()));
		// event.correctingType(ECheck.MODIFICATION);

		eventContainer.addEvent(new WireHashKey(site.getAgentLink().getId(), site
				.getNameId(), ETypeOfWire.BOUND_FREE), site,
				ECheck.MODIFICATION, CEvent.AFTER_STATE);

		// key = new UHashKey(site.getAgentLink().getId(), site.getNameId(),
		// EKeyOfState.BOUND_FREE);
		// AEvent<Boolean> event2 = (AEvent<Boolean>) eventContainer
		// .getEvent(key);
		// event2.getState().setAfterState(false);
		// event2.correctingType(ECheck.MODIFICATION);

	}

	protected final void addRuleSitesToNetworkNotation(boolean existInRule,
			INetworkNotation netNotation, CSite site) {
		if (netNotation != null) {
			NetworkNotationMode agentMode = NetworkNotationMode.NONE;
			NetworkNotationMode linkStateMode = NetworkNotationMode.NONE;
			NetworkNotationMode internalStateMode = NetworkNotationMode.NONE;

			agentMode = NetworkNotationMode.TEST;
			linkStateMode = NetworkNotationMode.TEST_OR_MODIFY;
			netNotation.addToAgentsFromRules(site, agentMode,
					internalStateMode, linkStateMode);
		}
	}

	protected final void addToNetworkNotation(StateType index,
			INetworkNotation netNotation, CSite site) {
		if (netNotation != null) {
			netNotation.checkLinkForNetworkNotation(index, site);
			netNotation.checkLinkToUsedSites(index, site);
		}
	}
}
