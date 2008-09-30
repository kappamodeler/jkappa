package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.plectix.simulator.SimulationMain;

public class CRule {

	private List<CConnectedComponent> leftHandSide;
	private List<CConnectedComponent> rightHandSide;
	private double activity = 0.;
	private String name;
	private double ruleRate;
	private int automorphismNumber = 1;
	private boolean infinityRate = false;

	public boolean isInfinityRate() {
		return infinityRate;
	}

	public void setInfinityRate(boolean infinityRate) {
		this.infinityRate = infinityRate;
	}

	private int maxAgentID = 0;

	private List<Action> actionList;

	private HashMap<CAgent, CAgent> agentAddList;

	private List<CInjection> injList;

	public final int getAutomorphismNumber() {
		return automorphismNumber;
	}

	public final double getRuleRate() {
		return ruleRate;
	}

	private final void markedLHS() {
		int counter = 1;
		if (leftHandSide != null)
			for (CConnectedComponent cc : leftHandSide)
				for (CAgent agent : cc.getAgents()) {
					agent.setIdInRuleSide(counter);
					counter++;
				}
		maxAgentID = counter;
	}

	// private final void
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
		this.injList = injectionList;
		if (rightHandSide != null)
			for (CConnectedComponent cc : rightHandSide)
				cc.getAgentFromSolutionForRHS().clear();
		// for(CConnectedComponent cc: leftHandSide)
		// cc.set

		// for (CConnectedComponent cc : rightHandSide) {
		// for (CAgent agent : cc.getAgents()) {
		// int agentIdInCC = getAgentIdInCCBySideId(agent);
		// CAgent agentFromInSolution = leftConnectedComponent
		// .getAgentByIdFromSolution(agentIdInCC, injection);
		//
		// }
		//
		// }

		for (Action action : actionList) {
			if (action.getLeftCComponent() == null)
				action.doAction(null);
			else
				action.doAction(injectionList.get(leftHandSide.indexOf(action
						.getLeftCComponent())));
		}
	}

	private final void markedRHS(List<List<Score>> scoresMatrix) {

		for (List<Score> scoreList : scoresMatrix) {
			for (Score score : scoreList) {
				if (score.getValue() == 1
						&& score.getRhsAgent().getIdInRuleSide() == CAgent.UNMARKED) {
					score.getRhsAgent().setIdInRuleSide(
							score.getLhsAgent().getIdInRuleSide());
					break;
				}
			}
		}
	}

	private final void indexingRHSAgents() {
		markedLHS();
		HashMap<Integer, List<CAgent>> lhsAgentMap = createAgentMap(leftHandSide);
		HashMap<Integer, List<CAgent>> rhsAgentMap = createAgentMap(rightHandSide);
		List<List<Score>> scoresMatrix = new ArrayList<List<Score>>();

		for (List<CAgent> agentList : lhsAgentMap.values()) {
			for (CAgent agent : agentList) {
				scoresMatrix.add(calculateScoreList(rhsAgentMap, agent));

			}
		}
		// make list of indexes
		markedRHS(scoresMatrix);
		scoresMatrix.clear();

		createActionList();
		// TODO delete excess Actions, which repeat

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

	private final void createActionList() {
		actionList = new ArrayList<Action>();

		// if (leftHandSide == null) {
		// // for (CAgent agent : rightAgentList)
		// for (CConnectedComponent cc : rightHandSide)
		// for (CAgent agent : cc.getAgents())
		// actionList.add(new Action(agent, cc, Action.ACTION_ADD));
		// return;
		// }
		if (rightHandSide == null) {
			for (CConnectedComponent ccL : leftHandSide)
				for (CAgent lAgent : ccL.getAgents())
					// for (CAgent agent : leftAgentList)
					actionList.add(new Action(lAgent, ccL, Action.ACTION_DEL));
			return;
		}

		for (CConnectedComponent ccR : rightHandSide)
			for (CAgent rAgent : ccR.getAgents()) {
				// for (CAgent rAgent : rightAgentList) {
				if (rAgent.getIdInRuleSide() == CAgent.UNMARKED) {
					actionList.add(new Action(rAgent, ccR, Action.ACTION_ADD));
					rAgent.setIdInRuleSide(CAgent.ACTION_CREATE);
				}
			}

		for (CConnectedComponent ccL : leftHandSide)
			for (CAgent lAgent : ccL.getAgents()) {
				if (!isAgentFromLHSHasFoundInRHS(lAgent, ccL))
					actionList.add(new Action(lAgent, ccL, Action.ACTION_DEL));
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
		if (ccList == null)
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

	private final List<Score> calculateScoreList(
			HashMap<Integer, List<CAgent>> rhsAgentMap, CAgent lhsAgent) {
		List<Score> listScore = new ArrayList<Score>();
		if (rhsAgentMap.get(lhsAgent.getNameId()) == null)
			return listScore;
		for (CAgent rhsAgent : rhsAgentMap.get(lhsAgent.getNameId())) {
			listScore.add(new Score(lhsAgent, rhsAgent));
		}
		return listScore;
	}

	public void setAutomorphismNumber(int automorphismNumber) {
		this.automorphismNumber = automorphismNumber;
	}

	public CRule(List<CConnectedComponent> left,
			List<CConnectedComponent> right, String name, double ruleRate) {
		this.leftHandSide = left;
		this.rightHandSide = right;
		setConnectedComponentLinkRule(left);
		setConnectedComponentLinkRule(right);
		for (CConnectedComponent cc : this.leftHandSide) {
			cc.initSpanningTreeMap();
		}
		this.ruleRate = ruleRate;
		this.name = name;
		calculateAutomorphismsNumber();
		indexingRHSAgents();
	}

	private final void calculateAutomorphismsNumber() {
		if (leftHandSide != null)
			if (this.leftHandSide.size() == 2) {
				if (this.leftHandSide.get(0).unify(
						this.leftHandSide.get(1).getAgents().get(0))
						&& this.leftHandSide.get(1).unify(
								this.leftHandSide.get(0).getAgents().get(0)))
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

	public final List<CInjection> getSomeInjectionList() {
		List<CInjection> list = new ArrayList<CInjection>();
		Random rand = new Random();

		for (CConnectedComponent cc : this.leftHandSide) {
			list.add(cc.getInjectionsList().get(
					rand.nextInt(cc.getInjectionsList().size())));
		}
		return list;
	}

	public final void calcultateActivity() {
		activity = 1.;
		for (CConnectedComponent cc : this.leftHandSide) {
			activity *= cc.getInjectionsList().size();
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

	private class Action {
		private static final byte ACTION_BRK = 0;
		private static final byte ACTION_DEL = 1;
		private static final byte ACTION_ADD = 2;
		private static final byte ACTION_BND = 3;
		private static final byte ACTION_MOD = 4;
		private static final byte ACTION_NON = -1;

		private byte action = ACTION_NON;

		private CAgent fromAgent;
		private CAgent toAgent;

		private CConnectedComponent rightConnectedComponent;
		private CConnectedComponent leftConnectedComponent;

		public CConnectedComponent getCLeftComponent() {
			return leftConnectedComponent;
		}

		private CInjection injection;

		private CSite siteFrom;
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
				// if (rightConnectedComponent.getAgentFromSolutionForRHS() ==
				// null)
				rightConnectedComponent.addAgentFromSolutionForRHS(agent);
				((CSolution) SimulationMain.getSimulationManager()
						.getSimulationData().getSolution()).addAgent(agent);

				agentAddList.put(toAgent, agent);
				toAgent.setIdInRuleSide(maxAgentID++);
				// if (rightConnectedComponent.getAgentFromSolutionForRHS() ==
				// null)
				// rightConnectedComponent.addAgentFromSolutionForRHS(agent);
				break;
			}
			case ACTION_NON:{
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
					// agentFromInSolution = leftConnectedComponent
					// .getAgentByIdFromSolution(siteFrom.getAgentLink()
					// .getIdInConnectedComponent(), injection);
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
					// agentToInSolution = leftConnectedComponent
					// .getAgentByIdFromSolution(siteTo.getAgentLink()
					// .getIdInConnectedComponent(), injection);
				}

				agentFromInSolution.getSite(siteFrom.getNameId())
						.getLinkState().setSite(
								agentToInSolution.getSite(siteTo.getNameId()));
				// if (rightConnectedComponent.getAgentFromSolutionForRHS() ==
				// null)
				rightConnectedComponent
						.addAgentFromSolutionForRHS(agentFromInSolution);

				break;
			}
			case ACTION_BRK: {

				CAgent agentFromInSolution;
				int agentIdInCC = getAgentIdInCCBySideId(siteFrom
						.getAgentLink());
				agentFromInSolution = leftConnectedComponent
						.getAgentByIdFromSolution(agentIdInCC, injection);

				// agentFromInSolution = leftConnectedComponent
				// .getAgentByIdFromSolution(siteFrom.getAgentLink()
				// .getIdInConnectedComponent(), injection);

				agentFromInSolution.getSite(siteFrom.getNameId())
						.getLinkState().setSite(null);
				agentFromInSolution.getSite(siteFrom.getNameId())
						.getLinkState().setStatusLink(
								CLinkState.STATUS_LINK_FREE);

				// if (rightConnectedComponent.getAgentFromSolutionForRHS() ==
				// null)
				rightConnectedComponent
						.addAgentFromSolutionForRHS(agentFromInSolution);

				break;
			}
			case ACTION_DEL: {
				/**
				 * Done.
				 */

				CAgent agent = leftConnectedComponent.getAgentByIdFromSolution(
						fromAgent.getIdInConnectedComponent(), injection);
				for (CSite site : agent.getSites()) {
					CSite solutionSite = (CSite) site.getLinkState().getSite();
					if (solutionSite != null) {
						solutionSite.getLinkState().setSite(null);
						solutionSite.getLinkState().setStatusLink(
								CLinkState.STATUS_LINK_FREE);
					}
				}

				for (CSite site : injection.getSiteList()) {
					site.removeInjectionsFromCCToSite(injection);
					site.getLift().clear();
				}
				injection.getConnectedComponent().getInjectionsList().remove(
						injection);

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

				// CAgent agent =
				// leftConnectedComponent.getAgentByIdFromSolution(
				// siteFrom.getAgentLink().getIdInConnectedComponent(),
				// injection);
				agentFromInSolution.getSite(siteTo.getNameId())
						.getInternalState().setNameId(nameInternalStateId);
				// agentFromInSolution.getSite(siteFrom.getNameId()).
				// getInternalState()
				// .setNameId(nameInternalStateId);

				// agent.getSite(siteFrom.getNameId()).getInternalState()
				// .setNameId(nameInternalStateId);
				// if (rightConnectedComponent.getAgentFromSolutionForRHS() ==
				// null)
				rightConnectedComponent
						.addAgentFromSolutionForRHS(agentFromInSolution);
				break;
			}
			}
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
				this.action = ACTION_NON;
				return null;
			}
			List<Action> list = new ArrayList<Action>();

			for (CSite fromSite : fromAgent.getSites()) {
				CSite toSite = toAgent.getSite(fromSite.getNameId());
				if (fromSite.getInternalState().getStateNameId() != toSite
						.getInternalState().getStateNameId())
					list.add(new Action(toSite, rightConnectedComponent,
							leftConnectedComponent, ACTION_MOD));

				if ((fromSite.getLinkState().getSite() == null)
						&& (toSite.getLinkState().getSite() == null))
					continue;

				if ((fromSite.getLinkState().getSite() != null)
						&& (toSite.getLinkState().getSite() == null)) {
					list.add(new Action(toSite, rightConnectedComponent,
							leftConnectedComponent, ACTION_BRK));
					continue;
				}

				if ((fromSite.getLinkState().getSite() == null)
						&& (toSite.getLinkState().getSite() != null)) {
					list.add(new Action(toSite, (CSite) toSite.getLinkState()
							.getSite(), rightConnectedComponent,
							leftConnectedComponent));
					continue;
				}

				CSite lConnectSite = (CSite) fromSite.getLinkState().getSite();
				CSite rConnectSite = (CSite) toSite.getLinkState().getSite();
				if (lConnectSite.getAgentLink().getIdInRuleSide() == rConnectSite
						.getAgentLink().getIdInRuleSide())
					continue;
				list.add(new Action(toSite, rightConnectedComponent,
						leftConnectedComponent, ACTION_BRK));
				list.add(new Action(toSite, (CSite) toSite.getLinkState()
						.getSite(), rightConnectedComponent,
						leftConnectedComponent));
			}

			return list;

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
			this.action = ACTION_NON;
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
		 * Constructor "ACTION_MOD".
		 * 
		 * @param site
		 * @param nameInternalState
		 */
		/*
		 * public Action(CSite site, CConnectedComponent ccR,
		 * CConnectedComponent ccL, byte action) { this.siteTo = site;
		 * this.action = ACTION_MOD; this.rightConnectedComponent = ccR;
		 * this.leftConnectedComponent = ccL; this.nameInternalStateId =
		 * site.getNameId(); }
		 */

		/**
		 * Constructor "ACTION_BRK" and "ACTION_MOD".
		 * 
		 * @param site
		 * @param action
		 */
		public Action(CSite site, CConnectedComponent ccR,
				CConnectedComponent ccL, byte action) {
			this.rightConnectedComponent = ccR;
			this.leftConnectedComponent = ccL;
			switch (action) {
			case ACTION_BRK: {
				this.siteFrom = site;
				this.action = ACTION_BRK;
				break;
			}
			case ACTION_MOD: {
				this.siteTo = site;
				this.action = ACTION_MOD;
				this.nameInternalStateId = site.getInternalState().getNameId();
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
				agent.setIdInRuleSide(maxAgentID++);
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

	private class Score {
		private CAgent lhsAgent;
		private CAgent rhsAgent;
		private int value = -1;

		public CAgent getLhsAgent() {
			return lhsAgent;
		}

		public CAgent getRhsAgent() {
			return rhsAgent;
		}

		public int getValue() {
			return value;
		}

		public Score(CAgent lhs, CAgent rhs) {
			this.lhsAgent = lhs;
			this.rhsAgent = rhs;
			initScoreValue();
		}

		public void initScoreValue() {
			/**
			 * Field value maybe not only 0 or 1, because we can compare link
			 * states with bound or free status. But we are not sure if we need
			 * such check.
			 */

			if (lhsAgent.getSites().size() != rhsAgent.getSites().size()) {
				value = 0;
				return;
			}

			for (CSite lhsSite : lhsAgent.getSites()) {
				CSite rhsSite = rhsAgent.getSite(lhsSite.getNameId());
				if ((rhsSite == null)
						|| (!(checkInternalState(lhsSite, rhsSite)))
				// || (!(checkLinkState(lhsSite, rhsSite)))
				) {
					value = 0;
					return;
				}
			}
			value = 1;
		}

		private final boolean checkInternalState(CSite lhsSite, CSite rhsSite) {
			if ((lhsSite.getInternalState() == null)
					&& (rhsSite.getInternalState() == null))
				return true;

			if ((lhsSite.getInternalState() != null)
					&& (rhsSite.getInternalState() != null))
				return true;
			return false;
		}

		private final boolean checkLinkState(CSite lhsSite, CSite rhsSite) {
			CLinkState lhsLinkState = lhsSite.getLinkState();
			CLinkState rhsLinkState = rhsSite.getLinkState();

			if ((lhsLinkState.getStatusLink() == CLinkState.STATUS_LINK_WILDCARD)
					&& (rhsLinkState.getStatusLink() == CLinkState.STATUS_LINK_WILDCARD))
				return true;

			if (((lhsLinkState.getStatusLink() == CLinkState.STATUS_LINK_BOUND) && (lhsLinkState
					.getSite() == null))
					&& ((rhsLinkState.getStatusLink() == CLinkState.STATUS_LINK_WILDCARD) && (rhsLinkState
							.getSite() == null)))
				return true;

			return false;
		}
	}
}
