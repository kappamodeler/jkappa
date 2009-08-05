package com.plectix.simulator.components.complex.subviews.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractLinkState;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;
import com.plectix.simulator.components.complex.subviews.WrapperTwoSet;
import com.plectix.simulator.components.complex.subviews.storage.ISubViews;
import com.plectix.simulator.components.complex.subviews.storage.SubViewsExeption;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IObservablesConnectedComponent;

public class AbstractionRule {
	private List<AbstractAction> actions;
	private int ruleId;
	private boolean isApply = false;
	private IObservablesConnectedComponent obsCC;
	
	private List<CAbstractAgent> lhsAgents;
	private List<CAbstractAgent> rhsAgents;


	public AbstractionRule(CRule rule) {
		actions = new LinkedList<AbstractAction>();
		this.ruleId = rule.getRuleID();
		
		this.lhsAgents = initListAgents(rule.getLeftHandSide());
		this.rhsAgents = initListAgents(rule.getRightHandSide());

		List<CAbstractAgent> left = initListAgents(rule.getLeftHandSide());
		List<CAbstractAgent> right = initListAgents(rule.getRightHandSide());
		initAtomicActions(left, right);
	}
	
	
	public AbstractionRule(IObservablesConnectedComponent cc){
		actions = new LinkedList<AbstractAction>();
		obsCC = cc;
		this.ruleId = cc.getId();
		List<IConnectedComponent> leftList = new LinkedList<IConnectedComponent>();
		leftList.add(cc);
		List<CAbstractAgent> left = initListAgents(leftList);
		List<CAbstractAgent> right = initListAgents(null);
		initAtomicActions(left, right);
	}

	public void initActionsToSubViews(Map<Integer, List<ISubViews>> subViewsMap) {
		for (AbstractAction action : actions)
			action.initSubViews(subViewsMap);
	}

	public WrapperTwoSet apply(
			Map<Integer, CAbstractAgent> agentNameIdToAgent,
			Map<Integer, List<ISubViews>> subViewsMap) throws SubViewsExeption {

		if (!isApply) {
			for (AbstractAction action : actions) {
				boolean isEnd = true;
				if (action.canApply())
					continue;
				for (ISubViews subViews : action.getSubViews())
					try {
						if (subViews.test(action)) {
							isEnd = false;
						} else if (action.getActionType() == EAbstractActionType.TEST_ONLY
								|| action.getActionType() == EAbstractActionType.DELETE)
							return null;
					} catch (SubViewsExeption e) {
						if (action.getActionType() != EAbstractActionType.DELETE)
							e.printStackTrace();
					}
				if (isEnd)
					return null;
				action.setApply(true);
			}
		}

		WrapperTwoSet activatedRules = new WrapperTwoSet();
		isApply = true;
		for (AbstractAction action : actions) {
			if (action.getActionType() != EAbstractActionType.TEST_ONLY) {
				action.clearSitesSideEffect();
				for (ISubViews subViews : action.getSubViews()) {
					if (subViews.burnRule(action))
						activatedRules.getFirst().addAll(subViews.getSubViewClass()
								.getRulesId());
				}
				for (List<ISubViews> subViewsList : subViewsMap.values())
					for (ISubViews subViews : subViewsList)
						if (subViews.burnBreakAllNeedLinkState(action))
							activatedRules.getSecond().addAll(subViews.getSubViewClass()
									.getRulesId());
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
	 * @param listIn
	 *            given connected components
	 * @return list of abstract agent
	 */
	private List<CAbstractAgent> initListAgents(List<IConnectedComponent> listIn) {
		List<CAbstractAgent> listOut = new LinkedList<CAbstractAgent>();
		Map<Integer, CAbstractAgent> map = new LinkedHashMap<Integer, CAbstractAgent>();
		if (listIn == null)
			return listOut;
		for (IConnectedComponent c : listIn)
			for (CAgent a : c.getAgents()) {
				CAbstractAgent newAgent = new CAbstractAgent(a);
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


	public boolean isApply() {
		return isApply;
	}
	
	public IObservablesConnectedComponent getObsConnectedComponent(){
		return obsCC;
	}

	public List<AbstractAction> getLHSActions() {
		List<AbstractAction> outList = new LinkedList<AbstractAction>();
		for (AbstractAction action : actions)
			if (action.getActionType() != EAbstractActionType.ADD)
				outList.add(action);
		return outList;
	}
	
//	/**
//	 * This method initializes current rule.<br>
//	 * Initializes abstract actions.
//	 */
//	public void initAbstractRule() {
//		//markAddNecessaryAgents();
//	}
	
	/**
	 * This method returns agents, necessary for "focus rule".
	 * 
	 * @return necessary agents.
	 */
	public List<CAbstractAgent> getFocusedAgents() {
//		List<CAbstractAgent> listOut = new ArrayList<CAbstractAgent>();
//		listOut.addAll(getAddAgents(lhsAgents));
//		listOut.addAll(getAddAgents(rhsAgents));
//		for (CAbstractAgent agent : listOut) {
//			if (!agent.includedInCollection(agentsList)) {
//				agentsList.add(agent);
//			}
//		}
		
		List<CAbstractAgent> agentsList = new LinkedList<CAbstractAgent>();
		for(AbstractAction action : actions){
			if(action.getActionType()==EAbstractActionType.ADD){
				CAbstractAgent agent = action.getLeftHandSideAgent();
				if (!agent.includedInCollection(agentsList)) {
					agentsList.add(agent);
				}
			}
			if(action.getActionType()==EAbstractActionType.DELETE){
				CAbstractAgent agent = action.getRightHandSideAgent();
				if (!agent.includedInCollection(agentsList)) {
					agentsList.add(agent);
				}
			}
			if(action.getActionType()==EAbstractActionType.TEST_AND_MODIFICATION){
				CAbstractAgent agent = action.getLeftHandSideAgent();
				if (!agent.includedInCollection(agentsList)) {
					agentsList.add(agent);
				}
				CAbstractAgent agent2 = action.getRightHandSideAgent();
				if (!agent2.includedInCollection(agentsList)) {
					agentsList.add(agent2);
				}
			}
		}
		

		return agentsList;
	}
	
//	/**
//	 * Util method. Uses for {@link #getFocusedAgents()}.<br>
//	 * Finds necessary agents.
//	 * 
//	 * @param listIn
//	 *            given list for finds.
//	 * @return necessary agents.
//	 */
//	private List<CAbstractAgent> getAddAgents(List<CAbstractAgent> listIn) {
//		List<CAbstractAgent> listOut = new ArrayList<CAbstractAgent>();
//		if (listIn.isEmpty() || listIn.get(0).getNameId() == CSite.NO_INDEX)
//			return listOut;
//		for (CAbstractAgent a : listIn)
//			if (a.isAdd())
//				listOut.add(a);
//		return listOut;
//	}
	/**
	 * This method returns agents from left handSide current rule.
	 * 
	 * @return agents from left handSide current rule.
	 */
	public List<CAbstractAgent> getLhsAgents() {
		return lhsAgents;
	}
	
	/**
	 * This method returns agents from right handSide current rule.
	 * 
	 * @return agents from right handSide current rule.
	 */
	public List<CAbstractAgent> getRhsAgents() {
		return rhsAgents;
	}
	
//	private void markAddNecessaryAgents() {
//
//		if (lhsAgents.get(0).getNameId() == CSite.NO_INDEX) {
//			markAgentsToAdd(rhsAgents);
//			return;
//		}
//
//		if (rhsAgents.isEmpty()) {
//			markAgentsToAdd(lhsAgents);
//			return;
//		}
//
//		int i = 0;
//		for (CAbstractAgent lhsAgent : lhsAgents) {
//			if (i >= rhsAgents.size()) {
//				lhsAgent.shouldAdd();
//				continue;
//			}
//			CAbstractAgent rhsAgent = rhsAgents.get(i++);
//			if (isFit(lhsAgent, rhsAgent)) {
//				markAddIfNecessary(lhsAgent, rhsAgent);
//			} else {
//				lhsAgent.shouldAdd();
//				rhsAgent.shouldAdd();
//			}
//		}
//		for (int j = i; j < rhsAgents.size(); j++) {
//			rhsAgents.get(j).shouldAdd();
//		}
//
//	}
//
//	private void markAddIfNecessary(CAbstractAgent fromAgent,
//			CAbstractAgent toAgent) {
//		if (toAgent == null) {
//			fromAgent.shouldAdd();
//			return;
//		}
//		for (Map.Entry<Integer, CAbstractSite> entry : fromAgent.getSitesMap()
//				.entrySet()) {
//			CAbstractSite siteFrom = entry.getValue();
//			CAbstractSite siteTo = toAgent.getSitesMap().get(entry.getKey());
//
//			if (siteFrom.getInternalState().getNameId() != siteTo
//					.getInternalState().getNameId()) {
//				fromAgent.shouldAdd();
//				toAgent.shouldAdd();
//			}
//
//			CAbstractLinkState lsFrom = siteFrom.getLinkState();
//			CAbstractLinkState lsTo = siteTo.getLinkState();
//
//			if (lsFrom.getAgentNameID() != lsTo.getAgentNameID()
//					|| (lsFrom.getLinkSiteNameID() != lsTo.getLinkSiteNameID())) {
//				fromAgent.shouldAdd();
//				toAgent.shouldAdd();
//			}
//		}
//	}
//
//	/**
//	 * Util method. Uses only in {@link #initAtomicActions()}. Creates "ADD"
//	 * action.
//	 * 
//	 * @param listIn
//	 *            given list of agents
//	 */
//	private void markAgentsToAdd(List<CAbstractAgent> listIn) {
//		for (CAbstractAgent a : listIn)
//			a.shouldAdd();
//	}
}
