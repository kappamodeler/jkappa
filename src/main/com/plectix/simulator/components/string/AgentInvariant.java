/**
 * This class computes some invariants about Agents to compare them.
 */
package com.plectix.simulator.components.string;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.util.PrimeNumbers;

public final class AgentInvariant {

	public static final AgentInvariantComparator AGENT_INVARIANT_COMPARATOR = new AgentInvariantComparator();

	public static final AgentInvariantRANKComparator AGENT_INVARIANT_RANK_COMPARATOR = new AgentInvariantRANKComparator();
	
	private int rankOld = 0; // 0 means not set yet
	private int rankNew = 0; // 0 means not set yet
	
	private long productOfNeighborPrimes = 0;

	private int numberOfConnections = 0;
	private IAgent agent = null;  // this is set in the constructor
	private List<ISite> sortedSites = null;  // we don't initialize this list if we don't have to...
	private List<AgentInvariant> neighborAgentList = null;  // we don't initialize this list if we don't have to...
	
	public AgentInvariant(IAgent agent) {
		if (agent == null) {
			throw new RuntimeException("Agent can not be null!");
		}
		this.agent = agent;
			
		for (ISite site : agent.getSites()) {
			if (site.getLinkState().getStatusLink() == CLinkStatus.BOUND) {
				numberOfConnections++;
			} else if (site.getLinkState().getStatusLink() != CLinkStatus.FREE) {
				// we expect that the site will be either BOUND or FREE. throw exception otherwise:
				throw new RuntimeException("Unexpected State: Link state is neither BOUND not FREE.");
			}
		}
	}
	
	public final List<ISite> getSortedSites() {
		if (sortedSites == null) {
			sortedSites = new ArrayList<ISite>(agent.getSites());
			// here they will be sorted by name since they are on the same Agent:
			Collections.sort(sortedSites, SiteComparator.SITE_COMPARATOR);
		}
		return sortedSites;
	}

	public final void computeNeighbors(Map<IAgent, AgentInvariant> agentToAgentInvariantMap) {
		if (neighborAgentList == null) {
			neighborAgentList = new ArrayList<AgentInvariant>();
		}
		
		for (ISite site : agent.getSites()) {
			if (site.getLinkState().getStatusLink() == CLinkStatus.BOUND) {
				AgentInvariant neighbor = agentToAgentInvariantMap.get(site.getLinkState().getSite().getAgentLink()); 
				if (neighbor == null) {
					throw new RuntimeException("Could not find neighbor Agent in map!");
				}
				neighborAgentList.add(neighbor);
			}
		}
	}
	
	public final void computeProductOfNeighborPrimes() {
		if (neighborAgentList == null) {
			throw new RuntimeException("neighborAgentList is not initialized yet!");
		}
		
		productOfNeighborPrimes = 1;
		for (AgentInvariant agentInvariant : neighborAgentList) {
			productOfNeighborPrimes *= PrimeNumbers.FIRST_8242[agentInvariant.getRankNew() - 1];
		}
	}

	public final void saveRank() {
		rankOld = rankNew;
	}

	public final boolean areRanksEqual() {
		return rankNew == rankOld;
	}

	public final void doubleRankNew() {
		rankNew *= 2;
	}
	
	public final int getNumberOfSites() {
		return agent.getSites().size();
	}

	public final String getName() {
		return agent.getName();
	}

	public final IAgent getAgent() {
		return agent;
	}

	public final long getProductOfNeighborPrimes() {
		return productOfNeighborPrimes;
	}
	
	public final int getRankNew() {
		return rankNew;
	}

	public final void setRankNew(int rank) {
		rankNew = rank;
	}
	
	public final int getNumberOfConnections() {
		return numberOfConnections;
	}
	

	/**
	 * This class compares two AgentInvariants with respect to invariants
	 * 
	 * @author ecemis
	 */
	public static final class AgentInvariantComparator implements Comparator<AgentInvariant> {
		private AgentInvariantComparator() {
			super();
		}

		public final int compare(AgentInvariant o1, AgentInvariant o2) {
			// first compare the number of connections
			int result = Double.compare(o1.getNumberOfConnections(), o2.getNumberOfConnections());
			if (result != 0) {
				return result;
			}
			
			// then compare the number of sites
			result = Double.compare(o1.getNumberOfSites(), o2.getNumberOfSites());
			if (result != 0) {
				return result;
			}
			
			// then compare agent names:
			result = o1.getName().compareTo(o2.getName());
			if (result != 0) {
				return result;
			}
			
			// agent names are the same... and the number of connections are also the same...
			// so let's compare sites:
			List<ISite> sortedSites1 = o1.getSortedSites();
			List<ISite> sortedSites2 = o2.getSortedSites();
			for (int i= 0; i < sortedSites1.size(); i++) {
				result = SiteComparator.SITE_COMPARATOR.compare(sortedSites1.get(i), sortedSites2.get(i));
				if (result != 0) {
					return result;
				}
			}
			
			// all the invariants are the same!
			return 0;
		}
	}
	
	public static final class AgentInvariantRANKComparator implements Comparator<AgentInvariant> {
		private AgentInvariantRANKComparator() {
			super();
		}

		public final int compare(AgentInvariant o1, AgentInvariant o2) {
			int result = Double.compare(o1.getRankNew(), o2.getRankNew());
			if (result != 0) {
				return result;
			}
			
			return Double.compare(o1.getProductOfNeighborPrimes(), o1.getProductOfNeighborPrimes());
		}
	}


}