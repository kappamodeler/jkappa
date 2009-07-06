package com.plectix.simulator.action;

import java.io.Serializable;
import java.util.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.stories.CStoriesSiteStates.StateType;
import com.plectix.simulator.components.stories.newVersion.CEvent;
import com.plectix.simulator.components.stories.newVersion.ECheck;
import com.plectix.simulator.components.stories.newVersion.EKeyOfState;
import com.plectix.simulator.components.stories.newVersion.WireHashKey;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.SimulationData;

/**
 * This class implements "atomic action".
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
	 * Constructor. Creates atomic action, transforming choosen agent from left handside
	 * to the other one from right handside of the fixed rule
	 * 
	 * @param rule fixed rule
	 * @param fromAgent agent from left handSide of the rule
	 * @param toAgent agent from right handSide of the rule
	 * @param ccL given connected component, containing <b>fromAgent</b>
	 * @param ccR given connected component, containing <b>toAgent</b>
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
	 * This method apply current action in fixed rule application pool.
	 * @param pool rule application pool, storage where we get changeable substances from 
	 * and where we put the result
	 * @param injection injection, pointing to agents which we should take for application.
	 * @param netNotation network notation ("null" if it not "STORY" simulation mode).
	 * @param simulationData simulation data
	 */
	public abstract void doAction(RuleApplicationPool pool,
			CInjection injection, INetworkNotation netNotation,
			CEvent eventContainer, SimulationData simulationData);

	/**
	 * Util method. Adds information about given site to given network notation
	 * @param index information type
	 * @param netNotation network notation
	 * @param site given site
	 */
	protected abstract void addToNetworkNotation(StateType index,
			INetworkNotation netNotation, CSite site);

	/**
	 * Util method. Initializes unconfigured state.
	 * @param siteExistsInRule key, <tt>true</tt> if given site exists in rule, otherwise <tt>false</tt>
	 * @param netNotation given network notation
	 * @param site given site
	 */
	protected abstract void addRuleSitesToNetworkNotation(
			boolean siteExistsInRule, INetworkNotation netNotation, CSite site);

	/**
	 * This method returns takes agent from left handside of the rule by it's image - agent from right
	 * handside, and then returns it's id in connected component
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

	protected void addToEventContainer(CEvent eventContainer,
			CAgent agentFromInSolution, ECheck type) {
		if (eventContainer == null)
			return;
		// AGENT
		eventContainer.addEvent(new WireHashKey(agentFromInSolution.getId(),
				EKeyOfState.AGENT), null, type, CEvent.BEFORE_STATE);
		for (CSite s : getAgentFrom().getSites()) {
			CSite site = agentFromInSolution.getSiteByNameId(s.getNameId());
			CLinkRank linkRank = s.getLinkState().getStatusLinkRank();
			if (linkRank != CLinkRank.BOUND_OR_FREE) {
				// FREE/BOUND
				eventContainer.addEvent(new WireHashKey(agentFromInSolution
						.getId(), site.getNameId(), EKeyOfState.BOUND_FREE),
						site, type, CEvent.BEFORE_STATE);

				if (linkRank != CLinkRank.SEMI_LINK) {
					eventContainer.addEvent(
							new WireHashKey(agentFromInSolution.getId(), site
									.getNameId(), EKeyOfState.LINK_STATE),
							site, type, CEvent.BEFORE_STATE);
				}
			}

			if (s.getInternalState().getNameId() != CInternalState.EMPTY_STATE
					.getNameId())
				eventContainer.addEvent(
						new WireHashKey(agentFromInSolution.getId(), site
								.getNameId(), EKeyOfState.INTERNAL_STATE),
						site, type, CEvent.BEFORE_STATE);
		}
	}

	/**
	 * This method analyses current action and creates new actions, if possible.
	 * 
	 * @return collection of new actions
	 */
	public final Collection<CAction> createAtomicActions() {
		// TODO it is very strange place. is there any case, where
		// fromAgent.getSites() == null ???
		if (fromAgent.getSites() == null) {
			myType = CActionType.NONE;
			return null;
		}

		Set<CAction> list = new HashSet<CAction>();

		for (CSite fromSite : fromAgent.getSites()) {
			CSite toSite = toAgent.getSiteByNameId(fromSite.getNameId());
			if (fromSite.getInternalState().getNameId() != toSite
					.getInternalState().getNameId()) {
				list.add(new CModifyAction(myRule, fromSite, toSite,
						leftConnectedComponent, rightConnectedComponent));
				// if (!isChangedSiteContains(toSite))
				// myRule.addChangedSite(toSite);
				myRule.addInhibitedChangedSite(fromSite, true, false);
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
				// myRule.addChangedSite(toSite);
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
				// myRule.addChangedSite(toSite);
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
			// myRule.addChangedSite(toSite);
			myRule.addInhibitedChangedSite(fromSite, false, true);
		}
		return Collections.unmodifiableSet(list);
	}

//=======================GETTERS AND SETTERS========================

	/**
	 * This method sets sites, which this action should be applied to.
	 * @param from site from the left handside of the rule
	 * @param to site from the right handside of the rule
	 */
	protected final void setActionApplicationSites(CSite from, CSite to) {
		siteTo = to;
		siteFrom = from;
	}

	/**
	 * This method sets type of current action.
	 * @param type type to be set
	 * @see CActionType
	 */
	protected final void setType(CActionType type) {
		myType = type;
	}

	/**
	 * This method returns site from the left handside of the rule, which this action 
	 * should be applied to.
	 */
	public final CSite getSiteFrom() {
		return siteFrom;
	}

	/**
	 * This method returns site from the right handside of the rule, which this action 
	 * should be applied to.
	 */
	public final CSite getSiteTo() {
		return siteTo;
	}

	/**
	 * This method returns type of current action.
	 * @return id of action's type
	 * @see CActionType
	 */
	public final int getTypeId() {
		return myType.getId();
	}

	/**
	 * Util method, used for output only.
	 */
	public final CAgent getAgentFrom() {
		return fromAgent;
	}

	/**
	 * Util method, used for output only.
	 */
	public final CAgent getAgentTo() {
		return toAgent;
	}

	/**
	 * This method returns connected component from right handSide rule,
	 * which this action should be applied to (may be "null").
	 */
	public final IConnectedComponent getRightCComponent() {
		return rightConnectedComponent;
	}

	/**
	 * This method returns connected component from left handSide rule,
	 * which this action should be applied to (may be "null").
	 */
	public final IConnectedComponent getLeftCComponent() {
		return leftConnectedComponent;
	}

}
