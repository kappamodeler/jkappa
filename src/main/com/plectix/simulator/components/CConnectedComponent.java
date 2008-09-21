package com.plectix.simulator.components;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.ISolution;

public class CConnectedComponent implements IConnectedComponent {

	private List<CAgent> agentList;

	private List<CAgent> injectionList;

	private HashMap<CAgent, CSpanningTree> spanningTreeMap;

	// private ArrayList<CAgentRule> agentList=new ArrayList<CAgentRule>();
	public CConnectedComponent(List<CAgent> connectedAgents) {
		agentList = connectedAgents;
		injectionList = new ArrayList<CAgent>();
		initSpanningTreeMap();
	}
	
	private final void initSpanningTreeMap() {
		CSpanningTree spTree;
		spanningTreeMap = new HashMap<CAgent, CSpanningTree>();
		if (agentList.size() == 0)
			return;
		for (CAgent agent : agentList) {
			spTree = new CSpanningTree(agentList.size(), agent);
			spanningTreeMap.put(agent, spTree);
		}
	}

	public final void setInjections(ISolution solution, CAgent agent) {
		if (unify(solution, agent))
			injectionList.add(agent);
	}

	@Override
	public final IInjection checkAndBuildInjection(ISolution solution, IAgent agent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final List<CAgent> getAgents() {
		// TODO Auto-generated method stub
		return null;
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

		CSpanningTree spTree = spanningTreeMap.get(agent);

		if (spTree == null)
			return false;

		return spanningTreeViewer(agent, spTree, spTree.getRootIndex());
	}


	private boolean compareSites(CSite currentSite, CSite solutionSite) {
		CLinkState currentLinkState = currentSite.getLinkState();
		CLinkState solutionLinkState = solutionSite.getLinkState();

		CInternalState currentInternalState = currentSite.getInternalState();
		CInternalState solutionInternalState = solutionSite.getInternalState();

		return (compareLinkStates(currentLinkState, solutionLinkState) && compareInternalStates(
				currentInternalState, solutionInternalState));
	}

	private boolean compareInternalStates(CInternalState currentState,
			CInternalState solutionState) {
		if (currentState != null && solutionState == null)
			return false;
		if (!(currentState.getState().equals(solutionState.getState())))
			return false;

		return true;
	}

	private boolean compareLinkStates(CLinkState currentState,
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
				&& currentState.getStatusLinkRank() == CLinkState.STATUS_LINK_BOUND)
			if (currentState.getSite().equals(solutionState.getSite()))
				if (currentState.getSite().getAgentLink().equals(
						solutionState.getSite().getAgentLink()))
					return true;

		if (currentState.getStatusLinkRank() == solutionState
				.getStatusLinkRank())
			return true;

		return false;
	}

	// is there injection or not
	private boolean spanningTreeViewer(CAgent agent, CSpanningTree spTree,
			int rootVertex) {
		spTree.setFalse(rootVertex);
		for (Integer v : spTree.getVertexes()[rootVertex]) {
			int count = 0;
			CAgent cAgent = agentList.get(v);
			if (count == 0 || spTree.getNewVertexElement(cAgent.getIdInConnectedComponent())) {
				for (CSite site : cAgent.getSites()) {
//					CSite sSite = agent.findSite(site);
					CSite sSite = agent.getSite(site.getName());
					if (sSite == null)
						return false;
					if (!compareSites(site, sSite))
						return false;
				}
				if (count != 0) {
					spanningTreeViewer(agent.findLinkAgent(cAgent), spTree, v);
				}
				count = 1;
			}

		}
		return true;
	}


}
