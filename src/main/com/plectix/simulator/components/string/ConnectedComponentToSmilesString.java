package com.plectix.simulator.components.string;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CLink;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.util.PlxLogger;

/**
 * This class is Singleton that creates a unique String from a ConnectedComponent's list of Agents. 
 * 
 * <br><br>
 * Here is a brief description of the algorithm:
 * <br><br>
 * We first sort the Agents according to some "graph theoretical invariants" 
 * using {@link AgentInvariant.AgentInvariantComparator}. Please see that class for sorting rules.
 * 
 * <br><br>
 * If no Agents are "equal" according to these initial sorting rules, we are done. 
 * We can print the Agents in that order which should be unique.
 * 
 * <br><br>
 * But no "perfect" set of invariants is known that will distinguish all possible graph symmetries. 
 * Even though the initial sorting would discriminate Agents in most cases, there would still be
 * some cases that some Agents would have equivalent invariants. Some of these equivalent cases
 * are real symmetries and some are not. For example, consider the following polymer:
 * 
 * <br><br>
 * <center><tt>A-A-A-A-A-A-A-A</tt></center>
 * 
 * <br><br>
 * The initial sorting will create two categories: The Agents at both ends will be in the first
 * category. The Agents in the middle will be in the second category. The Agents at the ends 
 * are really symmetrical. But the Agents in the middle are not all symmetrical, some are closer
 * to the ends than the others. And once we choose which end to start writing the unique String,
 * then none of the Agents are symmetrical anymore. 
 * 
 * <br><br>
 * If we have any equivalent Agent, we proceed as follows until we discriminate all equivalent cases:
 * First, we assign a rank to each agent. The initial ranks for the above example is:
 * 
 * <br><br>
 * <center><tt>1-2-2-2-2-2-2-1</tt></center> 
 * 
 * <br><br>
 * Then, we compute the product of prime numbers corresponding to the rank of the Agent's neighbors.
 * For example, if an Agent whose neighbors' ranks are 2, 2, 5 then the product of their corresponding
 * primes is 3 x 3 x 11 = 99. According to the prime factorization theorem, this procedure will always
 * provide an unambiguous result for any set of input ranks.
 * 
 * <br><br>
 * After computing the product of primes for each Agent, we resort the Agents using 
 * {@link AgentInvariant.AgentInvariantRankComparator} and re-rank them. Note that
 * this resorting preserves the previous ranks but only discriminates equivalent cases.
 * If no Agents have equivalent ranks, we are done.
 * 
 * <br><br>
 * If there are real symmetries (hence equivalent ranks such as the end Agents in the polymer example above), 
 * then these resorting would not change the previous ranks. This situation is called an "invariant partitioning".
 * In order to "break ties", the algorithm proceeds by doubling all ranks and reducing the rank of the first Agent 
 * with the equivalent rank by one. These new ranks are treated as a new invariant set, and the previous steps
 * of computing the product of prime numbers and resorting is repeated until there are no more equivalent
 * ranks. 
 * 
 * <br><br>
 * Some properties of the algorithm:
 * 
 * <ul>
 * <li>
 * It uses the passed ConnectedComponent in one statement: <code>connectedComponent.getAgents()</code> 
 * and works with the returned list of Agents. No other data member in CConnectedComponent is used.
 * <li>
 * It doesn't change this list of Agents, e.g. it doesn't reorder them in any way, it works on its separate copy. 
 * <li>
 * It doesn't change the list of Sites in Agents either, it works on its separate copy.
 * The only things that the algorithm changes is <code>linkIndex</code> of Sites in the ConnectedComponent. 
 * The initial values are not read. It calls <code>site.setLinkIndex()</code> on all sites and sets 
 * the <code>linkIndex</code> data to -1 or a positive integer it computes. 
 * </ul>
 * 
 * <br>
 * The class doesn't have any data member and uses static private methods only. Therefore the access to the public method is through a Singleton.
 * 
 * <br><br>
 * 
 * @author ecemis
 */
public class ConnectedComponentToSmilesString implements ConnectedComponentToStringInterface {

	private static final PlxLogger LOGGER = ThreadLocalData.getLogger(ConnectedComponentToSmilesString.class);
	
	private static final ConnectedComponentToSmilesString INSTANCE = new ConnectedComponentToSmilesString();
	
	private ConnectedComponentToSmilesString() {
		super();
	}
	
	public static final ConnectedComponentToSmilesString getInstance() {
		return INSTANCE;
	}
	
	public final String toUniqueString(IConnectedComponent connectedComponent) {
		List<CAgent> agentList = connectedComponent.getAgents();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ConnectedComponent has " + agentList.size() + " Agents");
		}
		
		List<AgentInvariant> agentInvariantList = new ArrayList<AgentInvariant>(agentList.size());
		for (CAgent agent : agentList) {
			agentInvariantList.add(new AgentInvariant(agent));
		}
		
		if (agentInvariantList.size() == 1) {
			// there is only one agent so we don't need any ranking!
			return toKappa(agentInvariantList);
		}
		
		// The following sets the new ranks for each AgentInvariant
		// These new ranks are our new invariants...
		boolean rankEquivalence = sortAndComputeRanks(agentInvariantList, AgentInvariant.AGENT_INVARIANT_COMPARATOR);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("--> Rank Equivalence = " + rankEquivalence + " Ranks: " + getRanksAsString(agentInvariantList));
		}
		
		if (rankEquivalence == false) {
			// we are done:
			return toKappa(agentInvariantList);
		}

		// let's have each AgentInvariant know its neighbors
		computeNeighbors(agentInvariantList);

		// infinite loop! hope it will finish...
		int iterationCount = 0;
		while (true) {
			for (AgentInvariant agentInvariant : agentInvariantList) {
				agentInvariant.computeProductOfNeighborPrimes();
			}

			// Let's save the ranks before computing the new ones...
			saveRanks(agentInvariantList);

			rankEquivalence = sortAndComputeRanks(agentInvariantList, AgentInvariant.AGENT_INVARIANT_RANK_COMPARATOR);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("--> Iteration= " + iterationCount++ 
						+ " [Rank Equivalence= " + rankEquivalence 
						+ "] Ranks= " + getRanksAsString(agentInvariantList));
			}

			if (!rankEquivalence) {
				// we are done with the infinite loop, the ranks are not equivalent ;-)
				// return Kappa string now:
				return toKappa(agentInvariantList);
			}

			// the ranks are equivalent... do we have invariant partitioning?
			boolean invariantPartitioning = true;
			for (AgentInvariant agentInvariant : agentInvariantList) {
				if (agentInvariant.areRanksEqual() == false) {
					invariantPartitioning = false;
					break;
				}
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("----> Invariant Partitioning= " + invariantPartitioning);
			}

			if (invariantPartitioning) {
				// extended connectivity method is complete and an invariant partitioning has been developed
				// we still have rank equivalence so we have to break ties now...
				breakTies(agentInvariantList);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("--> Broken Ties... New Ranks= " + getRanksAsString(agentInvariantList));
				}
			}
		}
		
		// we can never be here cause there is no break from the infinite loop above...
	}
	
	private static final String toKappa(List<AgentInvariant> agentInvariantList) {
		// set all link indices to -1
		for (AgentInvariant agentInvariant : agentInvariantList) {
			for (CSite site : agentInvariant.getSortedSites()) {
				site.setLinkIndex(-1);
			}
		}

		StringBuffer stringBuffer = new StringBuffer();
		
		int linkIndexCounter = 0;
		for (AgentInvariant agentInvariant : agentInvariantList) {
			CAgent agent = agentInvariant.getAgent();
			stringBuffer.append(agent.getName() + "(");
			
			 boolean firstSite = true;
		     for (CSite site : agentInvariant.getSortedSites()) {
		            if (firstSite) {
		                firstSite = false;
		            } else {
		            	stringBuffer.append(",");
		            }
		            
		            stringBuffer.append(site.getName());
		            
		            if (site.getInternalState().getNameId() != CSite.NO_INDEX) {
		            	stringBuffer.append("~" + site.getInternalState().getName());
		            }
		            
		    		CLink linkState = site.getLinkState();
		    		CLinkStatus statusLink = linkState.getStatusLink();
		    		
		    		if (statusLink == CLinkStatus.BOUND) {
		    			if (site.getLinkIndex() == -1) {
		    				// the initial count is zero so let's increment it
		    				linkIndexCounter++;
		    				// let's now set this site's link index
		    				site.setLinkIndex(linkIndexCounter);
		    				// let's find the site we are bound to and set its index too..
			    			linkState.getConnectedSite().setLinkIndex(linkIndexCounter);
		    			} 
		    			// let's dump our link index:
		    			stringBuffer.append("!" + site.getLinkIndex());
		    		} else if (statusLink != CLinkStatus.FREE) {
		    			// we expect that the site will be either BOUND or FREE. throw exception otherwise:
		    			throw new RuntimeException("Unexpected State: Link state is neither BOUND nor FREE.");
		    		} 
		        }

			stringBuffer.append("),");
		}
		stringBuffer.deleteCharAt(stringBuffer.length()-1);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("----> Kappa String= " + stringBuffer);
		}
		
		return stringBuffer.toString();
	}
	
	private static final void breakTies(List<AgentInvariant> agentInvariantList) {
		// The SMILES algorithm is not explained well here...
		// It doubles all ranks and reduces the value of the first node, which is tied, by one.
		// The set is then treated as a new invariant set...
		// But here biology differs from chemistry, we have site names which makes the problem harder in some ways

		List<AgentInvariant> equivalantAgents = new ArrayList<AgentInvariant>();
		int nodetoTieIndex = -1;
		for (int i = 1; i < agentInvariantList.size(); i++) {
			AgentInvariant agentInvariantCurrent = agentInvariantList.get(i);
			
			if (nodetoTieIndex == -1) {
				if (AgentInvariant.AGENT_INVARIANT_RANK_COMPARATOR.compare(agentInvariantCurrent, agentInvariantList.get(i-1)) == 0) {
					// this node is the first node in a series of symmetrical Agents...
					nodetoTieIndex = i-1;
					equivalantAgents.add(agentInvariantList.get(i-1));
					equivalantAgents.add(agentInvariantCurrent);
				}
			} else {
				if (AgentInvariant.AGENT_INVARIANT_RANK_COMPARATOR.compare(agentInvariantCurrent, agentInvariantList.get(nodetoTieIndex)) == 0) {
					equivalantAgents.add(agentInvariantCurrent);
				} else {
					break;
				}
			}
		}
		
		if (equivalantAgents.size() < 2) {
			throw new RuntimeException("Unexpected number of equivalent Agents: " + equivalantAgents.size());
		}
		
		// Let's double the ranks...
		for (int i = 0; i < agentInvariantList.size(); i++) {
			agentInvariantList.get(i).doubleRankNew();
		}	
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("----> breakTies: Number of agents with equivalent ranks: " + equivalantAgents.size() + " -> " + getRanksAsString(equivalantAgents));
		}
		
		// we should differentiate them here!

		int previousRank = equivalantAgents.get(0).getRankNew();
		sortAndComputeRanks(equivalantAgents, AgentInvariant.AGENT_INVARIANT_NEIGHBOR_SITE_COMPARATOR);

		// let's lower the rank of the first node by one...
		equivalantAgents.get(0).setRankNew(previousRank -1);
		
		for (int i = 1; i < equivalantAgents.size(); i++) {
			equivalantAgents.get(i).setRankNew(previousRank);
		}

		if (LOGGER.isDebugEnabled()) {
			if (equivalantAgents.get(0).getRankNew() == equivalantAgents.get(1).getRankNew()) {
				LOGGER.debug("----> We have equivalent ranks while breaking ties: " + getRanksAsString(equivalantAgents));
			} else {
				LOGGER.debug("----> breakTies: Successfully differentiated: " + getRanksAsString(equivalantAgents));
			}
		}

	}

	private static final void saveRanks(List<AgentInvariant> agentInvariantList) {
		for (AgentInvariant agentInvariant : agentInvariantList) {
			agentInvariant.saveRank();
		}
	}
	
	private static final String getRanksAsString(List<AgentInvariant> agentInvariantList) {
		StringBuffer stringBuffer = new StringBuffer();
		for (AgentInvariant agentInvariant : agentInvariantList) {
			stringBuffer.append(agentInvariant.getRankNew() + "(" + agentInvariant.getProductOfNeighborPrimes() + ")-"); 
		}
		return stringBuffer.toString();
	}
	
	/**
	 * Returns true if there are equivalent ranks, false otherwise. 
	 * 
	 * @param agentInvariantList
	 * @param agentInvariantComparator
	 * @return
	 */
	private static final boolean sortAndComputeRanks(List<AgentInvariant> agentInvariantList, Comparator<AgentInvariant> agentInvariantComparator) {
		Collections.sort(agentInvariantList, agentInvariantComparator);
		
		boolean rankEquivalence = false;
		int rankCounter = 1;
		agentInvariantList.get(0).setRankTemp(rankCounter);
		
		for (int i = 1; i < agentInvariantList.size(); i++) {
			AgentInvariant agentInvariantPrevious = agentInvariantList.get(i-1);
			AgentInvariant agentInvariantCurrent = agentInvariantList.get(i);
			
			if (agentInvariantComparator.compare(agentInvariantPrevious, agentInvariantCurrent) == 0) {
				rankEquivalence = true;
			} else {
				// they are not equal so let's increase the rank
				rankCounter += agentInvariantPrevious.getNumberOfConnections();
			}
			
			agentInvariantCurrent.setRankTemp(rankCounter);
		}
		
		for (AgentInvariant agentInvariant : agentInvariantList) {
			agentInvariant.setRankNew(agentInvariant.getRankTemp());
		}
		
		return rankEquivalence;
	}
	
	private static final void computeNeighbors(List<AgentInvariant> agentInvariantList) {
		Map<CAgent, AgentInvariant> agentToAgentInvariantMap = new HashMap<CAgent, AgentInvariant>(agentInvariantList.size());
		for (AgentInvariant agentInvariant : agentInvariantList) {
			agentToAgentInvariantMap.put(agentInvariant.getAgent(), agentInvariant);
		}
		
		for (AgentInvariant agentInvariant : agentInvariantList) {
			agentInvariant.computeNeighbors(agentToAgentInvariantMap);
		}
	}

}
