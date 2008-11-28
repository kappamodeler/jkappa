package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulatorManager;

public class CRule implements IRule {

	public static final IConnectedComponent EMPTY_LHS_CC = new CConnectedComponent(
			CConnectedComponent.EMPTY);
	private List<IConnectedComponent> leftHandSide;
	private List<IConnectedComponent> rightHandSide;
	private double activity = 0.;
	private String name;
	private double ruleRate;
	private List<ISite> sitesConnectedWithDeleted;
	private List<ISite> sitesConnectedWithBroken;

	private int automorphismNumber = 1;
	private boolean infinityRate = false;
	private List<IRule> activatedRule;
	private List<IObservablesConnectedComponent> activatedObservable;

	private int ruleID;

	private List<IAction> actionList;

	private HashMap<IAgent, IAgent> agentAddList;

	private List<IInjection> injList;

	private List<ISite> changedSites;

	private List<FixedSite> fixedSites;

	private IConstraint constraints;

	private int countAgentsLHS = 0;

	public int getCountAgentsLHS() {
		return countAgentsLHS;
	}

	public final int getRuleID() {
		return ruleID;
	}

	public final void setRuleID(int id) {
		this.ruleID = id;
	}

	public CRule(List<IConnectedComponent> left,
			List<IConnectedComponent> right, String name, double ruleRate,
			int ruleID) {
		this.leftHandSide = left;
		this.rightHandSide = right;
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

	class FixedSite {
		ISite site;
		boolean linkState;
		boolean internalState;

		public void setLinkState(boolean linkState) {
			this.linkState = linkState;
		}

		public void setInternalState(boolean internalState) {
			this.internalState = internalState;
		}

		public FixedSite(ISite site) {
			this.site = site;
			boolean linkState = false;
			boolean internalState = false;
		}
	}

	public List<ISite> getSitesConnectedWithDeleted() {
		return sitesConnectedWithDeleted;
	}

	public void setRuleRate(double ruleRate) {
		if (ruleRate >= 0) {
			this.ruleRate = ruleRate;
		} else {
			Logger logger = Logger.getLogger(this.getClass());
			logger.info("warning : rate of the rule '" + name
					+ "' was attempted to be set as negative");
			this.ruleRate = 0;
		}
	}

	public List<IObservablesConnectedComponent> getActivatedObservable() {
		return activatedObservable;
	}

	public void setActivatedObservable(
			List<IObservablesConnectedComponent> activatedObservable) {
		this.activatedObservable = activatedObservable;
	}

	public List<IRule> getActivatedRule() {
		return activatedRule;
	}

	public void setActivatedRule(List<IRule> activatedRule) {
		this.activatedRule = activatedRule;
	}

	public boolean isInfinityRate() {
		return infinityRate;
	}

	public void setInfinityRate(boolean infinityRate) {
		this.infinityRate = infinityRate;
	}

	public List<IAction> getActionList() {
		return actionList;
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

	private final HashMap<Integer, List<IAgent>> createAgentMap(
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
			INetworkNotation netNotation) {
		apply(injectionList, netNotation);
	}

	public void applyRule(List<IInjection> injectionList) {
		apply(injectionList, null);
	}

	protected void apply(List<IInjection> injectionList,
			INetworkNotation netNotation) {
		agentAddList = new HashMap<IAgent, IAgent>();
		sitesConnectedWithDeleted = new ArrayList<ISite>();
		sitesConnectedWithBroken = new ArrayList<ISite>();
		this.injList = injectionList;
		if (rightHandSide != null)
			for (IConnectedComponent cc : rightHandSide)
				cc.clearAgentsFromSolutionForRHS();

		for (IAction action : actionList) {
			if (action.getLeftCComponent() == null)
				action.doAction(null, netNotation);
			else
				action.doAction(injectionList.get(leftHandSide.indexOf(action
						.getLeftCComponent())), netNotation);
		}

		if (netNotation != null)
			addFixedSitesToNN(netNotation);
	}

	private void addFixedSitesToNN(INetworkNotation netNotation) {
		for (FixedSite fs : fixedSites) {
			CSite siteFromRule = (CSite) fs.site;
			IInjection inj = getInjectionBySiteToFromLHS(siteFromRule);
			IAgent agentToInSolution = inj.getConnectedComponent()
					.getAgentByIdFromSolution(
							siteFromRule.getAgentLink()
									.getIdInConnectedComponent(), inj);
			ISite site = agentToInSolution.getSite(siteFromRule.getNameId());
			// add fixed agents
			netNotation.addFixedSitesFromRules(site,
					CNetworkNotation.MODE_TEST, fs.internalState, fs.linkState);
		}
	}

	private final void markRHSAgents() {
		List<IAgent> rhsAgents = new ArrayList<IAgent>();
		List<IAgent> lhsAgents = new ArrayList<IAgent>();
		int indexAgentRHS = 0;

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
		fixedSites = new ArrayList<FixedSite>();
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
			if (SimulationMain.getSimulationManager().getSimulationData()
					.isStorify())
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

	public final void fillFixedSites(IAgent lhsAgent, IAgent rhsAgent) {
		for (ISite lhsSite : lhsAgent.getSites()) {
			ISite rhsSite = rhsAgent.getSite(lhsSite.getNameId());
			FixedSite fixedSite = new FixedSite(lhsSite);
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
					fixedSite.setInternalState(true);
			}
			if (fixedSite.linkState != false
					|| fixedSite.internalState != false)
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
		HashMap<Integer, List<IAgent>> lhsAgentMap = createAgentMap(leftHandSide);
		HashMap<Integer, List<IAgent>> rhsAgentMap = createAgentMap(rightHandSide);
		markRHSAgents();

		createActionList();

		sortActionList();

	}

	private final void sortActionList() {
		for (int i = 0; i < actionList.size(); i++) {
			for (int j = 0; j < actionList.size(); j++) {
				if (actionList.get(i).getAction() < actionList.get(j)
						.getAction()) {
					IAction actionMin = actionList.get(j);
					IAction actionR = actionList.get(i);
					actionList.set(j, actionR);
					actionList.set(i, actionMin);
				}
			}
		}
	}

	public List<ISite> getChangedSites() {
		return changedSites;
	}

	private final boolean isActivated(List<IAgent> agentsFromAnotherRules) {
		for (IAgent agent : agentsFromAnotherRules) {
			if (this.rightHandSide != null && checkRulesNullAgents(agent))
				return true;
			for (ISite site : agent.getSites()) {
				for (ISite changedSite : changedSites) {
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

	private boolean checkRulesNullAgents(IAgent agent) {
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

	public final void createActivatedObservablesList(CObservables observables) {
		activatedObservable = new ArrayList<IObservablesConnectedComponent>();
		for (IObservablesConnectedComponent obsCC : observables
				.getConnectedComponentList()) {
			if (obsCC.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX
					&& isActivated(obsCC.getAgents())) {
				activatedObservable.add(obsCC);
			}
		}
	}

	private final void createActionList() {
		changedSites = new ArrayList<ISite>();
		actionList = new ArrayList<IAction>();

		if (rightHandSide == null) {
			for (IConnectedComponent ccL : leftHandSide)
				for (IAgent lAgent : ccL.getAgents())
					actionList.add(new Action(lAgent, ccL, Action.ACTION_DEL));
			return;
		}

		for (IConnectedComponent ccR : rightHandSide)
			for (IAgent rAgent : ccR.getAgents()) {
				if ((countAgentsLHS == 0)
						|| (rAgent.getIdInRuleSide() > countAgentsLHS)) {
					actionList.add(new Action(rAgent, ccR, Action.ACTION_ADD));
					fillChangedSites(null, rAgent);// for activation map
					// creation
				}
			}

		if (leftHandSide.get(0) == EMPTY_LHS_CC)
			return;
		for (IConnectedComponent ccL : leftHandSide)
			for (IAgent lAgent : ccL.getAgents()) {
				if (!isAgentFromLHSHasFoundInRHS(lAgent, ccL))
					actionList.add(new Action(lAgent, ccL, Action.ACTION_DEL));
			}
	}

	private final void fillChangedSites(IAgent agentLeft, IAgent agentRight) {
		if (agentLeft == null)
			for (ISite site : agentRight.getSites()) {
				changedSites.add(site);
			}
	}

	private final boolean isAgentFromLHSHasFoundInRHS(IAgent lAgent,
			IConnectedComponent ccL) {
		for (IConnectedComponent ccR : rightHandSide)
			for (IAgent rAgent : ccR.getAgents()) {
				if (lAgent.getIdInRuleSide() == rAgent.getIdInRuleSide()) {
					Action newAction = new Action(lAgent, rAgent, ccR, ccL);
					actionList.addAll(newAction.createAtomicAction());
					return true;
				}
			}
		return false;
	}

	private final List<IAgent> getAgentsFromConnectedComponent(
			List<IConnectedComponent> ccList) {
		List<IAgent> agentList = new ArrayList<IAgent>();
		if (ccList.get(0).getAgents().get(0).getIdInRuleSide() == CAgent.UNMARKED)
			return agentList;
		for (IConnectedComponent cc : ccList)
			for (IAgent agent : cc.getAgents())
				agentList.add(agent);

		return agentList;
	}

	private final IAgent getAgentByIdInRuleFromLHS(Integer id) {
		for (IAgent agent : getAgentsFromConnectedComponent(leftHandSide))
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
		return leftHandSide;
	}

	public final List<IConnectedComponent> getRightHandSide() {
		return rightHandSide;
	}

	public boolean isClash(List<IInjection> injections) {
		if (injections.size() == 2) {
			for (ISite siteCC1 : injections.get(0).getSiteList())
				for (ISite siteCC2 : injections.get(1).getSiteList())
					if (siteCC1.getAgentLink().getId() == siteCC2
							.getAgentLink().getId())
						return true;
		}
		return false;
	}

	private final IInjection getInjectionBySiteToFromLHS(ISite siteTo) {
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

	public class Action implements IAction {
		public static final byte ACTION_BRK = 0;
		public static final byte ACTION_DEL = 1;
		public static final byte ACTION_ADD = 2;
		public static final byte ACTION_BND = 3;
		public static final byte ACTION_MOD = 4;
		public static final byte ACTION_NONE = -1;

		private byte action = ACTION_NONE;

		private IAgent fromAgent;
		private IAgent toAgent;

		private IConnectedComponent rightConnectedComponent;
		private IConnectedComponent leftConnectedComponent;

		/**
		 * Default constructor, create AtomicAction and add to "actionList".
		 * 
		 * @param fromAgent
		 * @param toAgent
		 */
		public Action(IAgent fromAgent, IAgent toAgent,
				IConnectedComponent ccR, IConnectedComponent ccL) {
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
		public Action(ISite fromSite, ISite toSite, IConnectedComponent ccR,
				IConnectedComponent ccL) {
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
		public Action(ISite siteFrom, ISite siteTo, IConnectedComponent ccR,
				IConnectedComponent ccL, byte action) {
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

		public Action(IAgent agent, IConnectedComponent cc, byte action) {
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

		public IConnectedComponent getCLeftComponent() {
			return leftConnectedComponent;
		}

		private ISite siteFrom;

		public IAgent getFromAgent() {
			return fromAgent;
		}

		public IAgent getToAgent() {
			return toAgent;
		}

		public ISite getSiteFrom() {
			return siteFrom;
		}

		public ISite getSiteTo() {
			return siteTo;
		}

		public Integer getNameInternalStateId() {
			return nameInternalStateId;
		}

		private ISite siteTo;
		private Integer nameInternalStateId;

		private final void addToNetworkNotation(int index,
				INetworkNotation netNotation, ISite site) {
			if (netNotation != null)
				switch (action) {
				case ACTION_BRK:
					netNotation.checkLinkForNetworkNotation(index, site);
					break;
				case ACTION_DEL:
					netNotation.checkLinkForNetworkNotationDel(index, site);
					break;
				case ACTION_ADD:
					netNotation.addToAgents(site, new CStoriesSiteStates(index,
							site.getInternalState().getNameId()), index);
					break;
				case ACTION_BND:
					netNotation.checkLinkForNetworkNotation(index, site);
					break;
				case ACTION_MOD:
					netNotation.addToAgents(site, new CStoriesSiteStates(index,
							site.getInternalState().getNameId()), index);
					break;
				}
		}

		private final void addRuleSitesToNetworkNotation(boolean existInRule,
				INetworkNotation netNotation, ISite site) {
			if (netNotation != null) {
				byte agentMode = CNetworkNotation.MODE_NONE;
				byte linkStateMode = CNetworkNotation.MODE_NONE;
				byte internalStateMode = CNetworkNotation.MODE_NONE;

				switch (action) {
				case ACTION_BRK:
					if (existInRule) {
						agentMode = CNetworkNotation.MODE_TEST;
						linkStateMode = CNetworkNotation.MODE_TEST_OR_MODIFY;
					} else
						linkStateMode = CNetworkNotation.MODE_MODIFY;
					break;
				case ACTION_DEL:
					if (existInRule) {
						agentMode = CNetworkNotation.MODE_TEST_OR_MODIFY;
						linkStateMode = CNetworkNotation.MODE_TEST_OR_MODIFY;
						if (site.getInternalState().getNameId() != CSite.NO_INDEX)
							internalStateMode = CNetworkNotation.MODE_TEST_OR_MODIFY;
						else
							internalStateMode = CNetworkNotation.MODE_MODIFY;
					} else
						linkStateMode = CNetworkNotation.MODE_MODIFY;
					break;
				case ACTION_ADD:
					agentMode = CNetworkNotation.MODE_TEST_OR_MODIFY;
					if (site.getInternalState().getNameId() != CSite.NO_INDEX)
						internalStateMode = CNetworkNotation.MODE_TEST_OR_MODIFY;
					if (site.getLinkState().getStatusLinkRank() != CLinkState.RANK_SEMI_LINK)
						linkStateMode = CNetworkNotation.MODE_TEST_OR_MODIFY;
					break;
				case ACTION_BND:
					agentMode = CNetworkNotation.MODE_TEST;
					linkStateMode = CNetworkNotation.MODE_TEST_OR_MODIFY;
					break;
				case ACTION_MOD:
					agentMode = CNetworkNotation.MODE_TEST_OR_MODIFY;
					internalStateMode = CNetworkNotation.MODE_TEST_OR_MODIFY;
					break;

				}
				netNotation.addToAgentsFromRules(site, agentMode,
						internalStateMode, linkStateMode);
			}
		}

		public final void doAction(IInjection injection,
				INetworkNotation netNotation) {

			switch (action) {
			case ACTION_ADD: {
				/**
				 * Done.
				 */
				IAgent agent = new CAgent(toAgent.getNameId());
				for (ISite site : toAgent.getSites()) {
					ISite siteAdd = new CSite(site.getNameId());
					siteAdd.setInternalState(new CInternalState(site
							.getInternalState().getStateNameId()));
					agent.addSite(siteAdd);
					addToNetworkNotation(CStoriesSiteStates.CURRENT_STATE,
							netNotation, siteAdd);
					addRuleSitesToNetworkNotation(false, netNotation, siteAdd);
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
				IAgent agentFromInSolution = leftConnectedComponent
						.getAgentByIdFromSolution(agentIdInCC, injection);
				rightConnectedComponent
						.addAgentFromSolutionForRHS(agentFromInSolution);

				break;
			}
			case ACTION_BND: {
				/**
				 * Done.
				 */
				IAgent agentFromInSolution;
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
					ISite injectedSite = agentFromInSolution.getSite(siteFrom
							.getNameId());
					injection.addToChangedSites(injectedSite);

					addToNetworkNotation(CStoriesSiteStates.LAST_STATE,
							netNotation, injectedSite);
					addRuleSitesToNetworkNotation(false, netNotation,
							injectedSite);
					// /////////////////////////////////////////////
				}

				IAgent agentToInSolution;
				if (siteTo.getAgentLink().getIdInRuleSide() > getAgentsFromConnectedComponent(
						leftHandSide).size()) {
					agentToInSolution = agentAddList.get(siteTo.getAgentLink());
				} else {
					int agentIdInCC = getAgentIdInCCBySideId(siteTo
							.getAgentLink());
					IInjection inj = getInjectionBySiteToFromLHS(siteTo);
					agentToInSolution = leftConnectedComponent
							.getAgentByIdFromSolution(agentIdInCC, inj);
				}

				agentFromInSolution.getSite(siteFrom.getNameId())
						.getLinkState().setSite(
								agentToInSolution.getSite(siteTo.getNameId()));

				addToNetworkNotation(CStoriesSiteStates.CURRENT_STATE,
						netNotation, agentFromInSolution.getSite(siteFrom
								.getNameId()));

				agentFromInSolution.getSite(siteFrom.getNameId())
						.setLinkIndex(siteFrom.getLinkIndex());
				agentToInSolution.getSite(siteTo.getNameId())
					.setLinkIndex(siteTo.getLinkIndex());
				
				
				break;
			}
			case ACTION_BRK: {

				IAgent agentFromInSolution;
				int agentIdInCC = getAgentIdInCCBySideId(siteFrom
						.getAgentLink());
				agentFromInSolution = leftConnectedComponent
						.getAgentByIdFromSolution(agentIdInCC, injection);

				ISite injectedSite = agentFromInSolution.getSite(siteFrom
						.getNameId());

				addToNetworkNotation(CStoriesSiteStates.LAST_STATE,
						netNotation, injectedSite);
				addRuleSitesToNetworkNotation(true, netNotation, injectedSite);

				ISite linkSite = (ISite) injectedSite.getLinkState().getSite();
				if ((siteFrom.getLinkState().getSite() == null)
						&& (linkSite != null)) {
					addToNetworkNotation(CStoriesSiteStates.LAST_STATE,
							netNotation, linkSite);

					linkSite.getLinkState().setSite(null);
					linkSite.getLinkState().setStatusLink(
							CLinkState.STATUS_LINK_FREE);
					if (siteTo != null) {
						linkSite.setLinkIndex(siteTo.getLinkIndex());
					}
					injection.addToChangedSites(linkSite);
					rightConnectedComponent.addAgentFromSolutionForRHS(linkSite
							.getAgentLink());
					addToNetworkNotation(CStoriesSiteStates.CURRENT_STATE,
							netNotation, linkSite);

				}

				agentFromInSolution.getSite(siteFrom.getNameId())
						.getLinkState().setSite(null);
				agentFromInSolution.getSite(siteFrom.getNameId())
						.getLinkState().setStatusLink(
								CLinkState.STATUS_LINK_FREE);
				// /////////////////////////////////////////////

				injection.addToChangedSites(injectedSite);

				addToNetworkNotation(CStoriesSiteStates.CURRENT_STATE,
						netNotation, injectedSite);
				/**
				 * Break a bond for this rules: A(x!_)->A(x)
				 */
				if (siteFrom.getLinkState().getSite() == null
						&& linkSite != null) {
					addSiteToConnectedWithBroken(linkSite);
					addRuleSitesToNetworkNotation(false, netNotation, linkSite);
				}
				// /////////////////////////////////////////////
				agentFromInSolution.getSite(siteFrom.getNameId())
						.setLinkIndex(siteFrom.getLinkIndex());
				break;
			}
			case ACTION_DEL: {
				/**
				 * Done.
				 */

				IAgent agent = leftConnectedComponent.getAgentByIdFromSolution(
						fromAgent.getIdInConnectedComponent(), injection);
				for (ISite site : agent.getSites()) {
					removeSiteToConnectedWithDeleted(site);
					ISite solutionSite = (ISite) site.getLinkState().getSite();

					if (solutionSite != null) {
						addToNetworkNotation(CStoriesSiteStates.LAST_STATE,
								netNotation, solutionSite);

						addSiteToConnectedWithDeleted(solutionSite);
						solutionSite.getLinkState().setSite(null);
						solutionSite.getLinkState().setStatusLink(
								CLinkState.STATUS_LINK_FREE);
						solutionSite.setLinkIndex(-1);
						addToNetworkNotation(CStoriesSiteStates.CURRENT_STATE,
								netNotation, solutionSite);
						addRuleSitesToNetworkNotation(false, netNotation,
								solutionSite);
						// solutionSite.removeInjectionsFromCCToSite(injection);
					}
				}

				for (ILiftElement lift : agent.getEmptySite().getLift()) {
					agent.getEmptySite().removeInjectionsFromCCToSite(
							lift.getInjection());
					lift.getInjection().getConnectedComponent()
							.removeInjection(lift.getInjection());
				}

				for (ISite site : agent.getSites()) {
					addToNetworkNotation(CStoriesSiteStates.LAST_STATE,
							netNotation, site);
					addRuleSitesToNetworkNotation(true, netNotation, site);
					for (ILiftElement lift : site.getLift()) {
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
				IAgent agentFromInSolution = leftConnectedComponent
						.getAgentByIdFromSolution(agentIdInCC, injection);

				// /////////////////////////////////////////////
				ISite injectedSite = agentFromInSolution.getSite(siteTo
						.getNameId());
				addToNetworkNotation(CStoriesSiteStates.LAST_STATE,
						netNotation, injectedSite);
				addRuleSitesToNetworkNotation(false, netNotation, injectedSite);

				injectedSite.getInternalState().setNameId(nameInternalStateId);
				injection.addToChangedSites(injectedSite);

				addToNetworkNotation(CStoriesSiteStates.CURRENT_STATE,
						netNotation, injectedSite);

				// /////////////////////////////////////////////
				break;
			}
			}
		}

		private void removeSiteToConnectedWithDeleted(ISite checkedSite) {
			for (int i = 0; i < sitesConnectedWithDeleted.size(); i++) {
				if (sitesConnectedWithDeleted.get(i) == checkedSite) {
					sitesConnectedWithDeleted.remove(i);
					return;
				}
			}
		}

		private void addSiteToConnectedWithDeleted(ISite checkedSite) {
			for (ISite site : sitesConnectedWithDeleted)
				if (site == checkedSite)
					return;
			sitesConnectedWithDeleted.add(checkedSite);
		}

		private void addSiteToConnectedWithBroken(ISite checkedSite) {
			for (ISite site : sitesConnectedWithBroken)
				if (site == checkedSite)
					return;
			sitesConnectedWithBroken.add(checkedSite);
		}

		private final int getAgentIdInCCBySideId(IAgent toAgent2) {
			for (IConnectedComponent cc : leftHandSide)
				for (IAgent agentL : cc.getAgents())
					if (agentL.getIdInRuleSide() == toAgent2.getIdInRuleSide()) {
						if (leftConnectedComponent == null)
							leftConnectedComponent = cc;
						return agentL.getIdInConnectedComponent();
					}
			return -1;
		}

		public IConnectedComponent getRightCComponent() {
			return rightConnectedComponent;
		}

		public IConnectedComponent getLeftCComponent() {
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

			for (ISite fromSite : fromAgent.getSites()) {
				ISite toSite = toAgent.getSite(fromSite.getNameId());
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
					list.add(new Action(toSite, (ISite) toSite.getLinkState()
							.getSite(), rightConnectedComponent,
							leftConnectedComponent));
					if (!isChangedSiteContains(toSite))
						changedSites.add(toSite);
					continue;
				}

				ISite lConnectSite = (ISite) fromSite.getLinkState().getSite();
				ISite rConnectSite = (ISite) toSite.getLinkState().getSite();
				if (lConnectSite == null || rConnectSite == null)
					continue;
				if ((lConnectSite.getAgentLink().getIdInRuleSide() == rConnectSite
						.getAgentLink().getIdInRuleSide())
						&& (lConnectSite.equals(rConnectSite)))
					continue;
				list.add(new Action(fromSite, toSite, rightConnectedComponent,
						leftConnectedComponent, ACTION_BRK));
				list.add(new Action(toSite, (ISite) toSite.getLinkState()
						.getSite(), rightConnectedComponent,
						leftConnectedComponent));
				if (!isChangedSiteContains(toSite))
					changedSites.add(toSite);
			}
			return list;
		}

		private boolean isChangedSiteContains(ISite site) {
			for (ISite siteCh : changedSites)
				if (siteCh == site)
					return true;
			return false;
		}

		private final void createBound() {
			for (ISite site : toAgent.getSites())
				if (site.getLinkState().getSite() != null)
					actionList.add(new Action(site, (site.getLinkState()
							.getSite()), rightConnectedComponent, null));

		}

	}

	public boolean isClashForInfiniteRule() {
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

	public List<ISite> getSitesConnectedWithBroken() {
		return this.sitesConnectedWithBroken;
	}

	public void applyLastRuleForStories(List<IInjection> injectionsList,
			INetworkNotation netNotation) {
		for (IInjection inj : injectionsList) {
			for (ISite site : inj.getSiteList())
				netNotation.checkLinkForNetworkNotationDel(
						CStoriesSiteStates.LAST_STATE, site);
		}
	}

}
