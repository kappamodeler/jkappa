package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import junit.framework.ComparisonCompactor;

public class CRule {

	private List<CConnectedComponent> leftHandSide;
	private List<CConnectedComponent> rightHandSide;
	private double activity = 0.;
	private String name;
	private double ruleRate;
	private int automorphismNumber = 1;

	private List<Action> actionList;

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
		// TODO delete excess Actions, which  repeat
		
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
		List<CAgent> leftAgentList = getAgentsFromConnectedComponent(leftHandSide);
		List<CAgent> rightAgentList = getAgentsFromConnectedComponent(rightHandSide);

		if (leftHandSide == null) {
			for (CAgent agent : rightAgentList)
				actionList.add(new Action(agent, Action.ACTION_ADD));
			return;
		}
		if (rightHandSide == null) {
			for (CAgent agent : leftAgentList)
				actionList.add(new Action(agent, Action.ACTION_DEL));
			return;
		}

		for (CAgent lAgent : leftAgentList) {
			boolean isFind = false;
			for (CAgent rAgent : rightAgentList) {
				if (rAgent.getIdInRuleSide() == CAgent.UNMARKED) {
					actionList.add(new Action(rAgent, Action.ACTION_ADD));
					rAgent.setIdInRuleSide(CAgent.ACTION_CREATE);
				}
				if (lAgent.getIdInRuleSide() == rAgent.getIdInRuleSide()) {
					Action newAction = new Action(lAgent, rAgent);
					isFind = true;
					break;
				}
			}
			if (!isFind)
				actionList.add(new Action(lAgent, Action.ACTION_DEL));
		}

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

	private final CAgent getAgentByIdInRuleSideFromLHS(Integer id) {
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

		private CSite site;
		private CSite toSite;
		private Integer nameInternalStateId;

		public byte getAction() {
			return action;
		}

		private final void createAtomicAction() {
			if (fromAgent.getSites() == null) {
				this.action = ACTION_NON;
				return;
			}

			for (CSite site : fromAgent.getSites()) {
				CSite toSite = toAgent.getSite(site.getNameId());
				if (site.getInternalState().getStateNameId() != toSite
						.getInternalState().getStateNameId())
					actionList.add(new Action(site, toSite.getInternalState()
							.getStateNameId()));

				if ((site.getLinkState().getSite() == null)
						&& (toSite.getLinkState().getSite() == null))
					continue;

				if ((site.getLinkState().getSite() != null)
						&& (toSite.getLinkState().getSite() == null))
					actionList.add(new Action(site, ACTION_BRK));

				if ((site.getLinkState().getSite() == null)
						&& (toSite.getLinkState().getSite() != null))
					actionList.add(new Action(site, toSite, ACTION_BND));

				CSite lConnectSite = (CSite) site.getLinkState().getSite();
				CSite rConnectSite = (CSite) toSite.getLinkState().getSite();
				if ((lConnectSite == null) || (rConnectSite == null))
					continue;
				if (lConnectSite.getAgentLink().getIdInRuleSide() == rConnectSite
						.getAgentLink().getIdInRuleSide())
					continue;
				int lId = lConnectSite.getAgentLink().getIdInRuleSide();
				int rId = rConnectSite.getAgentLink().getIdInRuleSide();
				if (rId < lId) {
					actionList.add(new Action(site, ACTION_BRK));
					actionList.add(new Action(site, toSite, ACTION_BND));
				}

			}

		}

		/**
		 * Default constructor, create AtomicAction and add to "actionList".
		 * 
		 * @param fromAgent
		 * @param toAgent
		 */
		public Action(CAgent fromAgent, CAgent toAgent) {
			this.fromAgent = fromAgent;
			this.toAgent = toAgent;
			this.action = ACTION_NON;
			createAtomicAction();
		}

		/**
		 * Constructor "ACTION_BND".
		 * 
		 * @param fromSite
		 * @param toSite
		 * @param action
		 */
		public Action(CSite fromSite, CSite toSite, byte action) {
			this.site = fromSite;
			this.toSite = toSite;

			switch (action) {
			case ACTION_BND: {
				this.action = ACTION_BND;
				break;
			}
			}
		}

		/**
		 * Constructor "ACTION_MOD".
		 * 
		 * @param site
		 * @param nameInternalState
		 */
		public Action(CSite site, Integer nameInternalStateId) {
			this.site = site;
			this.action = ACTION_MOD;
			this.nameInternalStateId = nameInternalStateId;
		}

		/**
		 * Constructor "ACTION_BRK".
		 * 
		 * @param site
		 * @param action
		 */
		public Action(CSite site, byte action) {
			this.site = site;
			switch (action) {
			case ACTION_BRK: {
				this.action = ACTION_BRK;
				break;
			}
			}
		}

		/**
		 * Constructor "ACTION_ADD" or "ACTION_DEL".
		 * 
		 * @param agent
		 * @param action
		 */
		public Action(CAgent agent, byte action) {
			switch (action) {
			case ACTION_ADD: {
				this.action = ACTION_ADD;
				this.toAgent = agent;
				break;
			}
			case ACTION_DEL: {
				this.action = ACTION_DEL;
				this.fromAgent = agent;
				break;
			}
			}
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
			// TODO
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
