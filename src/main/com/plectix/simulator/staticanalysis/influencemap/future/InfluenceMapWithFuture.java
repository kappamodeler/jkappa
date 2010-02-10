package com.plectix.simulator.staticanalysis.influencemap.future;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.interfaces.ObservableConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.action.ActionType;
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

public final class InfluenceMapWithFuture extends InfluenceMap {

	public InfluenceMapWithFuture() {
		super();
	}

	@Override
	public final void initInfluenceMap(List<AbstractionRule> rules,
			Observables observables, ContactMap contactMap,
			Map<String, AbstractAgent> agentNameToAgent) {
		initObsRules(observables);

		for (AbstractionRule rule : rules) {
			List<MarkAgentWithFuture> activatedSites = new LinkedList<MarkAgentWithFuture>();
			List<MarkAgentWithFuture> inhibitedSites = new LinkedList<MarkAgentWithFuture>();
			fillingActivatedAndInhibitedSites(activatedSites, inhibitedSites,
					contactMap, rule, agentNameToAgent);
			int ruleId = rule.getRuleId();
			for (AbstractionRule ruleCheck : rules) {
				int ruleCheckId = ruleCheck.getRuleId();
				fillingMap(this.getActivationMap(), activatedSites, ruleId,
						ruleCheckId, ruleCheck.getLeftHandSideActions());
				fillingMap(this.getInhibitionMap(), inhibitedSites, ruleId,
						ruleCheckId, ruleCheck.getLeftHandSideActions());
			}

			for (List<AbstractionRule> rulesCheck : this.getObservbableRules()
					.values())
				for (AbstractionRule ruleCheck : rulesCheck) {
					fillingMap(this.getActivationMapObservables(),
							activatedSites, ruleId, ruleCheck.getRuleId(),
							ruleCheck.getLeftHandSideActions());
					fillingMap(this.getInhibitionMapObservables(),
							inhibitedSites, ruleId, ruleCheck.getRuleId(),
							ruleCheck.getLeftHandSideActions());
				}
		}
	}

	private final void initObsRules(Observables observables) {
		Map<Integer, List<AbstractionRule>> map = new HashMap<Integer, List<AbstractionRule>>();
		if (observables != null)
			for (ObservableConnectedComponentInterface cc : observables
					.getConnectedComponentList()) {
				AbstractionRule rule = new AbstractionRule(cc);
				List<AbstractionRule> list = map.get(rule.getRuleId());
				if (list == null) {
					list = new LinkedList<AbstractionRule>();
					map.put(rule.getRuleId(), list);
				}
				list.add(rule);
			}
		this.setObservbableRules(map);
	}

	private static final void fillingActivatedAndInhibitedSites(
			List<MarkAgentWithFuture> activatedSites,
			List<MarkAgentWithFuture> inhibitedSites, ContactMap contactMap,
			AbstractionRule rule, Map<String, AbstractAgent> agentNameToAgent) {
		for (AbstractAction action : rule.getActions()) {
			switch (action.getActionType()) {
			case ADD: {
				AbstractAgent agent = action.getRightHandSideAgent();
				// if (agent.getSitesMap().isEmpty()) {
				activatedSites.add(new MarkAgentWithFuture(agent, null,
						ActionType.ADD));
				// break;
				// }

				for (AbstractSite site : agent.getSitesMap().values())
					activatedSites.add(new MarkAgentWithFuture(agent, site,
							ActionType.ADD));
				break;
			}
			case DELETE: {
				AbstractAgent agent = action.getLeftHandSideAgent();
				// if (agent.getSitesMap().isEmpty()) {
				inhibitedSites.add(new MarkAgentWithFuture(agent, null,
						ActionType.DELETE));
				// break;
				// }

				LinkedHashSet<String> sideEffect = new LinkedHashSet<String>();
				for (AbstractSite siteLHS : agent.getSitesMap().values()) {
					if (!isLinkStateHasSideEffect(siteLHS)) {
						inhibitedSites.add(new MarkAgentWithFuture(agent,
								siteLHS, ActionType.DELETE));
					} else
						sideEffect.add(siteLHS.getName());
				}
				for (AbstractSite clearSite : agentNameToAgent.get(
						agent.getName()).getSitesMap().values())
					if (!agent.getSitesMap().containsKey(clearSite.getName()))
						sideEffect.add(clearSite.getName());

				if (contactMap != null)
					findSideEffect(agent, sideEffect, contactMap,
							activatedSites, inhibitedSites, agentNameToAgent);
				break;
			}
			case TEST_AND_MODIFICATION: {
				AbstractAgent agent = action.getLeftHandSideAgent();
				LinkedHashSet<String> sideEffect = new LinkedHashSet<String>();
				for (AbstractSite siteLHS : agent.getSitesMap().values()) {
					AbstractSite siteRHS = action.getRightHandSideAgent()
							.getSiteByName(siteLHS.getName());
					if (!siteLHS.getInternalState().equalz(
							siteRHS.getInternalState())) {
						AbstractSite modSite = siteLHS.clone();
						modSite.getLinkState().setFreeLinkState();
						inhibitedSites.add(new MarkAgentWithFuture(agent,
								modSite, ActionType.MODIFY));

						AbstractSite modSite2 = siteRHS.clone();
						modSite2.getLinkState().setFreeLinkState();

						activatedSites.add(new MarkAgentWithFuture(agent,
								modSite2, ActionType.MODIFY));
					}
					AbstractLinkState linkStateLHS = siteLHS.getLinkState();
					AbstractLinkState linkStateRHS = siteRHS.getLinkState();
					if (!linkStateLHS.equalz(linkStateRHS)) {
						if (isLinkStateHasSideEffect(siteLHS)) {
							sideEffect.add(siteLHS.getName());
						}
						activatedSites.add(new MarkAgentWithFuture(agent,
								siteRHS, ActionType.BOUND));
						inhibitedSites.add(new MarkAgentWithFuture(agent,
								siteLHS, ActionType.BOUND));
					}
				}
				if (contactMap != null)
					findSideEffect(agent, sideEffect, contactMap,
							activatedSites, inhibitedSites, agentNameToAgent);
				break;
			}
			}
		}
	}

	private static final void findSideEffect(AbstractAgent agent,
			LinkedHashSet<String> sideEffect, ContactMap contactMap,
			List<MarkAgentWithFuture> activatedSites,
			List<MarkAgentWithFuture> inhibitedSites,
			Map<String, AbstractAgent> agentNameToAgent) {
		AbstractAgent clearAgent = agentNameToAgent.get(agent.getName());
		for (String sideEffectSiteId : sideEffect) {
			AbstractSite clearSite = clearAgent.getSiteByName(sideEffectSiteId);
			for (AbstractAgent sideEffectAgent : contactMap
					.getSideEffect(clearSite)) {
				inhibitedSites.add(new MarkAgentWithFuture(sideEffectAgent));
				AbstractAgent actAgent = new AbstractAgent(sideEffectAgent);
				for (AbstractSite actSite : actAgent.getSitesMap().values())
					actSite.getLinkState().setFreeLinkState();
				activatedSites.add(new MarkAgentWithFuture(actAgent));
			}
		}
	}

	private static final void fillingMap(
			Map<Integer, List<InfluenceMapEdge>> map,
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

	private static final boolean isIntersection(
			List<MarkAgentWithFuture> agents, List<AbstractAction> actions) {
		for (AbstractAction action : actions)
			for (MarkAgentWithFuture mAgent : agents) {
				if (!mAgent.getAgent().hasSimilarName(
						action.getLeftHandSideAgent()))
					continue;
				AbstractSite mSite = mAgent.getSite();
				if (action.getLeftHandSideAgent().getSitesMap().isEmpty())
					return true;

				if (mSite == null)
					return true;
				AbstractSite aSite = action.getLeftHandSideAgent()
						.getSiteByName(mSite.getName());
				if (aSite == null)
					continue;
				if (mAgent.getType() == ActionType.MODIFY) {
					if (mSite.getInternalState().equalz(
							aSite.getInternalState()))
						return true;
				} else {
					AbstractLinkState mLinkState = mSite.getLinkState();
					AbstractLinkState aLinkState = aSite.getLinkState();

					if (aLinkState.compareLinkStates(mLinkState)) {
						if (NameDictionary.isDefaultAgentName(aLinkState
								.getAgentName()))
							return true;
						if ((aLinkState.getAgentName().equals(mLinkState
								.getAgentName()))
								&& (aLinkState.getConnectedSiteName()
										.equals(mLinkState
												.getConnectedSiteName())))
							return true;
					}
				}
			}
		return false;
	}

	private static final boolean isLinkStateHasSideEffect(AbstractSite siteLHS) {
		return siteLHS.getLinkState().getStatusLinkRank() == LinkRank.BOUND_OR_FREE
				|| siteLHS.getLinkState().getStatusLinkRank() == LinkRank.SEMI_LINK;
	}

}
