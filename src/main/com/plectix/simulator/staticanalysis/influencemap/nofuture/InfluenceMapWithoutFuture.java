package com.plectix.simulator.staticanalysis.influencemap.nofuture;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.staticanalysis.LinkRank;
import com.plectix.simulator.staticanalysis.Observables;
import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.abstracting.AbstractLinkState;
import com.plectix.simulator.staticanalysis.abstracting.AbstractSite;
import com.plectix.simulator.staticanalysis.contactmap.ContactMap;
import com.plectix.simulator.staticanalysis.influencemap.InfluenceMap;
import com.plectix.simulator.staticanalysis.influencemap.InfluenceMapEdge;
import com.plectix.simulator.staticanalysis.subviews.base.AbstractAction;
import com.plectix.simulator.staticanalysis.subviews.base.AbstractionRule;
import com.plectix.simulator.util.NameDictionary;

public final class InfluenceMapWithoutFuture extends InfluenceMap{
	
	static Map<String, MarkAgentWithoutFuture> activatedAgents = new LinkedHashMap<String, MarkAgentWithoutFuture>();
	static Map<String, MarkAgentWithoutFuture> inhibitedAgents = new LinkedHashMap<String, MarkAgentWithoutFuture>();
	
	public InfluenceMapWithoutFuture() {
		super();
	}

	@Override
	public final void initInfluenceMap(List<AbstractionRule> rules, Observables observables,
			ContactMap contactMap,
			Map<String, AbstractAgent> agentNameToAgent) {
		for (AbstractionRule rule : rules) {
			fillingActivatedAndInhibitedSites(contactMap, rule, agentNameToAgent);
			for (AbstractionRule ruleCheck : rules) {
				int ruleId = rule.getRuleId();
				int ruleCheckId = ruleCheck.getRuleId();
				initializeMap(this.getActivationMap(), activatedAgents, ruleId, ruleCheckId,
						ruleCheck);
				initializeMap(this.getInhibitionMap(), inhibitedAgents, ruleId, ruleCheckId,
						ruleCheck);
			}
		}
	}

	private static final void fillingActivatedAndInhibitedSites(ContactMap contactMap,
			AbstractionRule rule, Map<String, AbstractAgent> agentNameToAgent) {
		for (AbstractAction action : rule.getActions()) {
			switch (action.getActionType()) {
			case ADD: {
				AbstractAgent agent = action.getRightHandSideAgent();
				MarkAgentWithoutFuture aAgent = getMarkAgent(activatedAgents, agent);
				for (AbstractSite site : agent.getSitesMap().values())
					aAgent.addMarkSite(new MarkSiteWithoutFuture(site, Quark.EXISTENCE_QUARK));
				break;
			}
			case DELETE: {
				AbstractAgent agent = action.getLeftHandSideAgent();
				MarkAgentWithoutFuture aAgent = getMarkAgent(inhibitedAgents, agent);
				LinkedHashSet<String > sideEffect = new LinkedHashSet<String>();
				for (AbstractSite siteLHS : agent.getSitesMap().values()) {
					if (!isLinkStateHasSideEffect(siteLHS))
						aAgent.addMarkSite(new MarkSiteWithoutFuture(siteLHS, Quark.EXISTENCE_QUARK));
					else
						sideEffect.add(siteLHS.getName());
				}
				for (AbstractSite clearSite : agentNameToAgent.get(
						agent.getName()).getSitesMap().values())
					if (!agent.getSitesMap().containsKey(clearSite.getName()))
						sideEffect.add(clearSite.getName());

				if (contactMap != null)
					findSideEffect(agent, sideEffect, contactMap,
							activatedAgents, inhibitedAgents,
							agentNameToAgent);
				break;
			}
			case TEST_AND_MODIFICATION: {
				AbstractAgent agent = action.getLeftHandSideAgent();
				LinkedHashSet<String> sideEffect = new LinkedHashSet<String>();
				for (AbstractSite siteLHS : agent.getSitesMap().values()) {
					AbstractSite siteRHS = action.getRightHandSideAgent()
							.getSiteByName(siteLHS.getName());
					// if (!siteLHS.getInternalState().equalz(
					// siteRHS.getInternalState())) {
					if (!siteLHS.getInternalState().hasDefaultName()) {
						AbstractSite modSite = siteLHS.clone();
						modSite.getLinkState().setFreeLinkState();
						MarkAgentWithoutFuture mAgent = getMarkAgent(inhibitedAgents, agent);
						mAgent.addMarkSite(new MarkSiteWithoutFuture(modSite,
								Quark.INTERNAL_STATE_QUARK));
						// inhibitedAgents.add(new MarkAgent(agent, modSite,
						// CActionType.MODIFY));
						modSite = siteRHS.clone();
						mAgent = getMarkAgent(activatedAgents, agent);
						mAgent.addMarkSite(new MarkSiteWithoutFuture(modSite,
								Quark.INTERNAL_STATE_QUARK));
						// modSite.getLinkState().setFreeLinkState();
						// activatedAgents.add(new MarkAgent(agent, modSite,
						// CActionType.MODIFY));
					}
					AbstractLinkState linkStateLHS = siteLHS.getLinkState();
					AbstractLinkState linkStateRHS = siteRHS.getLinkState();
					if (!linkStateLHS.equalz(linkStateRHS)) {
						if (!isLinkStateHasSideEffect(siteLHS)) {
							// activatedAgents.add(new MarkAgent(agent, siteRHS,
							// CActionType.BOUND));
							// inhibitedAgents.add(new MarkAgent(agent, siteLHS,
							// CActionType.BOUND));
							MarkAgentWithoutFuture mAgent = getMarkAgent(activatedAgents,
									agent);
							mAgent.addMarkSite(new MarkSiteWithoutFuture(siteRHS,
									Quark.LINK_STATE_QUARK));
							mAgent = getMarkAgent(inhibitedAgents, agent);
							mAgent.addMarkSite(new MarkSiteWithoutFuture(siteLHS,
									Quark.LINK_STATE_QUARK));
						} else
							sideEffect.add(siteLHS.getName());
					} else {
						MarkAgentWithoutFuture mAgent = getMarkAgent(activatedAgents, agent);
						mAgent.addMarkSite(new MarkSiteWithoutFuture(siteRHS,
								Quark.LINK_STATE_QUARK));
						mAgent = getMarkAgent(inhibitedAgents, agent);
						mAgent.addMarkSite(new MarkSiteWithoutFuture(siteLHS,
								Quark.LINK_STATE_QUARK));
					}

				}
				if (contactMap != null)
					findSideEffect(agent, sideEffect, contactMap,
							activatedAgents, inhibitedAgents,
							agentNameToAgent);
				break;
			}
			}
		}
	}

	private static final MarkAgentWithoutFuture getMarkAgent(Map<String, MarkAgentWithoutFuture> agents,
			AbstractAgent agent) {
		MarkAgentWithoutFuture aAgent = agents.get(agent.getName());
		if (aAgent == null) {
			aAgent = new MarkAgentWithoutFuture();
			agents.put(agent.getName(), aAgent);
		}
		return aAgent;
	}

	private static final void findSideEffect(AbstractAgent agent,
			LinkedHashSet<String> sideEffect, ContactMap contactMap,
			Map<String, MarkAgentWithoutFuture> activatedAgents,
			Map<String, MarkAgentWithoutFuture> inhibitedAgents,
			Map<String, AbstractAgent> agentNameToAgent) {
		AbstractAgent clearAgent = agentNameToAgent.get(agent.getName());
		for (String sideEffectSiteName : sideEffect) {
			AbstractSite clearSite = clearAgent.getSiteByName(sideEffectSiteName);
			for (AbstractAgent sideEffectAgent : contactMap
					.getSideEffect(clearSite)) {
				// inhibitedSites.add(new MarkAgent(sideEffectAgent,
				// CActionType.BREAK));
				MarkAgentWithoutFuture mAgent = getMarkAgent(inhibitedAgents,
						sideEffectAgent);
				mAgent.addMarkSite(new MarkSiteWithoutFuture(sideEffectAgent));
				AbstractAgent actAgent = new AbstractAgent(sideEffectAgent);
				for (AbstractSite actSite : actAgent.getSitesMap().values())
					actSite.getLinkState().setFreeLinkState();
				// activatedSites.add(new MarkAgent(actAgent,
				// CActionType.BREAK));
				mAgent = getMarkAgent(activatedAgents, actAgent);
				mAgent.addMarkSite(new MarkSiteWithoutFuture(actAgent));
			}
		}
	}

	private static final void initializeMap(Map<Integer, List<InfluenceMapEdge>> map,
			Map<String, MarkAgentWithoutFuture> agents, int ruleId, int ruleCheckId,
			AbstractionRule ruleCheck) {
		if (isIntersection(agents, ruleCheck)) {
			List<InfluenceMapEdge> list = map.get(ruleId);
			if (list == null) {
				list = new LinkedList<InfluenceMapEdge>();
				map.put(ruleId, list);
			}
			list.add(new InfluenceMapEdge(ruleId, ruleCheckId));
		}
	}

	private static final boolean isIntersection(Map<String, MarkAgentWithoutFuture> agents,
			AbstractionRule ruleCheck) {
		boolean isOneIntersection = false;
		for (AbstractAction action : ruleCheck.getLeftHandSideActions()) {
			AbstractAgent checkAgent = action.getLeftHandSideAgent();
			MarkAgentWithoutFuture mAgent = agents.get(checkAgent.getName());
			if (mAgent == null)
				continue;
			if (checkAgent.getSitesMap().isEmpty())
				return true;
			for (AbstractSite checkSite : checkAgent.getSitesMap().values()) {
				boolean isEndLinkState = false;
				boolean hasInternalState = false;
				boolean isEndInternalState = false;

				boolean isEnd = false;
				List<MarkSiteWithoutFuture> sitesList = mAgent.getMarkSites(checkSite
						.getName());
				if (sitesList == null)
					continue;
				isOneIntersection = true;
				for (MarkSiteWithoutFuture mSite : sitesList) {
					switch (mSite.getType()) {
					case INTERNAL_STATE_QUARK: {
						hasInternalState = true;
						if (isEndInternalState)
							break;
						isEndInternalState = intersectionInternalState(
								checkSite, mSite);
						break;
					}
					case LINK_STATE_QUARK: {
						if (isEndLinkState)
							break;
						isEndLinkState = intersectionLinkState(checkSite, mSite);
						break;
					}
					case EXISTENCE_QUARK: {
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

	private static final boolean intersectionLinkState(AbstractSite checkSite,
			MarkSiteWithoutFuture mSite) {
		AbstractLinkState checkLS = checkSite.getLinkState();
		AbstractLinkState mLS = mSite.getSite().getLinkState();
		if (checkLS.getStatusLinkRank() == LinkRank.BOUND_OR_FREE) {
			return true;
		}
		if ((checkLS.getStatusLinkRank() == LinkRank.SEMI_LINK && mLS
				.getStatusLinkRank() != LinkRank.FREE)
				|| (checkLS.getStatusLinkRank() == LinkRank.FREE && mLS
						.getStatusLinkRank() == LinkRank.FREE)) {
			return true;
		}
		if (checkLS.getStatusLinkRank() == LinkRank.BOUND
				&& mLS.getStatusLinkRank() == LinkRank.BOUND) {
			if (checkLS.getAgentName() == mLS.getAgentName()
					&& checkLS.getConnectedSiteName().equals(mLS.getConnectedSiteName()))
				return true;
		}
		return false;
	}

	private static final boolean intersectionInternalState(AbstractSite checkSite,
			MarkSiteWithoutFuture mSite) {
		String checkName = checkSite.getInternalState().getName();
		String mName = mSite.getSite().getInternalState().getName();
		if (NameDictionary.isDefaultInternalStateName(checkName)) {
			return true;
		}
		if (checkName.equals(mName))
			return true;
		return false;
	}

	private static final boolean isLinkStateHasSideEffect(AbstractSite siteLHS) {
		return siteLHS.getLinkState().getStatusLinkRank() == LinkRank.BOUND_OR_FREE
				|| siteLHS.getLinkState().getStatusLinkRank() == LinkRank.SEMI_LINK;
	}
	
}
