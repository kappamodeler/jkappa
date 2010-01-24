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
import java.util.Map.Entry;

import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.LinkStatus;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.Site;

public class RootedRule {

	private List<ShadowAgent> roots;

	// actions for all agents
	private Map<Integer, List<String>> actionStringsByAgentIDInRuleHandSide;

	private Map<Integer, ShadowAgent> mapAfter;
	private Map<Integer, ShadowAgent> mapBefore;

	// parent rule
	private Rule rule;

	/**
	 * clone RootedRule from RuleMaster
	 * 
	 * @param mapBefore
	 * @param mapAfter
	 */
	public RootedRule(Map<Integer, ShadowAgent> mapBefore,
			Map<Integer, ShadowAgent> mapAfter) {
		actionStringsByAgentIDInRuleHandSide = new LinkedHashMap<Integer, List<String>>();
		this.mapBefore = new LinkedHashMap<Integer, ShadowAgent>();
		this.mapAfter = new LinkedHashMap<Integer, ShadowAgent>();

		RuleCompressionUtils.shadowClone(mapBefore, this.mapBefore);
		RuleCompressionUtils.shadowClone(mapAfter, this.mapAfter);

	}

	public void setRootsAndFullActionInfo(Set<Integer> numbers) {
		roots = new LinkedList<ShadowAgent>();
		for (Integer i : numbers) {
			ShadowAgent agent = mapBefore.get(i);
			if (agent == null) {
				roots.add(mapAfter.get(i));
			} else {
				roots.add(agent);
			}
		}

		rangeAgentsFromRoots();
		fullAddActions();
		fullModifyActions();
		fullBreakActions();
		fullBoundActions();
		fullDeleteActions();
		fullTestActions();
	}

	// ============================================================================================
	// methods for find correspondence

	/**
	 * find map : agents of this rule -> agents of argumentRule
	 */
	public Map<Integer, Integer> findCorrespondenceToRule(RootedRule r) {
		Map<Integer, List<String>> secondRuleActions = r.getActions();
		Map<Integer, Integer> correspondence = new LinkedHashMap<Integer, Integer>();
		Set<Integer> used = new LinkedHashSet<Integer>();
		for (Integer i : actionStringsByAgentIDInRuleHandSide.keySet()) {
			Integer j = getSecondRuleAction(
					actionStringsByAgentIDInRuleHandSide.get(i),
					secondRuleActions);
			if (j != null) {
				correspondence.put(i, j);
				used.add(j);
			} else {
				return null;
			}
		}
		if (used.size() == secondRuleActions.keySet().size()) {
			return expand(correspondence, r);
		}
		return null;
	}

	/**
	 * expand correspondence from action agents to all compatible agents
	 * 
	 * @param correspondence
	 * @param r
	 * @return
	 */
	private Map<Integer, Integer> expand(Map<Integer, Integer> correspondence,
			RootedRule r) {
		Stack<Integer> search = new Stack<Integer>();
		Set<Integer> used = new LinkedHashSet<Integer>();
		search.addAll(correspondence.keySet());
		while (!search.isEmpty()) {
			Integer pop = search.pop();
			if (used.contains(pop))
				continue;

			used.add(pop);

			ShadowAgent popAgent = mapBefore.get(pop);
			if (popAgent == null) {
				popAgent = mapAfter.get(pop);
			}
			for (Site s : getNeighboorsSites(popAgent)) {
				if (s.getLinkState().getStatusLink() == LinkStatus.FREE)
					continue;

				Agent linkedAgent = s.getLinkState().getConnectedSite()
						.getParentAgent();
				int idInRuleHandside = linkedAgent.getIdInRuleHandside();
				Integer old = correspondence.get(idInRuleHandside);
				Integer idAgentByConnectedSite = r.idAgentByConnectedSite(
						correspondence.get(pop), s.getName());
				if (old == null) {
					ShadowAgent shadowAgent = r.getMapBefore().get(idAgentByConnectedSite);
					if(shadowAgent==null){
						shadowAgent = r.getMapAfter().get(idAgentByConnectedSite);
					}
					if(shadowAgent!=null
							&& linkedAgent.getName().equals(shadowAgent.getName())){
						correspondence.put(idInRuleHandside,
								idAgentByConnectedSite);
					}
				} else {
					if (idAgentByConnectedSite != null
							&& !old.equals(idAgentByConnectedSite)) {
						throw new RuntimeException("interanl error");
					}
				}

				search.add(idInRuleHandside);
			}

		}
		return correspondence;
	}

	Integer idAgentByConnectedSite(Integer idAgent, String siteName) {
		ShadowAgent shadowAgent = mapBefore.get(idAgent);
		if (shadowAgent == null) {
			shadowAgent = mapAfter.get(idAgent);
			if (shadowAgent == null) {
				return null;
			}
		}
		Site siteByName = shadowAgent.getSiteByName(siteName);
		if (siteByName == null) {
			return null;
		}
		Site connectedSite = siteByName.getLinkState().getConnectedSite();
		if (connectedSite == null) {
			return null;
		}
		return connectedSite.getParentAgent().getIdInRuleHandside();

	}

	/**
	 * return idInRule HandSide of agents with actions as list
	 * 
	 * @param list
	 *            of action-string
	 * @param secondRuleActions
	 * @return
	 */
	private Integer getSecondRuleAction(List<String> list,
			Map<Integer, List<String>> secondRuleActions) {
		for (Entry<Integer, List<String>> entry : secondRuleActions.entrySet()) {
			if (RuleCompressionUtils.equiv(entry.getValue(), list)) {
				return entry.getKey();
			}
		}
		return null;
	}

	Set<Site> getNeighboorsSites(ShadowAgent agent) {
		Set<Site> siteToNeighboors = new HashSet<Site>();
		for (Site s : agent.getSites()) {
			if (s.getLinkState().getStatusLink() == LinkStatus.BOUND) {
				Site connectedSite = s.getLinkState().getConnectedSite();
				if (connectedSite != null) {
					siteToNeighboors.add(s);
				}
			}
		}
		return siteToNeighboors;
	}

	/**
	 * range tree of agents (roots of trees - roots)
	 */
	private void rangeAgentsFromRoots() {

		Stack<ShadowAgent> markedAgents = new Stack<ShadowAgent>();
		int number = 0;
		for (ShadowAgent sa : mapBefore.values()) {
			number++;
			sa.setRange(-1);
		}
		for (ShadowAgent sa : mapAfter.values()) {
			if (mapBefore.get(sa.getIdInRuleHandside()) == null) {
				number++;
				continue;
			}

			sa.setRange(-1);
		}

		for (ShadowAgent sa : roots) {
			markedAgents.add(sa);
			sa.setRange(0);
		}
		// number -= roots.size();
		while (!markedAgents.isEmpty()) {
			ShadowAgent popAgent = markedAgents.pop();
			number--;

			if (mapBefore.get(popAgent.getIdInRuleHandside()) == null) {
				continue;
			}

			for (Site siteNeigh : getNeighboorsSites(popAgent)) {
				ShadowAgent neigh = (ShadowAgent) siteNeigh.getLinkState()
						.getConnectedSite().getParentAgent();
				if (neigh.getRange() != -1) {
					if (popAgent.getParentInTree() != null) {
						throw new RuntimeException("cyclic rule!!");
					}
					popAgent.setSiteToParentInTree(siteNeigh);
					continue;
				}
				neigh.setRange(popAgent.getRange() + 1);
				markedAgents.push(neigh);
			}
		}
		if (number != 0) {
			throw new RuntimeException("less roots that it is needed");
		}
	}

	// ============================================================
	// getters
	Map<Integer, List<String>> getActions() {
		return actionStringsByAgentIDInRuleHandSide;
	}

	public Map<Integer, ShadowAgent> getMapBefore() {
		return mapBefore;
	}

	public Map<Integer, ShadowAgent> getMapAfter() {
		return mapAfter;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	public Rule getRule() {
		return rule;
	}

	public List<Integer> getRoots() {
		List<Integer> list = new LinkedList<Integer>();
		for (ShadowAgent sa : roots) {
			list.add(sa.getIdInRuleHandside());
		}
		return list;
	}

	// ===========================================================================
	// strings and actions methods

	private void fullTestActions() {
		for (ShadowAgent sa : roots) {
			if (sa.isActionAgent())
				continue;

			String action = "TEST : " + sa.getName() + "()";
			putStringToActionList(sa.getIdInRuleHandside(), action);
		}
	}

	private void fullBreakActions() {
		for (ShadowAgent sa : mapBefore.values()) {
			if (!sa.isActionAgent())
				continue;
			if (mapAfter.get(sa.getIdInRuleHandside()) == null) {
				for (Site s : sa.getSites()) {
					if (s.getLinkState().getStatusLink() == LinkStatus.BOUND) {
						Site other = s.getLinkState().getConnectedSite();
						putStringForBreakAction(sa, s, other);
					}
				}

			} else {
				for (Site s : sa.getSites()) {
					if (s.getLinkState().getStatusLink() == LinkStatus.BOUND) {
						Site other = s.getLinkState().getConnectedSite();
						Site doubleSite = mapAfter
								.get(sa.getIdInRuleHandside()).getSiteByName(
										s.getName());
						if (doubleSite.getLinkState().getStatusLink() == LinkStatus.FREE
								|| (other != null && other.getParentAgent()
										.getIdInRuleHandside() != doubleSite
										.getLinkState().getConnectedSite()
										.getParentAgent().getIdInRuleHandside())) {

							putStringForBreakAction(sa, s, other);
						}
					}
				}
			}
		}
	}

	private void putStringForBreakAction(ShadowAgent sa, Site s, Site other) {
		int idInRuleHandside = sa.getIdInRuleHandside();
		if (other == null
				|| ((ShadowAgent) other.getParentAgent()).getRange() > sa
						.getRange()) {
			String action = "BREAK : " + sa.getName() + "(" + s.getName()
					+ ") " + getActionString(sa);
			putStringToActionList(idInRuleHandside, action);
		} else {
			if (((ShadowAgent) other.getParentAgent()).getRange() == sa
					.getRange()) {
				String action = "BREAK : "
						+ sa.getName()
						+ "("
						+ s.getName()
						+ ")"
						+ getActionString(sa)
						+ " BREAK : "
						+ other.getParentAgent().getName()
						+ "("
						+ other.getName()
						+ ")"
						+ getActionString((ShadowAgent) (other.getParentAgent()));
				putStringToActionList(idInRuleHandside, action);
			}
		}
	}

	private void putStringToActionList(int idInRuleHandside, String action) {
		if (actionStringsByAgentIDInRuleHandSide.get(idInRuleHandside) == null) {
			actionStringsByAgentIDInRuleHandSide.put(idInRuleHandside,
					new LinkedList<String>());
		}
		actionStringsByAgentIDInRuleHandSide.get(idInRuleHandside).add(action);
	}

	private void fullBoundActions() {
		for (ShadowAgent sa : mapBefore.values()) {
			if (!sa.isActionAgent())
				continue;
			int idInRuleHandside = sa.getIdInRuleHandside();
			if (mapAfter.get(idInRuleHandside) == null)
				continue;

			for (Site s : sa.getSites()) {
				Site other = mapAfter.get(idInRuleHandside).getSiteByName(
						s.getName());

				if (other.getLinkState().getStatusLink() == LinkStatus.FREE
						|| other.getLinkState().getConnectedSite() == null)
					continue;
				Agent boundedAgent = other.getLinkState().getConnectedSite()
						.getParentAgent();
				if (mapBefore.get(boundedAgent.getIdInRuleHandside()) == null) {
					// see next "for" for this case
					continue;
				}
				if (s.getLinkState().getStatusLink() == LinkStatus.FREE
						|| s.getLinkState().getConnectedSite() == null
						|| s.getLinkState().getConnectedSite().getParentAgent()
								.getIdInRuleHandside() != boundedAgent
								.getIdInRuleHandside()) {

					putStringToBoundAction(sa, s, other, mapBefore
							.get(boundedAgent.getIdInRuleHandside()));
				}

			}
		}
		for (ShadowAgent sa : mapAfter.values()) {
			if (!sa.isActionAgent())
				continue;
			if (mapBefore.get(sa.getIdInRuleHandside()) != null) {
				throw new RuntimeException();
			}
			for (Site s : sa.getSites()) {
				if (s.getLinkState().getStatusLink() == LinkStatus.BOUND) {
					Site other = s.getLinkState().getConnectedSite();
					if (mapBefore.get(other.getParentAgent()
							.getIdInRuleHandside()) == null) {
						putStringToBoundAction(sa, s, other, other
								.getParentAgent());

					} else {
						putStringToBoundAction(sa, s, other, mapBefore
								.get(other.getParentAgent()
										.getIdInRuleHandside()));
					}
				}
			}
		}
	}

	private void putStringToBoundAction(ShadowAgent sa, Site s, Site other,
			Agent boundedAgent) {
		String action = "BOUND : " + sa.getName() + "(" + s.getName() + ")"
				+ getActionString(sa) + "BOUND : " + boundedAgent.getName()
				+ "(" + other.getName() + ")"
				+ getActionString((ShadowAgent) boundedAgent);
		int idInRuleHandside = sa.getIdInRuleHandside();
		putStringToActionList(idInRuleHandside, action);
	}

	private void fullModifyActions() {
		for (ShadowAgent sa : mapBefore.values()) {
			if (!sa.isActionAgent())
				continue;
			if (mapAfter.get(sa.getIdInRuleHandside()) == null)
				continue;
			int idInRuleHandside = sa.getIdInRuleHandside();
			for (Site other : mapAfter.get(idInRuleHandside).getSites()) {
				Site site = mapBefore.get(idInRuleHandside).getSiteByName(
						other.getName());

				if (!other.getInternalState().equalz(site.getInternalState())) {
					String action = "MODIFY : " + sa.getName() + "("
							+ other.getName() + ") " + "internalState : "
							+ other.getInternalState().getName()
							+ getActionString(sa);
					putStringToActionList(idInRuleHandside, action);
				}
			}
		}
	}

	private void fullDeleteActions() {
		for (ShadowAgent sa : mapBefore.values()) {
			if (!sa.isActionAgent())
				continue;
			int idInRuleHandside = sa.getIdInRuleHandside();
			if (mapAfter.get(idInRuleHandside) != null)
				continue;

			String action = "DELETE : " + sa.getName() + getActionString(sa);
			putStringToActionList(idInRuleHandside, action);

		}
	}

	private void fullAddActions() {

		for (ShadowAgent sa : mapAfter.values()) {
			if (!sa.isActionAgent())
				continue;
			int idInRuleHandside = sa.getIdInRuleHandside();

			if (mapBefore.get(idInRuleHandside) != null) {
				throw new RuntimeException();
			}

			String action = "ADD : " + sa.toString();
			putStringToActionList(idInRuleHandside, action);
		}

	}

	private String getActionString(ShadowAgent sa) {
		StringBuffer path = new StringBuffer(" path = ");
		ShadowAgent runner = sa;

		while (runner.getParentInTree() != null) {
			Site siteToParentInTree = runner.getParentInTree();
			path.append(siteToParentInTree.getName() + ",");
			path.append(siteToParentInTree.getLinkState().getConnectedSite()
					.getName() + ",");
			path.append(siteToParentInTree.getLinkState().getConnectedSite()
					.getParentAgent().getName());
			runner = (ShadowAgent) siteToParentInTree.getLinkState()
					.getConnectedSite().getParentAgent();
		}
		return path.toString();
	}

	public Collection<String> getActionsString() {
		Collection<String> strings = new LinkedList<String>();
		for (Integer i : actionStringsByAgentIDInRuleHandSide.keySet()) {
			strings.addAll(actionStringsByAgentIDInRuleHandSide.get(i));
		}
		return strings;
	}

}
