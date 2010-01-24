package com.plectix.simulator.staticanalysis.rulecompression;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.ConnectedComponent;
import com.plectix.simulator.staticanalysis.InternalState;
import com.plectix.simulator.staticanalysis.Link;
import com.plectix.simulator.staticanalysis.LinkRank;
import com.plectix.simulator.staticanalysis.LinkStatus;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.Site;
import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.abstracting.AbstractSite;
import com.plectix.simulator.staticanalysis.localviews.LocalViewsMain;

public class RootedRulesGroup {

	private Map<RootedRule, Map<Integer, Integer>> rulesAndGluingThem;
	private RootedRule headerRule;
	private RootedRule compressed;
	private LocalViewsMain localViews;

	private ShadowAgent obstruction;
	private Set<Site> problemSites;
	private Set<Site> noProblemSites;
	private List<Integer> neededAgents;
	private String obstructionSite;

	public RootedRulesGroup(RootedRule r) {
		headerRule = r;
		rulesAndGluingThem = new LinkedHashMap<RootedRule, Map<Integer, Integer>>();
	}

	// algorithm part
	// ========================================================

	public boolean tryAdd(RootedRule r) {
		Map<Integer, Integer> gluing = headerRule.findCorrespondenceToRule(r);
		if (gluing == null) {
			return false;
		} else {
			rulesAndGluingThem.put(r, gluing);
			return true;
		}
	}

	public void findCommonPart() {
		obstruction = null;
		problemSites = new LinkedHashSet<Site>();

		if (rulesAndGluingThem.size() == 0) {
			return;
		}

		compressed = new RootedRule(headerRule.getMapBefore(), headerRule
				.getMapAfter());
		compressed.setRule(null);
		neededAgents = new LinkedList<Integer>();
		noProblemSites = new LinkedHashSet<Site>();

		Stack<Integer> stack = new Stack<Integer>();
		stack.addAll(headerRule.getRoots());
		Set<Integer> studied = new HashSet<Integer>();

		Map<Integer, ShadowAgent> compressedMapBefore = compressed
				.getMapBefore();

		obstructionSite = null;

		while (!stack.isEmpty()) {
			Integer current = stack.pop();
			studied.add(current);
			neededAgents.add(current);

			// rules have same actions therefore they have same add actions
			if (headerRule.getMapBefore().get(current) == null)
				continue;

			ShadowAgent compressedAgentBefore = compressedMapBefore
					.get(current);

			brushAgents(current, compressedAgentBefore);
			if (obstruction != null) {
				break;
			}
			for (Site s : compressedAgentBefore.getSites()) {
				Site connectedSite = s.getLinkState().getConnectedSite();
				if (connectedSite != null) {
					int idInRuleHandside = connectedSite.getParentAgent()
							.getIdInRuleHandside();
					if (!studied.contains(idInRuleHandside)) {
						stack.add(idInRuleHandside);
					}

				}
			}
		}
		if (obstruction == null) {
			examineProblemSites();
		}
	}

	/**
	 * examine problems with merge a(x!1),b(x!1) and a(x!1),c(x!1) to a(x!_)
	 * 
	 * @return problemSites empty - not problem. not empty - there are problem
	 */
    void examineProblemSites() {
		for (Site s : noProblemSites) {
			problemSites.remove(s);
		}
		if (problemSites.isEmpty()) {
			return;
		}
		Stack<Site> stack = new Stack<Site>();
		stack.addAll(problemSites);
		while (!stack.isEmpty()) {
			Site site = stack.pop();
			for (Site connectedSite : probablyConnectedWith(site)) {
				if (!RuleCompressionUtils.uniqueConponent(new AbstractSite(connectedSite, new AbstractAgent(connectedSite
						.getParentAgent().getName())),
						localViews)) {
					// stack not empty!
					return;
				}
			}
			problemSites.remove(site);
		}
		return;

	}

	private Set<Site> probablyConnectedWith(Site pop) {
		Set<Site> answer = new LinkedHashSet<Site>();

		Integer id = pop.getParentAgent().getIdInRuleHandside();

		for (RootedRule r : rulesAndGluingThem.keySet()) {
			Integer idInRule = rulesAndGluingThem.get(r).get(id);
			ShadowAgent sa = r.getMapBefore().get(idInRule);
			Site siteInRule = sa.getSiteByName(pop.getName());
			if (siteInRule != null) {
				Site connectedSite = siteInRule.getLinkState()
						.getConnectedSite();
				if (siteInRule.getLinkState().getStatusLinkRank() == LinkRank.SEMI_LINK) {
					throw new RuntimeException(
							"this site must be removed from problemSites!");
				}
				if (connectedSite != null) {
					answer.add(connectedSite);
				}

			}
		}

		return answer;
	}

	Rule buildCompressedRule() {
		QuantitativeCompressor q = new QuantitativeCompressor(localViews);
		if (rulesAndGluingThem.size() == 0) {
			q.compress(headerRule.getRule());
			return q.getCompressedRule();
		}
		if(compressed.getRule()!=null){
			return compressed.getRule();
		}

		List<ConnectedComponentInterface> listBefore = new LinkedList<ConnectedComponentInterface>();
		List<ConnectedComponentInterface> listAfter = new LinkedList<ConnectedComponentInterface>();

		Rule rule = headerRule.getRule();
		if (rule.getLeftHandSide() != null) {
			for (ConnectedComponentInterface c : rule.getLeftHandSide()) {

				List<Agent> newLeftSide = new LinkedList<Agent>();
				for (Agent agent : c.getAgents()) {
					int id = agent.getIdInRuleHandside();
					if (neededAgents.contains(id)) {
						newLeftSide.add(compressed.getMapBefore().get(id));
					}
				}
				if (!newLeftSide.isEmpty()) {
					sortBefore(newLeftSide);
					listBefore.add(new ConnectedComponent(newLeftSide));
				}
			}
		}
		if (rule.getRightHandSide() != null) {
			for (ConnectedComponentInterface c : rule.getRightHandSide()) {
				List<Agent> newRightSide = new LinkedList<Agent>();
				for (Agent agent : c.getAgents()) {
					int id = agent.getIdInRuleHandside();
					if (neededAgents.contains(id)) {
						newRightSide.add(compressed.getMapAfter().get(id));
					}
				}
				if (!newRightSide.isEmpty()) {
					sortAfter(newRightSide);
					listAfter.add(new ConnectedComponent(newRightSide));
				}
			}
		}
		if (listBefore.isEmpty()) {
			listBefore.add(new ConnectedComponent());
		}
		q.compress(new Rule(listBefore, listAfter, rule.getName()
				+ "_compressedGroup", 1, 0, false));

		compressed.setRule(q.getCompressedRule());
		return q.getCompressedRule();
	

	}

	/**
	 * find common part of test information by this id. Write to second argument<br>
	 * If we have a problem with reachability then we full field obstruction
	 * 
	 * @param id
	 * @param commonTestedInformation
	 */
    void brushAgents(Integer id, ShadowAgent commonTestedInformation) {

		List<AbstractAgent> masks = new LinkedList<AbstractAgent>();
		for (RootedRule r : rulesAndGluingThem.keySet()) {

			// added agents cann't be brushed
			ShadowAgent shadowAgent = r.getMapBefore().get(
					rulesAndGluingThem.get(r).get(id));
			if (shadowAgent == null) {
				continue;
			}
			Agent realAgent = shadowAgent.getRealAgent();
			generalize(commonTestedInformation, realAgent);
			masks.add(new AbstractAgent(realAgent));
		}
		masks.add(new AbstractAgent(headerRule.getMapBefore().get(id).getRealAgent()));
		String site = localViews.getObstructionSiteForCoherentAgentAndList(
				masks, new AbstractAgent(commonTestedInformation));
		if (site != null) {
			obstruction = commonTestedInformation;
			obstructionSite = site;
		}
	}

	/**
	 * different internal states ->set empty InternalState different link -> set
	 * wildcard or semilink
	 * 
	 * @param brushedAgent
	 * @param realAgent
	 */
    void generalize(ShadowAgent brushedAgent, Agent realAgent) {

		for (Site realSite : realAgent.getSites()) {
			String siteName = realSite.getName();
			Site site = brushedAgent.getSiteByName(siteName);
			if (site != null) {
				int idInRuleHandside = brushedAgent.getIdInRuleHandside();
				ShadowAgent applying = compressed.getMapAfter().get(idInRuleHandside);
				Site applyingSite =null;
				if(applying!=null){
					applyingSite = applying.getSiteByName(siteName);
				}
				
				if (site.getInternalState() != InternalState.EMPTY_STATE
						&& realSite.getInternalState() != InternalState.EMPTY_STATE) {
					if (!site.getInternalState().equalz(
							realSite.getInternalState())) {
						site.setInternalState(InternalState.EMPTY_STATE);
						
						ShadowAgent shadowAgentAfter = headerRule.getMapAfter().get(idInRuleHandside);
						ShadowAgent shadowAgentBefore = headerRule.getMapBefore().get(idInRuleHandside);
						
						if(shadowAgentAfter!=null){
							if(shadowAgentBefore.getSiteByName(siteName).getInternalState().equalz(shadowAgentAfter.getSiteByName(siteName).getInternalState())){
								applyingSite.setInternalState(InternalState.EMPTY_STATE);
							}
						}
					}
				}
				if (site.getLinkState().getStatusLink() == LinkStatus.WILDCARD) {
					continue;
				}
				if (!site.getLinkState().equalz(realSite.getLinkState())) {
					if (site.getLinkState().getStatusLink() == LinkStatus.FREE
							|| realSite.getLinkState().getStatusLink() == LinkStatus.FREE) {
						site.getLinkState().setWildLinkState();
						applyingSite.getLinkState().setWildLinkState();
						continue;
					}

					if (realSite.getLinkState().getStatusLinkRank() == LinkRank.SEMI_LINK) {
						noProblemSites.add(site);
					} else {
						problemSites.add(site);
					}
					site.getLinkState().setSemiLink();
					
					ShadowAgent shadowAgentAfter = headerRule.getMapAfter().get(idInRuleHandside);
					ShadowAgent shadowAgentBefore = headerRule.getMapBefore().get(idInRuleHandside);
					
					if(shadowAgentAfter!=null){
						if(shadowAgentBefore.getSiteByName(siteName).getLinkState().equalz(shadowAgentAfter.getSiteByName(siteName).getLinkState())){
							applyingSite.getLinkState().setSemiLink();
						}
					}
				}
				if(site.getInternalState().equalz(InternalState.EMPTY_STATE)&&site.getLinkState().getStatusLink()==LinkStatus.WILDCARD){
					if(applying==null){
						brushedAgent.removeSite(site.getName());
					}
					else{
						if(applyingSite.getInternalState().equalz(InternalState.EMPTY_STATE)&& applyingSite.getLinkState().getStatusLink()==LinkStatus.WILDCARD){
							brushedAgent.removeSite(site.getName());
							applying.removeSite(site.getName());
						}
					}
				}
			}
		}
	}

	public boolean divide(Map<Rule, RootedRulesGroup> groups) {

		if (obstruction != null) {
			List<RootedRule> rules = featureDivider(obstructionSite,
					obstruction.getIdInRuleHandside());

			RootedRulesGroup rg = getNewGroup(rules);
			rg.setViews(localViews);
			for (RootedRule rr : rules) {
				groups.put(rr.getRule(), rg);
			}
			return true;
		} else {
			if (!problemSites.isEmpty()) {
				Site s = problemSites.iterator().next();
				List<RootedRule> rules = featureDivider(s.getName(), s
						.getParentAgent().getIdInRuleHandside());
				RootedRulesGroup rg = getNewGroup(rules);
				rg.setViews(localViews);
				for (RootedRule rr : rules) {
					groups.put(rr.getRule(), rg);
				}
				return true;
			}

			return false;
		}

	}

	private List<RootedRule> featureDivider(String siteName, int idInRuleHandside) {
		Link linkState = headerRule.getMapBefore().get(idInRuleHandside)
				.getSiteByName(siteName).getLinkState();
		List<RootedRule> secondListRule = new LinkedList<RootedRule>();
		for (RootedRule rr : rulesAndGluingThem.keySet()) {
			Integer id = rulesAndGluingThem.get(rr).get(idInRuleHandside);
			ShadowAgent shadowAgent = rr.getMapBefore().get(id);
			if (shadowAgent == null) {
				continue;
			}
			Link other = shadowAgent.getSiteByName(siteName).getLinkState();
			if (!linkState.equalz(other)) {
				secondListRule.add(rr);
			}
		}
		if (secondListRule.isEmpty()) {
			InternalState is = headerRule.getMapBefore().get(idInRuleHandside)
					.getSiteByName(siteName).getInternalState();
			for (RootedRule rr : rulesAndGluingThem.keySet()) {
				Integer id = rulesAndGluingThem.get(rr).get(idInRuleHandside);
				ShadowAgent shadowAgent = rr.getMapBefore().get(id);
				if (shadowAgent == null) {
					continue;
				}
				InternalState other = shadowAgent.getSiteByName(siteName)
						.getInternalState();
				if (!is.equalz(other)) {
					secondListRule.add(rr);
				}
			}
		}
		if (secondListRule.isEmpty()) {
			throw new RuntimeException("this site doesn't divide!");
		}
		return secondListRule;
	}

	private RootedRulesGroup getNewGroup(List<RootedRule> list) {
		RootedRulesGroup rg = new RootedRulesGroup(list.get(0));
		rulesAndGluingThem.remove(list.get(0));

		// TODO Optimize : doesn't need build map each time
		for (int i = 1; i < list.size(); i++) {
			rg.tryAdd(list.get(i));
			rulesAndGluingThem.remove(list.get(i));
		}

		return rg;

	}

	// =====================================================
	// utils

	private void sortAfter(List<Agent> newRightSide) {
		int i = 0;
		for (Agent agent : newRightSide) {
			if (compressed.getMapBefore().get(agent.getIdInRuleHandside()) == null) {
				continue;
			}
			i++;
		}
		for (Agent agent : newRightSide) {
			if (compressed.getMapBefore().get(agent.getIdInRuleHandside()) == null) {
				agent.setIdInConnectedComponent(i);
				i++;
			}

		}

	}

	private void sortBefore(List<Agent> newLeftSide) {
		int i = 0;
		for (Agent agent : newLeftSide) {
			if (compressed.getMapAfter().get(agent.getIdInRuleHandside()) == null)
				continue;

			agent.setIdInConnectedComponent(i);
			compressed.getMapAfter().get(agent.getIdInRuleHandside())
					.setIdInConnectedComponent(i);
			i++;
		}
		for (Agent agent : newLeftSide) {
			if (compressed.getMapAfter().get(agent.getIdInRuleHandside()) == null) {
				agent.setIdInConnectedComponent(i);
				i++;
			}

		}

	}

	// ===================================================================
	// getters and setters

	public RootedRule getFirstRule() {
		return headerRule;
	}

	public Rule getCompressedRule() {
		return buildCompressedRule();
	}

	public void setViews(LocalViewsMain views) {
		localViews = views;
	}

	public Collection<RootedRule> getRules() {
		return rulesAndGluingThem.keySet();
	}
	
	public ShadowAgent getObstruction(){
		return obstruction;
	}

}
