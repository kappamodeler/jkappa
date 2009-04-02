package com.plectix.simulator.components;

import java.io.Serializable;
import java.util.*;

import org.apache.log4j.Logger;

import com.plectix.simulator.action.*;
import com.plectix.simulator.components.contactMap.ChangedSite;
import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.stories.CNetworkNotation.NetworkNotationMode;
import com.plectix.simulator.components.stories.CStoriesSiteStates.StateType;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.SimulationData;

/**
 * This class is an implementation of 'rule' entity. 
 * 
 * <br><br>
 * Rule has two handsides with list of connected components each, name and rate.
 * 
 * <br>
 * In general we have kappa file line like
 * <br><br>
 * <code>'ruleName' leftHandSide -> rightHandSide @ ruleRate</code>,
 * where 
 * <br>
 * <li><code>ruleName</code> - name of this rule</li>
 * <li><code>leftHandside</code> - list of substances (maybe empty)</li>
 * <li><code>rightHandside</code> - list of substances (maybe empty), which will replace leftHandside 
 * after applying this rule</li> 
 * <li><code>ruleRate</code> - rate of this rule, i.e. number, which affects on rule 
 * application frequency in a whole process </li>
 * <br><br>
 * For example, we have kappa file line such as : <code>'name' A(x) -> B(), C() @ 0.0</code>
 * <br>
 * This one means rule, which transform agent A with site x to agents B and C. Notice that this 
 * rule will never be applied, because it has zero <code>ruleRate</code>
 * @see CConnectedComponent
 * @author evlasov
 */
public class CRule implements Serializable {
	private static final long serialVersionUID = 6045806917402381525L;
	
	private final List<IConnectedComponent> leftHandside;
	private final List<IConnectedComponent> rightHandside;
	private final String ruleName;
	private final boolean isStorify;
	private List<ISite> sitesConnectedWithDeleted;
	private List<ISite> sitesConnectedWithBroken;
	private boolean rHSEqualsLHS;

	private int automorphismNumber = 1;
	private boolean infiniteRate = false;
	private List<CRule> activatedRules;
	private List<CRule> activatedRuleForXMLOutput;
	private List<CRule> inhibitedRule;

	private List<IObservablesConnectedComponent> activatedObservable;
	private List<IObservablesConnectedComponent> activatedObservableForXMLOutput;
	private List<IObservablesConnectedComponent> inhibitedObservable;

	private int ruleID;
	private List<IAction> actionList;
	private Map<IAgent, IAgent> agentAddList;
	private List<IInjection> injList;
	private List<ISite> changedActivatedSites;
	private List<ChangedSite> changedInhibitedSites;
	private List<ChangedSite> fixedSites;
	private double activity = 0.;
	private double rate;

	/**
	 * The only CRule constructor.
	 * 
	 * @param leftHandsideComponents left handside of the rule, list of connected components
	 * @param rightHandsideComponents right handside of the rule, list of connected components
	 * @param ruleName name of the rule 
	 * @param ruleRate rate of the rule
	 * @param ruleID unique rule identificator
	 * @param isStorify <tt>true</tt> if simulator run in storify mode, <tt>false</tt> otherwise
	 */
	public CRule(List<IConnectedComponent> leftHandsideComponents, 
			List<IConnectedComponent> rightHandsideComponents, 
			String ruleName, double ruleRate, int ruleID, boolean isStorify) {
		if (leftHandsideComponents == null) {
			leftHandside = new ArrayList<IConnectedComponent>();
			leftHandside.add(CConnectedComponent.EMPTY);
		} else {
			this.leftHandside = leftHandsideComponents;
		}
		this.rightHandside = rightHandsideComponents;
		this.isStorify = isStorify;
		this.rate = ruleRate;
		setConnectedComponentLinkRule(leftHandsideComponents);
		setConnectedComponentLinkRule(rightHandsideComponents);
		for (IConnectedComponent cc : this.leftHandside) {
			cc.initSpanningTreeMap();
		}
		if (ruleRate == Double.MAX_VALUE) {
			this.infiniteRate = true;
			this.rate = 1;
		} else {
			this.rate = ruleRate;
		}

		this.ruleName = ruleName;
		this.ruleID = ruleID;
		calculateAutomorphismsNumber();
		markRHSAgents();
		createActionList();
		sortActionList();
		if (isStorify) {
			rHSEqualsLHS = true;
			for (IAction action : actionList) {
				if (action.getTypeId() != CActionType.NONE.getId()) {
					rHSEqualsLHS = false;
					break;
				}
			}
		}
	}
	
	/**
	 * This method links every connected component in given list with this rule 
	 * @param cList give list of connected component
	 */
	private final void setConnectedComponentLinkRule(
			List<IConnectedComponent> cList) {
		if (cList == null)
			return;
		for (IConnectedComponent cc : cList)
			cc.setRule(this);
	}
	
	/**
	 * 
	 * @return <tt>true</tt> if left handside of this rule contains no substances, otherwise <tt>false</tt>
	 */
	public final boolean leftHandSideIsEmpty() {
		return leftHandside.contains(CConnectedComponent.EMPTY);
	}

	/**
	 * 
	 * @return <tt>true</tt> if right handside of this rule contains no substances, otherwise <tt>false</tt>
	 */
	public final boolean rightHandSideIsEmpty() {
		return rightHandside == null;
	}

	/**
	 * 
	 * @return list of rules which couldn't be applied after application of this rule
	 */
	public List<CRule> getInhibitedRule() {
		return inhibitedRule;
	}

	/**
	 * 
	 * @return list of observables which quantity reduces after application of this rule
	 */
	public List<IObservablesConnectedComponent> getInhibitedObservable() {
		return inhibitedObservable;
	}
	
	/**
	 * 
	 * @return <tt>true</tt> if left handside of this rule contains the same substances as right handside,
	 * otherwise <tt>false</tt>
	 */
	public boolean isRHSEqualsLHS() {
		return rHSEqualsLHS;
	}

	/**
	 * Indicates if rate of this rule is infinite
	 * @return <tt>true</tt> if rate of this rule is infinite, otherwise <tt>false</tt>
	 */
	public final boolean isInfiniteRated() {
		return infiniteRate;
	}

	/**
	 * Sets rate of this rule to infinity
	 * @param infinityRate
	 */
	public final void setInfinityRate(boolean infinityRate) {
		this.infiniteRate = infinityRate;
	}

	/**
	 * This method is used by simulator in "storify" mode to apply current rule
	 * @param injectionList list of injections, which point to substances this rule will be applied to
	 * @param netNotation INetworkNotation object which keep information about rule application
	 * @param simulationData simulation data
	 * @param isLast is <tt>true</tt> if and only if this application is the latest in current simulation,
	 * otherwise false 
	 */
	public void applyRuleForStories(List<IInjection> injectionList,
			INetworkNotation netNotation, SimulationData simulationData,
			boolean isLast) {
		apply(injectionList, netNotation, simulationData, isLast);
	}

	/**
	 * This method is used by simulator in "simulation" mode to apply current rule
	 * @param injectionList list of injections, which point to substances this rule will be applied to
	 * @param simulationData simulation data
	 */
	public void applyRule(List<IInjection> injectionList, SimulationData simulationData) {
		apply(injectionList, null, simulationData, false);
	}

	/**
	 * This method searches agent in solution, which was added with the latest 
	 * application of this rule 
	 * @param rhsAgent agent from the right handside of the rule, which was "parent"
	 * of the unknown agent
	 * @return agent in solution, which was added with the latest application of this rule
	 */
	public final IAgent getAgentAdd(IAgent rhsAgent) {
		return agentAddList.get(rhsAgent);
	}

	/**
	 * This method returns IDs of all agents, which was added by the latest application of this rule 
	 * @return list of IDs of all agents, which was added by the latest application of this rule
	 */
	public List<Long> getAgentsAddedID() {
		if (agentAddList.size() == 0)
			return null;
		List<Long> list = new ArrayList<Long>();
		Iterator<IAgent> iterator = agentAddList.keySet().iterator();
		while (iterator.hasNext()) {
			IAgent agent = agentAddList.get(iterator.next());
			list.add(agent.getId());
		}
		return list;
	}

	/**
	 * This method puts agent in solution, which was added with the latest 
	 * application of this rule, with it's "parent" - agent from the right handside of the rule
	 * @param rhsAgent agent from the right handside of the rule
	 * @param agentFromSolution agent from solution
	 */
	public final void putAgentAdd(IAgent rhsAgent, IAgent agentFromSolution) {
		agentAddList.put(rhsAgent, agentFromSolution);
	}

	/**
	 * The main method for the rule application
	 * @param injectionList list of injections, which point to substances this rule will be applied to
	 * @param netNotation INetworkNotation object which keep information about rule application
	 * @param simulationData simulation data
	 * @param isLast is <tt>true</tt> if and only if this application is the latest in current simulation,
	 * otherwise false 
	 */
	protected final void apply(List<IInjection> injectionList,
			INetworkNotation netNotation, SimulationData simulationData,
			boolean isLast) {

		agentAddList = new HashMap<IAgent, IAgent>();
		sitesConnectedWithDeleted = new ArrayList<ISite>();
		sitesConnectedWithBroken = new ArrayList<ISite>();
		this.injList = injectionList;
		ISolution solution = simulationData.getKappaSystem().getSolution(); 
		RuleApplicationPool pool = solution.prepareRuleApplicationPool(injectionList);
		
		if (rightHandside != null) {
			for (IConnectedComponent cc : rightHandside) {
				cc.clearAgentsFromSolutionForRHS();
			}
		}

		for (IAction action : actionList) {
			if (action.getLeftCComponent() == null) {
				action.doAction(pool, null, netNotation, simulationData);
			} else {
				action.doAction(pool, injectionList.get(leftHandside.indexOf(action
						.getLeftCComponent())), netNotation, simulationData);
			}
		}

		solution.applyRule(pool);
		
		if (netNotation != null) {
			addFixedSitesToNetworkNotation(netNotation);
		}
	}

	/**
	 * This method applies the last rule for stories
	 * @param injectionsList list of injections, which point to substances this rule will be applied to
	 * @param netNotation INetworkNotation object which keep information about rule application
	 */
	public final void applyLastRuleForStories(List<IInjection> injectionsList,
			INetworkNotation netNotation) {
		for (IInjection inj : injectionsList) {
			for (ISite site : inj.getSiteList()) {
				netNotation.checkLinkForNetworkNotationDel(StateType.BEFORE,
						site);
			}
		}

	}
	
	/**
	 * This method adds information about changed site in solution to the given network notation
	 * @param netNotation network notation
	 * @param site site from solution
	 */
	private final void addNewSiteToNetworkNotation(
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

	/**
	 * This method adds information about what happened with agents (or it's sites if being exact) 
	 * in solution, that was injected by "fixed" agents in rule, to the given network notation.
	 * 
	 * Fixed site means that we have two agents in different handsides of the rule, which are
	 * placed on the same positions and has similar names and sites.   
	 * 
	 * @param netNotation network notation
	 */
	private final void addFixedSitesToNetworkNotation(INetworkNotation netNotation) {
		for (ISite siteInSolution : this.sitesConnectedWithBroken) {
			addNewSiteToNetworkNotation(netNotation, siteInSolution);
			if (!netNotation.changedSitesContains(siteInSolution)) {
				netNotation.checkLinkToUsedSites(StateType.BEFORE,
						siteInSolution);
				netNotation.checkLinkToUsedSites(StateType.AFTER,
						siteInSolution);
			}
		}

		for (ISite siteInSolution : this.sitesConnectedWithDeleted) {
			addNewSiteToNetworkNotation(netNotation, siteInSolution);
			if (!netNotation.changedSitesContains(siteInSolution)) {
				netNotation.checkLinkToUsedSites(StateType.BEFORE,
						siteInSolution);
				netNotation.checkLinkToUsedSites(StateType.AFTER,
						siteInSolution);
			}
		}

		for (ChangedSite fs : fixedSites) {
			CSite siteFromRule = (CSite) fs.getSite();
			IInjection inj = getInjectionBySiteToFromLHS(siteFromRule);
			IAgent agentToInSolution = inj.getAgentFromImageById(
					siteFromRule.getAgentLink().getIdInConnectedComponent());
			ISite site;
			if (siteFromRule.getNameId() == CSite.NO_INDEX)
				site = agentToInSolution.getEmptySite();
			else
				site = agentToInSolution.getSite(siteFromRule.getNameId());
			// add fixed agents
			netNotation.addFixedSitesFromRules(site, NetworkNotationMode.TEST,
					fs.isInternalState(), fs.isLinkState());
			if (!netNotation.changedSitesContains(site)) {
				netNotation.checkLinkToUsedSites(StateType.BEFORE, site);
				netNotation.checkLinkToUsedSites(StateType.AFTER, site);
			}
		}
	}

	/**
	 * This method sets idInRuleSide parameters to the agents from the rule's right handside
	 * and needed for initialization
	 */
	private final void markRHSAgents() {
		List<IAgent> rhsAgents = new ArrayList<IAgent>();
		List<IAgent> lhsAgents = new ArrayList<IAgent>();
		fixedSites = new ArrayList<ChangedSite>();

		if (leftHandside.get(0).isEmpty()) {
			int indexAgentRHS = 0;
			for (IConnectedComponent cc : rightHandside)
				for (IAgent agent : cc.getAgents())
					if (agent.getIdInRuleHandside() == CAgent.UNMARKED)
						agent.setIdInRuleSide(indexAgentRHS++);
			return;
		} else {
			for (IConnectedComponent cc : leftHandside) {
				lhsAgents.addAll(cc.getAgents());
			}
		}

		if (rightHandside == null)
			return;
		for (IConnectedComponent cc : rightHandside) {
			rhsAgents.addAll(cc.getAgents());
		}
		sortAgentsByIdInRuleHandside(rhsAgents);
		sortAgentsByIdInRuleHandside(lhsAgents);

		int index = 0;
		for (IAgent lhsAgent : lhsAgents) {
			if ((index < rhsAgents.size()) && !(rhsAgents.get(index).equalz(lhsAgent) 
					&& rhsAgents.get(index).siteMapsAreEqual(lhsAgent))) {
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

	/**
	 * This method searches for the similar sites of two agents. We use it on initialization 
	 * @param lhsAgent agent from left handside
	 * @param rhsAgent agent from right handside
	 */
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
								.getIdInRuleHandside() == rhsSite.getLinkState()
								.getSite().getAgentLink().getIdInRuleHandside())
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

	/**
	 * This method sorts agents by id in rule's handside
	 * @param list list of agents to be sorted
	 */
	private final void sortAgentsByIdInRuleHandside(List<IAgent> list) {
		IAgent left;
		IAgent right;
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = i + 1; j < list.size(); j++) {
				left = list.get(i);
				right = list.get(j);
				if (left.getIdInRuleHandside() > right.getIdInRuleHandside()) {
					list.set(i, right);
					list.set(j, left);
				}
			}
		}
	}

	/**
	 * This method sorts action of this rule by priority
	 */
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

	/**
	 * This method returns agents that are included both in right handside 
	 * of the current rule and in another rules
	 * @param agentsFromAnotherRules list of agents from another rules
	 * @return <tt>true</tt> if there's agents that are included both in right handside 
	 * of the current rule and in another rules, otherwise <tt>false</tt>
	 */
	private boolean hasAgentIntersection(List<IAgent> agentsFromAnotherRules) {
		if (rightHandside != null)
			for (IConnectedComponent cc : rightHandside)
				for (IAgent agentRight : cc.getAgents())
					if (agentRight.includedInCollection(agentsFromAnotherRules))
						return true;
		return false;
	}

	/**
	 * This method founds if there's agent from the given list which is activates by this rule
	 * @param agentsFromAnotherRules list of agents
	 * @return <tt>true</tt> if there's agent from the given list which is activates by this rule,
	 * otherwise <tt>false</tt>
	 */
	private final boolean isActivated(List<IAgent> agentsFromAnotherRules) {
		if (this.rightHandside == null)
			return false;
		int allIntersectsSites = 0;
		if (!hasAgentIntersection(agentsFromAnotherRules))
			return false;

		for (IAgent agent : agentsFromAnotherRules) {
			if (checkRulesNullAgents(agent))
				return true;

			for (ISite site : agent.getSites()) {
				for (ISite changedSite : changedActivatedSites) {
					if (changedSite.equalz(site)) {
						allIntersectsSites++;
						IInternalState currentInternalState = changedSite
								.getInternalState();
						IInternalState internalState = site.getInternalState();
						if (!(currentInternalState.isRankRoot())
								&& !(internalState.isRankRoot())) {
							if (internalState.getNameId() != currentInternalState
									.getNameId())
								return false;
						}
					}
				}
			}

			for (ISite site : agent.getSites()) {
				for (ISite changedSite : changedActivatedSites) {
					if (changedSite.equalz(site)) {
						ILinkState currentLinkState = changedSite
								.getLinkState();
						ILinkState linkState = site.getLinkState();

						if (currentLinkState.isLeftBranchStatus()
								&& linkState.isLeftBranchStatus())
							continue;

						if (linkState.getStatusLinkRank() == CLinkRank.BOUND_OR_FREE)
							continue;

						if (currentLinkState.getStatusLinkRank() == CLinkRank.BOUND_OR_FREE)
							continue;

						if ((currentLinkState.getStatusLinkRank() == CLinkRank.SEMI_LINK || currentLinkState
								.getStatusLinkRank() == CLinkRank.BOUND)
								&& linkState.getStatusLinkRank() == CLinkRank.SEMI_LINK)
							continue;

						if (currentLinkState.isLeftBranchStatus()
								&& (!(linkState.isLeftBranchStatus())))
							return false;

						if ((!(currentLinkState.isLeftBranchStatus()))
								&& linkState.isLeftBranchStatus())
							return false;

						if (currentLinkState.getStatusLinkRank() == linkState
								.getStatusLinkRank()
								&& currentLinkState.getStatusLinkRank() == CLinkRank.BOUND)
							if (!(currentLinkState.getSite().equalz(linkState
									.getSite())))
								return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * This method founds if there's agent from the given list which is inhibited by this rule
	 * @param agentsFromAnotherRules list of agents
	 * @return <tt>true</tt> if there's agent from the given list which is inhibited by this rule,
	 * otherwise <tt>false</tt>
	 */
	private final boolean isInhibited(List<IAgent> agentsFromAnotherRules) {
		for (IAgent agent : agentsFromAnotherRules) {
			if (this.leftHandside != null && checkRulesNullAgents(agent))
				return true;

			if (!hasAgentIntersection(agentsFromAnotherRules))
				return false;

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
							} else {
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

	/**
	 * This method helps to avoid code duplication
	 */
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

	/**
	 * Util method 
	 */
	private final boolean checkRulesNullAgents(IAgent agent) {
		if (this.rightHandside != null) {
			for (IConnectedComponent cc : this.getRightHandSide())
				for (IAgent agentFromRule : cc.getAgents())
					if (agent.equalz(agentFromRule))
						if (agentFromRule.getSites().isEmpty() || agent.getSites().isEmpty())
							return true;
		}
		return false;
	}

	//----------------------FILL OF ACTIVATED AND INHIBITED COMPONENTS--------------------- 
	/**
	 * This method updates list of rules (using rules from another, given list) 
	 * which are activated by this one.
	 * @param rules given list of rules  
	 */
	public final void updateActivatedRulesList(List<CRule> rules) {
		activatedRules = new ArrayList<CRule>();
		activatedRuleForXMLOutput = new ArrayList<CRule>();
		for (CRule rule : rules) {
			for (IConnectedComponent cc : rule.getLeftHandSide()) {
				if (isActivated(cc.getAgents())) {
					activatedRules.add(rule);
					if (!checkEmbedding(rule.getLeftHandSide()))
						activatedRuleForXMLOutput.add(rule);
					break;
				}
			}
		}
	}

	/**
	 * This method updates list of rules (using rules from another, given list) 
	 * which are inhibited by this one.
	 * @param rules given list of rules  
	 */
	public final void updateInhibitedRulesList(List<CRule> rules) {
		inhibitedRule = new ArrayList<CRule>();
		for (CRule rule : rules) {
			if (this != rule)
				for (IConnectedComponent cc : rule.getLeftHandSide()) {
					if (isInhibited(cc.getAgents())) {
						inhibitedRule.add(rule);
						break;
					}
				}
		}
	}
	
	/**
	 * This methods checks if given list of connected components is included in left handside
	 * of this rule
	 * @param listCC given list of connected components
	 * @return <tt>true</tt> if listCC is included in left handside of this rule
	 */
	private boolean checkEmbedding(List<IConnectedComponent> listCC) {
		int counter = 0;
		for (IConnectedComponent cc : listCC) {
			for (IConnectedComponent checkCC : this.leftHandside) {
				if (cc.unify(checkCC.getAgents().get(0))) {
					counter++;
					break;
				}
			}
		}
		if (counter == listCC.size())
			return true;

		return false;
	}

	/**
	 * This methods check out observable components which are activated by this rule
	 * @param observables all observable components in current simulation
	 */
	public final void initializeActivatedObservablesList(CObservables observables) {
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

	/**
	 * This methods check out observable components which are inhibited by this rule
	 * @param observables all observable components in current simulation
	 */
	public final void initializeInhibitedObservablesList(CObservables observables) {
		inhibitedObservable = new ArrayList<IObservablesConnectedComponent>();
		for (IObservablesConnectedComponent obsCC : observables
				.getConnectedComponentList()) {
			if (obsCC.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX
					&& isInhibited(obsCC.getAgents())) {
				inhibitedObservable.add(obsCC);
			}
		}
	}

	/**
	 * Util method using when creating actions list
	 * @param fromSite
	 * @param internalState
	 * @param linkState
	 */
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
	//---------------------XML OUTPUT INFORMATION---------------------------------------
	
	/**
	 * This method returns list of activated rules to be printed to XML
	 * @return list of activated rules, that are to be printed to XML
	 */
	public List<CRule> getActivatedRuleForXMLOutput() {
		return activatedRuleForXMLOutput;
	}
	
	/**
	 * This method returns list of activated observables to be printed to XML
	 * @return list of activated observables, that are to be printed to XML
	 */
	public List<IObservablesConnectedComponent> getActivatedObservableForXMLOutput() {
		return activatedObservableForXMLOutput;
	}

	/**
	 * This methods creates atomic-actions list for this rule
	 */
	private final void createActionList() {
		changedActivatedSites = new ArrayList<ISite>();
		changedInhibitedSites = new ArrayList<ChangedSite>();
		actionList = new ArrayList<IAction>();

		if (rightHandside != null)
			for (IConnectedComponent cc : rightHandside)
				for (IAgent agentRight : cc.getAgents())
					for (ISite site : agentRight.getSites()) {
						changedActivatedSites.add(site);
					}

		if (rightHandside == null) {
			for (IConnectedComponent ccL : leftHandside)
				for (IAgent lAgent : ccL.getAgents()) {
					actionList.add(new CDeleteAction(this, lAgent, ccL));
					for (ISite site : lAgent.getSites()) {
						changedInhibitedSites.add(new ChangedSite(site, true, true));
					}
				}
			return;
		}

		int lhsAgentsQuantity = 0;
		if (!this.leftHandSideIsEmpty()) {
			for (IConnectedComponent cc : leftHandside) {
				lhsAgentsQuantity += cc.getAgents().size();
			}	
		}
		
		for (IConnectedComponent ccR : rightHandside) {
			for (IAgent rAgent : ccR.getAgents()) {
				if ((lhsAgentsQuantity == 0) || (rAgent.getIdInRuleHandside() > lhsAgentsQuantity)) {
					actionList.add(new CAddAction(this, rAgent, ccR));
					// fillChangedSites(rAgent);
				}
			}
		}

		if (leftHandside.get(0).isEmpty())
			return;
		for (IConnectedComponent ccL : leftHandside)
			for (IAgent lAgent : ccL.getAgents()) {
				this.isAgentFromLHSHasFoundInRHS(lAgent, ccL);
			}
	}

	/**
	 * This method adds all actions among to a given agent
	 * @param lAgent given agent
	 * @param ccL connected component which contains lAgent
	 */
	private final void isAgentFromLHSHasFoundInRHS(IAgent lAgent,
			IConnectedComponent ccL) {
		for (IConnectedComponent ccR : rightHandside) {
			for (IAgent rAgent : ccR.getAgents()) {
				if (lAgent.getIdInRuleHandside() == rAgent.getIdInRuleHandside()) {
					IAction newAction = new CDefaultAction(this, lAgent,
							rAgent, ccL, ccR);
					actionList.add(newAction);
					actionList.addAll(newAction.createAtomicActions());
					return;
				}
			}
		}
		actionList.add(new CDeleteAction(this, lAgent, ccL));
	}

	/**
	 * This methods extracts all agents from a given connected components
	 * @param ccList list of connected components
	 * @return list of agents, included in ccList
	 */
	public final List<IAgent> getAgentsFromConnectedComponent(
			List<IConnectedComponent> ccList) {
		List<IAgent> agentList = new ArrayList<IAgent>();
		if (ccList.get(0).getAgents().get(0).getIdInRuleHandside() == CAgent.UNMARKED)
			return agentList;
		for (IConnectedComponent cc : ccList)
			for (IAgent agent : cc.getAgents())
				agentList.add(agent);

		return agentList;
	}

	/**
	 * Calculates automorphism number for this rule
	 */
	private final void calculateAutomorphismsNumber() {
		if (leftHandside != null)
			if (this.leftHandside.size() == 2) {
				if (this.leftHandside.get(0).getAgents().size() == this.leftHandside
						.get(1).getAgents().size())
					if (this.leftHandside.get(0).isAutomorphism(
							this.leftHandside.get(1).getAgents().get(0)))
						automorphismNumber = 2;
			}
	}

	/**
	 * This method checks if every connected component in left handside of this rule has
	 * injection to solution
	 * @return <tt>true</tt> if every connected component in left handside of this rule has
	 * injection to solution, otherwise <tt>false</tt> 
	 */
	public final boolean canBeApplied() {
		int injCounter = 0;
		List<IInjection> injList = new ArrayList<IInjection>();
		for (IConnectedComponent cc : this.getLeftHandSide()) {
			if (!cc.isEmpty() && cc.getInjectionsList().size() != 0) {
				injCounter++;
				injList.add(cc.getInjectionsList().iterator().next());
			} else if (cc.isEmpty()) {
				injCounter++;
			}
		}
		if (injCounter == this.getLeftHandSide().size()) {
			return true;
		}
		return false;
	}

	/**
	 * This method calculates activity of this rule according to it's current parameters
	 */
	public final void calcultateActivity() {
		activity = 1.;
		for (IConnectedComponent cc : this.leftHandside) {
			activity *= cc.getInjectionsList().size();
		}
		// activity *= constraintData.getActivity();
		activity *= rate;
		activity /= automorphismNumber;
	}

	/**
	 * This method returns the name of this rule
	 * @return the name of this rule
	 */
	public final String getName() {
		return ruleName;
	}

	/**
	 * This method returns activity of this rule
	 * @return activity of this rule
	 */
	public final double getActivity() {
		return activity;
	}

	/**
	 * This method sets activity of this rule to a given value
	 * @param activity new value
	 */
	public final void setActivity(Double activity) {
		this.activity = activity;
	}

	/**
	 * This method returns list of components from the left handside of this rule  
	 * @return list of components from the left handside of this rule
	 */
	public final List<IConnectedComponent> getLeftHandSide() {
		if (leftHandside == null) {
			return null;
		} else {
			return Collections.unmodifiableList(leftHandside);
		}
	}

	/**
	 * This method returns list of components from the right handside of this rule  
	 * @return list of components from the right handside of this rule
	 */
	public final List<IConnectedComponent> getRightHandSide() {
		if (rightHandside == null) {
			return null;
		} else {
			return Collections.unmodifiableList(rightHandside);
		}
	}

	/**
	 * This method indicates if 2 injections are in clash
	 * @param injections list of injections with power = 2
	 * @return <tt>true</tt> if injections are in clash, otherwise <tt>false</tt>
	 */
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

	/**
	 * This method indicates if injections from left handside are in clash in case this rule
	 * has infinite rate
	 * @return <tt>true</tt> if injections are in clash, otherwise <tt>false</tt>
	 */
	public final boolean isClashForInfiniteRule() {
		if (this.leftHandside.size() == 2) {
			if (this.leftHandside.get(0).getInjectionsList().size() == 1
					&& this.leftHandside.get(1).getInjectionsList().size() == 1) {
				List<IInjection> injList = new ArrayList<IInjection>();
				injList.add(this.leftHandside.get(0).getFirstInjection());
				injList.add(this.leftHandside.get(1).getFirstInjection());
				return isClash(injList);
			}
		}
		return false;
	}

	/**
	 * This method returns injection from the injection-list from the latest application of this rule,
	 * which points to connected component, including siteTo
	 * @param siteTo given site
	 * @return injection from the injection-list from the latest application of this rule,
	 * which points to connected component, including siteTo
	 */
	public final IInjection getInjectionBySiteToFromLHS(ISite siteTo) {
		int sideId = siteTo.getAgentLink().getIdInRuleHandside();
		int i = 0;
		for (IConnectedComponent cc : leftHandside) {
			for (IAgent agent : cc.getAgents())
				if (agent.getIdInRuleHandside() == sideId)
					return injList.get(i);
			i++;
		}
		return null;
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

	public final void addAction(IAction action) {
		actionList.add(action);
	}

	/**
	 * We use this method to compare rules
	 * @param obj another rule
	 * @return <tt>true</tt> if rules have similar id's, otherwise <tt>false</tt>
	 */
	public final boolean equalz(CRule obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof CRule)) {
			return false;
		}

		CRule rule = (CRule) obj;

		return rule.getRuleID() == ruleID;
	}

	/**
	 * This method indicates whether this rule included in given collection of rules 
	 * @param collection collection of rules
	 * @return <tt>true</tt> if this rule included in given collection of rules, 
	 * otherwise <tt>false</tt>
	 */
	public final boolean includedInCollection(Collection<CRule> collection) {
		for (CRule rule : collection) {
			if (this.equalz(rule)) {
				return true;
			}
		}
		return false;
	}

	
	

	//------------------------GETTERS AND SETTERS------------------------------------
	
	/**
	 * Returns id number of current rule
	 * @return rule id
	 */
	public final int getRuleID() {
		return ruleID;
	}

	/**
	 * Sets id of current rule to a given value 
	 * @param id new value of rule id
	 */
	public final void setRuleID(int id) {
		this.ruleID = id;
	}

	/**
	 * Returns current rule rate
	 * @return rule rate
	 */
	public final double getRate() {
		return rate;
	}

	/**
	 * Sets current rule rate to a given value
	 * @param ruleRate new value of rule rate
	 */
	public final void setRuleRate(double ruleRate) {
		if (ruleRate >= 0) {
			this.rate = ruleRate;
		} else {
			Logger logger = Logger.getLogger(this.getClass());
			logger.info("warning : rate of the rule '" + ruleName
					+ "' was attempted to be set as negative");
			this.rate = 0;
		}
	}

	/**
	 * Returns list of observable components which activate by this rule 
	 * @return list of observable components which activate by this rule
	 */
	public final List<IObservablesConnectedComponent> getActivatedObservable() {
		return Collections.unmodifiableList(activatedObservable);
	}
	
	/**
	 * Returns list of rules which activate by this rule 
	 * @return list of rules which activate by this rule
	 */
	public final List<CRule> getActivatedRules() {
		return Collections.unmodifiableList(activatedRules);
	}

	/**
	 * Returns list of actions, which this rule performs
	 * @return list of actions, which this rule performs
	 */
	public final List<IAction> getActionList() {
		return Collections.unmodifiableList(actionList);
	}

	public String toString() {
		String st = "ruleName=" + this.ruleName + " ";

		return st;
	}
}