package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.plectix.simulator.components.actions.*;
import com.plectix.simulator.interfaces.*;

public abstract class CAction implements IAction {
	private IAgent fromAgent;
	private IAgent toAgent;
	private CRule myRule;
	private final IConnectedComponent rightConnectedComponent;
	private IConnectedComponent leftConnectedComponent;
	private ISite siteFrom = null;
	private CActionType myType;
	private ISite siteTo = null;
	private Integer nameInternalStateId;

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
	
	protected void setSiteSet(ISite from, ISite to) {
		siteTo = to;
		siteFrom = from;
	}
	
	public ISite getSiteFrom() {
		return siteFrom;
	}

	public ISite getSiteTo() {
		return siteTo;
	}
	
	public void setType(CActionType type) {
		myType = type;
	}
	
	public int getTypeId() {
		return myType.getId();
	}
	
	public IAgent getFromAgent() {
		return fromAgent;
	}

	public IAgent getToAgent() {
		return toAgent;
	}

	public Integer getNameInternalStateId() {
		return nameInternalStateId;
	}

	protected abstract void addToNetworkNotation(int index,
			INetworkNotation netNotation, ISite site);

	public abstract void addRuleSitesToNetworkNotation(boolean existInRule,
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

	public IConnectedComponent getRightCComponent() {
		return rightConnectedComponent;
	}

	public IConnectedComponent getLeftCComponent() {
		return leftConnectedComponent;
	}

	public final List<IAction> createAtomicActions() {
		//TODO it is very strange place. is there any case, where fromAgent.getSites() == null ???
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
				if (!isChangedSiteContains(toSite))
					myRule.addChangedSite(toSite);
			}

			// if ((fromSite.getLinkState().getSite() == null)
			// && (toSite.getLinkState().getSite() == null))
			// continue;
			if ((fromSite.getLinkState().getStatusLink() == CLinkState.STATUS_LINK_FREE)
					&& (toSite.getLinkState().getStatusLink() == CLinkState.STATUS_LINK_FREE))
				continue;

			// if ((fromSite.getLinkState().getSite() != null)
			// && (toSite.getLinkState().getSite() == null)) {
			if ((fromSite.getLinkState().getStatusLink() != CLinkState.STATUS_LINK_FREE)
					&& (toSite.getLinkState().getStatusLink() == CLinkState.STATUS_LINK_FREE)) {
				list.add(new CBreakAction(myRule, fromSite, toSite,
						leftConnectedComponent, rightConnectedComponent));
				if (!isChangedSiteContains(toSite))
					myRule.addChangedSite(toSite);
				continue;
			}

			// if ((fromSite.getLinkState().getSite() == null)
			// && (toSite.getLinkState().getSite() != null)) {
			if ((fromSite.getLinkState().getStatusLink() == CLinkState.STATUS_LINK_FREE)
					&& (toSite.getLinkState().getStatusLink() == CLinkState.STATUS_LINK_BOUND)) {
				list.add(new CBoundAction(myRule, toSite, toSite.getLinkState().getSite(),  
						leftConnectedComponent, rightConnectedComponent));
				if (!isChangedSiteContains(toSite))
					myRule.addChangedSite(toSite);
				continue;
			}

			ISite lConnectSite = (ISite) fromSite.getLinkState().getSite();
			ISite rConnectSite = (ISite) toSite.getLinkState().getSite();
			if (lConnectSite == null || rConnectSite == null)
				continue;
			if ((lConnectSite.getAgentLink().getIdInRuleSide() == rConnectSite
					.getAgentLink().getIdInRuleSide())
					&& (lConnectSite.equals(rConnectSite)))
				continue;
			list.add(new CBreakAction(myRule, fromSite, toSite, leftConnectedComponent, 
					rightConnectedComponent));
			list.add(new CBoundAction(myRule, toSite, (ISite) toSite.getLinkState()
					.getSite(), leftConnectedComponent, rightConnectedComponent));
			if (!isChangedSiteContains(toSite))
				myRule.addChangedSite(toSite);
		}
		return Collections.unmodifiableList(list);
	}

	private boolean isChangedSiteContains(ISite site) {
		for (ISite siteCh : myRule.getChangedSites()) {
			if (siteCh == site) {
				return true;
			}
		}
		return false;
	}
}
