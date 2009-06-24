package com.plectix.simulator.components.complex.subviews.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;
import com.plectix.simulator.components.complex.subviews.storage.ISubViews;
import com.plectix.simulator.components.complex.subviews.storage.SubViewsExeption;
import com.plectix.simulator.interfaces.IConnectedComponent;

public class SubViewsRule {
	private List<AbstractAction> actions;
	private int ruleId;

	public SubViewsRule(CRule rule) {
		actions = new LinkedList<AbstractAction>();
		this.ruleId = rule.getRuleID();
		List<CAbstractAgent> left = initListAgents(rule.getLeftHandSide());
		List<CAbstractAgent> right = initListAgents(rule.getRightHandSide());
		initAtomicActions(left, right);
	}

	public void initActionsToSubViews(Map<Integer, List<ISubViews>> subViewsMap) {
		for (AbstractAction action : actions) {
			if (action.getRightHandSideAgent() == null)
				continue; // TODO Delete action!!!!!
			CAbstractAgent agent = action.getLeftHandSideAgent();
			if (agent == null)
				agent = action.getRightHandSideAgent();
			List<ISubViews> subViewsList = subViewsMap.get(agent.getNameId());
			for (ISubViews subViews : subViewsList) {
				if (subViews.isAgentFit(agent))
					action.addSubViews(subViews);
			}
		}
	}

	public boolean apply(Map<Integer, CAbstractAgent> agentNameIdToAgent,
			Map<Integer, List<ISubViews>> subViewsMap) throws SubViewsExeption {
		boolean isEnd = true;
		for (AbstractAction action : actions) {
			for (ISubViews subViews : action.getSubViews())
				if (subViews.test(action))
					isEnd = false;
		}
		if(isEnd)
			return false;
		boolean isAdd = false;
		for (AbstractAction action : actions)
			for (ISubViews subViews : action.getSubViews())
				if (subViews.burnRule(action))
					isAdd = true;
		return isAdd;
	}

	/**
	 * Util method. Uses for sort and creates list of abstract agent by given
	 * connected components.
	 * 
	 * @param listIn
	 *            given connected components
	 * @return list of abstract agent
	 */
	private List<CAbstractAgent> initListAgents(List<IConnectedComponent> listIn) {
		List<CAbstractAgent> listOut = new LinkedList<CAbstractAgent>();
		Map<Integer, CAbstractAgent> map = new HashMap<Integer, CAbstractAgent>();
		if (listIn == null)
			return listOut;
		for (IConnectedComponent c : listIn)
			for (CAgent a : c.getAgents()) {
				CAbstractAgent newAgent = new CAbstractAgent(a);
				for (CSite s : a.getSites()) {
					CAbstractSite newSite = new CAbstractSite(s, newAgent);
					newAgent.addSite(newSite);
				}
				map.put(a.getIdInRuleHandside(), newAgent);
			}

		List<Integer> indexList = new ArrayList<Integer>();

		indexList.addAll(map.keySet());
		Collections.sort(indexList);
		for (Integer i : indexList)
			listOut.add(map.get(i));
		return listOut;
	}

	/**
	 * This method initializes abstract atomic actions.
	 */
	private void initAtomicActions(List<CAbstractAgent> lhs,
			List<CAbstractAgent> rhs) {

		if (lhs.get(0).getNameId() == CSite.NO_INDEX) {
			addAgentsToAdd(rhs);
			return;
		}

		if (rhs.isEmpty()) {
			for (CAbstractAgent a : lhs) {
				addAgentToDelete(a);
			}
			return;
		}

		int i = 0;
		for (CAbstractAgent lhsAgent : lhs) {
			if (i >= rhs.size()) {
				addAgentToDelete(lhsAgent);
				continue;
			}
			CAbstractAgent rhsAgent = rhs.get(i++);
			if (isFit(lhsAgent, rhsAgent)) {
				actions.add(new AbstractAction(lhsAgent, rhsAgent));
			} else {
				addAgentToDelete(lhsAgent);
				addAgentToAdd(rhsAgent);
			}
		}
		for (int j = i; j < rhs.size(); j++) {
			CAbstractAgent rhsAgent = rhs.get(j);
			addAgentToAdd(rhsAgent);
		}

	}

	/**
	 * Util method. Uses only in {@link #initAtomicActions()}. Creates "ADD"
	 * action.
	 * 
	 * @param listIn
	 *            given list of agents
	 */
	private void addAgentsToAdd(List<CAbstractAgent> listIn) {
		for (CAbstractAgent a : listIn)
			addAgentToAdd(a);
	}

	/**
	 * Util method. Uses only in {@link #initAtomicActions()}. Creates "ADD"
	 * action.
	 * 
	 * @param agentIn
	 *            given agent
	 */
	private void addAgentToAdd(CAbstractAgent agentIn) {
		actions.add(new AbstractAction(null, agentIn));
	}

	/**
	 * Util method. Uses only in {@link #initAtomicActions()}. Creates "DELETE"
	 * action.
	 * 
	 * @param agentIn
	 *            given agent
	 */
	private void addAgentToDelete(CAbstractAgent agentIn) {
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
	private boolean isFit(CAbstractAgent a1, CAbstractAgent a2) {
		if (a1.getNameId() != a2.getNameId())
			return false;
		if (a1.getSitesMap().size() != a2.getSitesMap().size())
			return false;

		for (Map.Entry<Integer, CAbstractSite> entry : a1.getSitesMap()
				.entrySet()) {
			CAbstractSite s1 = entry.getValue();
			CAbstractSite s2 = a2.getSitesMap().get(entry.getKey());
			if ((s2 == null) || (s1.getNameId() != s2.getNameId()))
				return false;
		}
		return true;
	}

	public List<AbstractAction> getActions() {
		return actions;
	}

	public int getRuleId() {
		return ruleId;
	}
}
