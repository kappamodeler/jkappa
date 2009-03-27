package com.plectix.simulator.components.string;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.ILinkState;
import com.plectix.simulator.interfaces.ISite;

public class ConnectedComponentToSmilesString implements ConnectedComponentToStringInterface {

	private static final Logger LOGGER = Logger.getLogger(ConnectedComponentToSmilesString.class);
	
	public final String toUniqueString(IConnectedComponent connectedComponent) {
		List<IAgent> agentList = connectedComponent.getAgents();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ConnectedComponent has " + agentList.size() + " Agents");
		}
		
		List<AgentInvariant> agentInvariantList = new ArrayList<AgentInvariant>(agentList.size());
		for (IAgent agent : agentList) {
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
						+ " Rank Equivalence= " + rankEquivalence 
						+ " Ranks= " + getRanksAsString(agentInvariantList));
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
			for (ISite site : agentInvariant.getSortedSites()) {
				site.setLinkIndex(-1);
			}
		}

		StringBuffer stringBuffer = new StringBuffer();
		
		int linkIndexCounter = 0;
		for (AgentInvariant agentInvariant : agentInvariantList) {
			IAgent agent = agentInvariant.getAgent();
			stringBuffer.append(agent.getName() + "(");
			
			 boolean firstSite = true;
		     for (ISite site : agentInvariant.getSortedSites()) {
		            if (firstSite) {
		                firstSite = false;
		            } else {
		            	stringBuffer.append(",");
		            }
		            
		            stringBuffer.append(site.getName());
		            
		            if (site.getInternalState().getNameId() != CSite.NO_INDEX) {
		            	stringBuffer.append("~" + site.getInternalState().getName());
		            }
		            
		    		ILinkState linkState = site.getLinkState();
		    		CLinkStatus statusLink = linkState.getStatusLink();
		    		
		    		if (statusLink == CLinkStatus.BOUND) {
		    			if (site.getLinkIndex() == -1) {
		    				// the initial count is zero so let's increment it
		    				linkIndexCounter++;
		    				// let's now set this site's link index
		    				site.setLinkIndex(linkIndexCounter);
		    				// let's find the site we are bound to and set its index too..
			    			linkState.getSite().setLinkIndex(linkIndexCounter);
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

		int nodetoTieIndex = -1;
		for (int i = 1; i < agentInvariantList.size(); i++) {
			AgentInvariant agentInvariantPrevious = agentInvariantList.get(i-1);
			AgentInvariant agentInvariantCurrent = agentInvariantList.get(i);
			
			if (AgentInvariant.AGENT_INVARIANT_RANK_COMPARATOR.compare(agentInvariantPrevious, agentInvariantCurrent) == 0) {
				// this node is selected randomly cause all these nodes are symmetrical...
				nodetoTieIndex = i-1;
				break;
			}
		}
		
		for (int i = 0; i < agentInvariantList.size(); i++) {
			agentInvariantList.get(i).doubleRankNew();
		}	
		
		// let's lower the rank of this node by one...
		agentInvariantList.get(nodetoTieIndex).setRankNew(agentInvariantList.get(nodetoTieIndex).getRankNew() - 1);
	}

	private static final void saveRanks(List<AgentInvariant> agentInvariantList) {
		for (AgentInvariant agentInvariant : agentInvariantList) {
			agentInvariant.saveRank();
		}
	}
	
	private static final String getRanksAsString(List<AgentInvariant> agentInvariantList) {
		StringBuffer stringBuffer = new StringBuffer();
		for (AgentInvariant agentInvariant : agentInvariantList) {
			stringBuffer.append(agentInvariant.getRankNew() + "-"); 
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
		
		int rankCounter = 1;
		agentInvariantList.get(0).setRankNew(rankCounter);
		
		for (int i = 1; i < agentInvariantList.size(); i++) {
			AgentInvariant agentInvariantPrevious = agentInvariantList.get(i-1);
			AgentInvariant agentInvariantCurrent = agentInvariantList.get(i);
			
			if (agentInvariantComparator.compare(agentInvariantPrevious, agentInvariantCurrent) != 0) {
				// they are not equal so let's increase the rank
				rankCounter++;
			}
			
			agentInvariantCurrent.setRankNew(rankCounter);
		}
		
		if (agentInvariantList.size() == rankCounter) {
			return false;
		} else {
			return true;
		}
	}
	
	private static final void computeNeighbors(List<AgentInvariant> agentInvariantList) {
		Map<IAgent, AgentInvariant> agentToAgentInvariantMap = new HashMap<IAgent, AgentInvariant>(agentInvariantList.size());
		for (AgentInvariant agentInvariant : agentInvariantList) {
			agentToAgentInvariantMap.put(agentInvariant.getAgent(), agentInvariant);
		}
		
		for (AgentInvariant agentInvariant : agentInvariantList) {
			agentInvariant.computeNeighbors(agentToAgentInvariantMap);
		}
	}

}
