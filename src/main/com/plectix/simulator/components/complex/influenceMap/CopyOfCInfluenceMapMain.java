//package com.plectix.simulator.components.complex.influenceMap;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//import com.plectix.simulator.action.CActionType;
//import com.plectix.simulator.components.CLinkRank;
//import com.plectix.simulator.components.CSite;
//import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
//import com.plectix.simulator.components.complex.abstracting.CAbstractLinkState;
//import com.plectix.simulator.components.complex.abstracting.CAbstractSite;
//import com.plectix.simulator.components.complex.contactMap.CContactMap;
//import com.plectix.simulator.components.complex.subviews.base.AbstractAction;
//import com.plectix.simulator.components.complex.subviews.base.SubViewsRule;
//
//public class CopyOfCInfluenceMapMain {
//	private enum ETypeMap {
//		ACTIVATION_MAP, INHIBITION_MAP
//	}
//
//	private Map<Integer, List<InfluenceMapEdge>> activationMap;
//	private Map<Integer, List<InfluenceMapEdge>> inhibitionMap;
//
//	public CopyOfCInfluenceMapMain() {
//		activationMap = new HashMap<Integer, List<InfluenceMapEdge>>();
//		inhibitionMap = new HashMap<Integer, List<InfluenceMapEdge>>();
//	}
//
//	public void initInfluenceMap(List<SubViewsRule> rules,
//			CContactMap contactMap,
//			Map<Integer, CAbstractAgent> agentNameIdToAgent) {
//		for (SubViewsRule rule : rules) {
//			List<MarkAgent> activatedAgents = new LinkedList<MarkAgent>();
//			List<MarkAgent> inhibitedAgents = new LinkedList<MarkAgent>();
//			fillingActivatedAndInhibitedSites(activatedAgents, inhibitedAgents,
//					contactMap, rule, agentNameIdToAgent);
//			for (SubViewsRule ruleCheck : rules) {
//				int ruleId = rule.getRuleId();
//				int ruleCheckId = ruleCheck.getRuleId();
//				fillingMap(activationMap, activatedAgents, ruleId, ruleCheckId,
//						ruleCheck, ETypeMap.ACTIVATION_MAP);
//				fillingMap(inhibitionMap, inhibitedAgents, ruleId, ruleCheckId,
//						ruleCheck, ETypeMap.INHIBITION_MAP);
//			}
//		}
//	}
//
//	private void fillingActivatedAndInhibitedSites(
//			List<MarkAgent> activatedSites, List<MarkAgent> inhibitedSites,
//			CContactMap contactMap, SubViewsRule rule,
//			Map<Integer, CAbstractAgent> agentNameIdToAgent) {
//		for (AbstractAction action : rule.getActions()) {
//			switch (action.getActionType()) {
//			case ADD: {
//				CAbstractAgent agent = action.getRightHandSideAgent();
//				if (agent.getSitesMap().isEmpty()) {
//					activatedSites.add(new MarkAgent(agent, null,
//							CActionType.ADD));
//					break;
//				}
//				for (CAbstractSite site : agent.getSitesMap().values())
//					activatedSites.add(new MarkAgent(agent, site,
//							CActionType.ADD));
//				break;
//			}
//			case DELETE: {
//				CAbstractAgent agent = action.getLeftHandSideAgent();
//				if (agent.getSitesMap().isEmpty()) {
//					inhibitedSites.add(new MarkAgent(agent, null,
//							CActionType.DELETE));
//					break;
//				}
//				HashSet<Integer> sideEffect = new HashSet<Integer>();
//				for (CAbstractSite siteLHS : agent.getSitesMap().values()) {
//					if (!isLinkStateHasSideEffect(siteLHS)) {
//						inhibitedSites.add(new MarkAgent(agent, siteLHS,
//								CActionType.DELETE));
//					} else
//						sideEffect.add(siteLHS.getNameId());
//				}
//				for (CAbstractSite clearSite : agentNameIdToAgent.get(
//						agent.getNameId()).getSitesMap().values())
//					if (!agent.getSitesMap().containsKey(clearSite.getNameId()))
//						sideEffect.add(clearSite.getNameId());
//
//				findSideEffect(agent, sideEffect, contactMap, activatedSites,
//						inhibitedSites, agentNameIdToAgent);
//				break;
//			}
//			case TEST_AND_MODIFICATION: {
//				CAbstractAgent agent = action.getLeftHandSideAgent();
//				HashSet<Integer> sideEffect = new HashSet<Integer>();
//				for (CAbstractSite siteLHS : agent.getSitesMap().values()) {
//					CAbstractSite siteRHS = action.getRightHandSideAgent()
//							.getSite(siteLHS.getNameId());
//					if (!siteLHS.getInternalState().equalz(
//							siteRHS.getInternalState())) {
//						CAbstractSite modSite = siteLHS.clone();
//						modSite.getLinkState().setFreeLinkState();
//						inhibitedSites.add(new MarkAgent(agent, modSite,
//								CActionType.MODIFY));
//						modSite = siteRHS.clone();
//						modSite.getLinkState().setFreeLinkState();
//						activatedSites.add(new MarkAgent(agent, modSite,
//								CActionType.MODIFY));
//					}
//					CAbstractLinkState linkStateLHS = siteLHS.getLinkState();
//					CAbstractLinkState linkStateRHS = siteRHS.getLinkState();
//					if (!linkStateLHS.equalz(linkStateRHS)) {
//						if (!isLinkStateHasSideEffect(siteLHS)) {
//							activatedSites.add(new MarkAgent(agent, siteRHS,
//									CActionType.BOUND));
//							inhibitedSites.add(new MarkAgent(agent, siteLHS,
//									CActionType.BOUND));
//						} else
//							sideEffect.add(siteLHS.getNameId());
//					} else {
//						activatedSites.add(new MarkAgent(agent, siteRHS,
//								CActionType.NONE));
//						inhibitedSites.add(new MarkAgent(agent, siteLHS,
//								CActionType.NONE));
//					}
//				}
//				findSideEffect(agent, sideEffect, contactMap, activatedSites,
//						inhibitedSites, agentNameIdToAgent);
//				break;
//			}
//			}
//		}
//	}
//
//	private void findSideEffect(CAbstractAgent agent,
//			HashSet<Integer> sideEffect, CContactMap contactMap,
//			List<MarkAgent> activatedSites, List<MarkAgent> inhibitedSites,
//			Map<Integer, CAbstractAgent> agentNameIdToAgent) {
//		CAbstractAgent clearAgent = agentNameIdToAgent.get(agent.getNameId());
//		for (Integer sideEffectSiteId : sideEffect) {
//			CAbstractSite clearSite = clearAgent.getSite(sideEffectSiteId);
//			for (CAbstractAgent sideEffectAgent : contactMap
//					.getSideEffect(clearSite)) {
//				inhibitedSites.add(new MarkAgent(sideEffectAgent,
//						CActionType.BREAK));
//				CAbstractAgent actAgent = new CAbstractAgent(sideEffectAgent);
//				for (CAbstractSite actSite : actAgent.getSitesMap().values())
//					actSite.getLinkState().setFreeLinkState();
//				activatedSites.add(new MarkAgent(actAgent, CActionType.BREAK));
//			}
//		}
//	}
//
//	private void fillingMap(Map<Integer, List<InfluenceMapEdge>> map,
//			List<MarkAgent> agents, int ruleId, int ruleCheckId,
//			SubViewsRule ruleCheck, ETypeMap typeMap) {
//		if (isIntersection(agents, ruleCheck, typeMap)) {
//			List<InfluenceMapEdge> list = map.get(ruleId);
//			if (list == null) {
//				list = new LinkedList<InfluenceMapEdge>();
//				map.put(ruleId, list);
//			}
//			list.add(new InfluenceMapEdge(ruleId, ruleCheckId));
//		}
//	}
//
//	private boolean isIntersection(List<MarkAgent> agents,
//			SubViewsRule ruleCheck, ETypeMap typeMap) {
//		for (AbstractAction action : ruleCheck.getLHSActions()) {
//			// for (MarkAgent mAgent : agents) {
//			boolean isTrue = false;
//			for (MarkAgent mAgent : getNeedAgents(action, agents)) {
//				if (mAgent.getAgent().getNameId() != action
//						.getLeftHandSideAgent().getNameId())
//					continue;
//				CAbstractSite mSite = mAgent.getSite();
//				if (mSite == null)
//					return true;
//				CAbstractSite aSite = action.getLeftHandSideAgent().getSite(
//						mSite.getNameId());
//				if (aSite == null)
//					continue;
//				if (mAgent.getType() == CActionType.MODIFY) {
//					if (mSite.getInternalState().equalz(
//							aSite.getInternalState()))
//						isTrue = true;
//					// return true;
//					else
//						if(aSite.getInternalState().getNameId() != CSite.NO_INDEX)
//							break;
//				} else {
//					CAbstractLinkState mLinkState = mSite.getLinkState();
//					CAbstractLinkState aLinkState = aSite.getLinkState();
//
//					if (aLinkState.compareLinkStates(mLinkState)) {
//						if (aLinkState.getAgentNameID() == CSite.NO_INDEX){
//							isTrue = true;
//	//						return true;
//							continue;
//						}
//						if ((aLinkState.getAgentNameID() == mLinkState
//								.getAgentNameID())
//								&& (aLinkState.getLinkSiteNameID() == mLinkState
//										.getLinkSiteNameID())){
//							isTrue = true;
////							return true;
//						}
//					} else break;
//				}
//			}
//			if(isTrue)
//				return true;
//		}
//		return false;
//	}
//
//	private List<MarkAgent> getNeedAgents(AbstractAction action,
//			List<MarkAgent> agents) {
//		List<MarkAgent> outList = new LinkedList<MarkAgent>();
//		for (MarkAgent mAgent : agents)
//			if (mAgent.getAgent().getNameId() == action.getLeftHandSideAgent()
//					.getNameId())
//				outList.add(mAgent);
//		return outList;
//	}
//
//	private boolean isLinkStateHasSideEffect(CAbstractSite siteLHS) {
//		return siteLHS.getLinkState().getStatusLinkRank() == CLinkRank.BOUND_OR_FREE
//				|| siteLHS.getLinkState().getStatusLinkRank() == CLinkRank.SEMI_LINK;
//	}
//
//}
