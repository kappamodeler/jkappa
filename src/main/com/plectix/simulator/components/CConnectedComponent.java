package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.plectix.simulator.components.CObservables.ObservablesConnectedComponent;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.ISolution;

public class CConnectedComponent implements IConnectedComponent {

	private List<CAgent> agentList;

	private HashMap<Integer, List<CSpanningTree>> spanningTreeMap;

	private List<CSite> injectedSites;

	private List<CAgentLink> agentLinkList;

	private List<CInjection> injectionsList;

	private CRule rule;

	private List<CAgent> agentFromSolutionForRHS;

	public final CAgent getAgentByIdFromSolution(int id, CInjection injection) {
		for (CAgentLink agentL : injection.getAgentLinkList())
			if (agentL.getIdAgentFrom() == id)
				return agentL.getAgentTo();
		return null;
	}

	public void addAgentFromSolutionForRHS(CAgent agentFromSolutionForRHS) {
		this.agentFromSolutionForRHS.add(agentFromSolutionForRHS);
	}

	public List<CAgent> getAgentFromSolutionForRHS() {
		return agentFromSolutionForRHS;
	}

	public CConnectedComponent(List<CAgent> connectedAgents) {
		agentList = connectedAgents;
		injectionsList = new ArrayList<CInjection>();
		agentFromSolutionForRHS = new ArrayList<CAgent>();
	}

	public void removeInjection(CInjection injection) {
		injectionsList.remove(injection);
	}

	public final void initSpanningTreeMap() {
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

	private final void addLiftsToCurrentChangedStates(CInjection injection) {
		for (CSite changedSite : injectedSites) {
			changedSite.addToLift(new CLiftElement(this, injection));
		}
	}

	public final void setInjections(CAgent agent) {
		if (unify(agent)) {
			CInjection injection = new CInjection(this, injectedSites,
					agentLinkList);
			injectionsList.add(injection);
			addLiftsToCurrentChangedStates(injection);
		}
	}

	public final void doPositiveUpdate(
			List<CConnectedComponent> connectedComponentList) {
		if (connectedComponentList == null)
			return;
		for (CConnectedComponent cc : connectedComponentList) {
			for (CAgent agent : cc.agentFromSolutionForRHS)
				if (!agent.isAgentHaveLinkToConnectedComponent(this)) {
					setInjections(agent);
				}
			// setInjections(cc.getAgentFromSolutionForRHS());
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
	public final boolean unify(CAgent agent) {
		injectedSites = new ArrayList<CSite>();
		agentLinkList = new ArrayList<CAgentLink>();

		if (spanningTreeMap == null)
			return false;

		List<CSpanningTree> spList = spanningTreeMap.get(agent.getNameId());

		if (spList == null) {
			return false;
		}

		for (CSpanningTree tree : spList) {
			if (tree != null) {
				tree.resetNewVertex();
				if (agentList.get(tree.getRootIndex()).getSites().isEmpty()) {
					injectedSites.add(agent.EMPTY_SITE);
					agentLinkList.add(new CAgentLink(0, agent));
					return true;
				} else {
					if (compareAgents(agentList.get(tree.getRootIndex()), agent))
						if (spanningTreeViewer(agent, tree,
								tree.getRootIndex(), false))
							return true;
				}

			}
		}
		return false;
	}

	public final boolean isAutomorphism(CAgent agent) {
		if (spanningTreeMap == null)
			return false;

		List<CSpanningTree> spList = spanningTreeMap.get(agent.getNameId());

		if (spList == null)
			return false;

		for (CSpanningTree tree : spList) {
			if (tree != null) {
				tree.resetNewVertex();
				if (agentList.get(tree.getRootIndex()).getSites().isEmpty()
						&& agent.getSites().isEmpty()) {
					return true;
				} else {
					if (fullEqualityOfAgents(
							agentList.get(tree.getRootIndex()), agent))
						if (spanningTreeViewer(agent, tree,
								tree.getRootIndex(), true))
							return true;
				}

			}
		}
		return false;
	}

	private final boolean fullEqualityOfAgents(CAgent cc1Agent, CAgent cc2Agent) {
		if (cc1Agent == null || cc2Agent == null)
			return false;
		if (cc1Agent.getSites().size() != cc2Agent.getSites().size())
			return false;

		for (CSite cc1Site : cc1Agent.getSites()) {
			CSite cc2Site = cc2Agent.getSite(cc1Site.getNameId());
			if (cc2Site == null)
				return false;
			if (!compareSites(cc1Site, cc2Site, true))
				return false;
		}
		return true;
	}

	private final boolean compareAgents(CAgent currentAgent,
			CAgent solutionAgent) {
		if (currentAgent == null || solutionAgent == null)
			return false;
		for (CSite site : currentAgent.getSites()) {
			CSite solutionSite = solutionAgent.getSite(site.getNameId());
			if (solutionSite == null)
				return false;
			if (!compareSites(site, solutionSite, false))
				return false;
			injectedSites.add(solutionSite);
		}
		agentLinkList.add(new CAgentLink(currentAgent
				.getIdInConnectedComponent(), solutionAgent));
		return true;
	}

	private boolean compareSites(CSite currentSite, CSite solutionSite,
			boolean fullEquality) {
		CLinkState currentLinkState = currentSite.getLinkState();
		CLinkState solutionLinkState = solutionSite.getLinkState();

		CInternalState currentInternalState = currentSite.getInternalState();
		CInternalState solutionInternalState = solutionSite.getInternalState();

		if (!fullEquality)
			return (compareLinkStates(currentLinkState, solutionLinkState) && compareInternalStates(
					currentInternalState, solutionInternalState));
		else
			return (fullEqualityLinkStates(currentLinkState, solutionLinkState) && fullEqualityInternalStates(
					currentInternalState, solutionInternalState));

	}

	private final boolean compareInternalStates(CInternalState currentState,
			CInternalState solutionState) {
		if (currentState.getNameId() != CSite.NO_INDEX
				&& solutionState.getNameId() == CSite.NO_INDEX)
			return false;
		if (currentState.getNameId() == CSite.NO_INDEX
				&& solutionState.getNameId() != CSite.NO_INDEX)
			return true;
		if (!(currentState.getNameId() == solutionState.getNameId()))
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
				/*
				 * if (currentState.getStatusLinkRank() == CLinkState.RANK_BOUND
				 * && currentState.getSite().getAgentLink().equals(
				 * solutionState.getSite().getAgentLink()))
				 */return true;

		if (currentState.getStatusLinkRank() == solutionState
				.getStatusLinkRank()
				&& currentState.getStatusLinkRank() != CLinkState.RANK_BOUND)
			return true;

		return false;
	}

	private final boolean fullEqualityInternalStates(
			CInternalState currentState, CInternalState solutionState) {
		if (currentState.getNameId() == CSite.NO_INDEX
				&& solutionState.getNameId() == CSite.NO_INDEX)
			return true;
		if (currentState.getNameId() == solutionState.getNameId())
			return true;

		return false;
	}

	private final boolean fullEqualityLinkStates(CLinkState currentState,
			CLinkState solutionState) {

		if (currentState.getStatusLinkRank() == solutionState
				.getStatusLinkRank()
				&& currentState.getStatusLinkRank() == CLinkState.RANK_BOUND)
			if (currentState.getSite().equals(solutionState.getSite()))
				return true;

		if (currentState.getStatusLinkRank() == solutionState
				.getStatusLinkRank()
				&& currentState.getStatusLinkRank() != CLinkState.RANK_BOUND)
			return true;

		return false;
	}

	// is there injection or not and create lifts
	private final boolean spanningTreeViewer(CAgent agent,
			CSpanningTree spTree, int rootVertex, boolean fullEquality) {
		spTree.setTrue(rootVertex);
		for (Integer v : spTree.getVertexes()[rootVertex]) {
			CAgent cAgent = agentList.get(v);// get next agent from spanning
			if (!(spTree
					.getNewVertexElement(cAgent.getIdInConnectedComponent()))) {
				CAgent sAgent = agent.findLinkAgent(cAgent);
				if (fullEquality && !(fullEqualityOfAgents(cAgent, sAgent)))
					return false;
				if (!fullEquality && !compareAgents(cAgent, sAgent))
					return false;
				spanningTreeViewer(sAgent, spTree, v, fullEquality);
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

	public List<CInjection> getInjectionsList() {
		return injectionsList;
	}
}
