package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.interfaces.IConstraint;

public class CRule {

	public static final CConnectedComponent EMPTY_LHS_CC = new CConnectedComponent(
			CConnectedComponent.EMPTY);
	private List<CConnectedComponent> leftHandSide;
	private List<CConnectedComponent> rightHandSide;
	private double activity = 0.;
	private String name;
	private double ruleRate;
	private List<CSite> sitesConnectedWithDeleted;

	public List<CSite> getAgentsConnectedWithDeleted() {
		return sitesConnectedWithDeleted;
	}

	public void setRuleRate(double ruleRate) {
		this.ruleRate = ruleRate;
	}

	private int ruleID;

	private int automorphismNumber = 1;
	private boolean infinityRate = false;
	private List<CRule> activatedRule;
	private List<ObservablesConnectedComponent> activatedObservable;

	public List<ObservablesConnectedComponent> getActivatedObservable() {
		return activatedObservable;
	}

	public void setActivatedObservable(
			List<ObservablesConnectedComponent> activatedObservable) {
		this.activatedObservable = activatedObservable;
	}

	private int maxAgentID = 0;

	private List<Action> actionList;

	private HashMap<CAgent, CAgent> agentAddList;

	private List<CInjection> injList;

	private List<CSite> changedSites;

	private IConstraint constraints;

	private int countAgentsLHS = 0;

	public int getCountAgentsLHS() {
		return countAgentsLHS;
	}

	public final int getRuleID() {
		return ruleID;
	}

	public CRule(List<CConnectedComponent> left,
			List<CConnectedComponent> right, String name, double ruleRate,
			int ruleID) {
		this.leftHandSide = left;
		this.rightHandSide = right;
		setConnectedComponentLinkRule(left);
		setConnectedComponentLinkRule(right);
		if (leftHandSide == null) {
			leftHandSide = new ArrayList<CConnectedComponent>();
			leftHandSide.add(EMPTY_LHS_CC);
		}
		for (CConnectedComponent cc : this.leftHandSide) {
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

	public List<CRule> getActivatedRule() {
		return activatedRule;
	}

	public void setActivatedRule(List<CRule> activatedRule) {
		this.activatedRule = activatedRule;
	}

	public boolean isInfinityRate() {
		return infinityRate;
	}

	public void setInfinityRate(boolean infinityRate) {
		this.infinityRate = infinityRate;
	}

	public List<Action> getActionList() {
		return actionList;
	}

	public final int getAutomorphismNumber() {
		return automorphismNumber;
	}

	public final double getRuleRate() {
		return ruleRate;
	}

	private final void markedLHS() {
		int counter = 1;
		if (leftHandSide.get(0) == EMPTY_LHS_CC) {
			maxAgentID = 1;
			return;
		}
		for (CConnectedComponent cc : leftHandSide)
			for (CAgent agent : cc.getAgents()) {
				agent.setIdInRuleSide(counter);
				counter++;
			}
		maxAgentID = counter;
	}

	private final HashMap<Integer, List<CAgent>> createAgentMap(
			List<CConnectedComponent> ccList) {
		HashMap<Integer, List<CAgent>> map = new HashMap<Integer, List<CAgent>>();
		if (ccList == null)
			return map;
		for (CConnectedComponent cc : ccList)
			for (CAgent agent : cc.getAgents()) {
				List<CAgent> list = map.get(agent.getNameId());
				if (list == null) {
					list = new ArrayList<CAgent>();
					map.put(agent.getNameId(), list);
				}
				list.add(agent);
			}
		return map;
	}

	public void applyRule(List<CInjection> injectionList) {
		agentAddList = new HashMap<CAgent, CAgent>();
		sitesConnectedWithDeleted = new ArrayList<CSite>();
		this.injList = injectionList;
		if (rightHandSide != null)
			for (CConnectedComponent cc : rightHandSide)
				cc.getAgentFromSolutionForRHS().clear();

		for (Action action : actionList) {
			if (action.getLeftCComponent() == null)
				action.doAction(null);
			else
				action.doAction(injectionList.get(leftHandSide.indexOf(action
						.getLeftCComponent())));
		}
	}

	private final void markRHSAgents() {
		List<CAgent> rhsAgents = new ArrayList<CAgent>();
		List<CAgent> lhsAgents = new ArrayList<CAgent>();
		int indexAgentRHS = 0;

		if (leftHandSide.get(0) == EMPTY_LHS_CC) {
			countAgentsLHS = 0;
			markRHSAgentsUnmarked(0);
			return;
		} else {
			for (CConnectedComponent cc : leftHandSide) {
				indexAgentRHS = indexAgentRHS + cc.getAgents().size();
				lhsAgents.addAll(cc.getAgents());
			}
			countAgentsLHS = indexAgentRHS;
		}

		if (rightHandSide == null)
			return;
		for (CConnectedComponent cc : rightHandSide) {
			rhsAgents.addAll(cc.getAgents());
		}
		sortAgentsByRuleSide(rhsAgents);
		sortAgentsByRuleSide(lhsAgents);

		int index = 0;
		for (CAgent lhsAgent : lhsAgents) {
			if ((index < rhsAgents.size())
					&& !(rhsAgents.get(index).equals(lhsAgent) && rhsAgents
							.get(index).getSiteMap().equals(
									lhsAgent.getSiteMap()))) {
				// rhsAgents.get(index)
				// .setIdInRuleSide(lhsAgent.getIdInRuleSide());
				// } else {
				break;
			}
			index++;

		}

		for (int i = index; i < rhsAgents.size(); i++) {
			if (rhsAgents.size() == 1)
				rhsAgents.get(i).setIdInRuleSide(lhsAgents.size() + i + 1);
			else
				rhsAgents.get(i).setIdInRuleSide(lhsAgents.size() + i);
		}
	}

	public static final void sortAgentsByRuleSide(List<CAgent> list) {
		CAgent left;
		CAgent right;
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
		for (CConnectedComponent cc : rightHandSide)
			for (CAgent agent : cc.getAgents())
				if (agent.getIdInRuleSide() == CAgent.UNMARKED)
					agent.setIdInRuleSide(indexAgentRHS++);
	}

	private final void indexingRHSAgents() {
		// markedLHS();
		HashMap<Integer, List<CAgent>> lhsAgentMap = createAgentMap(leftHandSide);
		HashMap<Integer, List<CAgent>> rhsAgentMap = createAgentMap(rightHandSide);
		markRHSAgents();

		createActionList();

		sortActionList();

	}

	private final void sortActionList() {
		for (int i = 0; i < actionList.size(); i++) {
			for (int j = 0; j < actionList.size(); j++) {
				if (actionList.get(i).getAction() < actionList.get(j)
						.getAction()) {
					Action actionMin = actionList.get(j);
					Action actionR = actionList.get(i);
					actionList.set(j, actionR);
					actionList.set(i, actionMin);
				}
			}
		}
	}

	public List<CSite> getChangedSites() {
		return changedSites;
	}

	private final boolean isActivated(List<CAgent> agentsFromAnotherRules) {
		for (CAgent agent : agentsFromAnotherRules) {
			if (this.rightHandSide != null && checkRulesNullAgents(agent))
				return true;
			for (CSite site : agent.getSites()) {
				for (CSite changedSite : changedSites) {
					if (changedSite.equals(site)) {
						CInternalState currentInternalState = changedSite
								.getInternalState();
						CInternalState internalState = site.getInternalState();
						if (!(currentInternalState.isRankRoot())
								&& !(internalState.isRankRoot())) {
							if (internalState.getNameId() != currentInternalState
									.getNameId())
								continue;
						}

						CLinkState currentLinkState = changedSite
								.getLinkState();
						CLinkState linkState = site.getLinkState();

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

	private boolean checkRulesNullAgents(CAgent agent) {
		for (CConnectedComponent cc : this.getRightHandSide())

			for (CAgent agentFromRule : cc.getAgents())
				if (agent.equals(agentFromRule))
					if (agentFromRule.getSites().size() == 0
							|| agent.getSites().size() == 0)
						return true;
		return false;
	}

	public final void createActivatedRulesList(List<CRule> rules) {
		activatedRule = new ArrayList<CRule>();
		for (CRule rule : rules) {
			// if (this != rule)
			for (CConnectedComponent cc : rule.getLeftHandSide()) {
				if (isActivated(cc.getAgents())) {
					activatedRule.add(rule);
					break;
				}
			}
		}
	}

	public final void createActivatedObservablesList(CObservables observables) {
		activatedObservable = new ArrayList<ObservablesConnectedComponent>();
		for (ObservablesConnectedComponent obsCC : observables
				.getConnectedComponentList()) {
			if (// obsCC.getMainAutomorphismNumber()==
			// ObservablesConnectedComponent.NO_INDEX &&
			isActivated(obsCC.getAgents())) {
				activatedObservable.add(obsCC);
			}
		}
	}

	private final void createActionList() {
		changedSites = new ArrayList<CSite>();
		actionList = new ArrayList<Action>();

		if (rightHandSide == null) {
			for (CConnectedComponent ccL : leftHandSide)
				for (CAgent lAgent : ccL.getAgents())
					actionList.add(new Action(lAgent, ccL, Action.ACTION_DEL));
			return;
		}

		for (CConnectedComponent ccR : rightHandSide)
			for (CAgent rAgent : ccR.getAgents()) {
				if ((countAgentsLHS == 0)
						|| (rAgent.getIdInRuleSide() > countAgentsLHS)) {
					actionList.add(new Action(rAgent, ccR, Action.ACTION_ADD));
					fillChangedSites(null, rAgent);// for activation map
					// creation
				}
			}

		if (leftHandSide.get(0) == EMPTY_LHS_CC)
			return;
		for (CConnectedComponent ccL : leftHandSide)
			for (CAgent lAgent : ccL.getAgents()) {
				if (!isAgentFromLHSHasFoundInRHS(lAgent, ccL))
					actionList.add(new Action(lAgent, ccL, Action.ACTION_DEL));
			}
	}

	private final void fillChangedSites(CAgent agentLeft, CAgent agentRight) {
		if (agentLeft == null)
			for (CSite site : agentRight.getSites()) {
				changedSites.add(site);
			}
	}

	private final boolean isAgentFromLHSHasFoundInRHS(CAgent lAgent,
			CConnectedComponent ccL) {
		for (CConnectedComponent ccR : rightHandSide)
			for (CAgent rAgent : ccR.getAgents()) {
				if (lAgent.getIdInRuleSide() == rAgent.getIdInRuleSide()) {
					Action newAction = new Action(lAgent, rAgent, ccR, ccL);
					actionList.addAll(newAction.createAtomicAction());
					return true;
				}
			}
		return false;
	}

	private final List<CAgent> getAgentsFromConnectedComponent(
			List<CConnectedComponent> ccList) {
		List<CAgent> agentList = new ArrayList<CAgent>();
		if (ccList.get(0).getAgents().get(0).getIdInRuleSide() == CAgent.UNMARKED)
			return agentList;
		for (CConnectedComponent cc : ccList)
			for (CAgent agent : cc.getAgents())
				agentList.add(agent);

		return agentList;
	}

	private final CAgent getAgentByIdInRuleFromLHS(Integer id) {
		for (CAgent agent : getAgentsFromConnectedComponent(leftHandSide))
			if (agent.getIdInRuleSide() == id)
				return agent;
		return null;
	}

	public void setAutomorphismNumber(int automorphismNumber) {
		this.automorphismNumber = automorphismNumber;
	}

	private final void calculateAutomorphismsNumber() {
		if (leftHandSide != null)
			if (this.leftHandSide.size() == 2) {
				if (this.leftHandSide.get(0).isAutomorphism(
						this.leftHandSide.get(1).getAgents().get(0)))
					automorphismNumber = 2;
			}
	}

	private final void setConnectedComponentLinkRule(
			List<CConnectedComponent> cList) {
		if (cList == null)
			return;
		for (CConnectedComponent cc : cList)
			cc.setRule(this);
	}

	public final void calcultateActivity() {
		activity = 1.;
		for (CConnectedComponent cc : this.leftHandSide) {
			activity *= cc.getInjectionsQuantity();
		}
		activity *= ruleRate;
		activity /= automorphismNumber;
	}

	public final String getName() {
		return name;
	}

	public final Double getActivity() {

		return activity;
	}

	public final void setActivity(Double activity) {
		this.activity = activity;
	}

	public final List<CConnectedComponent> getLeftHandSide() {
		return leftHandSide;
	}

	public final List<CConnectedComponent> getRightHandSide() {
		return rightHandSide;
	}

	public boolean isClash(List<CInjection> injections) {
		if (injections.size() == 2) {
			for (CSite siteCC1 : injections.get(0).getSiteList())
				for (CSite siteCC2 : injections.get(1).getSiteList())
					if (siteCC1.getAgentLink().getId() == siteCC2
							.getAgentLink().getId())
						return true;
		}
		return false;
	}

	public class Action {
		public static final byte ACTION_BRK = 0;
		public static final byte ACTION_DEL = 1;
		public static final byte ACTION_ADD = 2;
		public static final byte ACTION_BND = 3;
		public static final byte ACTION_MOD = 4;
		public static final byte ACTION_NONE = -1;

		private byte action = ACTION_NONE;

		private CAgent fromAgent;
		private CAgent toAgent;

		private CConnectedComponent rightConnectedComponent;
		private CConnectedComponent leftConnectedComponent;

		public CConnectedComponent getCLeftComponent() {
			return leftConnectedComponent;
		}

		private CSite siteFrom;

		public CAgent getFromAgent() {
			return fromAgent;
		}

		public CAgent getToAgent() {
			return toAgent;
		}

		public CSite getSiteFrom() {
			return siteFrom;
		}

		public CSite getSiteTo() {
			return siteTo;
		}

		public Integer getNameInternalStateId() {
			return nameInternalStateId;
		}

		private CSite siteTo;
		private Integer nameInternalStateId;

		public final void doAction(CInjection injection) {

			switch (action) {
			case ACTION_ADD: {
				/**
				 * Done.
				 */
				CAgent agent = new CAgent(toAgent.getNameId());
				for (CSite site : toAgent.getSites()) {
					CSite siteAdd = new CSite(site.getNameId());
					siteAdd.setInternalState(new CInternalState(site
							.getInternalState().getStateNameId()));
					agent.addSite(siteAdd);
				}
				rightConnectedComponent.addAgentFromSolutionForRHS(agent);
				((CSolution) SimulationMain.getSimulationManager()
						.getSimulationData().getSolution()).addAgent(agent);

				agentAddList.put(toAgent, agent);
				// toAgent.setIdInRuleSide(maxAgentID++);
				break;
			}
			case ACTION_NONE: {
				int agentIdInCC = getAgentIdInCCBySideId(toAgent);
				CAgent agentFromInSolution = leftConnectedComponent
						.getAgentByIdFromSolution(agentIdInCC, injection);
				rightConnectedComponent
						.addAgentFromSolutionForRHS(agentFromInSolution);
				break;
			}
			case ACTION_BND: {
				/**
				 * Done.
				 */
				CAgent agentFromInSolution;
				if (siteFrom.getAgentLink().getIdInRuleSide() > getAgentsFromConnectedComponent(
						leftHandSide).size()) {
					agentFromInSolution = agentAddList.get(siteFrom
							.getAgentLink());
				} else {
					int agentIdInCC = getAgentIdInCCBySideId(siteFrom
							.getAgentLink());
					agentFromInSolution = leftConnectedComponent
							.getAgentByIdFromSolution(agentIdInCC, injection);

					// /////////////////////////////////////////////
					CSite injectedSite = agentFromInSolution.getSite(siteFrom
							.getNameId());
					injection.addToChangedSites(injectedSite);
					// /////////////////////////////////////////////
				}

				CAgent agentToInSolution;
				if (siteTo.getAgentLink().getIdInRuleSide() > getAgentsFromConnectedComponent(
						leftHandSide).size()) {
					agentToInSolution = agentAddList.get(siteTo.getAgentLink());
				} else {
					int agentIdInCC = getAgentIdInCCBySideId(siteTo
							.getAgentLink());
					CInjection inj = getInjectionBySiteToFromLHS(siteTo);
					agentToInSolution = leftConnectedComponent
							.getAgentByIdFromSolution(agentIdInCC, inj);
				}

				agentFromInSolution.getSite(siteFrom.getNameId())
						.getLinkState().setSite(
								agentToInSolution.getSite(siteTo.getNameId()));
				break;
			}
			case ACTION_BRK: {

				CAgent agentFromInSolution;
				int agentIdInCC = getAgentIdInCCBySideId(siteFrom
						.getAgentLink());
				agentFromInSolution = leftConnectedComponent
						.getAgentByIdFromSolution(agentIdInCC, injection);

				CSite injectedSite = agentFromInSolution.getSite(siteFrom
						.getNameId());

				CSite linkSite = (CSite) injectedSite.getLinkState().getSite();
				if ((siteFrom.getLinkState().getSite() == null)
						&& (linkSite != null)) {
					linkSite.getLinkState().setSite(null);
					linkSite.getLinkState().setStatusLink(
							CLinkState.STATUS_LINK_FREE);
					injection.addToChangedSites(linkSite);
					rightConnectedComponent.addAgentFromSolutionForRHS(linkSite
							.getAgentLink());
				}

				agentFromInSolution.getSite(siteFrom.getNameId())
						.getLinkState().setSite(null);
				agentFromInSolution.getSite(siteFrom.getNameId())
						.getLinkState().setStatusLink(
								CLinkState.STATUS_LINK_FREE);
				// /////////////////////////////////////////////

				injection.addToChangedSites(injectedSite);
				// /////////////////////////////////////////////

				break;
			}
			case ACTION_DEL: {
				/**
				 * Done.
				 */

				CAgent agent = leftConnectedComponent.getAgentByIdFromSolution(
						fromAgent.getIdInConnectedComponent(), injection);
				for (CSite site : agent.getSites()) {
					removeAgentToConnectedWithDeleted(site);
					CSite solutionSite = (CSite) site.getLinkState().getSite();

					if (solutionSite != null) {
						addAgentToConnectedWithDeleted(solutionSite);
						solutionSite.getLinkState().setSite(null);
						solutionSite.getLinkState().setStatusLink(
								CLinkState.STATUS_LINK_FREE);
						// solutionSite.removeInjectionsFromCCToSite(injection);
					}
				}

				for (CLiftElement lift : agent.EMPTY_SITE.getLift()) {
					agent.EMPTY_SITE.removeInjectionsFromCCToSite(lift
							.getInjection());
					lift.getInjection().getConnectedComponent()
							.removeInjection(lift.getInjection());
				}

				for (CSite site : agent.getSites()) {
					for (CLiftElement lift : site.getLift()) {
						site.removeInjectionsFromCCToSite(lift.getInjection());
						lift.getInjection().getConnectedComponent()
								.removeInjection(lift.getInjection());
					}
					site.getLift().clear();
					injection.removeSiteFromSitesList(site);
				}
				// injection.getConnectedComponent().getInjectionsList()
				// .remove(injection);

				((CSolution) SimulationMain.getSimulationManager()
						.getSimulationData().getSolution()).removeAgent(agent);

				break;
			}
			case ACTION_MOD: {
				/**
				 * Done.
				 */
				int agentIdInCC = getAgentIdInCCBySideId(siteTo.getAgentLink());
				CAgent agentFromInSolution = leftConnectedComponent
						.getAgentByIdFromSolution(agentIdInCC, injection);

				// /////////////////////////////////////////////
				CSite injectedSite = agentFromInSolution.getSite(siteTo
						.getNameId());
				injectedSite.getInternalState().setNameId(nameInternalStateId);
				injection.addToChangedSites(injectedSite);
				// /////////////////////////////////////////////
				break;
			}
			}
		}

		private void removeAgentToConnectedWithDeleted(CSite checkedSite) {
			for (int i = 0; i < sitesConnectedWithDeleted.size(); i++) {
				if (sitesConnectedWithDeleted.get(i) == checkedSite) {
					sitesConnectedWithDeleted.remove(i);
					return;
				}
			}
		}

		private void addAgentToConnectedWithDeleted(CSite checkedSite) {
			for (CSite site : sitesConnectedWithDeleted)
				if (site == checkedSite)
					return;
			sitesConnectedWithDeleted.add(checkedSite);
		}

		private final CInjection getInjectionBySiteToFromLHS(CSite siteTo) {
			int sideId = siteTo.getAgentLink().getIdInRuleSide();
			int i = 0;
			for (CConnectedComponent cc : leftHandSide) {

				for (CAgent agent : cc.getAgents())
					if (agent.getIdInRuleSide() == sideId)
						return injList.get(i);
				i++;
			}

			return null;
		}

		private final int getAgentIdInCCBySideId(CAgent agent) {
			if (leftConnectedComponent != null) {

				for (CAgent agentL : leftConnectedComponent.getAgents())
					if (agentL.getIdInRuleSide() == agent.getIdInRuleSide())
						return agentL.getIdInConnectedComponent();
			} else {
				for (CConnectedComponent cc : leftHandSide)
					for (CAgent agentL : cc.getAgents())
						if (agentL.getIdInRuleSide() == agent.getIdInRuleSide()) {
							leftConnectedComponent = cc;
							return agentL.getIdInConnectedComponent();
						}
			}

			return 0;
		}

		public CConnectedComponent getRightCComponent() {
			return rightConnectedComponent;
		}

		public CConnectedComponent getLeftCComponent() {
			return leftConnectedComponent;
		}

		public byte getAction() {
			return action;
		}

		public final List<Action> createAtomicAction() {
			if (fromAgent.getSites() == null) {
				this.action = ACTION_NONE;
				return null;
			}
			List<Action> list = new ArrayList<Action>();

			for (CSite fromSite : fromAgent.getSites()) {
				CSite toSite = toAgent.getSite(fromSite.getNameId());
				if (fromSite.getInternalState().getStateNameId() != toSite
						.getInternalState().getStateNameId()) {
					list.add(new Action(fromSite, toSite,
							rightConnectedComponent, leftConnectedComponent,
							ACTION_MOD));
					if (!isChangedSiteContains(toSite))
						changedSites.add(toSite);
				}

				// if ((fromSite.getLinkState().getSite() == null)
				// && (toSite.getLinkState().getSite() == null))
				// continue;
				if ((fromSite.getLinkState().getStatusLink() == CLinkState.STATUS_LINK_FREE)
						&& (toSite.getLinkState().getStatusLink() == CLinkState.STATUS_LINK_FREE))
					continue;

				// if ((fromSite.getLinkState().getSite() != null)
				// && (toSite.getLinkState().getSite() == null)) {
				if ((fromSite.getLinkState().getStatusLink() != CLinkState.STATUS_LINK_FREE)
						&& (toSite.getLinkState().getStatusLink() == CLinkState.STATUS_LINK_FREE)) {
					list.add(new Action(fromSite, toSite,
							rightConnectedComponent, leftConnectedComponent,
							ACTION_BRK));
					if (!isChangedSiteContains(toSite))
						changedSites.add(toSite);
					continue;
				}

				// if ((fromSite.getLinkState().getSite() == null)
				// && (toSite.getLinkState().getSite() != null)) {
				if ((fromSite.getLinkState().getStatusLink() == CLinkState.STATUS_LINK_FREE)
						&& (toSite.getLinkState().getStatusLink() == CLinkState.STATUS_LINK_BOUND)) {
					list.add(new Action(toSite, (CSite) toSite.getLinkState()
							.getSite(), rightConnectedComponent,
							leftConnectedComponent));
					if (!isChangedSiteContains(toSite))
						changedSites.add(toSite);
					continue;
				}

				CSite lConnectSite = (CSite) fromSite.getLinkState().getSite();
				CSite rConnectSite = (CSite) toSite.getLinkState().getSite();
				if (lConnectSite == null || rConnectSite == null)
					continue;
				if ((lConnectSite.getAgentLink().getIdInRuleSide() == rConnectSite
						.getAgentLink().getIdInRuleSide())
						&& (lConnectSite.equals(rConnectSite)))
					continue;
				list.add(new Action(fromSite, toSite, rightConnectedComponent,
						leftConnectedComponent, ACTION_BRK));
				list.add(new Action(toSite, (CSite) toSite.getLinkState()
						.getSite(), rightConnectedComponent,
						leftConnectedComponent));
				if (!isChangedSiteContains(toSite))
					changedSites.add(toSite);
			}
			return list;
		}

		private boolean isChangedSiteContains(CSite site) {
			for (CSite siteCh : changedSites)
				if (siteCh == site)
					return true;
			return false;
		}

		/**
		 * Default constructor, create AtomicAction and add to "actionList".
		 * 
		 * @param fromAgent
		 * @param toAgent
		 */
		public Action(CAgent fromAgent, CAgent toAgent,
				CConnectedComponent ccR, CConnectedComponent ccL) {
			this.fromAgent = fromAgent;
			this.toAgent = toAgent;
			this.rightConnectedComponent = ccR;
			this.leftConnectedComponent = ccL;
			this.action = ACTION_NONE;
			actionList.add(this);
		}

		/**
		 * Constructor "ACTION_BND".
		 * 
		 * @param fromSite
		 * @param toSite
		 * @param action
		 */
		public Action(CSite fromSite, CSite toSite, CConnectedComponent ccR,
				CConnectedComponent ccL) {
			this.siteFrom = fromSite;
			this.siteTo = toSite;
			this.rightConnectedComponent = ccR;
			this.leftConnectedComponent = ccL;
			this.action = ACTION_BND;
		}

		/**
		 * Constructor "ACTION_BRK" and "ACTION_MOD".
		 * 
		 * @param site
		 * @param action
		 */
		public Action(CSite siteFrom, CSite siteTo, CConnectedComponent ccR,
				CConnectedComponent ccL, byte action) {
			this.rightConnectedComponent = ccR;
			this.leftConnectedComponent = ccL;
			switch (action) {
			case ACTION_BRK: {
				this.siteFrom = siteFrom;
				this.action = ACTION_BRK;
				break;
			}
			case ACTION_MOD: {
				this.siteFrom = siteFrom;
				this.siteTo = siteTo;
				this.action = ACTION_MOD;
				this.nameInternalStateId = siteTo.getInternalState()
						.getNameId();
				break;
			}
			}
		}

		/**
		 * Constructor "ACTION_ADD" and "ACTION_DEL".
		 * 
		 * @param agent
		 * @param ccR
		 * @param action
		 */

		public Action(CAgent agent, CConnectedComponent cc, byte action) {
			switch (action) {
			case ACTION_ADD: {
				this.action = ACTION_ADD;
				this.toAgent = agent;
				// agent.setIdInRuleSide(maxAgentID++);
				this.rightConnectedComponent = cc;
				createBound();
				break;
			}
			case ACTION_DEL: {
				this.action = ACTION_DEL;
				this.fromAgent = agent;
				this.leftConnectedComponent = cc;
				break;
			}
			}
		}

		private final void createBound() {
			for (CSite site : toAgent.getSites())
				if (site.getLinkState().getSite() != null)
					actionList.add(new Action(site, ((CSite) site
							.getLinkState().getSite()),
							rightConnectedComponent, null));

		}

	}

	public boolean isClashForInfiniteRule() {
		if (this.leftHandSide.size() == 2) {
			if (this.leftHandSide.get(0).getInjectionsQuantity() == 1
					&& this.leftHandSide.get(1).getInjectionsQuantity() == 1) {
				List<CInjection> injList = new ArrayList<CInjection>();
				injList.add(this.leftHandSide.get(0).getFirstInjection());
				injList.add(this.leftHandSide.get(1).getFirstInjection());
				return isClash(injList);
			}
		}
		return false;
	}
}
