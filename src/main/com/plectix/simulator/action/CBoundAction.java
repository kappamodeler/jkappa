package com.plectix.simulator.action;

import java.util.LinkedHashSet;
import java.util.Set;

import com.plectix.simulator.components.CAgent;
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
 * Class implements "BOUND" action type.
 * @author avokhmin
 * @see CActionType
 */
@SuppressWarnings("serial")
public class CBoundAction extends CAction {
	private final CSite mySiteFrom;
	private final CSite mySiteTo;
	private final CRule myRule;

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

	@Override
	public final void doAction(RuleApplicationPool pool, CInjection injection,
			CEvent eventContainer,
			SimulationData simulationData) {
		// TODO copypaste detected =(
		CAgent agentFromInSolution;
		if (mySiteFrom.getParentAgent().getIdInRuleHandside() > myRule.getAgentsFromConnectedComponent(
				myRule.getLeftHandSide()).size()) {
			agentFromInSolution = myRule.getAgentAdd(mySiteFrom.getParentAgent());
		} else {
			int agentIdInCC = getAgentIdInCCBySideId(mySiteFrom.getParentAgent());

			agentFromInSolution = injection.getAgentFromImageById(agentIdInCC);

			CSite injectedSite = agentFromInSolution.getSiteByNameId(mySiteFrom
					.getNameId());
			injection.addToChangedSites(injectedSite);

		}

		CAgent agentToInSolution;
		if (mySiteTo.getParentAgent().getIdInRuleHandside() > myRule.getAgentsFromConnectedComponent(
				myRule.getLeftHandSide()).size()) {
			agentToInSolution = myRule.getAgentAdd(mySiteTo.getParentAgent());
		} else {
			int agentIdInCC = getAgentIdInCCBySideId(mySiteTo.getParentAgent());
			CInjection inj = myRule.getInjectionBySiteToFromLHS(mySiteTo);
			agentToInSolution = inj.getAgentFromImageById(agentIdInCC);
		}

		ThreadLocalData.getTypeById().setTypeOfAgent(agentFromInSolution.getId(), agentFromInSolution.getNameId());
		ThreadLocalData.getTypeById().setTypeOfAgent(agentToInSolution.getId(), agentToInSolution.getNameId());
		agentFromInSolution.getSiteByNameId(mySiteFrom.getNameId()).getLinkState()
				.connectSite(agentToInSolution.getSiteByNameId(mySiteTo.getNameId()));

		agentToInSolution.getSiteByNameId(mySiteTo.getNameId()).getLinkState()
		.connectSite(agentFromInSolution.getSiteByNameId(mySiteFrom.getNameId()));
		addToEventContainer(eventContainer, agentFromInSolution
				.getSiteByNameId(mySiteFrom.getNameId()),CEvent.AFTER_STATE);
		addToEventContainer(eventContainer, agentToInSolution
				.getSiteByNameId(mySiteTo.getNameId()),CEvent.AFTER_STATE);

		agentFromInSolution.getSiteByNameId(mySiteFrom.getNameId()).setLinkIndex(
				mySiteFrom.getLinkIndex());
		agentToInSolution.getSiteByNameId(mySiteTo.getNameId()).setLinkIndex(
				mySiteTo.getLinkIndex());

	}

	private final void addToEventContainer(CEvent eventContainer,
			CSite site, boolean state) {
		if (eventContainer == null)
			return;
		
		ThreadLocalData.getTypeById().setTypeOfAgent(site.getParentAgent().getId(), site.getParentAgent().getNameId());
		eventContainer.addAtomicEvent(new WireHashKey(site.getParentAgent().getId(), site
				.getNameId(), ETypeOfWire.LINK_STATE), site,
				EActionOfAEvent.MODIFICATION, state);

		eventContainer.addAtomicEvent(new WireHashKey(site.getParentAgent().getId(), site
				.getNameId(), ETypeOfWire.BOUND_FREE), site,
				EActionOfAEvent.MODIFICATION, state);
	}
	
	/**
	 * This method returns actual sites, which this action performs bounding to.
	 * They can be not the same as siteTo and siteFrom! The point is that bound 
	 * action can be applied to the new sites, created by some add-actions before.
	 * @return actual sites (exactly 2), which this action performs bounding to.
	 */
	public final Set<CSite> getBoundingSites() {
		Set<CSite> set = new LinkedHashSet<CSite>();
		CSite imageTo = null;
		CSite imageFrom = null;
		Set<CAgent> lhsAgents = new LinkedHashSet<CAgent>();
		for (IConnectedComponent cc : myRule.getLeftHandSide()) {
			lhsAgents.addAll(cc.getAgents());
		}
		if (!lhsAgents.isEmpty()) {
			for (CAgent agentL : lhsAgents) {
				int id = agentL.getIdInRuleHandside();

				CAgent tryThis = this.getSiteFrom().getParentAgent();
				if (id == tryThis.getIdInRuleHandside()) {
					imageFrom = agentL.getSiteByNameId(this.getSiteFrom().getNameId());
					set.add(imageFrom);
				}
				
				tryThis = this.getSiteTo().getParentAgent();
				if (id == tryThis.getIdInRuleHandside()) {
					imageTo = agentL.getSiteByNameId(this.getSiteTo().getNameId());
					set.add(imageTo);
				}
			}
		}
		// first we should always add siteFrom info
		if (imageFrom == null) {
			set.add(this.getSiteFrom());
		}
		// and only then - siteTo info
		if (imageTo == null) {
			set.add(this.getSiteTo());
		}
		// There's always should be 2 elements
		return set;
	}
}
