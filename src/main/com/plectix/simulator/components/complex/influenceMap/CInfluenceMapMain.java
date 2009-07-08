package com.plectix.simulator.components.complex.influenceMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractLinkState;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;
import com.plectix.simulator.components.complex.contactMap.CContactMap;
import com.plectix.simulator.components.complex.subviews.base.AbstractAction;
import com.plectix.simulator.components.complex.subviews.base.SubViewsRule;

public class CInfluenceMapMain {
	private Map<Integer, List<InfluenceMapEdge>> activationMap;
	private Map<Integer, List<InfluenceMapEdge>> inhibitionMap;

	public CInfluenceMapMain() {
		activationMap = new HashMap<Integer, List<InfluenceMapEdge>>();
		inhibitionMap = new HashMap<Integer, List<InfluenceMapEdge>>();
	}

	public void initInfluenceMap(List<SubViewsRule> rules,
			CContactMap contactMap,
			Map<Integer, CAbstractAgent> agentNameIdToAgent) {
		for (SubViewsRule rule : rules) {
			Map<Integer, MarkAgent> activatedAgents = new HashMap<Integer, MarkAgent>();
			Map<Integer, MarkAgent> inhibitedAgents = new HashMap<Integer, MarkAgent>();
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
			Map<Integer, MarkAgent> activatedAgents,
			Map<Integer, MarkAgent> inhibitedAgents, CContactMap contactMap,
			SubViewsRule rule, Map<Integer, CAbstractAgent> agentNameIdToAgent) {
		for (AbstractAction action : rule.getActions()) {
			switch (action.getActionType()) {
			case ADD: {
				CAbstractAgent agent = action.getRightHandSideAgent();
				MarkAgent aAgent = getMarkAgent(activatedAgents, agent);
				for (CAbstractSite site : agent.getSitesMap().values())
					aAgent.addMarkSite(new MarkSite(site, EAction.ALL));
				break;
			}
			case DELETE: {
				CAbstractAgent agent = action.getLeftHandSideAgent();
				MarkAgent aAgent = getMarkAgent(inhibitedAgents, agent);
				HashSet<Integer> sideEffect = new HashSet<Integer>();
				for (CAbstractSite siteLHS : agent.getSitesMap().values()) {
					if (!isLinkStateHasSideEffect(siteLHS))
						aAgent.addMarkSite(new MarkSite(siteLHS, EAction.ALL));
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
				HashSet<Integer> sideEffect = new HashSet<Integer>();
				for (CAbstractSite siteLHS : agent.getSitesMap().values()) {
					CAbstractSite siteRHS = action.getRightHandSideAgent()
							.getSite(siteLHS.getNameId());
					// if (!siteLHS.getInternalState().equalz(
					// siteRHS.getInternalState())) {
					if (siteLHS.getInternalState().getNameId() != CSite.NO_INDEX) {
						CAbstractSite modSite = siteLHS.clone();
						modSite.getLinkState().setFreeLinkState();
						MarkAgent mAgent = getMarkAgent(inhibitedAgents, agent);
						mAgent.addMarkSite(new MarkSite(modSite,
								EAction.INTERNAL_STATE));
						// inhibitedAgents.add(new MarkAgent(agent, modSite,
						// CActionType.MODIFY));
						modSite = siteRHS.clone();
						mAgent = getMarkAgent(activatedAgents, agent);
						mAgent.addMarkSite(new MarkSite(modSite,
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
							MarkAgent mAgent = getMarkAgent(activatedAgents,
									agent);
							mAgent.addMarkSite(new MarkSite(siteRHS,
									EAction.LINK_STATE));
							mAgent = getMarkAgent(inhibitedAgents, agent);
							mAgent.addMarkSite(new MarkSite(siteLHS,
									EAction.LINK_STATE));
						} else
							sideEffect.add(siteLHS.getNameId());
					} else {
						MarkAgent mAgent = getMarkAgent(activatedAgents, agent);
						mAgent.addMarkSite(new MarkSite(siteRHS,
								EAction.LINK_STATE));
						mAgent = getMarkAgent(inhibitedAgents, agent);
						mAgent.addMarkSite(new MarkSite(siteLHS,
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

	private static MarkAgent getMarkAgent(Map<Integer, MarkAgent> agents,
			CAbstractAgent agent) {
		MarkAgent aAgent = agents.get(agent.getNameId());
		if (aAgent == null) {
			aAgent = new MarkAgent(agent);
			agents.put(agent.getNameId(), aAgent);
		}
		return aAgent;
	}

	private void findSideEffect(CAbstractAgent agent,
			HashSet<Integer> sideEffect, CContactMap contactMap,
			Map<Integer, MarkAgent> activatedAgents,
			Map<Integer, MarkAgent> inhibitedAgents,
			Map<Integer, CAbstractAgent> agentNameIdToAgent) {
		CAbstractAgent clearAgent = agentNameIdToAgent.get(agent.getNameId());
		for (Integer sideEffectSiteId : sideEffect) {
			CAbstractSite clearSite = clearAgent.getSite(sideEffectSiteId);
			for (CAbstractAgent sideEffectAgent : contactMap
					.getSideEffect(clearSite)) {
				// inhibitedSites.add(new MarkAgent(sideEffectAgent,
				// CActionType.BREAK));
				MarkAgent mAgent = getMarkAgent(inhibitedAgents,
						sideEffectAgent);
				mAgent.addMarkSite(new MarkSite(sideEffectAgent,
						EAction.LINK_STATE));
				CAbstractAgent actAgent = new CAbstractAgent(sideEffectAgent);
				for (CAbstractSite actSite : actAgent.getSitesMap().values())
					actSite.getLinkState().setFreeLinkState();
				// activatedSites.add(new MarkAgent(actAgent,
				// CActionType.BREAK));
				mAgent = getMarkAgent(activatedAgents, actAgent);
				mAgent.addMarkSite(new MarkSite(actAgent, EAction.LINK_STATE));
			}
		}
	}

	private void fillingMap(Map<Integer, List<InfluenceMapEdge>> map,
			Map<Integer, MarkAgent> agents, int ruleId, int ruleCheckId,
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

	private boolean isIntersection(Map<Integer, MarkAgent> agents,
			SubViewsRule ruleCheck) {
		boolean isOneIntersection = false;
		for (AbstractAction action : ruleCheck.getLHSActions()) {
			CAbstractAgent checkAgent = action.getLeftHandSideAgent();
			MarkAgent mAgent = agents.get(checkAgent.getNameId());
			if (mAgent == null)
				continue;
			if (checkAgent.getSitesMap().isEmpty())
				return true;
			for (CAbstractSite checkSite : checkAgent.getSitesMap().values()) {
				boolean isEndLinkState = false;
				boolean hasInternalState = false;
				boolean isEndInternalState = false;

				boolean isEnd = false;
				List<MarkSite> sitesList = mAgent.getMarkSites(checkSite
						.getNameId());
				if (sitesList == null)
					continue;
				isOneIntersection = true;
				for (MarkSite mSite : sitesList) {
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
			MarkSite mSite) {
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
			MarkSite mSite) {
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
	
	public List<Integer> getActivationByRule(Integer ruleId){
		List<Integer> answer = new LinkedList<Integer>();
		List<InfluenceMapEdge> list = activationMap.get(ruleId);
		if (list==null){
			return null;
		}
		for(InfluenceMapEdge iE : activationMap.get(ruleId)){
			answer.add(iE.getToRule());
		}
		return answer;
	}

}
