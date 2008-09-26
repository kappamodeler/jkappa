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

	private HashMap<Integer, List<CAgent>> lhsAgentMap;
	private HashMap<Integer, List<CAgent>> rhsAgentMap;
	private List<List<Score>> scoresMatrix;

	public int getAutomorphismNumber() {
		return automorphismNumber;
	}

	public double getRuleRate() {
		return ruleRate;
	}

	private final void markedLHS() {
		int counter = 1;
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

	private final void markedRHS() {

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
		lhsAgentMap = createAgentMap(leftHandSide);
		rhsAgentMap = createAgentMap(rightHandSide);
		scoresMatrix = new ArrayList<List<Score>>();

		for (List<CAgent> agentList : lhsAgentMap.values()) {
			for (CAgent agent : agentList) {
				scoresMatrix.add(calculateScoreList(agent));

			}
			// make list of indexes
			markedRHS();

			scoresMatrix.clear();
		}

	}

	private final List<Score> calculateScoreList(CAgent lhsAgent) {
		List<Score> listScore = new ArrayList<Score>();

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

	private void calculateAutomorphismsNumber() {
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

	public List<CInjection> getSomeInjectionList() {
		List<CInjection> list = new ArrayList<CInjection>();
		Random rand = new Random();
		for (CConnectedComponent cc : this.leftHandSide) {
			list.add(cc.getInjectionsList().get(
					rand.nextInt(cc.getInjectionsList().size())));
		}
		return list;
	}

	public void calcultateActivity() {
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
		private static final byte ACTION_ADD = 0;
		private static final byte ACTION_BRK = 1;
		private static final byte ACTION_DEL = 2;
		private static final byte ACTION_BND = 3;
		private static final byte ACTION_MOD = 4;

		private byte action;

		private CAgent fromAgent;
		private CAgent toAgent;

		private int idAgentInCC;
		private CSite site;
		private String nameInternalState;

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
						|| (!(checkLinkState(lhsSite, rhsSite)))) {
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
