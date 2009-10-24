/**
 * This class computes some invariants about Agents to compare them.
 */
package com.plectix.simulator.util.string;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.LinkStatus;
import com.plectix.simulator.staticanalysis.Site;
import com.plectix.simulator.util.PrimeNumbers;

public final class AgentInvariant {

	public static final AgentInvariantComparator AGENT_INVARIANT_COMPARATOR = new AgentInvariantComparator();

	public static final AgentInvariantRankComparator AGENT_INVARIANT_RANK_COMPARATOR = new AgentInvariantRankComparator();
	
	public static final AgentInvariantNeighborSiteComparator AGENT_INVARIANT_NEIGHBOR_SITE_COMPARATOR = new AgentInvariantNeighborSiteComparator();
	
	private int rankOld = 0; // 0 means not set yet
	private int rankNew = 0; // 0 means not set yet
	private int rankTemp = 0; // 0 means not set yet
	
	private long productOfNeighborPrimes = 0;

	private int numberOfConnections = 0;
	private int numberOfWildcards = 0;
	private Agent agent = null;  // this is set in the constructor
	private List<Site> sortedSites = null;  // we don't initialize this list if we don't have to...
	private List<AgentInvariant> neighborAgentList = null;  // we don't initialize this list if we don't have to...
	
	public AgentInvariant(Agent agent) {
		if (agent == null) {
			throw new RuntimeException("Agent can not be null!");
		}
		this.agent = agent;
			
		for (Site site : agent.getSites()) {
			if (site.getLinkState().getStatusLink() == LinkStatus.BOUND) {
				numberOfConnections++;
			} else if (site.getLinkState().getStatusLink() == LinkStatus.WILDCARD) {
				numberOfWildcards++;
			} else if (site.getLinkState().getStatusLink() != LinkStatus.FREE) {
				// we expect that the site will be either BOUND, WILDCARD, or FREE. throw exception otherwise:
				throw new RuntimeException("Unexpected State: Link state is neither BOUND nor WILDCARD nor FREE.");
			}
		}
	}
	
	/**
	 * Returns the sites of this Agent sorted using {@link SiteComparator}
	 * 
	 * @return
	 */
	public final List<Site> getSortedSites() {
		if (sortedSites == null) {
			sortedSites = new ArrayList<Site>(agent.getSites());
			// here they will be sorted by name since they are on the same Agent:
			Collections.sort(sortedSites, SiteComparator.SITE_COMPARATOR);
		}
		return sortedSites;
	}

	/**
	 * Compile a list of AgentInvariants which are bound to this AgentInvariant 
	 * 
	 * @param targetAgentAgentInvariantMap
	 */
	public final void computeNeighbors(Map<Agent, AgentInvariant> targetAgentAgentInvariantMap) {
		if (neighborAgentList != null) {
			return;
		}

		neighborAgentList = new ArrayList<AgentInvariant>();
		
		for (Site site : getSortedSites()) {
			if (site.getLinkState().getStatusLink() == LinkStatus.BOUND) {
				Site connectedSite = site.getLinkState().getConnectedSite();
				if (connectedSite != null) {
					AgentInvariant neighbor = targetAgentAgentInvariantMap.get(connectedSite.getParentAgent()); 
					if (neighbor == null) {
						throw new RuntimeException("Could not find neighbor Agent in map!");
					}
					// all the neighbors are sorted in the alphabetical order of the sites they are connected to
					neighborAgentList.add(neighbor);
				}
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
			productOfNeighborPrimes *= PrimeNumbers.FIRST_8242[agentInvariant.getRankNew() + agentInvariant.getNeighborRank(this) - 1];
		}
	}

	/**
	 * Returns the index of this neighbor in the neighbor list
	 * 
	 * @param neighborAgent
	 * @return
	 */
	private final int getNeighborRank(AgentInvariant neighborAgent) {
		final int index = neighborAgentList.indexOf(neighborAgent);
		if (index == -1) {
			throw new RuntimeException("Given agent is not a neighbor!");
		}
		return index;
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
	public final Agent getAgent() {
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
	 * Returns the temporary Rank of this Agent
	 * 
	 * @return
	 */
	public final int getRankTemp() {
		return rankTemp;
	}

	/**
	 * Sets the temporary Rank of this Agent.
	 * 
	 * @param rank
	 */
	public final void setRankTemp(int rank) {
		rankTemp = rank;
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
	 * Returns the number of wildcard sites of this Agent.
	 * 
	 * @return
	 */
	public final int getNumberOfWildcards() {
		return numberOfWildcards;
	}

	private static final Site getConnectionSite(AgentInvariant thisAgent, AgentInvariant neighborAgent) {
		for (Site site : thisAgent.getAgent().getSites()) {
			if (site.getLinkState().getStatusLink() == LinkStatus.BOUND) {
				Site connectedSite = site.getLinkState().getConnectedSite();
				if (connectedSite == null) {
					return null;
				}
				if (connectedSite.getParentAgent() == neighborAgent.getAgent()) {
					return site;
				}
			}
		}
		throw new RuntimeException("Agents are not neighbors!");
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

		public final int compare(final AgentInvariant o1, final AgentInvariant o2) {
			if (o1 == null) {
				if (o2 == null) {
					return 0;
				} else {
					return -1;
				}
			} else {
				if (o2 == null) {
					return 1;
				} else {
					// don't handle this case here... go below...
				}
			}
			
			// first compare the number of connections
			int result = Double.compare(o1.getNumberOfConnections(), o2.getNumberOfConnections());
			if (result != 0) {
				return result;
			}
			
			result = Double.compare(o1.getNumberOfWildcards(), o2.getNumberOfWildcards());
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
			List<Site> sortedSites1 = o1.getSortedSites();
			List<Site> sortedSites2 = o2.getSortedSites();
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
			
			return Double.compare(o1.getProductOfNeighborPrimes(), o2.getProductOfNeighborPrimes());
		}
	}
	
	public static final class AgentInvariantNeighborSiteComparator implements Comparator<AgentInvariant> {
		private AgentInvariantNeighborSiteComparator() {
			super();
		}

		public final int compare(final AgentInvariant o1, final AgentInvariant o2) {
			if (o1 == null) {
				if (o2 == null) {
					return 0;
				} else {
					return -1;
				}
			} else {
				if (o2 == null) {
					return 1;
				} else {
					// don't handle this case here... go below...
				}
			}
			
			int result = Double.compare(o1.neighborAgentList.size(), o2.neighborAgentList.size());
			if (result != 0) {
				// this case should never happen! but it doesn't hurt to have it here...
				return result;
			}
				
			List<AgentInvariant> neighborAgentList1 = new ArrayList<AgentInvariant>(o1.neighborAgentList);
			List<AgentInvariant> neighborAgentList2 = new ArrayList<AgentInvariant>(o2.neighborAgentList);
			
			Collections.sort(neighborAgentList1, AGENT_INVARIANT_RANK_COMPARATOR);
			Collections.sort(neighborAgentList2, AGENT_INVARIANT_RANK_COMPARATOR);
			
			for (int i= 0; i < neighborAgentList1.size(); i++) {
				if (i+1 != neighborAgentList1.size()) {
					final boolean neighborAgentList1IsRandom = AGENT_INVARIANT_RANK_COMPARATOR.compare(neighborAgentList1.get(i), neighborAgentList1.get(i+1)) == 0;
					final boolean neighborAgentList2IsRandom = AGENT_INVARIANT_RANK_COMPARATOR.compare(neighborAgentList2.get(i), neighborAgentList2.get(i+1)) == 0;
					
					if (neighborAgentList1IsRandom) {
						if (neighborAgentList2IsRandom) {
							// can not compare randomly!
							return 0;
						} else {
							return +1;
						}
					} else {
						if (neighborAgentList2IsRandom) {
							return -1;
						} else {
							// compare below...
						}
					}
				}
				
				result = AGENT_INVARIANT_RANK_COMPARATOR.compare(neighborAgentList1.get(i), neighborAgentList2.get(i));
				if (result != 0) {
					return result;
				}
				
				if (neighborAgentList1.get(i).getRankNew() >= o1.getRankNew()) {
					return 0;
				}
				
				// Let's find the sites connected to that neighbor:
				Site site1 = getConnectionSite(o1, neighborAgentList1.get(i));
				Site site2 = getConnectionSite(o2, neighborAgentList2.get(i));
				
				// some part of the following comparison is redundant. but at least we don't duplicate any code!
				result = SiteComparator.SITE_COMPARATOR.compare(site1, site2);
				if (result != 0) {
					return result;
				}	
			}
			
			// could not differentiate ;-(
			return 0;
		}
	}

}