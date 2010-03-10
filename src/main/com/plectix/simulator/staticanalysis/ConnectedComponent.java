package com.plectix.simulator.staticanalysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulationclasses.injections.LiftElement;
import com.plectix.simulator.simulationclasses.probability.SkipListSelector;
import com.plectix.simulator.simulationclasses.probability.WeightedItemSelector;
import com.plectix.simulator.simulationclasses.solution.SuperSubstance;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.util.string.ConnectedComponentToSmilesString;

/**
 * This class implements "connected component" entity.<br><br>
 * It unites a pack of connected agents. We state that all the agents included in 
 * connected component somehow connected with each other (there is a path consisted by
 * several connections, connecting each pair of agents in fixed connected component).<br><br>
 * 
 * @author avokhmin
 *
 */
public class ConnectedComponent implements ConnectedComponentInterface {
	
	private final List<Agent> agents = new ArrayList<Agent>();
	private Map<String, List<SpanningTree>> spanningTreeMap;
	
	// TODO please rename it, or even rename it
	private List<Agent> agentFromSolutionForRightHandSide;
	private Map<Integer, Agent> agentsLinks;
	private List<Site> injectedSites;
	private Rule rule;
	private SuperSubstance superSubstance = null;
	// for the better searching
	private WeightedItemSelector<Injection> injections 
				= new SkipListSelector<Injection>();
	private final boolean isEmpty;

	/**
	 * private empty connected component constructor
	 */
	public ConnectedComponent() {
		isEmpty = true;
		agents.add(new Agent());
		injections = new WeightedItemSelector<Injection>() {
			private final Injection injection = ThreadLocalData.getEmptyInjection();
			
			@Override
			public Set<Injection> asSet() {
				Set<Injection> oneItemSet = new LinkedHashSet<Injection>();
				oneItemSet.add(injection);
				return oneItemSet;
			}

			@Override
			public double getTotalWeight() {
				return injection.getWeight();
			}

			@Override
			public Injection select() {
				return injection;
			}

			@Override
			public void updatedItem(Injection item) {
				
			}

			@Override
			public void updatedItems(
					Collection<Injection> changedWeightedItemList) {
				
			}
		};
		agentFromSolutionForRightHandSide = new ArrayList<Agent>();
	}

	/**
	 * Main constructor. Creates connected component with given list of connected agents.
	 * @param connectedAgents list of agents, which can be source for the new connected component 
	 */
	public ConnectedComponent(Collection<Agent> connectedAgents) {
		agents.addAll(connectedAgents);
		agentFromSolutionForRightHandSide = new ArrayList<Agent>();
		isEmpty = false;
	}

	public final boolean isEmpty() {
		return isEmpty;
	}
	
	public final void setSuperSubstance(SuperSubstance substance) {
		superSubstance = substance;
	}

	public final SuperSubstance getSubstance() {
		return superSubstance;
	}

	/**
	 * This method registers some agents from solution when apply rule to the util collection needed
	 * on positive update.
	 * @param agentFromSolutionForRHS agent to be registered
	 */
	//TODO move?
	public final void addAgentFromSolutionForRHS(Agent agent) {
		this.agentFromSolutionForRightHandSide.add(agent);
	}

	/**
	 * This method clears util collection of solution agents needed
	 * on positive update.
	 */
	//TODO move?
	public final void clearAgentsFromSolutionForRHS() {
		agentFromSolutionForRightHandSide.clear();
	}

	/**
	 * This method returns util collection of agents needed on positive update.
	 * @return util list of agents, needed on positive update
	 */
	//TODO move?
	public final List<Agent> getAgentFromSolutionForRHS() {
		return agentFromSolutionForRightHandSide;
	}

	/**
	 * This method registers injection to current connected component with given id.
	 * @param injection injection to register
	 * @param id id of injection
	 */
	private final void addInjection(Injection injection) {
		injections.updatedItem(injection);
	}

	/**
	 * This method unregisters given injection from current connected component
	 * @param injection injection to remove
	 */
	public final void removeInjection(Injection injection) {
		injection.eliminate();
	}

	public long getInjectionsWeight() {
		return (long)(injections.getTotalWeight());
	}
	
	/**
	 * This method initializes "spanning tree map" structure, which is used for comparing two components
	 * and so on
	 */
	public final void initSpanningTreeMap() {
		SpanningTree spTree;
		spanningTreeMap = new LinkedHashMap<String, List<SpanningTree>>();
		if (agents.isEmpty())
			return;

		for (Agent agentAdd : agents) {
			spTree = new SpanningTree(agents.size(), agentAdd);
			List<SpanningTree> list = spanningTreeMap
					.get(agentAdd.getName());
			if (list == null) {
				list = new ArrayList<SpanningTree>();
				spanningTreeMap.put(agentAdd.getName(), list);
			}
			list.add(spTree);
		}
	}

	/**
	 * Sets new injection for this connected component.
	 * @param injection injection to be set
	 */
	public final void setInjection(Injection injection) {
		this.addInjection(injection);
		for (Site changedSite : injectedSites) {
			changedSite.addToLift(new LiftElement(this, injection));
		}
	}

	/**
	 * Returns new injection for current ConnectedComponent by input agent.<br>
	 * If injection can't be create, returns "null".
	 * @param agent given agent
	 */
	public final Injection createInjection(Agent agent) {
		if (unify(agent)) {
			Injection injection = new Injection(this, injectedSites,
					agentsLinks);
			return injection;
		}
		return null;
	}

	/**
	 * This method does positive update for current ConnectedComponent using given list of 
	 * connected components.
	 * @param connectedComponentList list of ConnectedComponents to be used
	 */
	public final void doPositiveUpdate(List<ConnectedComponentInterface> connectedComponentList) {
		if (connectedComponentList == null)
			return;
		for (ConnectedComponentInterface cc : connectedComponentList) {
			for (Agent agent : cc.getAgentFromSolutionForRHS()) {
				Injection inj = this.createInjection(agent);
				if (inj != null) {
					if (!agent.hasSimilarInjection(inj)) {
						setInjection(inj);
					}
				}
			}
		}
	}

	/**
	 * This method returns all agents from current ConnectedComponent
	 * @return list of this component's agents
	 */
	public final List<Agent> getAgents() {
		return agents;
	}

	public final boolean unify(Agent agent) {
		injectedSites = new ArrayList<Site>();
		agentsLinks = new LinkedHashMap<Integer, Agent>();

		if (spanningTreeMap == null)
			return false;

		List<SpanningTree> spList = spanningTreeMap.get(agent.getName());

		if (spList == null) {
			return false;
		}

		for (SpanningTree tree : spList) {
			injectedSites.clear();
			agentsLinks.clear();
			if (tree != null) {
				tree.resetNewVertex();
				if (agents.get(tree.getRootIndex()).getSites().isEmpty()) {
					injectedSites.add(agent.getDefaultSite());
					agentsLinks.put(0, agent);
					return true;
				} else {
					if (compareAgents(agents.get(tree.getRootIndex()), agent))
						if (viewSpanningTree(agent, tree,
								tree.getRootIndex(), false))
							if (this.getAgents().size() == agentsLinks.size() && isInjectionCorrect())
								return true;
							else {
								injectedSites.clear();
								agentsLinks.clear();
							}
				}

			}
		}
		return false;
	}

	private boolean isInjectionCorrect(){
		for(int i = 0; i < agents.size(); i++){
			Agent agentThis = agents.get(i);
			Agent agentSolution = agentsLinks.get(agentThis.getIdInConnectedComponent());
			for(Site siteThis : agentThis.getSites()){
				Site connectedSiteThis = siteThis.getLinkState().getConnectedSite();
				if(connectedSiteThis == null)
					continue;
				int linkAgentThisId = connectedSiteThis.getParentAgent().getIdInConnectedComponent();
				Site connectedSiteSolution = agentSolution.getSiteByName(siteThis.getName()).getLinkState().getConnectedSite();
				if(connectedSiteSolution.getParentAgent().getId() != agentsLinks.get(linkAgentThisId).getId())
					return false;
			}
		}
		return true;
	}
	
	public final boolean isAutomorphicTo(Agent agent) {
		if (spanningTreeMap == null)
			return false;

		List<SpanningTree> spList = spanningTreeMap.get(agent.getName());

		if (spList == null)
			return false;

		for (SpanningTree tree : spList) {
			if (tree != null) {
				tree.resetNewVertex();
				if (agents.get(tree.getRootIndex()).getSites().isEmpty()
						&& agent.getSites().isEmpty()) {
					return true;
				} else {
					if (agentsAreCompletelyEqual(
							agents.get(tree.getRootIndex()), agent))
						if (viewSpanningTree(agent, tree,
								tree.getRootIndex(), true))
							return true;
				}

			}
		}
		return false;
	}

	/**
	 * This method compares two agents for being completely equal.
	 * @param sourceAgent first agent
	 * @param targetAgent second agent
	 * @return <tt>true</tt> if these agents are completely equal, otherwise <tt>false</tt>
	 */
	//TODO MOVE??
	private final boolean agentsAreCompletelyEqual(Agent sourceAgent, Agent targetAgent) {
		if (sourceAgent == null || targetAgent == null)
			return false;
		if (sourceAgent.getSites().size() != targetAgent.getSites().size())
			return false;

		for (Site cc1Site : sourceAgent.getSites()) {
			Site cc2Site = targetAgent.getSiteByName(cc1Site.getName());
			if (cc2Site == null)
				return false;
			if (!cc1Site.expandedEqualz(cc2Site, true))
				return false;
		}
		return true;
	}

	/**
	 * Util method for partial agents comparison
	 * @param currentAgent first agent
	 * @param solutionAgent second agent
	 * @return <tt>true</tt> if these agents are partially equal, otherwise <tt>false</tt>
	 */
	private final boolean compareAgents(Agent currentAgent, Agent solutionAgent) {
		if (currentAgent == null || solutionAgent == null)
			return false;
		for (Site site : currentAgent.getSites()) {
			Site solutionSite = solutionAgent.getSiteByName(site.getName());
			if (solutionSite == null)
				return false;
			if (!site.expandedEqualz(solutionSite, false))
				return false;
			injectedSites.add(solutionSite);
		}
		agentsLinks.put(currentAgent.getIdInConnectedComponent(), solutionAgent);
		return true;
	}

	/**
	 * Util method, uses in {@link #viewSpanningTree(Agent, SpanningTree, int, boolean)}.
	 * @param sourceAgent given agent
	 * @param targetAgent given agent
	 * @return Returns list of connect sites between given agents
	 */
	private final List<Site> getConnectedSite(Agent sourceAgent, Agent targetAgent) {
		List<Site> siteList = new ArrayList<Site>();

		for (Site sF : sourceAgent.getSites()) {
			for (Site sT : targetAgent.getSites()) {
				if (sF == sT.getLinkState().getConnectedSite()) {
					siteList.add(sF);
				}
			}
		}
		return siteList;
	}

	/**
	 * Util method.
	 */
	private final boolean viewSpanningTree(Agent agent, SpanningTree spanningTree, 
			int rootVertex, boolean doCompleteComparision) {
		spanningTree.setTrue(rootVertex);
		for (Integer v : spanningTree.getVertexes()[rootVertex]) {
			Agent cAgent = agents.get(v);// get next agent from spanning
			if (!(spanningTree
					.getNewVertexElement(cAgent.getIdInConnectedComponent()))) {
				List<Site> sitesFrom = getConnectedSite(agents
						.get(rootVertex), agents.get(v));
				Agent sAgent = agent.findLinkAgent(cAgent, sitesFrom);
				if (doCompleteComparision && !(agentsAreCompletelyEqual(cAgent, sAgent)))
					return false;
				if (!doCompleteComparision && !compareAgents(cAgent, sAgent))
					return false;
				viewSpanningTree(sAgent, spanningTree, v, doCompleteComparision);
			}
		}
		return true;
	}

	/**
	 * This method sets rule containing this connected component.
	 * @param rule new value
	 */
	public final void setRule(Rule rule) {
		this.rule = rule;
	}

	/**
	 * This method returns all injections from current connected components.
	 * @return list of injections from this connected component
	 */
	// TODO get rid of
	public final Collection<Injection> getInjectionsList() {
		return injections.asSet();
	}

	public final void updateInjection(Injection injection, long newPower) {
		injection.setPower(newPower);
		injections.updatedItem(injection);
	}
	
	/**
	 * This method returns random injection from current connected component
	 * @param random random number generator
	 * @return random injection from current connected component
	 */
	public final Injection getRandomInjection() {
		return injections.select();
	}

	/**
	 * This method returns first injection from current ConnectedComponent.
	 * Used only for checking clashes in case of infinite rated rules
	 * @return first injection from list of injections
	 */
	public final Injection getFirstInjection() {
		// TODO check if we really need it
		return injections.select();
	}

	/**
	 * This method sorts agents from current connected component by id in rule handside.
	 * @return sorted collection of component's agents 
	 */
	public final List<Agent> getAgentsSortedByIdInRule() {
		List<Agent> temp = new ArrayList<Agent>();
		temp.addAll(agents);
		Collections.sort(temp);
		return temp;
	}

	private final Set<Injection> getIncomingInjectionsSet() {
		Set<Injection> injList = new LinkedHashSet<Injection>();
		for (Agent agent : agents) {
			for (Site site : agent.getSites()) {
				for (LiftElement lift : site.getLift()) {
					injList.add(lift.getInjection());
				}
			}
			// default-site case
			for (LiftElement lift : agent.getDefaultSite().getLift()) {
				injList.add(lift.getInjection());
			}
		}
		return injList;
	}
	
	public final void burnIncomingInjections() {
		for (Injection inj : getIncomingInjectionsSet()) {
			inj.setSimple();
		}
	}
	
	public final void incrementIncomingInjections() {
		for (Injection inj : getIncomingInjectionsSet()) {
			inj.incPower();
		}
	}
	
	public final void deleteIncomingInjections() {
		for (Injection injection : getIncomingInjectionsSet()) {
			if (injection != ThreadLocalData.getEmptyInjection()) {
				for (Site site : injection.getChangedSites()) {
					site.getParentAgent().getDefaultSite().clearIncomingInjections(injection);
					site.getParentAgent().getDefaultSite().clearLifts();
					site.clearIncomingInjections(injection);
					site.clearLifts();
				}
				for (Site site : injection.getSiteList()) {
					site.removeInjectionFromLift(injection);
				}
				injection.getConnectedComponent().removeInjection(injection);
			}
		}		
	}
	
	// -----------------------hash, toString, equals-----------------------------


	public final String getSmilesString() {
		return ConnectedComponentToSmilesString.getInstance().toUniqueString(this);
	}
	
	@Override
	public final String toString() {
		if (this.isEmpty()) {
			return "EMPTY";
		}
		return getSmilesString(); 
	}
}