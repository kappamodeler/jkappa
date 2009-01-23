package com.plectix.simulator.action;

import java.io.Serializable;
import java.util.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.components.stories.CStoriesSiteStates.StateType;
import com.plectix.simulator.interfaces.*;

/*package*/abstract class CAction implements IAction, Serializable {
	private final IAgent fromAgent;
	private final IAgent toAgent;
	private final CRule myRule;
	private final IConnectedComponent rightConnectedComponent;
	private IConnectedComponent leftConnectedComponent;
	private CActionType myType;
	private ISite siteTo = null;
	private ISite siteFrom = null;
	private int nameInternalStateId;

	/**
	 * Default constructor, create AtomicCAction and add to "actionList".
	 * 
	 * @param fromAgent
	 * @param toAgent
	 */

	protected CAction(CRule rule, IAgent fromAgent, IAgent toAgent,
			IConnectedComponent ccL, IConnectedComponent ccR) {
		this.fromAgent = fromAgent;
		this.toAgent = toAgent;
		this.rightConnectedComponent = ccR;
		this.leftConnectedComponent = ccL;
		this.myRule = rule;
	}

	protected final void setSiteSet(ISite from, ISite to) {
		siteTo = to;
		siteFrom = from;
	}

	protected final void setType(CActionType type) {
		myType = type;
	}

	public final ISite getSiteFrom() {
		return siteFrom;
	}

	public final ISite getSiteTo() {
		return siteTo;
	}

	public final int getTypeId() {
		return myType.getId();
	}

	public final IAgent getAgentFrom() {
		return fromAgent;
	}

	public final IAgent getAgentTo() {
		return toAgent;
	}

	public final Integer getNameInternalStateId() {
		return nameInternalStateId;
	}

	protected abstract void addToNetworkNotation(StateType index,
			INetworkNotation netNotation, ISite site);

	protected abstract void addRuleSitesToNetworkNotation(boolean existInRule,
			INetworkNotation netNotation, ISite site);

	protected final int getAgentIdInCCBySideId(IAgent toAgent2) {
		for (IConnectedComponent cc : myRule.getLeftHandSide())
			for (IAgent agentL : cc.getAgents())
				if (agentL.getIdInRuleSide() == toAgent2.getIdInRuleSide()) {
					if (leftConnectedComponent == null)
						leftConnectedComponent = cc;
					return agentL.getIdInConnectedComponent();
				}
		return -1;
	}

	public final IConnectedComponent getRightCComponent() {
		return rightConnectedComponent;
	}

	public final IConnectedComponent getLeftCComponent() {
		return leftConnectedComponent;
	}

	public final List<IAction> createAtomicActions() {
		// TODO it is very strange place. is there any case, where
		// fromAgent.getSites() == null ???
		if (fromAgent.getSites() == null) {
			myType = CActionType.NONE;
			return null;
		}

		List<IAction> list = new ArrayList<IAction>();

		for (ISite fromSite : fromAgent.getSites()) {
			ISite toSite = toAgent.getSite(fromSite.getNameId());
			if (fromSite.getInternalState().getStateNameId() != toSite
					.getInternalState().getStateNameId()) {
				list.add(new CModifyAction(myRule, fromSite, toSite,
						leftConnectedComponent, rightConnectedComponent));
				//if (!isChangedSiteContains(toSite))
					myRule.addChangedSite(toSite);
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
					myRule.addChangedSite(toSite);
					myRule.addInhibitedChangedSite(fromSite, false, true);
				continue;
			}

			// if ((fromSite.getLinkState().getSite() == null)
			// && (toSite.getLinkState().getSite() != null)) {
			if ((fromSite.getLinkState().getStatusLink() == CLinkStatus.FREE)
					&& (toSite.getLinkState().getStatusLink() == CLinkStatus.BOUND)) {
				list.add(new CBoundAction(myRule, toSite, toSite.getLinkState()
						.getSite(), leftConnectedComponent,
						rightConnectedComponent));
					myRule.addChangedSite(toSite);
					myRule.addInhibitedChangedSite(fromSite, false, true);
				continue;
			}

			ISite lConnectSite = (ISite) fromSite.getLinkState().getSite();
			ISite rConnectSite = (ISite) toSite.getLinkState().getSite();
			if (lConnectSite == null || rConnectSite == null)
				continue;
			if ((lConnectSite.getAgentLink().getIdInRuleSide() == rConnectSite
					.getAgentLink().getIdInRuleSide())
					&& (lConnectSite.equalz(rConnectSite)))
				continue;
			list.add(new CBreakAction(myRule, fromSite, toSite,
					leftConnectedComponent, rightConnectedComponent));
			list.add(new CBoundAction(myRule, toSite, (ISite) toSite
					.getLinkState().getSite(), leftConnectedComponent,
					rightConnectedComponent));
				myRule.addChangedSite(toSite);
				myRule.addInhibitedChangedSite(fromSite, false, true);
		}
		return Collections.unmodifiableList(list);
	}

}
