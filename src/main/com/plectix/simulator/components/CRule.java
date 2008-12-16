package com.plectix.simulator.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.plectix.simulator.components.actions.CAddAction;
import com.plectix.simulator.components.actions.CDefaultAction;
import com.plectix.simulator.components.actions.CDeleteAction;
import com.plectix.simulator.interfaces.IAction;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IAgentLink;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IConstraint;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.IInternalState;
import com.plectix.simulator.interfaces.ILinkState;
import com.plectix.simulator.interfaces.INetworkNotation;
import com.plectix.simulator.interfaces.IObservables;
import com.plectix.simulator.interfaces.IObservablesConnectedComponent;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.simulator.Simulator;

public class CRule implements IRule, Serializable {

	public static final IConnectedComponent EMPTY_LHS_CC = new CConnectedComponent(
			CConnectedComponent.EMPTY);
	private List<IConnectedComponent> leftHandSide;
	private List<IConnectedComponent> rightHandSide;
	private double activity = 0.;
	private final String name;
	private String data;
	private double ruleRate;
	private List<ISite> sitesConnectedWithDeleted;
	private List<ISite> sitesConnectedWithBroken;

	private int automorphismNumber = 1;
	private boolean infinityRate = false;
	private List<IRule> activatedRule;
	private List<IRule> inhibitedRule;

	private List<IObservablesConnectedComponent> activatedObservable;
	private List<IObservablesConnectedComponent> inhibitedObservable;

	public List<IRule> getInhibitedRule() {
		return inhibitedRule;
	}

	public List<IObservablesConnectedComponent> getInhibitedObservable() {
		return inhibitedObservable;
	}

	private int ruleID;
	private List<IAction> actionList;
	private Map<IAgent, IAgent> agentAddList;
	private List<IInjection> injList;
	private List<ISite> changedActivatedSites;
	private List<ChangedSite> changedInhibitedSites;
	private List<ChangedSite> fixedSites;
	private IConstraint constraints;
	private int countAgentsLHS = 0;
	private final boolean isStorify;

	public final String getData(boolean isOcamlStyleObsName) {
		if (data == null) {
			String line = Simulator.printPartRule(leftHandSide,
					isOcamlStyleObsName);
			line = line + "->";
			line = line
					+ Simulator.printPartRule(rightHandSide,
							isOcamlStyleObsName);
			data = line;
		}
		return data;
	}

	public final void setData(String data) {
		this.data = new String(data);
	}

	public CRule(List<IConnectedComponent> left,
			List<IConnectedComponent> right, String name, double ruleRate,
			int ruleID, boolean isStorify) {
		this.leftHandSide = left;
		this.rightHandSide = right;
		this.isStorify = isStorify;
		setConnectedComponentLinkRule(left);
		setConnectedComponentLinkRule(right);
		if (leftHandSide == null) {
			leftHandSide = new ArrayList<IConnectedComponent>();
			leftHandSide.add(EMPTY_LHS_CC);
		}
		for (IConnectedComponent cc : this.leftHandSide) {
			cc.initSpanningTreeMap();
		}
		if (ruleRate == Double.MAX_VALUE) {
			this.infinityRate = true;
			this.ruleRate = 1;
		} else
			this.ruleRate = ruleRate;

		this.name = name;
		this.ruleID = ruleID;
		calculateAutomorphismsNumber();
		indexingRHSAgents();
	}

	public final int getCountAgentsLHS() {
		return countAgentsLHS;
	}

	public final int getRuleID() {
		return ruleID;
	}

	public final void setRuleID(int id) {
		this.ruleID = id;
	}

	public final void setRuleRate(double ruleRate) {
		if (ruleRate >= 0) {
			this.ruleRate = ruleRate;
		} else {
			Logger logger = Logger.getLogger(this.getClass());
			logger.info("warning : rate of the rule '" + name
					+ "' was attempted to be set as negative");
			this.ruleRate = 0;
		}
	}

	public final List<IObservablesConnectedComponent> getActivatedObservable() {
		return Collections.unmodifiableList(activatedObservable);
	}

	public final List<IRule> getActivatedRule() {
		return Collections.unmodifiableList(activatedRule);
	}

	public final List<ISite> getChangedSites() {
		return Collections.unmodifiableList(changedActivatedSites);
	}

	public final List<IAction> getActionList() {
		return Collections.unmodifiableList(actionList);
	}

	public final void setActivatedObservable(
			List<IObservablesConnectedComponent> activatedObservable) {
		this.activatedObservable = activatedObservable;
	}

	public final void setActivatedRule(List<IRule> activatedRule) {
		this.activatedRule = activatedRule;
	}

	public final boolean isInfinityRate() {
		return infinityRate;
	}

	public final void setInfinityRate(boolean infinityRate) {
		this.infinityRate = infinityRate;
	}

	public final int getAutomorphismNumber() {
		return automorphismNumber;
	}

	public final double getRuleRate() {
		return ruleRate;
	}

	// private final void markedLHS() {
	// int counter = 1;
	// if (leftHandSide.get(0) == EMPTY_LHS_CC) {
	// maxAgentID = 1;
	// return;
	// }
	// for (IConnectedComponent cc : leftHandSide)
	// for (IAgent agent : cc.getAgents()) {
	// agent.setIdInRuleSide(counter);
	// counter++;
	// }
	// maxAgentID = counter;
	// }

	private final Map<Integer, List<IAgent>> createAgentMap(
			List<IConnectedComponent> ccList) {
		HashMap<Integer, List<IAgent>> map = new HashMap<Integer, List<IAgent>>();
		if (ccList == null)
			return map;
		for (IConnectedComponent cc : ccList)
			for (IAgent agent : cc.getAgents()) {
				List<IAgent> list = map.get(agent.getNameId());
				if (list == null) {
					list = new ArrayList<IAgent>();
					map.put(agent.getNameId(), list);
				}
				list.add(agent);
			}
		return map;
	}

	public final void applyRuleForStories(List<IInjection> injectionList,
			INetworkNotation netNotation, Simulator simulator) {
		apply(injectionList, netNotation, simulator);
	}

	public void applyRule(List<IInjection> injectionList, Simulator simulator) {
		apply(injectionList, null, simulator);
	}

	public final IAgent getAgentAdd(IAgent key) {
		return agentAddList.get(key);
	}

	public final void putAgentAdd(IAgent key, IAgent value) {
		agentAddList.put(key, value);
	}

	private final void storifyAgents(List<IInjection> injectionList) {
		for (IInjection inj : injectionList)
			if (inj != CInjection.EMPTY_INJECTION)
				for (IAgentLink al : inj.getAgentLinkList())
					al.storifyAgent();
	}

	protected final void apply(List<IInjection> injectionList,
			INetworkNotation netNotation, Simulator simulator) {
		if (netNotation != null) {
			storifyAgents(injectionList);
		}

		agentAddList = new HashMap<IAgent, IAgent>();
		sitesConnectedWithDeleted = new ArrayList<ISite>();
		sitesConnectedWithBroken = new ArrayList<ISite>();
		this.injList = injectionList;
		if (rightHandSide != null) {
			for (IConnectedComponent cc : rightHandSide) {
				cc.clearAgentsFromSolutionForRHS();
			}
		}

		for (IAction action : actionList) {
			if (action.getLeftCComponent() == null) {
				action.doAction(null, netNotation, simulator);
			} else {
				action.doAction(injectionList.get(leftHandSide.indexOf(action
						.getLeftCComponent())), netNotation, simulator);
			}
		}

		if (netNotation != null) {
			addFixedSitesToNN(netNotation);
		}
	}

	private final void addRuleSitesToNetworkNotation(
			INetworkNotation netNotation, ISite site) {
		if (netNotation != null) {
			byte agentMode = CNetworkNotation.MODE_NONE;
			byte linkStateMode = CNetworkNotation.MODE_NONE;
			byte internalStateMode = CNetworkNotation.MODE_NONE;
			linkStateMode = CNetworkNotation.MODE_MODIFY;
			netNotation.addToAgentsFromRules(site, agentMode,
					internalStateMode, linkStateMode);
		}
	}

	private final void addFixedSitesToNN(INetworkNotation netNotation) {
		for (ISite siteInSolution : this.sitesConnectedWithBroken)
			addRuleSitesToNetworkNotation(netNotation, siteInSolution);

		for (ISite siteInSolution : this.sitesConnectedWithDeleted)
			addRuleSitesToNetworkNotation(netNotation, siteInSolution);

		for (ChangedSite fs : fixedSites) {
			CSite siteFromRule = (CSite) fs.getSite();
			IInjection inj = getInjectionBySiteToFromLHS(siteFromRule);
			IAgent agentToInSolution = inj.getConnectedComponent()
					.getAgentByIdFromSolution(
							siteFromRule.getAgentLink()
									.getIdInConnectedComponent(), inj);
			ISite site = agentToInSolution.getSite(siteFromRule.getNameId());
			// add fixed agents
			netNotation.addFixedSitesFromRules(site,
					CNetworkNotation.MODE_TEST, fs.isInternalState(), fs
							.isLinkState());
		}
	}

	private final void markRHSAgents() {
		List<IAgent> rhsAgents = new ArrayList<IAgent>();
		List<IAgent> lhsAgents = new ArrayList<IAgent>();
		int indexAgentRHS = 0;
		fixedSites = new ArrayList<ChangedSite>();

		if (leftHandSide.get(0) == EMPTY_LHS_CC) {
			countAgentsLHS = 0;
			markRHSAgentsUnmarked(0);
			return;
		} else {
			for (IConnectedComponent cc : leftHandSide) {
				indexAgentRHS = indexAgentRHS + cc.getAgents().size();
				lhsAgents.addAll(cc.getAgents());
			}
			countAgentsLHS = indexAgentRHS;
		}

		if (rightHandSide == null)
			return;
		for (IConnectedComponent cc : rightHandSide) {
			rhsAgents.addAll(cc.getAgents());
		}
		sortAgentsByRuleSide(rhsAgents);
		sortAgentsByRuleSide(lhsAgents);

		int index = 0;
		for (IAgent lhsAgent : lhsAgents) {
			if ((index < rhsAgents.size())
					&& !(rhsAgents.get(index).equals(lhsAgent) && rhsAgents
							.get(index).getSiteMap().equals(
									lhsAgent.getSiteMap()))) {
				// rhsAgents.get(index)
				// .setIdInRuleSide(lhsAgent.getIdInRuleSide());
				// } else {
				break;
			}
			// filling of fixed agents
			if (index < rhsAgents.size() && isStorify)
				fillFixedSites(lhsAgent, rhsAgents.get(index));
			index++;
		}

		for (int i = index; i < rhsAgents.size(); i++) {
			if (index == 0)
				rhsAgents.get(i).setIdInRuleSide(lhsAgents.size() + i + 1);
			else
				rhsAgents.get(i).setIdInRuleSide(lhsAgents.size() + i);
		}
	}

	private final void fillFixedSites(IAgent lhsAgent, IAgent rhsAgent) {
		for (ISite lhsSite : lhsAgent.getSites()) {
			ISite rhsSite = rhsAgent.getSite(lhsSite.getNameId());
			ChangedSite fixedSite = new ChangedSite(lhsSite);
			if (lhsSite.getInternalState().equals(rhsSite.getInternalState())
					&& lhsSite.getInternalState().getNameId() != CSite.NO_INDEX) {
				fixedSite.setInternalState(true);
			}
			if (lhsSite.getLinkState().getSite() == null
					&& rhsSite.getLinkState().getSite() == null) {
				fixedSite.setLinkState(true);
			}
			if (lhsSite.getLinkState().getSite() != null
					&& rhsSite.getLinkState().getSite() != null) {
				if (lhsSite.getLinkState().getSite().equals(
						rhsSite.getLinkState().getSite())
						&& lhsSite.getLinkState().getSite().getAgentLink()
								.getIdInRuleSide() == rhsSite.getLinkState()
								.getSite().getAgentLink().getIdInRuleSide())
					fixedSite.setLinkState(true);
			}
			if (fixedSite.isLinkState() || fixedSite.isInternalState())
				fixedSites.add(fixedSite);
		}
	}

	private final void sortAgentsByRuleSide(List<IAgent> list) {
		IAgent left;
		IAgent right;
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = i + 1; j < list.size(); j++) {
				left = list.get(i);
				right = list.get(j);
				if (left.getIdInRuleSide() > right.getIdInRuleSide()) {
					list.set(i, right);
					list.set(j, left);
				}
			}
		}
	}

	private final void markRHSAgentsUnmarked(int indexAgentRHS) {
		for (IConnectedComponent cc : rightHandSide)
			for (IAgent agent : cc.getAgents())
				if (agent.getIdInRuleSide() == CAgent.UNMARKED)
					agent.setIdInRuleSide(indexAgentRHS++);
	}

	private final void indexingRHSAgents() {
		// markedLHS();
		Map<Integer, List<IAgent>> lhsAgentMap = createAgentMap(leftHandSide);
		Map<Integer, List<IAgent>> rhsAgentMap = createAgentMap(rightHandSide);
		markRHSAgents();

		createActionList();

		sortActionList();

	}

	// TODO use standard sort
	private final void sortActionList() {
		for (int i = 0; i < actionList.size(); i++) {
			for (int j = 0; j < actionList.size(); j++) {
				if (actionList.get(i).getTypeId() < actionList.get(j)
						.getTypeId()) {
					IAction actionMin = actionList.get(j);
					IAction actionR = actionList.get(i);
					actionList.set(j, actionR);
					actionList.set(i, actionMin);
				}
			}
		}
	}

	private final boolean isActivated(List<IAgent> agentsFromAnotherRules) {
		for (IAgent agent : agentsFromAnotherRules) {
			if (this.rightHandSide != null && checkRulesNullAgents(agent))
				return true;
			for (ISite site : agent.getSites()) {
				for (ISite changedSite : changedActivatedSites) {
					if (changedSite.equals(site)) {
						IInternalState currentInternalState = changedSite
								.getInternalState();
						IInternalState internalState = site.getInternalState();
						if (!(currentInternalState.isRankRoot())
								&& !(internalState.isRankRoot())) {
							if (internalState.getNameId() != currentInternalState
									.getNameId())
								continue;
						}

						ILinkState currentLinkState = changedSite
								.getLinkState();
						ILinkState linkState = site.getLinkState();

						if (currentLinkState.isLeftBranchStatus()
								&& linkState.isLeftBranchStatus())
							return true;

						if (currentLinkState.isLeftBranchStatus()
								&& linkState.getStatusLinkRank() == CLinkState.RANK_BOUND_OR_FREE)
							return true;

						if (currentLinkState.isLeftBranchStatus()
								&& (!(linkState.isLeftBranchStatus())))
							continue;

						if ((!(currentLinkState.isLeftBranchStatus()))
								&& linkState.isLeftBranchStatus())
							continue;

						if (currentLinkState.getStatusLinkRank() == linkState
								.getStatusLinkRank()
								&& currentLinkState.getStatusLinkRank() == CLinkState.RANK_BOUND)
							if (!(currentLinkState.getSite().equals(linkState
									.getSite())))
								continue;

						if (currentLinkState.getStatusLinkRank() == linkState
								.getStatusLinkRank()
								&& currentLinkState.getStatusLinkRank() == CLinkState.RANK_BOUND)
							if (currentLinkState.getSite().equals(
									linkState.getSite())
									&& (currentLinkState.getSite()
											.getInternalState().getNameId() != linkState
											.getSite().getInternalState()
											.getNameId()))
								continue;

						if (currentLinkState.getStatusLinkRank() >= linkState
								.getStatusLinkRank())
							return true;

						return true;
					}
				}
			}
		}
		return false;
	}

	private final boolean isInhibited(List<IAgent> agentsFromAnotherRules) {
		for (IAgent agent : agentsFromAnotherRules) {
			if (this.leftHandSide != null && checkRulesNullAgents(agent))
				return true;
			for (ISite site : agent.getSites()) {
				for (ChangedSite changedSite : changedInhibitedSites) {
					if (changedSite.getSite().equals(site)) {

						IInternalState currentInternalState = changedSite
								.getSite().getInternalState();
						IInternalState internalState = site.getInternalState();

						ILinkState currentLinkState = changedSite.getSite()
								.getLinkState();
						ILinkState linkState = site.getLinkState();

						if (!changedSite.isInternalState()
								&& changedSite.isLinkState()) {
							if (!(currentInternalState.isRankRoot())
									&& !(internalState.isRankRoot())) {
								if (internalState.getNameId() == currentInternalState
										.getNameId()) {
									if (checkInhibitedLinkStates(
											currentLinkState, linkState))
										return true;
								}
							}

							if (currentInternalState.isRankRoot()) {
								if (checkInhibitedLinkStates(currentLinkState,
										linkState))
									return true;
							}
						}

						if (changedSite.isInternalState()) {
							if (!(currentInternalState.isRankRoot())
									&& !(internalState.isRankRoot())) {
								if (internalState.getNameId() == currentInternalState
										.getNameId()) {
									if (linkState.getStatusLinkRank() == CLinkState.RANK_BOUND_OR_FREE)
										return true;
									if (checkInhibitedLinkStates(
											currentLinkState, linkState))
										return true;
								}
							}

							if (currentInternalState.isRankRoot()) {
								if (currentLinkState.getStatusLinkRank() == CLinkState.RANK_FREE
										&& linkState.getStatusLinkRank() == CLinkState.RANK_BOUND_OR_FREE)
									return true;
								if (checkInhibitedLinkStates(currentLinkState,
										linkState))
									return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private boolean checkInhibitedLinkStates(ILinkState currentLinkState,
			ILinkState linkState) {
		if (currentLinkState.getStatusLinkRank() == CLinkState.RANK_FREE
				&& linkState.getStatusLinkRank() == CLinkState.RANK_FREE)
			return true;
		if (currentLinkState.getStatusLinkRank() == CLinkState.RANK_BOUND
				&& linkState.getStatusLinkRank() == CLinkState.RANK_SEMI_LINK)
			return true;
		if (currentLinkState.getStatusLinkRank() == CLinkState.RANK_BOUND
				&& linkState.getStatusLinkRank() == CLinkState.RANK_BOUND)
			if (currentLinkState.getSite().equals(linkState.getSite()))
				return true;
		return false;
	}

	private final boolean checkRulesNullAgents(IAgent agent) {
		if (this.rightHandSide != null)
			for (IConnectedComponent cc : this.getRightHandSide())

				for (IAgent agentFromRule : cc.getAgents())
					if (agent.equals(agentFromRule))
						if (agentFromRule.getSites().size() == 0
								|| agent.getSites().size() == 0)
							return true;
		return false;
	}

	public final void createActivatedRulesList(List<IRule> rules) {
		activatedRule = new ArrayList<IRule>();
		for (IRule rule : rules) {
			// if (this != rule)
			for (IConnectedComponent cc : rule.getLeftHandSide()) {
				if (isActivated(cc.getAgents())) {
					activatedRule.add(rule);
					break;
				}
			}
		}
	}

	public final void createInhibitedRulesList(List<IRule> rules) {
		inhibitedRule = new ArrayList<IRule>();
		for (IRule rule : rules) {
			if (this != rule)
				for (IConnectedComponent cc : rule.getLeftHandSide()) {
					if (isInhibited(cc.getAgents())) {
						inhibitedRule.add(rule);
						break;
					}
				}
		}
	}

	public final void createActivatedObservablesList(IObservables observables) {
		activatedObservable = new ArrayList<IObservablesConnectedComponent>();
		for (IObservablesConnectedComponent obsCC : observables
				.getConnectedComponentList()) {
			if (obsCC.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX
					&& isActivated(obsCC.getAgents())) {
				activatedObservable.add(obsCC);
			}
		}
	}

	public final void createInhibitedObservablesList(IObservables observables) {
		inhibitedObservable = new ArrayList<IObservablesConnectedComponent>();
		for (IObservablesConnectedComponent obsCC : observables
				.getConnectedComponentList()) {
			if (obsCC.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX
					&& isInhibited(obsCC.getAgents())) {
				inhibitedObservable.add(obsCC);
			}
		}
	}

	private final void createActionList() {
		changedActivatedSites = new ArrayList<ISite>();
		changedInhibitedSites = new ArrayList<ChangedSite>();
		actionList = new ArrayList<IAction>();

		if (rightHandSide == null) {
			for (IConnectedComponent ccL : leftHandSide)
				for (IAgent lAgent : ccL.getAgents()) {
					actionList.add(new CDeleteAction(this, lAgent, ccL));
					fillInhibitedChangedSites(lAgent);// for
					// inhibition
					// map
					// creation
				}
			return;
		}

		for (IConnectedComponent ccR : rightHandSide) {
			for (IAgent rAgent : ccR.getAgents()) {
				if ((countAgentsLHS == 0)
						|| (rAgent.getIdInRuleSide() > countAgentsLHS)) {
					actionList.add(new CAddAction(this, rAgent, ccR));
					fillChangedSites(rAgent);// for
					// activation
					// map
					// creation
				}
			}
		}

		if (leftHandSide.get(0) == EMPTY_LHS_CC)
			return;
		for (IConnectedComponent ccL : leftHandSide)
			for (IAgent lAgent : ccL.getAgents()) {
				if (!isAgentFromLHSHasFoundInRHS(lAgent, ccL))
					actionList.add(new CDeleteAction(this, lAgent, ccL));
			}
	}

	private final void fillChangedSites(IAgent agentRight) {
		// if (agentLeft == null)
		for (ISite site : agentRight.getSites()) {
			changedActivatedSites.add(site);
		}
	}

	private final void fillInhibitedChangedSites(IAgent agentRight) {
		// if (agentLeft == null)
		for (ISite site : agentRight.getSites()) {
			changedInhibitedSites.add(new ChangedSite(site, true, true));
		}
	}

	private final boolean isAgentFromLHSHasFoundInRHS(IAgent lAgent,
			IConnectedComponent ccL) {
		for (IConnectedComponent ccR : rightHandSide)
			for (IAgent rAgent : ccR.getAgents()) {
				if (lAgent.getIdInRuleSide() == rAgent.getIdInRuleSide()) {
					IAction newAction = new CDefaultAction(this, lAgent,
							rAgent, ccL, ccR);
					actionList.add(newAction);
					actionList.addAll(newAction.createAtomicActions());
					return true;
				}
			}
		return false;
	}

	// TODO util??
	public final List<IAgent> getAgentsFromConnectedComponent(
			List<IConnectedComponent> ccList) {
		List<IAgent> agentList = new ArrayList<IAgent>();
		if (ccList.get(0).getAgents().get(0).getIdInRuleSide() == CAgent.UNMARKED)
			return agentList;
		for (IConnectedComponent cc : ccList)
			for (IAgent agent : cc.getAgents())
				agentList.add(agent);

		return Collections.unmodifiableList(agentList);
	}

	// TODO is this necessary?
	private final IAgent getAgentByIdInRuleFromLHS(Integer id) {
		for (IAgent agent : getAgentsFromConnectedComponent(leftHandSide))
			if (agent.getIdInRuleSide() == id)
				return agent;
		return null;
	}

	public final void setAutomorphismNumber(int automorphismNumber) {
		this.automorphismNumber = automorphismNumber;
	}

	private final void calculateAutomorphismsNumber() {
		if (leftHandSide != null)
			if (this.leftHandSide.size() == 2) {
				if (this.leftHandSide.get(0).getAgents().size() == this.leftHandSide
						.get(1).getAgents().size())
					if (this.leftHandSide.get(0).isAutomorphism(
							this.leftHandSide.get(1).getAgents().get(0)))
						automorphismNumber = 2;
			}
	}

	private final void setConnectedComponentLinkRule(
			List<IConnectedComponent> cList) {
		if (cList == null)
			return;
		for (IConnectedComponent cc : cList)
			cc.setRule(this);
	}

	public final void calcultateActivity() {
		activity = 1.;
		for (IConnectedComponent cc : this.leftHandSide) {
			activity *= cc.getInjectionsList().size();
		}
		activity *= ruleRate;
		activity /= automorphismNumber;
	}

	public final String getName() {
		return name;
	}

	public final double getActivity() {
		return activity;
	}

	public final void setActivity(Double activity) {
		this.activity = activity;
	}

	public final List<IConnectedComponent> getLeftHandSide() {
		if (leftHandSide == null) {
			return null;
		} else {
			return Collections.unmodifiableList(leftHandSide);
		}
	}

	public final List<IConnectedComponent> getRightHandSide() {
		if (rightHandSide == null) {
			return null;
		} else {
			return Collections.unmodifiableList(rightHandSide);
		}
	}

	public final boolean isClash(List<IInjection> injections) {
		if (injections.size() == 2) {
			for (ISite siteCC1 : injections.get(0).getSiteList())
				for (ISite siteCC2 : injections.get(1).getSiteList())
					if (siteCC1.getAgentLink().getId() == siteCC2
							.getAgentLink().getId())
						return true;
		}
		return false;
	}

	public final IInjection getInjectionBySiteToFromLHS(ISite siteTo) {
		int sideId = siteTo.getAgentLink().getIdInRuleSide();
		int i = 0;
		for (IConnectedComponent cc : leftHandSide) {
			for (IAgent agent : cc.getAgents())
				if (agent.getIdInRuleSide() == sideId)
					return injList.get(i);
			i++;
		}
		return null;
	}

	public final boolean isClashForInfiniteRule() {
		if (this.leftHandSide.size() == 2) {
			if (this.leftHandSide.get(0).getInjectionsList().size() == 1
					&& this.leftHandSide.get(1).getInjectionsList().size() == 1) {
				List<IInjection> injList = new ArrayList<IInjection>();
				injList.add(this.leftHandSide.get(0).getFirstInjection());
				injList.add(this.leftHandSide.get(1).getFirstInjection());
				return isClash(injList);
			}
		}
		return false;
	}

	public final List<ISite> getSitesConnectedWithBroken() {
		return Collections.unmodifiableList(sitesConnectedWithBroken);
	}

	public final void addSiteConnectedWithBroken(ISite site) {
		sitesConnectedWithBroken.add(site);
	}

	public final List<ISite> getSitesConnectedWithDeleted() {
		return Collections.unmodifiableList(sitesConnectedWithDeleted);
	}

	public final void addSiteConnectedWithDeleted(ISite site) {
		sitesConnectedWithDeleted.add(site);
	}

	public final ISite getSiteConnectedWithDeleted(int index) {
		return sitesConnectedWithDeleted.get(index);
	}

	public final void removeSiteConnectedWithDeleted(int index) {
		sitesConnectedWithDeleted.remove(index);
	}

	public final void applyLastRuleForStories(List<IInjection> injectionsList,
			INetworkNotation netNotation) {
		for (IInjection inj : injectionsList) {
			for (ISite site : inj.getSiteList())
				netNotation.checkLinkForNetworkNotationDel(
						CStoriesSiteStates.LAST_STATE, site);
		}
	}

	public final void addAction(IAction action) {
		actionList.add(action);
	}

	public final void addChangedSite(ISite toSite) {
		for (ISite inSite : changedActivatedSites)
			if (inSite == toSite)
				return;
		changedActivatedSites.add(toSite);
	}

	public final void addInhibitedChangedSite(ISite fromSite,
			boolean internalState, boolean linkState) {
		for (ChangedSite inSite : changedInhibitedSites)
			if (inSite.getSite() == fromSite) {
				if (!inSite.isInternalState())
					inSite.setInternalState(internalState);
				if (!inSite.isLinkState())
					inSite.setLinkState(linkState);
				return;
			}
		changedInhibitedSites.add(new ChangedSite(fromSite, internalState,
				linkState));
	}

	public List<ChangedSite> getChangedInhibitedSites() {
		return changedInhibitedSites;
	}
}
