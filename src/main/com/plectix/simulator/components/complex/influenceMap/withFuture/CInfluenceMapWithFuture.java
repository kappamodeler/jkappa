package com.plectix.simulator.components.complex.influenceMap.withFuture;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractLinkState;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;
import com.plectix.simulator.components.complex.contactMap.CContactMap;
import com.plectix.simulator.components.complex.influenceMap.AInfluenceMap;
import com.plectix.simulator.components.complex.influenceMap.InfluenceMapEdge;
import com.plectix.simulator.components.complex.subviews.base.AbstractAction;
import com.plectix.simulator.components.complex.subviews.base.AbstractionRule;
import com.plectix.simulator.interfaces.IObservablesConnectedComponent;

public class CInfluenceMapWithFuture extends AInfluenceMap {

	public CInfluenceMapWithFuture() {
		super();
	}

	public void initInfluenceMap(List<AbstractionRule> rules,
			CObservables observables, CContactMap contactMap,
			Map<Integer, CAbstractAgent> agentNameIdToAgent) {
		initObsRules(observables);

		for (AbstractionRule rule : rules) {
			List<MarkAgentWithFuture> activatedSites = new LinkedList<MarkAgentWithFuture>();
			List<MarkAgentWithFuture> inhibitedSites = new LinkedList<MarkAgentWithFuture>();
			fillingActivatedAndInhibitedSites(activatedSites, inhibitedSites,
					contactMap, rule, agentNameIdToAgent);
			int ruleId = rule.getRuleId();
			for (AbstractionRule ruleCheck : rules) {
				int ruleCheckId = ruleCheck.getRuleId();
				fillingMap(activationMap, activatedSites, ruleId, ruleCheckId,
						ruleCheck.getLHSActions());
				fillingMap(inhibitionMap, inhibitedSites, ruleId, ruleCheckId,
						ruleCheck.getLHSActions());
			}

			for (List<AbstractionRule> rulesCheck : obsRules.values())
				for (AbstractionRule ruleCheck : rulesCheck) {
					fillingMap(activationMapObs, activatedSites, ruleId,
							ruleCheck.getRuleId(), ruleCheck.getLHSActions());
					fillingMap(inhibitionMapObs, inhibitedSites, ruleId,
							ruleCheck.getRuleId(), ruleCheck.getLHSActions());
				}
		}
	}

	private void initObsRules(CObservables observables) {
		obsRules = new HashMap<Integer, List<AbstractionRule>>();
		for (IObservablesConnectedComponent cc : observables
				.getConnectedComponentList()) {
			AbstractionRule rule = new AbstractionRule(cc);
			List<AbstractionRule> list = obsRules.get(rule.getRuleId());
			if(list == null){
				list = new LinkedList<AbstractionRule>();
				obsRules.put(rule.getRuleId(), list);
			}
			list.add(rule);
		}
	}

	private void fillingActivatedAndInhibitedSites(
			List<MarkAgentWithFuture> activatedSites,
			List<MarkAgentWithFuture> inhibitedSites, CContactMap contactMap,
			AbstractionRule rule, Map<Integer, CAbstractAgent> agentNameIdToAgent) {
		for (AbstractAction action : rule.getActions()) {
			switch (action.getActionType()) {
			case ADD: {
				CAbstractAgent agent = action.getRightHandSideAgent();
				if (agent.getSitesMap().isEmpty()) {
					activatedSites.add(new MarkAgentWithFuture(agent, null,
							CActionType.ADD));
					break;
				}
				for (CAbstractSite site : agent.getSitesMap().values())
					activatedSites.add(new MarkAgentWithFuture(agent, site,
							CActionType.ADD));
				break;
			}
			case DELETE: {
				CAbstractAgent agent = action.getLeftHandSideAgent();
				if (agent.getSitesMap().isEmpty()) {
					inhibitedSites.add(new MarkAgentWithFuture(agent, null,
							CActionType.DELETE));
					break;
				}
				LinkedHashSet<Integer> sideEffect = new LinkedHashSet<Integer>();
				for (CAbstractSite siteLHS : agent.getSitesMap().values()) {
					if (!isLinkStateHasSideEffect(siteLHS)) {
						inhibitedSites.add(new MarkAgentWithFuture(agent,
								siteLHS, CActionType.DELETE));
					} else
						sideEffect.add(siteLHS.getNameId());
				}
				for (CAbstractSite clearSite : agentNameIdToAgent.get(
						agent.getNameId()).getSitesMap().values())
					if (!agent.getSitesMap().containsKey(clearSite.getNameId()))
						sideEffect.add(clearSite.getNameId());

				if (contactMap != null)
					findSideEffect(agent, sideEffect, contactMap,
							activatedSites, inhibitedSites, agentNameIdToAgent);
				break;
			}
			case TEST_AND_MODIFICATION: {
				CAbstractAgent agent = action.getLeftHandSideAgent();
				LinkedHashSet<Integer> sideEffect = new LinkedHashSet<Integer>();
				for (CAbstractSite siteLHS : agent.getSitesMap().values()) {
					CAbstractSite siteRHS = action.getRightHandSideAgent()
							.getSite(siteLHS.getNameId());
					if (!siteLHS.getInternalState().equalz(
							siteRHS.getInternalState())) {
						CAbstractSite modSite = siteLHS.clone();
						modSite.getLinkState().setFreeLinkState();
						inhibitedSites.add(new MarkAgentWithFuture(agent,
								modSite, CActionType.MODIFY));
						modSite = siteRHS.clone();
						modSite.getLinkState().setFreeLinkState();
						activatedSites.add(new MarkAgentWithFuture(agent,
								modSite, CActionType.MODIFY));
					}
					CAbstractLinkState linkStateLHS = siteLHS.getLinkState();
					CAbstractLinkState linkStateRHS = siteRHS.getLinkState();
					if (!linkStateLHS.equalz(linkStateRHS)) {
						if (isLinkStateHasSideEffect(siteLHS)) {
							sideEffect.add(siteLHS.getNameId());
						}
						activatedSites.add(new MarkAgentWithFuture(agent,
								siteRHS, CActionType.BOUND));
						inhibitedSites.add(new MarkAgentWithFuture(agent,
								siteLHS, CActionType.BOUND));
					}
				}
				if (contactMap != null)
					findSideEffect(agent, sideEffect, contactMap,
							activatedSites, inhibitedSites, agentNameIdToAgent);
				break;
			}
			}
		}
	}

	private void findSideEffect(CAbstractAgent agent,
			LinkedHashSet<Integer> sideEffect, CContactMap contactMap,
			List<MarkAgentWithFuture> activatedSites,
			List<MarkAgentWithFuture> inhibitedSites,
			Map<Integer, CAbstractAgent> agentNameIdToAgent) {
		CAbstractAgent clearAgent = agentNameIdToAgent.get(agent.getNameId());
		for (Integer sideEffectSiteId : sideEffect) {
			CAbstractSite clearSite = clearAgent.getSite(sideEffectSiteId);
			for (CAbstractAgent sideEffectAgent : contactMap
					.getSideEffect(clearSite)) {
				inhibitedSites.add(new MarkAgentWithFuture(sideEffectAgent,
						CActionType.BREAK));
				CAbstractAgent actAgent = new CAbstractAgent(sideEffectAgent);
				for (CAbstractSite actSite : actAgent.getSitesMap().values())
					actSite.getLinkState().setFreeLinkState();
				activatedSites.add(new MarkAgentWithFuture(actAgent,
						CActionType.BREAK));
			}
		}
	}

	private void fillingMap(Map<Integer, List<InfluenceMapEdge>> map,
			List<MarkAgentWithFuture> sites, int ruleId, int ruleCheckId,
			List<AbstractAction> actions) {
		if (isIntersection(sites, actions)) {
			List<InfluenceMapEdge> list = map.get(ruleId);
			if (list == null) {
				list = new LinkedList<InfluenceMapEdge>();
				map.put(ruleId, list);
			}
			list.add(new InfluenceMapEdge(ruleId, ruleCheckId));
		}
	}

	private boolean isIntersection(List<MarkAgentWithFuture> agents,
			List<AbstractAction> actions) {
		for (AbstractAction action : actions)
			for (MarkAgentWithFuture mAgent : agents) {
				if (mAgent.getAgent().getNameId() != action
						.getLeftHandSideAgent().getNameId())
					continue;
				CAbstractSite mSite = mAgent.getSite();
				if (mSite == null)
					return true;
				if(action.getLeftHandSideAgent().getSitesMap().isEmpty())
					return true;
				CAbstractSite aSite = action.getLeftHandSideAgent().getSite(
						mSite.getNameId());
				if (aSite == null)
					continue;
				if (mAgent.getType() == CActionType.MODIFY) {
					if (mSite.getInternalState().equalz(
							aSite.getInternalState()))
						return true;
				} else {
					CAbstractLinkState mLinkState = mSite.getLinkState();
					CAbstractLinkState aLinkState = aSite.getLinkState();

					if (aLinkState.compareLinkStates(mLinkState)) {
						if (aLinkState.getAgentNameID() == CSite.NO_INDEX)
							return true;
						if ((aLinkState.getAgentNameID() == mLinkState
								.getAgentNameID())
								&& (aLinkState.getLinkSiteNameID() == mLinkState
										.getLinkSiteNameID()))
							return true;
					}
				}
			}
		return false;
	}

	private boolean isLinkStateHasSideEffect(CAbstractSite siteLHS) {
		return siteLHS.getLinkState().getStatusLinkRank() == CLinkRank.BOUND_OR_FREE
				|| siteLHS.getLinkState().getStatusLinkRank() == CLinkRank.SEMI_LINK;
	}

}
