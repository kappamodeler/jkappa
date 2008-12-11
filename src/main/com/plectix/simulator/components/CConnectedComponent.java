package com.plectix.simulator.components;

import java.io.Serializable;
import java.util.*;

import com.plectix.simulator.interfaces.*;

public class CConnectedComponent implements IConnectedComponent, Serializable {

	public static final byte EMPTY = 0;

	private List<IAgent> agentList;
	private Map<Integer, List<CSpanningTree>> spanningTreeMap;
	private List<IAgent> agentFromSolutionForRHS;
	private List<IAgentLink> agentLinkList;
	private Map<Integer, IInjection> injectionsList;
	private List<ISite> injectedSites;
	private int maxId = -1;
	private IRule rule;

	public CConnectedComponent(byte empty) {
		switch (empty) {
		case EMPTY: {
			agentList = new ArrayList<IAgent>();
			agentList.add(new CAgent(CAgent.EMPTY, CAgent.EMPTY));
			injectionsList = new TreeMap<Integer, IInjection>();
			addInjection(CInjection.EMPTY_INJECTION, 0);
			agentFromSolutionForRHS = new ArrayList<IAgent>();
			break;
		}
		}
	}

	public CConnectedComponent(List<IAgent> connectedAgents) {
		agentList = connectedAgents;
		injectionsList = new TreeMap<Integer, IInjection>();
		agentFromSolutionForRHS = new ArrayList<IAgent>();
	}
	
	private final void addInjection(IInjection inj, int id) {
		if (inj != null) {
			maxId = Math.max(maxId, id);
			inj.setId(id);
			injectionsList.put(id, inj);
		}
	}

	public final IAgent getAgentByIdFromSolution(int id, IInjection injection) {
		for (IAgentLink agentL : injection.getAgentLinkList())
			if (agentL.getIdAgentFrom() == id)
				return agentL.getAgentTo();
		return null;
	}

	public final void addAgentFromSolutionForRHS(IAgent agentFromSolutionForRHS) {
		this.agentFromSolutionForRHS.add(agentFromSolutionForRHS);
	}

	public final void clearAgentsFromSolutionForRHS() { 
		agentFromSolutionForRHS.clear();
	}
	
	public final List<IAgent> getAgentFromSolutionForRHS() {
		return Collections.unmodifiableList(agentFromSolutionForRHS);
	}

	public final void removeInjection(IInjection injection) {
		if (injection == null) {
			return;
		}

		int id = injection.getId();

		if (injectionsList.get(id) != null) {
			if (injection != injectionsList.get(id)) {
				return;
			}
			IInjection inj = injectionsList.remove(maxId);
			if (id != maxId) {
				addInjection(inj, id);
			}
			maxId--;
		}
	}

	public final void initSpanningTreeMap() {
		CSpanningTree spTree;
		spanningTreeMap = new HashMap<Integer, List<CSpanningTree>>();
		if (agentList.isEmpty())
			return;

		for (IAgent agentAdd : agentList) {
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

	private final void addLiftsToCurrentChangedStates(IInjection injection) {
		for (ISite changedSite : injectedSites) {
			changedSite.addToLift(new CLiftElement(this, injection));
		}
	}

	public final void setInjections(IAgent agent) {
		if (unify(agent)) {
			IInjection injection = new CInjection(this, injectedSites,
					agentLinkList);
			addInjection(injection, maxId + 1);
			addLiftsToCurrentChangedStates(injection);
		}
	}

	public final void setInjection(IInjection inj) {
		addInjection(inj, maxId + 1);
		addLiftsToCurrentChangedStates(inj);
	}

	public final IInjection getInjection(IAgent agent) {
		if (unify(agent)) {
			CInjection injection = new CInjection(this, injectedSites,
					agentLinkList);
			return injection;
		}
		return null;
	}

	public final void doPositiveUpdate(
			List<IConnectedComponent> connectedComponentList) {
		if (connectedComponentList == null)
			return;
		for (IConnectedComponent cc : connectedComponentList) {
			for (IAgent agent : cc.getAgentFromSolutionForRHS()) {
				IInjection inj = getInjection(agent);
				if (inj != null) {
					if (!agent.isAgentHaveLinkToConnectedComponent(this, inj))
						setInjection(inj);
				}
			}

		}
	}

	public final List<IAgent> getAgents() {
		return Collections.unmodifiableList(agentList);
	}

	public final boolean unify(IAgent agent) {
		injectedSites = new ArrayList<ISite>();
		agentLinkList = new ArrayList<IAgentLink>();

		if (spanningTreeMap == null)
			return false;

		List<CSpanningTree> spList = spanningTreeMap.get(agent.getNameId());

		if (spList == null) {
			return false;
		}

		for (CSpanningTree tree : spList) {
			injectedSites.clear();
			agentLinkList.clear();
			if (tree != null) {
				tree.resetNewVertex();
				if (agentList.get(tree.getRootIndex()).getSites().isEmpty()) {
					injectedSites.add(agent.getEmptySite());
					agentLinkList.add(new CAgentLink(0, agent));
					return true;
				} else {
					if (compareAgents(agentList.get(tree.getRootIndex()), agent))
						if (spanningTreeViewer(agent, tree,
								tree.getRootIndex(), false))
							if (this.getAgents().size() == agentLinkList.size())
								return true;
							else {
								injectedSites.clear();
								agentLinkList.clear();
							}
				}

			}
		}
		return false;
	}

	public final boolean isAutomorphism(IAgent agent) {
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

	private final boolean fullEqualityOfAgents(IAgent cc1Agent, IAgent cc2Agent) {
		if (cc1Agent == null || cc2Agent == null)
			return false;
		if (cc1Agent.getSites().size() != cc2Agent.getSites().size())
			return false;

		for (ISite cc1Site : cc1Agent.getSites()) {
			ISite cc2Site = cc2Agent.getSite(cc1Site.getNameId());
			if (cc2Site == null)
				return false;
			if (!compareSites(cc1Site, cc2Site, true))
				return false;
		}
		return true;
	}

	private final boolean compareAgents(IAgent currentAgent,
			IAgent solutionAgent) {
		if (currentAgent == null || solutionAgent == null)
			return false;
		for (ISite site : currentAgent.getSites()) {
			ISite solutionSite = solutionAgent.getSite(site.getNameId());
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

	private final boolean compareSites(ISite currentSite, ISite solutionSite,
			boolean fullEquality) {
		ILinkState currentLinkState = currentSite.getLinkState();
		ILinkState solutionLinkState = solutionSite.getLinkState();

		IInternalState currentInternalState = currentSite.getInternalState();
		IInternalState solutionInternalState = solutionSite.getInternalState();

		if (!fullEquality)
			return (compareLinkStates(currentLinkState, solutionLinkState) && compareInternalStates(
					currentInternalState, solutionInternalState));
		else
			return (fullEqualityLinkStates(currentLinkState, solutionLinkState) && fullEqualityInternalStates(
					currentInternalState, solutionInternalState));

	}

	private final boolean compareInternalStates(IInternalState currentInternalState,
			IInternalState solutionInternalState) {
		if (currentInternalState.getNameId() != CSite.NO_INDEX
				&& solutionInternalState.getNameId() == CSite.NO_INDEX)
			return false;
		if (currentInternalState.getNameId() == CSite.NO_INDEX
				&& solutionInternalState.getNameId() != CSite.NO_INDEX)
			return true;
		if (!(currentInternalState.getNameId() == solutionInternalState
				.getNameId()))
			return false;

		return true;
	}

	private final boolean compareLinkStates(ILinkState currentState,
			ILinkState solutionLinkState) {
		if (currentState.isLeftBranchStatus()
				&& solutionLinkState.isRightBranchStatus())
			return false;
		if (currentState.isRightBranchStatus()
				&& solutionLinkState.isLeftBranchStatus())
			return false;

		if (currentState.getStatusLinkRank() < solutionLinkState
				.getStatusLinkRank())
			return true;

		if (currentState.getStatusLinkRank() == solutionLinkState
				.getStatusLinkRank()
				&& currentState.getStatusLinkRank() == CLinkState.RANK_BOUND)
			if (currentState.getSite().equals(solutionLinkState.getSite()))
				return true;

		if (currentState.getStatusLinkRank() == solutionLinkState
				.getStatusLinkRank()
				&& currentState.getStatusLinkRank() != CLinkState.RANK_BOUND)
			return true;

		return false;
	}

	private final boolean fullEqualityInternalStates(
			IInternalState currentInternalState,
			IInternalState solutionInternalState) {
		if (currentInternalState.getNameId() == CSite.NO_INDEX
				&& solutionInternalState.getNameId() == CSite.NO_INDEX)
			return true;
		if (currentInternalState.getNameId() == solutionInternalState
				.getNameId())
			return true;

		return false;
	}

	private final boolean fullEqualityLinkStates(ILinkState currentLinkState,
			ILinkState solutionLinkState) {

		if (currentLinkState.getStatusLinkRank() == solutionLinkState
				.getStatusLinkRank()
				&& currentLinkState.getStatusLinkRank() == CLinkState.RANK_BOUND)
			if (currentLinkState.getSite().equals(solutionLinkState.getSite()))
				return true;

		if (currentLinkState.getStatusLinkRank() == solutionLinkState
				.getStatusLinkRank()
				&& currentLinkState.getStatusLinkRank() != CLinkState.RANK_BOUND)
			return true;

		return false;
	}

	private final List<ISite> getConnectedSite(IAgent agentFrom, IAgent agentTo) {
		List<ISite> siteList = new ArrayList<ISite>();

		for (ISite sF : agentFrom.getSites()) {
			for (ISite sT : agentTo.getSites()) {
				if (sF == sT.getLinkState().getSite()) {
					siteList.add(sF);
				}
			}
		}

		return siteList;
	}

	// is there injection or not and create lifts
	private final boolean spanningTreeViewer(IAgent agent,
			CSpanningTree spTree, int rootVertex, boolean fullEquality) {
		spTree.setTrue(rootVertex);
		for (Integer v : spTree.getVertexes()[rootVertex]) {
			IAgent cAgent = agentList.get(v);// get next agent from spanning
			if (!(spTree
					.getNewVertexElement(cAgent.getIdInConnectedComponent()))) {
				List<ISite> sitesFrom = getConnectedSite(agentList
						.get(rootVertex), agentList.get(v));
				IAgent sAgent = agent.findLinkAgent(cAgent, sitesFrom);
				if (fullEquality && !(fullEqualityOfAgents(cAgent, sAgent)))
					return false;
				if (!fullEquality && !compareAgents(cAgent, sAgent))
					return false;
				spanningTreeViewer(sAgent, spTree, v, fullEquality);
			}
		}
		return true;
	}

	public final IRule getRule() {
		return rule;
	}

	public final void setRule(IRule rule) {
		this.rule = rule;
	}

	public final Collection<IInjection> getInjectionsList() {
		return Collections.unmodifiableCollection(injectionsList.values());
	}

	public final IInjection getRandomInjection(IRandom random) {
		int index;
		IInjection inj = null;
		index = random.getInteger(maxId + 1);
		inj = injectionsList.get(index);
		return inj;
	}

	public final IInjection getFirstInjection() {
		return injectionsList.get(0);
	}

	public final List<IAgent> getAgentsSortedByIdInRule() {
		List<IAgent> temp = new ArrayList<IAgent>();
		temp.addAll(agentList);
		Collections.sort(temp);
		return Collections.unmodifiableList(temp);
	}
}