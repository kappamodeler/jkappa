package com.plectix.simulator.action;

import java.io.Serializable;
import java.util.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.stories.CStoriesSiteStates.StateType;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.SimulationData;

/**
 * Implements standard abstract class atomic action.
 * @author avokhmin
 * @see CActionType
 */
@SuppressWarnings("serial")
public abstract class CAction implements Serializable {
	private final CAgent fromAgent;
	private final CAgent toAgent;
	private final CRule myRule;
	private final IConnectedComponent rightConnectedComponent;
	private IConnectedComponent leftConnectedComponent;
	private CActionType myType;
	private CSite siteTo = null;
	private CSite siteFrom = null;

	/**
	 * Default constructor, create AtomicCAction and add to "actionList".
	 * 
	 * @param fromAgent agent from left handSide
	 * @param toAgent agent from right handSide
	 * @param ccL given connected component, contains <b>fromAgent</b>
	 * @param ccR given connected component, contains <b>toAgent</b>
	 */
	protected CAction(CRule rule, CAgent fromAgent, CAgent toAgent,
			IConnectedComponent ccL, IConnectedComponent ccR) {
		this.fromAgent = fromAgent;
		this.toAgent = toAgent;
		this.rightConnectedComponent = ccR;
		this.leftConnectedComponent = ccL;
		this.myRule = rule;
	}

	/**
	 * This method apply current action by given data.
	 * @param pool
	 * @param injection given injection
	 * @param netNotation given network notation ("null" if it not "STORY" simulation mode).
	 * @param simulationData given simulation data
	 */
	public abstract void doAction(RuleApplicationPool pool, CInjection injection, 
			INetworkNotation netNotation,  SimulationData simulationData);

	/**
	 * Util method. Adds information about given "site" to given "netNotation".
	 * @param index type adds information
	 * @param netNotation given network notation
	 * @param site given site
	 */
	protected abstract void addToNetworkNotation(StateType index,
			INetworkNotation netNotation, CSite site);

	/**
	 * Util method. Initial unconfigured state.
	 * @param existInRule key, <tt>true</tt> if given site exist in rule, otherwise <tt>false</tt>
	 * @param netNotation given network notation
	 * @param site given site
	 */
	protected abstract void addRuleSitesToNetworkNotation(boolean existInRule,
			INetworkNotation netNotation, CSite site);

	/**
	 * This method returns {@link CAgent#getIdInConnectedComponent()} from left
	 * handSide by given agent from right handSide rule.
	 * @param toAgentRight given agent
	 * @return {@link CAgent#getIdInConnectedComponent()}
	 */
	protected final int getAgentIdInCCBySideId(CAgent toAgentRight) {
		// TODO May be should optimize?
		for (IConnectedComponent cc : myRule.getLeftHandSide())
			for (CAgent agentL : cc.getAgents())
				if (agentL.getIdInRuleHandside() == toAgentRight.getIdInRuleHandside()) {
					if (leftConnectedComponent == null)
						leftConnectedComponent = cc;
					return agentL.getIdInConnectedComponent();
				}
		return -1;
	}

	/**
	 * This method analyses current action and creates new actions, if possible. 
	 * @return collection of new actions
	 */
	public final List<CAction> createAtomicActions() {
		// TODO it is very strange place. is there any case, where
		// fromAgent.getSites() == null ???
		if (fromAgent.getSites() == null) {
			myType = CActionType.NONE;
			return null;
		}

		List<CAction> list = new ArrayList<CAction>();

		for (CSite fromSite : fromAgent.getSites()) {
			CSite toSite = toAgent.getSiteById(fromSite.getNameId());
			if (fromSite.getInternalState().getNameId() != toSite
					.getInternalState().getNameId()) {
				list.add(new CModifyAction(myRule, fromSite, toSite,
						leftConnectedComponent, rightConnectedComponent));
				//if (!isChangedSiteContains(toSite))
					//myRule.addChangedSite(toSite);
					myRule.addInhibitedChangedSite(fromSite,true, false);
			}

			// if ((fromSite.getLinkState().getSite() == null)
			// && (toSite.getLinkState().getSite() == null))
			// continue;
			if ((fromSite.getLinkState().getStatusLink() == CLinkStatus.FREE)
					&& (toSite.getLinkState().getStatusLink() == CLinkStatus.FREE))
				continue;

			// if ((fromSite.getLinkState().getSite() != null)
			// && (toSite.getLinkState().getSite() == null)) {
			if ((fromSite.getLinkState().getStatusLink() != CLinkStatus.FREE)
					&& (toSite.getLinkState().getStatusLink() == CLinkStatus.FREE)) {
				list.add(new CBreakAction(myRule, fromSite, toSite,
						leftConnectedComponent, rightConnectedComponent));
				//	myRule.addChangedSite(toSite);
					myRule.addInhibitedChangedSite(fromSite, false, true);
				continue;
			}

			// if ((fromSite.getLinkState().getSite() == null)
			// && (toSite.getLinkState().getSite() != null)) {
			if ((fromSite.getLinkState().getStatusLink() == CLinkStatus.FREE)
					&& (toSite.getLinkState().getStatusLink() == CLinkStatus.BOUND)) {
				list.add(new CBoundAction(myRule, toSite, toSite.getLinkState()
						.getConnectedSite(), leftConnectedComponent,
						rightConnectedComponent));
				//	myRule.addChangedSite(toSite);
					myRule.addInhibitedChangedSite(fromSite, false, true);
				continue;
			}

			CSite lConnectSite = (CSite) fromSite.getLinkState().getConnectedSite();
			CSite rConnectSite = (CSite) toSite.getLinkState().getConnectedSite();
			if (lConnectSite == null || rConnectSite == null)
				continue;
			if ((lConnectSite.getAgentLink().getIdInRuleHandside() == rConnectSite
					.getAgentLink().getIdInRuleHandside())
					&& (lConnectSite.equalz(rConnectSite)))
				continue;
			list.add(new CBreakAction(myRule, fromSite, toSite,
					leftConnectedComponent, rightConnectedComponent));
			list.add(new CBoundAction(myRule, toSite, (CSite) toSite
					.getLinkState().getConnectedSite(), leftConnectedComponent,
					rightConnectedComponent));
				//myRule.addChangedSite(toSite);
				myRule.addInhibitedChangedSite(fromSite, false, true);
		}
		return Collections.unmodifiableList(list);
	}

//=======================GETTERS AND SETTERS========================

	/**
	 * This method sets "siteFrom" and "siteTo".
	 * @param from given "siteFrom"
	 * @param to given "siteTo"
	 */
	protected final void setSiteSet(CSite from, CSite to) {
		siteTo = to;
		siteFrom = from;
	}

	/**
	 * This method sets type current action.
	 * @param type given type
	 * @see CActionType
	 */
	protected final void setType(CActionType type) {
		myType = type;
	}

	/**
	 * Util methiod. Uses for correlation sites.
	 */
	public final CSite getSiteFrom() {
		return siteFrom;
	}

	/**
	 * Util methiod. Uses for correlation sites.
	 */
	public final CSite getSiteTo() {
		return siteTo;
	}

	/**
	 * This method returns type of current action.
	 * @return type of current action.
	 * @see CActionType
	 */
	public final int getTypeId() {
		return myType.getId();
	}

	/**
	 * Util methiod. Uses for correlation agents.
	 */
	public final CAgent getAgentFrom() {
		return fromAgent;
	}

	/**
	 * Util methiod. Uses for correlation agents.
	 */
	public final CAgent getAgentTo() {
		return toAgent;
	}
	
	/**
	 * This method returns connected component from right handSide rule,
	 * where contains "site to" and "agent to", (may be "null").
	 */
	public final IConnectedComponent getRightCComponent() {
		return rightConnectedComponent;
	}

	/**
	 * This method returns connected component from left handSide rule,
	 * where contains "site from" and "agent from", (may be "null").
	 */
	public final IConnectedComponent getLeftCComponent() {
		return leftConnectedComponent;
	}
	
}
