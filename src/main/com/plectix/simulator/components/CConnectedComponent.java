package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.ISolution;

public class CConnectedComponent implements IConnectedComponent {

	private List<CAgent> agentList;

	private HashMap<Integer, List<CSpanningTree>> spanningTreeMap;

	private List<CState> injectedStates;

	private List<CInjection> injectionsList;

	private CRule rule;

	// private ArrayList<CAgentRule> agentList=new ArrayList<CAgentRule>();
	
	public CConnectedComponent(List<CAgent> connectedAgents) {
		agentList = connectedAgents;
		injectionsList = new ArrayList<CInjection>();
		initSpanningTreeMap();
	}

	private final void initSpanningTreeMap() {
		CSpanningTree spTree;
		spanningTreeMap = new HashMap<Integer, List<CSpanningTree>>();
		if (agentList.size() == 0)
			return;

		for (CAgent agentAdd : agentList) {
			spTree = new CSpanningTree(agentList.size(), agentAdd);
			List<CSpanningTree> list = spanningTreeMap
					.get(agentAdd.getNameId());
			if (list == null) {
				list = new ArrayList<CSpanningTree>();
				spanningTreeMap.put(agentAdd.getNameId(), list);
			}
			list.add(spTree);
		}

	}

	private final void addLiftsToCurrentChangedStates() {
		for (CState changedState : injectedStates) {
			// according to Jean's letter we need to add this if operator,
			// but then we need to define codomain of injection (maybe the list)
			// if (changedState.isRankRoot())
			changedState.addToLift(this);
		}
	}

	public final void setInjections(ISolution solution, CAgent agent) {
		injectedStates = new ArrayList<CState>();
		if (unify(solution, agent)) {
			addLiftsToCurrentChangedStates();
			injectionsList.add(new CInjection(this, injectedStates));
		}
	}

	@Override
	public final IInjection checkAndBuildInjection(ISolution solution,
			IAgent agent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final List<CAgent> getAgents() {
		return agentList;
	}

	@Override
	public final String getPrecompilationAsString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final void precompilationToString() {
		// TODO Auto-generated method stub

	}

	@Override
	public final void precompile() {
		// TODO Auto-generated method stub

	}

	@Override
	public final List<IInjection> pushout() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final boolean unify(ISolution solution, CAgent agent) {

		if (spanningTreeMap == null)
			return false;

		List<CSpanningTree> spList = spanningTreeMap.get(agent.getNameId());

		if (spList == null){
			return false;
		}
		
		for (CSpanningTree tree : spList) {
			if (tree != null)
				if (spanningTreeViewer(agent, tree, tree.getRootIndex()))
					return true;
		}
		return false;
	}

	private boolean compareSites(CSite currentSite, CSite solutionSite) {
		CLinkState currentLinkState = currentSite.getLinkState();
		CLinkState solutionLinkState = solutionSite.getLinkState();

		CInternalState currentInternalState = currentSite.getInternalState();
		CInternalState solutionInternalState = solutionSite.getInternalState();

		return (compareLinkStates(currentLinkState, solutionLinkState) && compareInternalStates(
				currentInternalState, solutionInternalState));
	}

	private final boolean compareInternalStates(CInternalState currentState,
			CInternalState solutionState) {
		if (currentState != null && solutionState == null)
			return false;
		if (!(currentState.getStateNameId() == solutionState.getStateNameId()))
			return false;

		return true;
	}

	private final boolean compareLinkStates(CLinkState currentState,
			CLinkState solutionState) {
		if (currentState.isLeftBranchStatus()
				&& solutionState.isRightBranchStatus())
			return false;
		if (currentState.isRightBranchStatus()
				&& solutionState.isLeftBranchStatus())
			return false;

		if (currentState.getStatusLinkRank() < solutionState
				.getStatusLinkRank())
			return true;

		if (currentState.getStatusLinkRank() == solutionState
				.getStatusLinkRank()
				&& currentState.getStatusLinkRank() == CLinkState.RANK_BOUND)
			if (currentState.getSite().equals(solutionState.getSite()))
				if (currentState.getSite().getAgentLink().equals(
						solutionState.getSite().getAgentLink()))
					return true;

		if (currentState.getStatusLinkRank() == solutionState
				.getStatusLinkRank())
			return true;

		return false;
	}

	// is there injection or not and create lifts
	private final boolean spanningTreeViewer(CAgent agent,
			CSpanningTree spTree, int rootVertex) {
		spTree.setFalse(rootVertex);
		for (Integer v : spTree.getVertexes()[rootVertex]) {
			int count = 0;
			CAgent cAgent = agentList.get(v);// get next agent from spanning
												// tree
			if (count == 0
					|| spTree.getNewVertexElement(cAgent
							.getIdInConnectedComponent())) {
				for (CSite site : cAgent.getSites()) {

					CSite solutionSite = agent.getSite(site.getNameId());
					if (solutionSite == null)
						return false;
					if (!compareSites(site, solutionSite))
						return false;
					injectedStates.add(solutionSite.getInternalState());
					injectedStates.add(solutionSite.getLinkState());
				}
				if (count != 0) {
					// findLinkAgent(cAgent) - returns agent from solution,
					// which is equal to cAgent
					spanningTreeViewer(agent.findLinkAgent(cAgent), spTree, v);
				}
				count = 1;
			}

		}
		return true;
	}

	public final CRule getRule() {
		return rule;
	}

	public final void setRule(CRule rule) {
		this.rule = rule;
	}
}
