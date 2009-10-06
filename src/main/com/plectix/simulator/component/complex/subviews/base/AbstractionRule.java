package com.plectix.simulator.component.complex.subviews.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.component.Agent;
import com.plectix.simulator.component.Rule;
import com.plectix.simulator.component.complex.abstracting.AbstractAgent;
import com.plectix.simulator.component.complex.abstracting.AbstractSite;
import com.plectix.simulator.component.complex.subviews.WrapperTwoSet;
import com.plectix.simulator.component.complex.subviews.storage.SubViewsExeption;
import com.plectix.simulator.component.complex.subviews.storage.SubViewsInterface;
import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.interfaces.ObservableConnectedComponentInterface;

public final class AbstractionRule {
	private final List<AbstractAction> actions;
	private final int ruleId;
	// TODO please correct me if I set the wrong name to this one
	private boolean wasApplied = false;
	private final ObservableConnectedComponentInterface observableComponent;
	private final List<AbstractAgent> leftHandSideAgents;
	private final List<AbstractAgent> rightHandSideAgents;

	public AbstractionRule(Rule rule) {
		actions = new LinkedList<AbstractAction>();
		this.ruleId = rule.getRuleId();
		this.leftHandSideAgents = initListAgents(rule.getLeftHandSide());
		this.rightHandSideAgents = initListAgents(rule.getRightHandSide());

		List<AbstractAgent> left = initListAgents(rule.getLeftHandSide());
		List<AbstractAgent> right = initListAgents(rule.getRightHandSide());
		initAtomicActions(left, right);
		observableComponent = null;
	}

	public AbstractionRule(ObservableConnectedComponentInterface observableComponent) {
		actions = new LinkedList<AbstractAction>();
		this.observableComponent = observableComponent;
		this.ruleId = observableComponent.getId();
		List<ConnectedComponentInterface> leftList = new LinkedList<ConnectedComponentInterface>();
		leftList.add(observableComponent);
		List<AbstractAgent> left = initListAgents(leftList);
		List<AbstractAgent> right = initListAgents(null);
		initAtomicActions(left, right);
		this.leftHandSideAgents = null;
		this.rightHandSideAgents = null;
	}

	public final void initActionsToSubViews(
			Map<String, List<SubViewsInterface>> subViews) {
		for (AbstractAction action : actions)
			action.initSubViews(subViews);
	}

	public final WrapperTwoSet apply(Map<String, AbstractAgent> agentNameToAgent,
			Map<String, List<SubViewsInterface>> subViewsMap)
			throws SubViewsExeption {

		if (!wasApplied) {
			for (AbstractAction action : actions) {
				boolean isEnd = true;
				if (action.canApply())
					continue;
				for (SubViewsInterface subViews : action.getSubViews())
					try {
						if (subViews.test(action)) {
							isEnd = false;
						} else if (action.getActionType() == AbstractActionType.TEST_ONLY
								|| action.getActionType() == AbstractActionType.DELETE)
							return null;
					} catch (SubViewsExeption e) {
						if (action.getActionType() != AbstractActionType.DELETE)
							e.printStackTrace();
					}
				if (isEnd)
					return null;
				action.setApplicable();
			}
		}

		WrapperTwoSet activatedRules = new WrapperTwoSet();
		wasApplied = true;
		for (AbstractAction action : actions) {
			if (action.getActionType() != AbstractActionType.TEST_ONLY) {
				action.clearSitesSideEffect();
				for (SubViewsInterface subViews : action.getSubViews()) {
					if (subViews.burnRule(action))
						activatedRules.firstSetAddAll(
								subViews.getSubViewClass().getRulesId());
				}
				for (List<SubViewsInterface> subViewsList : subViewsMap
						.values())
					for (SubViewsInterface subViews : subViewsList)
						if (subViews.burnBreakAllNeedLinkState(action))
							activatedRules.secondSetAddAll(
									subViews.getSubViewClass().getRulesId());
			}
		}
		if (!activatedRules.isEmpty()) {
			return activatedRules;
		} else {
			return null;

		}
	}

	/**
	 * Util method. Uses for sort and creates list of abstract agent by given
	 * connected components.
	 * 
	 * @param components
	 *            given connected components
	 * @return list of abstract agent
	 */
	private final List<AbstractAgent> initListAgents(
			List<ConnectedComponentInterface> components) {
		List<AbstractAgent> listOut = new LinkedList<AbstractAgent>();
		Map<Integer, AbstractAgent> map = new LinkedHashMap<Integer, AbstractAgent>();
		if (components == null)
			return listOut;
		for (ConnectedComponentInterface component : components)
			for (Agent agent : component.getAgents()) {
				AbstractAgent newAgent = new AbstractAgent(agent);
				map.put(agent.getIdInRuleHandside(), newAgent);
			}

		List<Integer> indexList = new ArrayList<Integer>();

		indexList.addAll(map.keySet());
		Collections.sort(indexList);
		for (int i : indexList)
			listOut.add(map.get(i));
		return listOut;
	}

	/**
	 * This method initializes abstract atomic actions.
	 */
	private final void initAtomicActions(List<AbstractAgent> leftHandSide,
			List<AbstractAgent> rightHandSide) {

		if (leftHandSide.get(0).hasDefaultName()) {
			addAgentsToAdd(rightHandSide);
			return;
		}

		if (rightHandSide.isEmpty()) {
			for (AbstractAgent a : leftHandSide) {
				addAgentToDelete(a);
			}
			return;
		}

		int i = 0;
		for (AbstractAgent lhsAgent : leftHandSide) {
			if (i >= rightHandSide.size()) {
				addAgentToDelete(lhsAgent);
				continue;
			}
			AbstractAgent rhsAgent = rightHandSide.get(i++);
			if (isFit(lhsAgent, rhsAgent)) {
				actions.add(new AbstractAction(lhsAgent, rhsAgent));
			} else {
				addAgentToDelete(lhsAgent);
				addAgentToAdd(rhsAgent);
			}
		}
		for (int j = i; j < rightHandSide.size(); j++) {
			AbstractAgent rhsAgent = rightHandSide.get(j);
			addAgentToAdd(rhsAgent);
		}

	}

	/**
	 * Util method. Uses only in {@link #initAtomicActions()}. Creates "ADD"
	 * action.
	 * 
	 * @param agents
	 *            given list of agents
	 */
	private final void addAgentsToAdd(List<AbstractAgent> agents) {
		for (AbstractAgent a : agents)
			addAgentToAdd(a);
	}

	/**
	 * Util method. Uses only in {@link #initAtomicActions()}. Creates "ADD"
	 * action.
	 * 
	 * @param agentIn
	 *            given agent
	 */
	private final void addAgentToAdd(AbstractAgent agentIn) {
		actions.add(new AbstractAction(null, agentIn));
	}

	/**
	 * Util method. Uses only in {@link #initAtomicActions()}. Creates "DELETE"
	 * action.
	 * 
	 * @param agentIn
	 *            given agent
	 */
	private final void addAgentToDelete(AbstractAgent agentIn) {
		actions.add(new AbstractAction(agentIn, null));
	}

	/**
	 * Util method. Compares given agents. Uses only in
	 * {@link #initAtomicActions()}
	 * 
	 * @param a1
	 *            given agent
	 * @param a2
	 *            given agent
	 * @return <tt>true</tt> if given agents are similar, otherwise
	 *         <tt>false</tt>
	 */
	private static final boolean isFit(AbstractAgent a1, AbstractAgent a2) {
		if (!a1.hasSimilarName(a2))
			return false;
		if (a1.getSitesMap().size() != a2.getSitesMap().size())
			return false;

		for (Map.Entry<String, AbstractSite> entry : a1.getSitesMap()
				.entrySet()) {
			AbstractSite s1 = entry.getValue();
			AbstractSite s2 = a2.getSitesMap().get(entry.getKey());
			if ((s2 == null) || (!s1.hasSimilarName(s2)))
				return false;
		}
		return true;
	}

	public final List<AbstractAction> getActions() {
		return actions;
	}

	public final int getRuleId() {
		return ruleId;
	}

	public final boolean isApply() {
		return wasApplied;
	}

	public final ObservableConnectedComponentInterface getObservableComponent() {
		return observableComponent;
	}

	public final List<AbstractAction> getLeftHandSideActions() {
		List<AbstractAction> outList = new LinkedList<AbstractAction>();
		for (AbstractAction action : actions)
			if (action.getActionType() != AbstractActionType.ADD)
				outList.add(action);
		return outList;
	}

	/**
	 * This method returns agents, necessary for "focus rule".
	 * 
	 * @return necessary agents.
	 */
	public final Collection<AbstractAgent> getFocusedAgents() {
		Collection<AbstractAgent> agentsList = new LinkedHashSet<AbstractAgent>();
		for (AbstractAction action : actions) {
			if (action.getActionType() == AbstractActionType.ADD) {
				AbstractAgent agent = action.getLeftHandSideAgent();
				if (!agent.includedInCollection(agentsList)) {
					agentsList.add(agent);
				}
			}
			if (action.getActionType() == AbstractActionType.DELETE) {
				AbstractAgent agent = action.getRightHandSideAgent();
				if (!agent.includedInCollection(agentsList)) {
					agentsList.add(agent);
				}
			}
			if (action.getActionType() == AbstractActionType.TEST_AND_MODIFICATION) {
				AbstractAgent agent = action.getLeftHandSideAgent();
				if (!agent.includedInCollection(agentsList)) {
					agentsList.add(agent);
				}
				AbstractAgent agent2 = action.getRightHandSideAgent();
				if (!agent2.includedInCollection(agentsList)) {
					agentsList.add(agent2);
				}
			}
		}

		return agentsList;
	}

	/**
	 * This method returns agents from left handSide current rule.
	 * 
	 * @return agents from left handSide current rule.
	 */
	public final List<AbstractAgent> getLeftHandSideAgents() {
		return leftHandSideAgents;
	}

	/**
	 * This method returns agents from right handSide current rule.
	 * 
	 * @return agents from right handSide current rule.
	 */
	public final List<AbstractAgent> getRightHandSideAgents() {
		return rightHandSideAgents;
	}
}
