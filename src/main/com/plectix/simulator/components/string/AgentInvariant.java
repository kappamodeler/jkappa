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
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.util.PrimeNumbers;

public final class AgentInvariant {

	public static final AgentInvariantComparator AGENT_INVARIANT_COMPARATOR = new AgentInvariantComparator();

	public static final AgentInvariantRankComparator AGENT_INVARIANT_RANK_COMPARATOR = new AgentInvariantRankComparator();
	
	private int rankOld = 0; // 0 means not set yet
	private int rankNew = 0; // 0 means not set yet
	
	private long productOfNeighborPrimes = 0;

	private int numberOfConnections = 0;
	private CAgent agent = null;  // this is set in the constructor
	private List<CSite> sortedSites = null;  // we don't initialize this list if we don't have to...
	private List<AgentInvariant> neighborAgentList = null;  // we don't initialize this list if we don't have to...
	
	public AgentInvariant(CAgent agent) {
		if (agent == null) {
			throw new RuntimeException("Agent can not be null!");
		}
		this.agent = agent;
			
		for (CSite site : agent.getSites()) {
			if (site.getLinkState().getStatusLink() == CLinkStatus.BOUND) {
				numberOfConnections++;
			} else if (site.getLinkState().getStatusLink() != CLinkStatus.FREE) {
				// we expect that the site will be either BOUND or FREE. throw exception otherwise:
				throw new RuntimeException("Unexpected State: Link state is neither BOUND not FREE.");
			}
		}
	}
	
	/**
	 * Returns the sites of this Agent sorted using {@link SiteComparator}
	 * 
	 * @return
	 */
	public final List<CSite> getSortedSites() {
		if (sortedSites == null) {
			sortedSites = new ArrayList<CSite>(agent.getSites());
			// here they will be sorted by name since they are on the same Agent:
			Collections.sort(sortedSites, SiteComparator.SITE_COMPARATOR);
		}
		return sortedSites;
	}

	/**
	 * Compile a list of AgentInvariants which are bound to this AgentInvariant 
	 * 
	 * @param agentToAgentInvariantMap
	 */
	public final void computeNeighbors(Map<CAgent, AgentInvariant> agentToAgentInvariantMap) {
		if (neighborAgentList == null) {
			neighborAgentList = new ArrayList<AgentInvariant>();
		}
		
		for (CSite site : agent.getSites()) {
			if (site.getLinkState().getStatusLink() == CLinkStatus.BOUND) {
				AgentInvariant neighbor = agentToAgentInvariantMap.get(site.getLinkState().getSite().getAgentLink()); 
				if (neighbor == null) {
					throw new RuntimeException("Could not find neighbor Agent in map!");
				}
				neighborAgentList.add(neighbor);
			}
		}
	}
	
	/**
	 * Computes the product of prime numbers corresponding to neighbor Agents' ranks.
	 */
	public final void computeProductOfNeighborPrimes() {
		if (neighborAgentList == null) {
			throw new RuntimeException("neighborAgentList is not initialized yet!");
		}
		
		productOfNeighborPrimes = 1;
		for (AgentInvariant agentInvariant : neighborAgentList) {
			productOfNeighborPrimes *= PrimeNumbers.FIRST_8242[agentInvariant.getRankNew() - 1];
		}
	}

	/**
	 * Copies the new Rank to the old Rank.
	 */
	public final void saveRank() {
		rankOld = rankNew;
	}

	/**
	 * Checks whether the new Rank is equal to the old Rank or not
	 * 
	 * @return
	 */
	public final boolean areRanksEqual() {
		return rankNew == rankOld;
	}

	/**
	 * Multiplies the new Rank by 2
	 */
	public final void doubleRankNew() {
		rankNew *= 2;
	}
	
	/**
	 * Returns the number of sites of this Agent.
	 * 
	 * @return
	 */
	public final int getNumberOfSites() {
		return agent.getSites().size();
	}

	/**
	 * Returns the name of this Agent.
	 * 
	 * @return
	 */
	public final String getName() {
		return agent.getName();
	}

	/**
	 * Returns the Agent in this AgentInvariant.
	 * 
	 * @return
	 */
	public final CAgent getAgent() {
		return agent;
	}

	/**
	 * Returns the product of neighbor primes computed with {@link #computeProductOfNeighborPrimes()}
	 * 
	 * @return
	 */
	public final long getProductOfNeighborPrimes() {
		return productOfNeighborPrimes;
	}
	
	/**
	 * Returns the new Rank of this Agent
	 * 
	 * @return
	 */
	public final int getRankNew() {
		return rankNew;
	}

	/**
	 * Sets the new Rank of this Agent.
	 * 
	 * @param rank
	 */
	public final void setRankNew(int rank) {
		rankNew = rank;
	}
	
	/**
	 * Returns the number of connections of this Agent.
	 * 
	 * @return
	 */
	public final int getNumberOfConnections() {
		return numberOfConnections;
	}
	

	/**
	 * This class compares two AgentInvariants with respect to some "graph theoretical invariants".
	 * The invariants selected here is not a "complete" set.
	 * No "perfect" set of invariants is known that will distinguish all possible graph symmetries. 
	 * We can add more invariants or eliminate some to fine tune the algorithm's performance.
	 * 
	 * <br><br>
	 * Here is the comparison rules:
	 * 
	 * <ul>
	 * <li> The Agent with less connections comes before
	 * <li> If two Agents have the same number of connections, then the Agent with less sites comes before
	 * <li> If two Agents are still equivalent, then the Agents are sorted according to their names
	 * <li> If two Agents are still equivalent, we compare their Sites using {@link SiteComparator}
	 * </ul>
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
			List<CSite> sortedSites1 = o1.getSortedSites();
			List<CSite> sortedSites2 = o2.getSortedSites();
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

	/**
	 * This class compares two AgentInvariants with respect 
	 * to their Ranks and to their product of neighbor primes.
	 * 
	 * <br><br>
	 * Here is the comparison rules:
	 * 
	 * <ul>
	 * <li> The Agent with lowest rank comes before
	 * <li> If two Agents have the same rank, then the Agent with the lowest product of primes comes before
	 * </ul>
	 * 
	 * @author ecemis
	 *
	 */
	public static final class AgentInvariantRankComparator implements Comparator<AgentInvariant> {
		private AgentInvariantRankComparator() {
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