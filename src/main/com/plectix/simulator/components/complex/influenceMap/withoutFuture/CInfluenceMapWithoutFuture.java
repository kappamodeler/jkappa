package com.plectix.simulator.components.complex.influenceMap.withoutFuture;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import com.plectix.simulator.components.complex.subviews.base.SubViewsRule;

public class CInfluenceMapWithoutFuture extends AInfluenceMap{
	
	public CInfluenceMapWithoutFuture() {
		super();
	}

	public void initInfluenceMap(List<SubViewsRule> rules, CObservables observables,
			CContactMap contactMap,
			Map<Integer, CAbstractAgent> agentNameIdToAgent) {
		for (SubViewsRule rule : rules) {
			Map<Integer, MarkAgentWithoutFuture> activatedAgents = new LinkedHashMap<Integer, MarkAgentWithoutFuture>();
			Map<Integer, MarkAgentWithoutFuture> inhibitedAgents = new LinkedHashMap<Integer, MarkAgentWithoutFuture>();
			fillingActivatedAndInhibitedSites(activatedAgents, inhibitedAgents,
					contactMap, rule, agentNameIdToAgent);
			for (SubViewsRule ruleCheck : rules) {
				int ruleId = rule.getRuleId();
				int ruleCheckId = ruleCheck.getRuleId();
				fillingMap(activationMap, activatedAgents, ruleId, ruleCheckId,
						ruleCheck);
				fillingMap(inhibitionMap, inhibitedAgents, ruleId, ruleCheckId,
						ruleCheck);
			}
		}
	}

	private void fillingActivatedAndInhibitedSites(
			Map<Integer, MarkAgentWithoutFuture> activatedAgents,
			Map<Integer, MarkAgentWithoutFuture> inhibitedAgents, CContactMap contactMap,
			SubViewsRule rule, Map<Integer, CAbstractAgent> agentNameIdToAgent) {
		for (AbstractAction action : rule.getActions()) {
			switch (action.getActionType()) {
			case ADD: {
				CAbstractAgent agent = action.getRightHandSideAgent();
				MarkAgentWithoutFuture aAgent = getMarkAgent(activatedAgents, agent);
				for (CAbstractSite site : agent.getSitesMap().values())
					aAgent.addMarkSite(new MarkSiteWithoutFuture(site, EAction.ALL));
				break;
			}
			case DELETE: {
				CAbstractAgent agent = action.getLeftHandSideAgent();
				MarkAgentWithoutFuture aAgent = getMarkAgent(inhibitedAgents, agent);
				LinkedHashSet<Integer> sideEffect = new LinkedHashSet<Integer>();
				for (CAbstractSite siteLHS : agent.getSitesMap().values()) {
					if (!isLinkStateHasSideEffect(siteLHS))
						aAgent.addMarkSite(new MarkSiteWithoutFuture(siteLHS, EAction.ALL));
					else
						sideEffect.add(siteLHS.getNameId());
				}
				for (CAbstractSite clearSite : agentNameIdToAgent.get(
						agent.getNameId()).getSitesMap().values())
					if (!agent.getSitesMap().containsKey(clearSite.getNameId()))
						sideEffect.add(clearSite.getNameId());

				if (contactMap != null)
					findSideEffect(agent, sideEffect, contactMap,
							activatedAgents, inhibitedAgents,
							agentNameIdToAgent);
				break;
			}
			case TEST_AND_MODIFICATION: {
				CAbstractAgent agent = action.getLeftHandSideAgent();
				LinkedHashSet<Integer> sideEffect = new LinkedHashSet<Integer>();
				for (CAbstractSite siteLHS : agent.getSitesMap().values()) {
					CAbstractSite siteRHS = action.getRightHandSideAgent()
							.getSite(siteLHS.getNameId());
					// if (!siteLHS.getInternalState().equalz(
					// siteRHS.getInternalState())) {
					if (siteLHS.getInternalState().getNameId() != CSite.NO_INDEX) {
						CAbstractSite modSite = siteLHS.clone();
						modSite.getLinkState().setFreeLinkState();
						MarkAgentWithoutFuture mAgent = getMarkAgent(inhibitedAgents, agent);
						mAgent.addMarkSite(new MarkSiteWithoutFuture(modSite,
								EAction.INTERNAL_STATE));
						// inhibitedAgents.add(new MarkAgent(agent, modSite,
						// CActionType.MODIFY));
						modSite = siteRHS.clone();
						mAgent = getMarkAgent(activatedAgents, agent);
						mAgent.addMarkSite(new MarkSiteWithoutFuture(modSite,
								EAction.INTERNAL_STATE));
						// modSite.getLinkState().setFreeLinkState();
						// activatedAgents.add(new MarkAgent(agent, modSite,
						// CActionType.MODIFY));
					}
					CAbstractLinkState linkStateLHS = siteLHS.getLinkState();
					CAbstractLinkState linkStateRHS = siteRHS.getLinkState();
					if (!linkStateLHS.equalz(linkStateRHS)) {
						if (!isLinkStateHasSideEffect(siteLHS)) {
							// activatedAgents.add(new MarkAgent(agent, siteRHS,
							// CActionType.BOUND));
							// inhibitedAgents.add(new MarkAgent(agent, siteLHS,
							// CActionType.BOUND));
							MarkAgentWithoutFuture mAgent = getMarkAgent(activatedAgents,
									agent);
							mAgent.addMarkSite(new MarkSiteWithoutFuture(siteRHS,
									EAction.LINK_STATE));
							mAgent = getMarkAgent(inhibitedAgents, agent);
							mAgent.addMarkSite(new MarkSiteWithoutFuture(siteLHS,
									EAction.LINK_STATE));
						} else
							sideEffect.add(siteLHS.getNameId());
					} else {
						MarkAgentWithoutFuture mAgent = getMarkAgent(activatedAgents, agent);
						mAgent.addMarkSite(new MarkSiteWithoutFuture(siteRHS,
								EAction.LINK_STATE));
						mAgent = getMarkAgent(inhibitedAgents, agent);
						mAgent.addMarkSite(new MarkSiteWithoutFuture(siteLHS,
								EAction.LINK_STATE));
					}

				}
				if (contactMap != null)
					findSideEffect(agent, sideEffect, contactMap,
							activatedAgents, inhibitedAgents,
							agentNameIdToAgent);
				break;
			}
			}
		}
	}

	private static MarkAgentWithoutFuture getMarkAgent(Map<Integer, MarkAgentWithoutFuture> agents,
			CAbstractAgent agent) {
		MarkAgentWithoutFuture aAgent = agents.get(agent.getNameId());
		if (aAgent == null) {
			aAgent = new MarkAgentWithoutFuture(agent);
			agents.put(agent.getNameId(), aAgent);
		}
		return aAgent;
	}

	private void findSideEffect(CAbstractAgent agent,
			LinkedHashSet<Integer> sideEffect, CContactMap contactMap,
			Map<Integer, MarkAgentWithoutFuture> activatedAgents,
			Map<Integer, MarkAgentWithoutFuture> inhibitedAgents,
			Map<Integer, CAbstractAgent> agentNameIdToAgent) {
		CAbstractAgent clearAgent = agentNameIdToAgent.get(agent.getNameId());
		for (Integer sideEffectSiteId : sideEffect) {
			CAbstractSite clearSite = clearAgent.getSite(sideEffectSiteId);
			for (CAbstractAgent sideEffectAgent : contactMap
					.getSideEffect(clearSite)) {
				// inhibitedSites.add(new MarkAgent(sideEffectAgent,
				// CActionType.BREAK));
				MarkAgentWithoutFuture mAgent = getMarkAgent(inhibitedAgents,
						sideEffectAgent);
				mAgent.addMarkSite(new MarkSiteWithoutFuture(sideEffectAgent,
						EAction.LINK_STATE));
				CAbstractAgent actAgent = new CAbstractAgent(sideEffectAgent);
				for (CAbstractSite actSite : actAgent.getSitesMap().values())
					actSite.getLinkState().setFreeLinkState();
				// activatedSites.add(new MarkAgent(actAgent,
				// CActionType.BREAK));
				mAgent = getMarkAgent(activatedAgents, actAgent);
				mAgent.addMarkSite(new MarkSiteWithoutFuture(actAgent, EAction.LINK_STATE));
			}
		}
	}

	private void fillingMap(Map<Integer, List<InfluenceMapEdge>> map,
			Map<Integer, MarkAgentWithoutFuture> agents, int ruleId, int ruleCheckId,
			SubViewsRule ruleCheck) {
		if (isIntersection(agents, ruleCheck)) {
			List<InfluenceMapEdge> list = map.get(ruleId);
			if (list == null) {
				list = new LinkedList<InfluenceMapEdge>();
				map.put(ruleId, list);
			}
			list.add(new InfluenceMapEdge(ruleId, ruleCheckId));
		}
	}

	private boolean isIntersection(Map<Integer, MarkAgentWithoutFuture> agents,
			SubViewsRule ruleCheck) {
		boolean isOneIntersection = false;
		for (AbstractAction action : ruleCheck.getLHSActions()) {
			CAbstractAgent checkAgent = action.getLeftHandSideAgent();
			MarkAgentWithoutFuture mAgent = agents.get(checkAgent.getNameId());
			if (mAgent == null)
				continue;
			if (checkAgent.getSitesMap().isEmpty())
				return true;
			for (CAbstractSite checkSite : checkAgent.getSitesMap().values()) {
				boolean isEndLinkState = false;
				boolean hasInternalState = false;
				boolean isEndInternalState = false;

				boolean isEnd = false;
				List<MarkSiteWithoutFuture> sitesList = mAgent.getMarkSites(checkSite
						.getNameId());
				if (sitesList == null)
					continue;
				isOneIntersection = true;
				for (MarkSiteWithoutFuture mSite : sitesList) {
					switch (mSite.getType()) {
					case INTERNAL_STATE: {
						hasInternalState = true;
						if (isEndInternalState)
							break;
						isEndInternalState = intersectionInternalState(
								checkSite, mSite);
						break;
					}
					case LINK_STATE: {
						if (isEndLinkState)
							break;
						isEndLinkState = intersectionLinkState(checkSite, mSite);
						break;
					}
					case ALL: {
						if (isEndInternalState && isEndLinkState)
							break;
						if (intersectionInternalState(checkSite, mSite)
								&& intersectionLinkState(checkSite, mSite)) {
							isEndInternalState = true;
							isEndLinkState = true;
						}
						break;
					}
					}
					if (((hasInternalState && isEndInternalState) || !hasInternalState)
							&& isEndLinkState) {
						isEnd = true;
						break;
					}
				}
				if (!isEnd)
					return false;
			}
		}
		if (isOneIntersection)
			return true;
		return false;
	}

	private static boolean intersectionLinkState(CAbstractSite checkSite,
			MarkSiteWithoutFuture mSite) {
		CAbstractLinkState checkLS = checkSite.getLinkState();
		CAbstractLinkState mLS = mSite.getSite().getLinkState();
		if (checkLS.getStatusLinkRank() == CLinkRank.BOUND_OR_FREE) {
			return true;
		}
		if ((checkLS.getStatusLinkRank() == CLinkRank.SEMI_LINK && mLS
				.getStatusLinkRank() != CLinkRank.FREE)
				|| (checkLS.getStatusLinkRank() == CLinkRank.FREE && mLS
						.getStatusLinkRank() == CLinkRank.FREE)) {
			return true;
		}
		if (checkLS.getStatusLinkRank() == CLinkRank.BOUND
				&& mLS.getStatusLinkRank() == CLinkRank.BOUND) {
			if (checkLS.getAgentNameID() == mLS.getAgentNameID()
					&& checkLS.getLinkSiteNameID() == mLS.getLinkSiteNameID())
				return true;
		}
		return false;
	}

	private static boolean intersectionInternalState(CAbstractSite checkSite,
			MarkSiteWithoutFuture mSite) {
		int checkNameId = checkSite.getInternalState().getNameId();
		int mNameId = mSite.getSite().getInternalState().getNameId();
		if (checkNameId == CSite.NO_INDEX) {
			return true;
		}
		if (checkNameId == mNameId)
			return true;
		return false;
	}

	private boolean isLinkStateHasSideEffect(CAbstractSite siteLHS) {
		return siteLHS.getLinkState().getStatusLinkRank() == CLinkRank.BOUND_OR_FREE
				|| siteLHS.getLinkState().getStatusLinkRank() == CLinkRank.SEMI_LINK;
	}
	
}
