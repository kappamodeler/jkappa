package com.plectix.simulator.components;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.ISolution;

public class CConnectedComponent implements IConnectedComponent{
	
	private List<CAgent> agentList;

	// private ArrayList<CAgentRule> agentList=new ArrayList<CAgentRule>();
	public CConnectedComponent(List<CAgent> connectedAgents) {
		agentList = connectedAgents;
		initSpanningTreeMap();
	}

	@Override
	public IInjection checkAndBuildInjection(ISolution solution, IAgent agent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CAgent> getAgents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPrecompilationAsString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void precompilationToString() {
		// TODO Auto-generated method stub

	}

	@Override
	public void precompile() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<IInjection> pushout() {
		// TODO Auto-generated method stub
		return null;
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

		if (currentState.getStatusLinkRank() <= solutionState
				.getStatusLinkRank())
			return true;

		return false;
	}

	@Override
	public Map<String, IConnectedComponent> unify(ISolution solution,
			IAgent agent) {

		if (spanningTreeMap == null)
			return null;

		if (agentList.size() == 1) {
			// checkStates
		}

		CSpanningTree spTree = spanningTreeMap.get(agent);

		return null;
	}

	HashMap<CAgent, CSpanningTree> spanningTreeMap;

	public void initSpanningTreeMap() {
		CSpanningTree spTree;
		spanningTreeMap = new HashMap<CAgent, CSpanningTree>();
		if (agentList.size() == 0)
			return;

		if (agentList.size() > 1)
			for (CAgent agent : agentList) {
				spTree = new CSpanningTree(agentList.size(), agent);
				spanningTreeMap.put(agent, spTree);
			}
		else
			spanningTreeMap.put(agentList.get(0), new CSpanningTree(agentList
					.size(), agentList.get(0)));
	}

}
