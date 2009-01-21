package com.plectix.simulator.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.action.CAddAction;
import com.plectix.simulator.action.CDefaultAction;
import com.plectix.simulator.action.CDeleteAction;
import com.plectix.simulator.components.CNetworkNotation.NetworkNotationMode;
import com.plectix.simulator.components.CStoriesSiteStates.StateType;
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
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationUtils;

public class CRule implements IRule, Serializable {

	public static final IConnectedComponent EMPTY_LHS_CC = new CConnectedComponent(
			CConnectedComponent.EMPTY);
	private List<IConnectedComponent> leftHandSide;
	private List<IConnectedComponent> rightHandSide;
	private double activity = 0.;
	private final String name;
	private String data;
	// private double ruleRate;
	private List<ISite> sitesConnectedWithDeleted;
	private List<ISite> sitesConnectedWithBroken;
	private boolean rHSEqualsLHS;

	private int automorphismNumber = 1;
	private boolean infinityRate = false;
	private List<IRule> activatedRule;
	private List<IRule> activatedRuleForXMLOutput;
	private List<IRule> inhibitedRule;

	private List<IObservablesConnectedComponent> activatedObservable;
	private List<IObservablesConnectedComponent> activatedObservableForXMLOutput;
	private List<IObservablesConnectedComponent> inhibitedObservable;

	public List<IRule> getInhibitedRule() {
		return inhibitedRule;
	}

	private List<IAgent> storyfiedAgents;

	public boolean isRHSEqualsLHS() {
		return rHSEqualsLHS;
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
	private ConstraintData constraintData;

	@Override
	public String toString() {
		String st = "ruleName=" + this.name+" ";
		
		// return super.toString();
		return st;
	}
	
	public final String getData(boolean isOcamlStyleObsName) {
		if (data == null) {
			String line = SimulationUtils.printPartRule(leftHandSide,
					isOcamlStyleObsName);
			line = line + "->";
			line = line
					+ SimulationUtils.printPartRule(rightHandSide,
							isOcamlStyleObsName);
			data = line;
		}
		return data;
	}

	public final void setData(String data) {
		this.data = new String(data);
	}

	public void clearStorifiedAgents() {
		this.storyfiedAgents.clear();
	}

	public CRule(List<IConnectedComponent> left,
			List<IConnectedComponent> right, String name,
			ConstraintData ruleRate, int ruleID, boolean isStorify) {
		this.leftHandSide = left;
		this.rightHandSide = right;
		this.isStorify = isStorify;
		this.constraintData = ruleRate;
		setConnectedComponentLinkRule(left);
		setConnectedComponentLinkRule(right);
		if (leftHandSide == null) {
			leftHandSide = new ArrayList<IConnectedComponent>();
			leftHandSide.add(EMPTY_LHS_CC);
		}
		for (IConnectedComponent cc : this.leftHandSide) {
			cc.initSpanningTreeMap();
		}
		if (ruleRate.getActivity() == Double.MAX_VALUE) {
			this.infinityRate = true;
			constraintData.setActivity(1);
			// this.ruleRate = 1;
		} else {
			// this.ruleRate = ruleRate.getActivity();
		}

		this.name = name;
		this.ruleID = ruleID;
		calculateAutomorphismsNumber();
		indexingRHSAgents();
	}

	public ConstraintData getConstraintData() {
		return constraintData;
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
			// this.ruleRate = ruleRate;
			constraintData.setActivity(ruleRate);
		} else {
			Logger logger = Logger.getLogger(this.getClass());
			logger.info("warning : rate of the rule '" + name
					+ "' was attempted to be set as negative");
			// this.ruleRate = 0;
			constraintData.setActivity(0);
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
		return constraintData.getActivity();
		// return ruleRate;
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

	public void applyRuleForStories(List<IInjection> injectionList,
			INetworkNotation netNotation, SimulationData simulationData,
			boolean isLast) {
		apply(injectionList, netNotation, simulationData, isLast);
	}

	public void applyRule(List<IInjection> injectionList,
			SimulationData simulationData) {
		apply(injectionList, null, simulationData, false);
	}

	public final IAgent getAgentAdd(IAgent key) {
		return agentAddList.get(key);
	}

	public final void putAgentAdd(IAgent key, IAgent value) {
		agentAddList.put(key, value);
	}

	private final void storifyAgents(List<IInjection> injectionList,
			boolean isLast) {
		for (IInjection inj : injectionList)
			if (inj != CInjection.EMPTY_INJECTION)
				for (IAgentLink al : inj.getAgentLinkList())
					if (!rHSEqualsLHS || isLast) {
						if (!al.getAgentTo().isStorify())
							storyfiedAgents.add(al.getAgentTo());
						al.storifyAgent();
					}
	}

	public List<IAgent> getStoryfiedAgents() {
		return storyfiedAgents;
	}

	protected final void apply(List<IInjection> injectionList,
			INetworkNotation netNotation, SimulationData simulationData,
			boolean isLast) {
		if (storyfiedAgents == null)
			storyfiedAgents = new ArrayList<IAgent>();
		if (netNotation != null) {
			storifyAgents(injectionList, isLast);
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
				action.doAction(null, netNotation, simulationData);
			} else {
				action.doAction(injectionList.get(leftHandSide.indexOf(action
						.getLeftCComponent())), netNotation, simulationData);
			}
		}

		if (netNotation != null) {
			addFixedSitesToNN(netNotation);
		}
	}

	private final void addRuleSitesToNetworkNotation(
			INetworkNotation netNotation, ISite site) {
		if (netNotation != null) {
			NetworkNotationMode agentMode = NetworkNotationMode.NONE;
			NetworkNotationMode linkStateMode = NetworkNotationMode.NONE;
			NetworkNotationMode internalStateMode = NetworkNotationMode.NONE;
			linkStateMode = NetworkNotationMode.MODIFY;
			netNotation.addToAgentsFromRules(site, agentMode,
					internalStateMode, linkStateMode);
		}
	}

	private final void addFixedSitesToNN(INetworkNotation netNotation) {
		for (ISite siteInSolution : this.sitesConnectedWithBroken){
			addRuleSitesToNetworkNotation(netNotation, siteInSolution);
			if(!netNotation.changedSitesContains(siteInSolution)){
				netNotation.checkLinkToUsedSites(StateType.BEFORE, siteInSolution);
				netNotation.checkLinkToUsedSites(StateType.AFTER, siteInSolution);
			}
		}

		for (ISite siteInSolution : this.sitesConnectedWithDeleted){
			addRuleSitesToNetworkNotation(netNotation, siteInSolution);
			if(!netNotation.changedSitesContains(siteInSolution)){
				netNotation.checkLinkToUsedSites(StateType.BEFORE, siteInSolution);
				netNotation.checkLinkToUsedSites(StateType.AFTER, siteInSolution);
			}
		}

		for (ChangedSite fs : fixedSites) {
			CSite siteFromRule = (CSite) fs.getSite();
			IInjection inj = getInjectionBySiteToFromLHS(siteFromRule);
			IAgent agentToInSolution = inj.getConnectedComponent()
					.getAgentByIdFromSolution(
							siteFromRule.getAgentLink()
									.getIdInConnectedComponent(), inj);
			ISite site;
			if (siteFromRule.getNameId() == CSite.NO_INDEX)
				site = agentToInSolution.getEmptySite();
			else
				site = agentToInSolution.getSite(siteFromRule.getNameId());
			// add fixed agents
			netNotation.addFixedSitesFromRules(site, NetworkNotationMode.TEST,
					fs.isInternalState(), fs.isLinkState());
			if(!netNotation.changedSitesContains(site)){
				netNotation.checkLinkToUsedSites(StateType.BEFORE, site);
				netNotation.checkLinkToUsedSites(StateType.AFTER, site);
			}
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
					&& !(rhsAgents.get(index).equalz(lhsAgent) && rhsAgents
							.get(index).siteMapsAreEqual(lhsAgent))) {

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
				if (lhsSite.getLinkState().getSite().equalz(
						rhsSite.getLinkState().getSite())
						&& lhsSite.getLinkState().getSite().getAgentLink()
								.getIdInRuleSide() == rhsSite.getLinkState()
								.getSite().getAgentLink().getIdInRuleSide())
					fixedSite.setLinkState(true);
			}
			if (fixedSite.isLinkState() || fixedSite.isInternalState())
				fixedSites.add(fixedSite);
		}
		if (lhsAgent.getSites().size() == 0 && rhsAgent.getSites().size() == 0) {
			ChangedSite fixedSite = new ChangedSite(lhsAgent.getEmptySite());
			fixedSite.setLinkState(true);
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
		markRHSAgents();

		createActionList();

		sortActionList();
		checkRHSEqualsLHS();

	}

	private void checkRHSEqualsLHS() {
		if (isStorify) {
			rHSEqualsLHS = false;
			for (IAction action : actionList) {
				if (action.getTypeId() != CActionType.NONE.getId())
					return;
			}
			rHSEqualsLHS = true;
		}
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
			if (checkRulesNullAgents(agent))
				return true;
			for (ISite site : agent.getSites()) {
				for (ISite changedSite : changedActivatedSites) {
					if (changedSite.equalz(site)) {
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
								&& linkState.getStatusLinkRank() == CLinkRank.BOUND_OR_FREE)
							return true;

						if (currentLinkState.isLeftBranchStatus()
								&& (!(linkState.isLeftBranchStatus())))
							continue;

						if ((!(currentLinkState.isLeftBranchStatus()))
								&& linkState.isLeftBranchStatus())
							continue;

						if (currentLinkState.getStatusLinkRank() == linkState
								.getStatusLinkRank()
								&& currentLinkState.getStatusLinkRank() == CLinkRank.BOUND)
							if (!(currentLinkState.getSite().equalz(linkState
									.getSite())))
								continue;

						if (currentLinkState.getStatusLinkRank() == linkState
								.getStatusLinkRank()
								&& currentLinkState.getStatusLinkRank() == CLinkRank.BOUND)
							if (currentLinkState.getSite().equalz(
									linkState.getSite())
									&& (currentLinkState.getSite()
											.getInternalState().getNameId() != linkState
											.getSite().getInternalState()
											.getNameId()))
								continue;

						if (!currentLinkState.getStatusLinkRank().smaller(
								linkState.getStatusLinkRank()))
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
					if (changedSite.getSite().equalz(site)) {
						// if (changedSite.getSite().equals(site)) {

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
									if (linkState.getStatusLinkRank() == CLinkRank.BOUND_OR_FREE)
										return true;
									if (checkInhibitedLinkStates(
											currentLinkState, linkState))
										return true;
								}
							}else{
								if (currentLinkState.getStatusLinkRank() == CLinkRank.FREE
										&& linkState.getStatusLinkRank() == CLinkRank.BOUND_OR_FREE)
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
		if (currentLinkState.getStatusLinkRank() == CLinkRank.FREE
				&& linkState.getStatusLinkRank() == CLinkRank.FREE)
			return true;
		if (currentLinkState.getStatusLinkRank() == CLinkRank.BOUND
				&& linkState.getStatusLinkRank() == CLinkRank.SEMI_LINK)
			return true;
		if (currentLinkState.getStatusLinkRank() == CLinkRank.BOUND
				&& linkState.getStatusLinkRank() == CLinkRank.BOUND)
			if (currentLinkState.getSite().equalz(linkState.getSite()))
				// if (currentLinkState.getSite().equals(linkState.getSite()))
				return true;
		return false;
	}

	private final boolean checkRulesNullAgents(IAgent agent) {
		if (this.rightHandSide != null)
			for (IConnectedComponent cc : this.getRightHandSide())

				for (IAgent agentFromRule : cc.getAgents())
					if (agent.equalz(agentFromRule))
						if (agentFromRule.getSites().size() == 0
								|| agent.getSites().size() == 0)
							return true;
		return false;
	}

	public final void createActivatedRulesList(List<IRule> rules) {
		activatedRule = new ArrayList<IRule>();
		activatedRuleForXMLOutput = new ArrayList<IRule>();
		for (IRule rule : rules) {
			// if (this != rule)
			for (IConnectedComponent cc : rule.getLeftHandSide()) {
				if (isActivated(cc.getAgents())) {
					activatedRule.add(rule);
					if (!checkEmbedding(rule.getLeftHandSide()))
						activatedRuleForXMLOutput.add(rule);
					break;
				}
			}
		}
	}

	public List<IRule> getActivatedRuleForXMLOutput() {
		return activatedRuleForXMLOutput;
	}

	private boolean checkEmbedding(List<IConnectedComponent> ccList) {
		int counter = 0;

		for (IConnectedComponent cc : ccList) {
			for (IConnectedComponent checkCC : this.leftHandSide) {
				if (cc.unify(checkCC.getAgents().get(0))) {
					counter++;
					break;
				}
			}
		}
		if (counter == ccList.size())
			return true;

		return false;
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
		activatedObservableForXMLOutput = new ArrayList<IObservablesConnectedComponent>();
		for (IObservablesConnectedComponent obsCC : observables
				.getConnectedComponentList()) {
			if (obsCC.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX
					&& isActivated(obsCC.getAgents())) {
				activatedObservable.add(obsCC);
				List<IConnectedComponent> listCC = new ArrayList<IConnectedComponent>();
				listCC.add(obsCC);
				if (!checkEmbedding(listCC))
					activatedObservableForXMLOutput.add(obsCC);
			}
		}
	}

	public List<IObservablesConnectedComponent> getActivatedObservableForXMLOutput() {
		return activatedObservableForXMLOutput;
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
		activity *= constraintData.getActivity();
		// activity *= ruleRate;
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
			for (ISite site : inj.getSiteList()) {
				netNotation.checkLinkForNetworkNotationDel(StateType.BEFORE,
						site, true);
				netNotation.checkLinkForNetworkNotationDel(StateType.BEFORE,
						site, false);
			}
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
