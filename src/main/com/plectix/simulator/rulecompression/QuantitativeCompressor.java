package com.plectix.simulator.rulecompression;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.plectix.simulator.component.Agent;
import com.plectix.simulator.component.ConnectedComponent;
import com.plectix.simulator.component.InternalState;
import com.plectix.simulator.component.Link;
import com.plectix.simulator.component.LinkStatus;
import com.plectix.simulator.component.Rule;
import com.plectix.simulator.component.Site;
import com.plectix.simulator.component.complex.abstracting.AbstractAgent;
import com.plectix.simulator.component.complex.abstracting.AbstractLinkState;
import com.plectix.simulator.component.complex.abstracting.AbstractSite;
import com.plectix.simulator.component.complex.localviews.LocalViewsMain;
import com.plectix.simulator.component.complex.subviews.base.AbstractAction;
import com.plectix.simulator.interfaces.ConnectedComponentInterface;

public class QuantitativeCompressor {

	// TODO -> subViews
	// private IAllSubViewsOfAllAgents allSubViews;
	private LocalViewsMain localviews;
	private Rule compressedRule;

	Map<Integer, Agent> mapAfter;
	Map<Integer, Agent> mapBefore;
	List<Integer> removedList;

	public QuantitativeCompressor(LocalViewsMain localviews) {
		this.localviews = localviews;
		this.compressedRule = null;
	}

	public boolean compress(Rule rule) {

		boolean answer = false;

		List<ConnectedComponentInterface> listBefore = new LinkedList<ConnectedComponentInterface>();
		List<ConnectedComponentInterface> listAfter = new LinkedList<ConnectedComponentInterface>();

		mapBefore = new LinkedHashMap<Integer, Agent>();
		if (rule.getLeftHandSide() != null) {
			for (ConnectedComponentInterface c : rule.getLeftHandSide()) {
				List<Agent> oldLeftSide = new LinkedList<Agent>(c.getAgents());
				List<Agent> newLeftSide = RuleCompressionUtils
						.cloneListOfAgents(oldLeftSide);
				for (Agent agent : newLeftSide) {
					mapBefore.put(agent.getIdInRuleHandside(), agent);
				}

			}
		}

		mapAfter = new LinkedHashMap<Integer, Agent>();
		if (rule.getRightHandSide() != null) {
			for (ConnectedComponentInterface c : rule.getRightHandSide()) {
				List<Agent> oldRightSide = new LinkedList<Agent>(c.getAgents());
				List<Agent> newRightSide = RuleCompressionUtils
						.cloneListOfAgents(oldRightSide);
				for (Agent agent : newRightSide) {
					mapAfter.put(agent.getIdInRuleHandside(), agent);
				}
			}
		}

		// clean up internal and link states
		answer = decreaseStates() || answer;

		// delete unnecessary agents (for example, tested leafs of component)
		answer = deleteEmptyEnds() || answer;

		if (rule.getLeftHandSide() != null) {
			for (ConnectedComponentInterface c : rule.getLeftHandSide()) {

				List<Agent> newLeftSide = new LinkedList<Agent>();
				for (Agent agent : c.getAgents()) {
					int id = agent.getIdInRuleHandside();
					if (!removedList.contains(id)) {
						newLeftSide.add(mapBefore.get(id));
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
					if (!removedList.contains(id)) {
						newRightSide.add(mapAfter.get(id));
					}
				}
				if (!newRightSide.isEmpty()) {
					sortAfter(newRightSide);
					listAfter.add(new ConnectedComponent(newRightSide));
				}
			}
		}
		sortNumberInRuleHandSide();
		compressedRule = new Rule(listBefore, listAfter, rule.getName()
				+ "_compressed", rule.getRate(), 0, false);
		return answer;
	}

	private void sortNumberInRuleHandSide() {
		int i = 0;
		for (int number : mapBefore.keySet()) {
			while (mapBefore.get(i) != null || mapAfter.get(i) != null) {
				i++;
			}
			if (number < i) {
				continue;
			}
			mapBefore.get(number).setIdInRuleSide(i);
			if (mapAfter.get(number) != null) {
				mapAfter.get(number).setIdInRuleSide(i);
			}
			i++;
		}
		for (int number : mapBefore.keySet()) {
			while (mapBefore.get(i) != null || mapAfter.get(i) != null) {
				i++;
			}
			if (number < i || mapBefore.get(number) != null) {
				continue;
			}
			mapAfter.get(number).setIdInRuleSide(i);
			i++;
		}
	}

	private void sortAfter(List<Agent> newRightSide) {
		int i = 0;
		for (Agent agent : newRightSide) {
			if (mapBefore.get(agent.getIdInRuleHandside()) == null) {
				continue;
			}
			i++;
		}
		for (Agent agent : newRightSide) {
			if (mapBefore.get(agent.getIdInRuleHandside()) == null) {
				agent.setIdInConnectedComponent(i);
				i++;
			}

		}

	}

	private void sortBefore(List<Agent> newLeftSide) {
		int i = 0;
		for (Agent agent : newLeftSide) {
			if (mapAfter.get(agent.getIdInRuleHandside()) == null)
				continue;

			agent.setIdInConnectedComponent(i);
			mapAfter.get(agent.getIdInRuleHandside())
					.setIdInConnectedComponent(i);
			i++;
		}
		for (Agent agent : newLeftSide) {
			if (mapAfter.get(agent.getIdInRuleHandside()) == null) {
				agent.setIdInConnectedComponent(i);
				i++;
			}

		}

	}

	protected boolean decreaseStates() {
		boolean changed = false;

		for (int idInRule : mapBefore.keySet()) {
			changed = decreaseInternalStatesAndFreeLinkStateToWild(mapBefore
					.get(idInRule), mapAfter.get(idInRule))
					|| changed;

		}

		return changed;
	}

	/**
	 * try to remove from rule unnecessary tested agents
	 * 
	 * @return
	 */
	protected boolean deleteEmptyEnds() {
		removedList = new LinkedList<Integer>();
		boolean removed = false;
		Stack<Agent> stack = new Stack<Agent>();
		stack.addAll(mapBefore.values());

		while (!stack.isEmpty()) {
			Agent agent = stack.pop();
			// may be trouble with empty agents TODO
			if (agent.getSites().size() > 1 || agent.getSites().size() == 0) {
				continue;
			}
			String siteName = agent.getSites().iterator().next().getName();

			Site site = agent.getSiteByName(siteName);

			if (!(site.getInternalState().isRankRoot())) {
				continue;
			}
			if (site.getLinkState().getStatusLink() != LinkStatus.BOUND) {
				continue;
			}

			Site connectedSite = site.getLinkState().getConnectedSite();
			if (connectedSite != null) {
				if (decreaseLinkSite(site)) {
					Agent parentAgent = connectedSite.getParentAgent();
					stack.add(parentAgent);
					decreaseInternalStatesAndFreeLinkStateToWild(mapBefore
							.get(parentAgent.getIdInRuleHandside()), mapAfter.get(parentAgent.getIdInRuleHandside()));
					removed = true;
				}
			}

		}

		return removed;
	}

	protected boolean decreaseLinkSite(Site site) {
		Site connectedSite = site.getLinkState().getConnectedSite();
		Agent agent = connectedSite.getParentAgent();
		AbstractAgent aAgent = RuleCompressionUtils.clone(agent);
		int size = localviews.getCountOfCoherentAgent(aAgent);

		Agent agent2 = mapAfter.get(agent.getIdInRuleHandside());
		if(agent2==null){
			return false;
		}
		aAgent.getSiteByName(connectedSite.getName()).getLinkState().setSemiLink();
		if (size == localviews.getCountOfCoherentAgent(aAgent)) {
			connectedSite.getLinkState().setSemiLink();

			Link linkState = agent2
					.getSiteByName(connectedSite.getName()).getLinkState();

			if (linkState.getStatusLink() != LinkStatus.FREE) {
				linkState.setSemiLink();
				aAgent.getSiteByName(connectedSite.getName()).getLinkState().setWildLinkState();
				if(size ==localviews.getCountOfCoherentAgent(aAgent)){
					connectedSite.getLinkState().setWildLinkState();
					linkState.setWildLinkState();
				}
			}
			

			Integer removedId = site.getParentAgent().getIdInRuleHandside();
			mapBefore.remove(removedId);
			mapAfter.remove(removedId);
			removedList.add(removedId);
			return true;
		}

		return false;
	}

	public Rule getCompressedRule() {
		return compressedRule;
	}

	protected boolean decreaseInternalStatesAndFreeLinkStateToWild(
			Agent agentBefore, Agent agentAfter) {
		boolean decreased = false;
		if (agentBefore == null) {
			return decreased;
		}
		AbstractAction action = new AbstractAction(RuleCompressionUtils
				.clone(agentBefore), RuleCompressionUtils.clone(agentAfter));
		AbstractAgent aAgent = action.getLeftHandSideAgent();
		int size = localviews.getCountOfCoherentAgent(aAgent);

		for (AbstractSite aSite : action.getTestedSites()) {
			decreased = decreaseInternalState(aAgent, agentBefore
					.getSiteByName(aSite.getName()), size)
					|| decreased;
			decreased = decreaseLinkState(aAgent, agentBefore
					.getSiteByName(aSite.getName()), true, size)
					|| decreased;

		}

		if (action.getModificatedSites() != null) {
			for (AbstractSite aSite : action.getModificatedSites()) {
				decreased = decreaseInternalState(aAgent, agentBefore
						.getSiteByName(aSite.getName()), size)
						|| decreased;
				decreased = decreaseLinkState(aAgent, agentBefore
						.getSiteByName(aSite.getName()), false, size)
						|| decreased;
			}
		}
		return decreased;
	}

	private boolean decreaseLinkState(AbstractAgent aAgent, Site site,
			boolean tested, int sizeOfProper) {
		// TODO optimize

		AbstractLinkState oldLinkState = new AbstractLinkState(aAgent.getSiteByName(
				site.getName()).getLinkState());

		Agent dualAgent = mapAfter.get(site.getParentAgent()
				.getIdInRuleHandside());
		if (oldLinkState.getStatusLink() == LinkStatus.FREE) {
			aAgent.getSiteByName(site.getName()).getLinkState().setWildLinkState();
			if (sizeOfProper == localviews.getCountOfCoherentAgent(aAgent)) {

				site.getLinkState().setWildLinkState();
				if (dualAgent != null) {
					Link linkState = dualAgent
							.getSiteByName(site.getName()).getLinkState();
					if (linkState.getStatusLink() == LinkStatus.FREE) {
						linkState.setWildLinkState();
					}
				}
				if (tested && site.getInternalState().hasDefaultName()) {
					site.getParentAgent().removeSite(site.getName());
					if (dualAgent != null) {
						dualAgent.removeSite(site.getName());
					}
				}
				return true;
			}
			aAgent.getSiteByName(site.getName()).setLinkState(oldLinkState);
			return false;
		} else {
			if (oldLinkState.getStatusLink() == LinkStatus.BOUND) {
				return false;
				
				//wildcard case
			} else {
				if (tested && site.getInternalState().hasDefaultName()) {
					site.getParentAgent().removeSite(site.getName());
					dualAgent.removeSite(site.getName());
					return true;
				}

				return false;
			}

		}

	}

	private boolean decreaseInternalState(AbstractAgent aAgent, Site site,
			int sizeOfProper) {
		// TODO optimize
		String oldInternal = aAgent.getSiteByName(site.getName()).getInternalState()
				.getName();
		aAgent.getSiteByName(site.getName()).getInternalState().setName(InternalState.DEFAULT_NAME);
		if (sizeOfProper == localviews.getCountOfCoherentAgent(aAgent)) {
			site.getInternalState().setName(InternalState.DEFAULT_NAME);
			return true;
		}
		aAgent.getSiteByName(site.getName()).getInternalState().setName(
				oldInternal);
		return false;
	}

}
