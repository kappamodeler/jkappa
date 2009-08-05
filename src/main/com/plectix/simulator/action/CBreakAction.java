package com.plectix.simulator.action;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.stories.enums.EActionOfAEvent;
import com.plectix.simulator.components.stories.enums.ETypeOfWire;
import com.plectix.simulator.components.stories.storage.CEvent;
import com.plectix.simulator.components.stories.storage.WireHashKey;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.ThreadLocalData;

/**
 * Class implements "BREAK" action type.
 * @author avokhmin
 * @see CActionType
 */
@SuppressWarnings("serial")
public class CBreakAction extends CAction {
	private final CSite mySiteFrom;
	private final CSite mySiteTo;
	private final CRule myRule;

	/**
	 * Constructor of CBreakAction.<br>
	 * <br>
	 * Example:<br>
	 * <code>A(x!1),B(y!1)->A(x),B(y)</code>, creates 2 <code>BREAK</code> actions.<br> 
	 * <li>relative to site "x" from agent "A":<br>
	 * <code>siteFrom</code> - site "x" from agent "A" from left handSide.<br>
	 * <code>siteTo</code> - site "x" from agent "A" from right handSide.<br>
	 * <code>ccL</code> - connected component "A(x!1),B(y!1)" from left handSide.<br>
	 * <code>ccR</code> - connected component "A(x)" from right handSide.<br>
	 * <code>rule</code> - rule "A(x!1),B(y!1)->A(x),B(y)".<br>
	 * </li>
	 * 
	 * <li>relative to site "y" from agent "B":<br>
	 * similarly.
	 * </li>
	 * 
	 * @param rule  given rule
	 * @param siteFrom given site from left handSide
	 * @param siteTo given site from right handSide
	 * @param ccL given connected component from left handSide
	 * @param ccR given connected component from right handSide
	 */
	public CBreakAction(CRule rule, CSite siteFrom, CSite siteTo,
			IConnectedComponent ccL, IConnectedComponent ccR) {
		super(rule, null, null, ccL, ccR);
		myRule = rule;
		mySiteFrom = siteFrom;
		mySiteTo = siteTo;
		setActionApplicationSites(mySiteFrom, mySiteTo);
		setType(CActionType.BREAK);
	}

	@Override
	public final void doAction(RuleApplicationPool pool, CInjection injection,
			CEvent eventContainer,
			SimulationData simulationData) {
		CAgent agentFromInSolution;
		int agentIdInCC = getAgentIdInCCBySideId(mySiteFrom.getParentAgent());
		agentFromInSolution = injection.getAgentFromImageById(agentIdInCC);

		CSite injectedSite = agentFromInSolution.getSiteByNameId(mySiteFrom.getNameId());

		CSite linkSite = (CSite) injectedSite.getLinkState().getConnectedSite();
		addToEventContainer(eventContainer, linkSite,CEvent.BEFORE_STATE);
		addToEventContainer(eventContainer, injectedSite,CEvent.BEFORE_STATE);
		if ((mySiteFrom.getLinkState().getConnectedSite() == null) && (linkSite != null)) {
			linkSite.getLinkState().connectSite(null);
			linkSite.getLinkState().setStatusLink(CLinkStatus.FREE);
			if (mySiteTo != null) {
				linkSite.setLinkIndex(-1);
			}
			injection.addToChangedSites(linkSite);
			getRightCComponent().addAgentFromSolutionForRHS(linkSite
					.getParentAgent());
		}

		injectedSite.getLinkState().connectSite(null);
		injectedSite.getLinkState().setStatusLink(CLinkStatus.FREE);
		injection.addToChangedSites(injectedSite);

		// Break connection for rules such as A(x!_)->A(x)
		if (mySiteFrom.getLinkState().getConnectedSite() == null && linkSite != null) {
			addSiteToConnectedWithBroken(linkSite);
		}
		addToEventContainer(eventContainer, linkSite,CEvent.AFTER_STATE);
		addToEventContainer(eventContainer, injectedSite,CEvent.AFTER_STATE);
		///////////////////////////////////////////////
		agentFromInSolution.getSiteByNameId(mySiteFrom.getNameId()).
			setLinkIndex(-1);
	}

	private final void addToEventContainer(CEvent eventContainer,
			CSite site, boolean state) {
		if (eventContainer == null || site == null)
			return;

		ThreadLocalData.getTypeById().setTypeOfAgent(site.getParentAgent().getId(), site.getParentAgent().getNameId());
		eventContainer.addAtomicEvent(new WireHashKey(site.getParentAgent().getId(), site
				.getNameId(), ETypeOfWire.LINK_STATE), site,
				EActionOfAEvent.MODIFICATION, state);

		eventContainer.addAtomicEvent(new WireHashKey(site.getParentAgent().getId(), site
				.getNameId(), ETypeOfWire.BOUND_FREE), site,
				EActionOfAEvent.MODIFICATION, state);
	}

	private final void addSiteToConnectedWithBroken(CSite checkedSite) {
		for (CSite site : myRule.getSitesConnectedWithBroken()) {
			if (site == checkedSite) {
				return;
			}
		}
		myRule.addSiteConnectedWithBroken(checkedSite);
	}

}
