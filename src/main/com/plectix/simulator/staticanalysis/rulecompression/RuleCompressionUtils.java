package com.plectix.simulator.staticanalysis.rulecompression;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.InternalState;
import com.plectix.simulator.staticanalysis.Link;
import com.plectix.simulator.staticanalysis.LinkStatus;
import com.plectix.simulator.staticanalysis.Site;
import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.abstracting.AbstractSite;
import com.plectix.simulator.staticanalysis.localviews.LocalViewsMain;

class RuleCompressionUtils {
		
	public static ArrayList<ShadowAgent> shadowClone(ArrayList<Agent> list) {
		ArrayList<ShadowAgent> newAgentsList = new ArrayList<ShadowAgent>();
		for (Agent agent : list) {
			ShadowAgent newAgent = new ShadowAgent(agent.getName(), agent
					.getId());
			newAgent.setRealAgent(agent);
			cloneSitesAndIdsInComponent(agent, newAgent);
			newAgentsList.add(newAgent);
		}
		for (int i = 0; i < newAgentsList.size(); i++) {
			for (Site siteNew : newAgentsList.get(i).getSites()) {
				Link lsNew = siteNew.getLinkState();
				Link lsOld = list.get(i).getSiteByName(siteNew.getName())
						.getLinkState();
				lsNew.setStatusLink(lsOld.getStatusLink());
				if (lsOld.getConnectedSite() != null) {
					Site siteOldLink = lsOld.getConnectedSite();
					int j = 0;
					for (; j < list.size(); j++) {
						if (list.get(j) == siteOldLink.getParentAgent())
							break;
					}
					int index = j;
					lsNew.connectSite(newAgentsList.get(index).getSiteByName(
							siteOldLink.getName()));
				}
			}
		}
		return newAgentsList;
	}

	private static void cloneSitesAndIdsInComponent(Agent agent, Agent newAgent) {
		newAgent.setIdInConnectedComponent(agent
				.getIdInConnectedComponent());
		newAgent.setIdInRuleSide(agent.getIdInRuleHandside());
		for (Site site : agent.getSites()) {
			Site newSite = new Site(site.getName(), newAgent);
			newSite.setLinkIndex(site.getLinkIndex());
			newSite.setInternalState(new InternalState(site
					.getInternalState().getName()));
			newAgent.addSite(newSite);
		}
	}

	public static List<Set<Integer>> addAllVariants(List<Set<Integer>> answer,
			Set<Integer> set) {
		if (answer.isEmpty()) {
			for (Integer i : set) {
				Set<Integer> newSet = new HashSet<Integer>();
				newSet.add(i);
				answer.add(newSet);
			}
			return answer;
		}
		List<Set<Integer>> newAnswer = new LinkedList<Set<Integer>>();
		for (Set<Integer> tempSet : answer) {
			newAnswer.addAll(addAllVariants(tempSet, set));
		}
		return newAnswer;

	}

	private static List<Set<Integer>> addAllVariants(Set<Integer> tempSet,
			Set<Integer> set) {
		List<Set<Integer>> answer = new LinkedList<Set<Integer>>();
		for (Integer i : set) {
			Set<Integer> newSet = new HashSet<Integer>();
			newSet.addAll(tempSet);
			newSet.add(i);
			answer.add(newSet);
		}
		return answer;
	}
	
	public static void shadowClone(Map<Integer, ShadowAgent> oldMap,
			Map<Integer, ShadowAgent> newMap) {
		for (Entry<Integer, ShadowAgent> entry : oldMap.entrySet()) {
			ShadowAgent agent = entry.getValue();
			ShadowAgent newAgent = new ShadowAgent(agent.getName(), agent
					.getId());
			newAgent.setRealAgent(agent.getRealAgent());
			if (agent.isActionAgent()) {
				newAgent.setActionAgent();
			}
			cloneSitesAndIdsInComponent(agent, newAgent);
			newMap.put(newAgent.getIdInRuleHandside(), newAgent);
		}
		for (Entry<Integer, ShadowAgent> entry : newMap.entrySet()) {
			Integer i = entry.getKey();
			for (Site siteNew : entry.getValue().getSites()) {
				Link lsNew = siteNew.getLinkState();
				Link lsOld = oldMap.get(i).getSiteByName(siteNew.getName())
						.getLinkState();
				lsNew.setStatusLink(lsOld.getStatusLink());
				if (lsOld.getConnectedSite() != null) {
					Site siteOldLink = lsOld.getConnectedSite();
					Integer index = 0;
					for (Integer j : oldMap.keySet()) {
						if (oldMap.get(j) == siteOldLink.getParentAgent()) {
							index = Integer.valueOf(j);
							break;
						}
					}
					lsNew.connectSite(newMap.get(index).getSiteByName(
							siteOldLink.getName()));
				}
			}
		}
	}

	public static boolean equiv(List<String> value, List<String> list) {
		for (String s : value) {
			if (!list.contains(s)) {
				return false;
			}
		}
		for (String s : list) {
			if (!value.contains(s)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * we have one site from agent and build agent with the one that site
	 * @param site
	 * @return
	 */
	private static AbstractAgent buildAgentBySite(AbstractSite site) {
		AbstractAgent first = new AbstractAgent(site.getParentAgent()
				.getName());
		AbstractSite newSite = new AbstractSite(site);
		first.addSite(newSite);
		return first;
	}

	public static AbstractAgent clone(Agent agent) {
		if (agent == null) {
			return null;
		}
		AbstractAgent newAgent = new AbstractAgent(agent);
		return newAgent;
	}
	
	public static ArrayList<Agent> cloneListOfAgents(List<Agent> instance) {
		ArrayList<Agent> newAgentsList = new ArrayList<Agent>();
		for (Agent agent : instance) {
			Agent newAgent = new Agent(agent.getName(), agent.getId());
			cloneSitesAndIdsInComponent(agent, newAgent);
			newAgentsList.add(newAgent);
		}
		for (int i = 0; i < newAgentsList.size(); i++) {
			for (Site siteNew : newAgentsList.get(i).getSites()) {
				Link lsNew = siteNew.getLinkState();
				Link lsOld = instance.get(i).getSiteByName(
						siteNew.getName()).getLinkState();
				lsNew.setStatusLink(lsOld.getStatusLink());
				if (lsOld.getConnectedSite() != null) {
					Site siteOldLink = lsOld.getConnectedSite();
					int j = 0;
					for (; j < instance.size(); j++) {
						if (instance.get(j) == siteOldLink.getParentAgent())
							break;
					}
					int index = j;
					lsNew.connectSite(newAgentsList.get(index).getSiteByName(
							siteOldLink.getName()));
				}
			}
		}
		return newAgentsList;
	}
	

	public static boolean uniqueConponent(AbstractSite site, LocalViewsMain localViews) {

		AbstractAgent first = RuleCompressionUtils
				.buildAgentBySite(site);
		Stack<AbstractAgent> stack = new Stack<AbstractAgent>();
		stack.add(first);
		while (!stack.isEmpty()) {
			AbstractAgent pop = stack.pop();
			List<AbstractAgent> coherentAgents = localViews
					.getCoherentAgents(pop);
			if (coherentAgents.size() != 1)
				return false;

			AbstractAgent next = coherentAgents.get(0);
			for (AbstractSite as : next.getSitesMap().values()) {
				if (as.getLinkState().getStatusLink() != LinkStatus.BOUND)
					continue;
				//we think that there are no cycles
				if(as.getName().equals(pop.getSitesMap().values().iterator().next().getName())){
					continue;
				}
				AbstractSite linkedSite = abstractSiteByConnectedWithHim(as);

				AbstractAgent connected = RuleCompressionUtils
						.buildAgentBySite(linkedSite);
				stack.push(connected);
			}
		}

		return true;
	}

	private static AbstractSite abstractSiteByConnectedWithHim(AbstractSite as) {
		AbstractAgent abstractAgent = new AbstractAgent(as.getLinkState().getAgentName());
		AbstractSite s = new AbstractSite(abstractAgent,as.getLinkState().getConnectedSiteName());
		s.getLinkState().setStatusLink(LinkStatus.BOUND);
		s.getLinkState().setAgentName(as.getParentAgent().getName());
		s.getLinkState().setLinkSiteName(as.getName());
		return s;
		
	}
}
