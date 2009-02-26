package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IContactMapAbstractAgent;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.simulator.SimulationData;

public class CContactMapAbstractSolution {

	private Map<Integer, IContactMapAbstractAgent> abstractAgentMapOld;
	private Map<Long, IContactMapAbstractAgent> abstractAgentMap;
//	private Map<Integer, List<Long>> agentNameIdToAgentId;
	private Map<Integer, IContactMapAbstractAgent> agentNameIdToAgent;
	private Map<Integer, List<IContactMapAbstractAgent>> agentNameIdToAgentsList;
	private final SimulationData simulationData;

	public Map<Integer, IContactMapAbstractAgent> getAbstractAgentMapOld() {
		return abstractAgentMapOld;
	}

	public CContactMapAbstractSolution(SimulationData simulationData) {
		this.abstractAgentMapOld = new HashMap<Integer, IContactMapAbstractAgent>();
		this.abstractAgentMap = new HashMap<Long, IContactMapAbstractAgent>();
		this.agentNameIdToAgent = new HashMap<Integer, IContactMapAbstractAgent>();
		this.agentNameIdToAgentsList = new HashMap<Integer, List<IContactMapAbstractAgent>>();
//		this.agentNameIdToAgentId = new HashMap<Integer, List<Long>>();
		this.simulationData = simulationData;
		fillModelMapOfAgents();
		// ==================
		fillAgentMap(simulationData.getKappaSystem().getSolution());
	}

	public boolean addNewData(List<IContactMapAbstractAgent> listIn) {
		boolean isAdd = false;
		for (IContactMapAbstractAgent a : listIn)
			if (addAgentToAgentsMap(a))
				isAdd = true;

		return isAdd;
	}

	private void fillModelMapOfAgents() {
		Collection<IAgent> agentMap = simulationData.getKappaSystem()
				.getSolution().getAgents();
		fillModelMapByAgentList(agentMap);

		for (IRule rule : simulationData.getKappaSystem().getRules()) {
			for (IConnectedComponent cc : rule.getLeftHandSide())
				fillModelMapByAgentList(cc.getAgents());
			if (rule.getRightHandSide() != null)
				for (IConnectedComponent cc : rule.getRightHandSide())
					fillModelMapByAgentList(cc.getAgents());
		}
	}

	private void fillModelMapByAgentList(Collection<IAgent> listIn) {
		for (IAgent a : listIn) {
			IContactMapAbstractAgent modelAgent = agentNameIdToAgent.get(a
					.getNameId());
			if (modelAgent == null) {
				modelAgent = new CContactMapAbstractAgent(a);
				agentNameIdToAgent.put(a.getNameId(), modelAgent);
			}

			for (ISite s : a.getSites()) {
				IContactMapAbstractSite as = new CContactMapAbstractSite(s);
				as.setAgentLink(modelAgent);
				modelAgent.addModelSite(as);
			}
		}
	}

	private void fillAgentMap(ISolution solution) {

		Collection<IAgent> agentMap = solution.getAgents();
		for (IAgent a : agentMap) {
			IContactMapAbstractAgent abstractAgent = new CContactMapAbstractAgent(
					a);
			abstractAgent.addSites(a, this.agentNameIdToAgent);
			addAgentToAgentsMap(abstractAgent);
		}
	}

	private boolean addAgentToAgentsMap(IContactMapAbstractAgent a) {
		List<IContactMapAbstractAgent> agentsFromSolution = agentNameIdToAgentsList
				.get(a.getId());

		if (agentsFromSolution == null) {
			agentsFromSolution = new ArrayList<IContactMapAbstractAgent>();
			agentNameIdToAgentsList.put(a.getNameId(), agentsFromSolution);
		} else if (a.includedInCollection(agentsFromSolution))
			return false;

		agentsFromSolution.add(a);

		return true;
	}

	public final List<IContactMapAbstractAgent> getListOfAgentsByNameID(
			int nameID) {
		List<IContactMapAbstractAgent> listAgents = agentNameIdToAgentsList.get(nameID);
		if (listAgents == null)
			return null;
		return listAgents;
	}

	public IContactMapAbstractSite findSite(Integer agentId, Integer siteId,
			int internalStateId, int agentLinkId, int siteLinkId,
			int internalStateLinkId) {
		// IContactMapAbstractAgent agent = abstractAgentMap.get(agentId);
		// if(agent == null)
		// return null;
		// List<IContactMapAbstractSite> list = agent.getSiteMap().get(siteId);
		// if(list == null)
		// return null;
		// for(IContactMapAbstractSite s : list){
		// if(s.getInternalState().getNameId() != internalStateId)
		// continue;
		// CContactMapLinkState linkState = s.getLinkState();
		// if(linkState.getAgentNameID() != agentLinkId)
		// continue;
		// if(linkState.getInternalStateNameID() != internalStateLinkId)
		// continue;
		// if(linkState.getLinkSiteNameID() != siteLinkId)
		// continue;
		// return s;
		// }
		return null;
	}

	public void print() {
		Iterator<Integer> aIter = abstractAgentMapOld.keySet().iterator();
		while (aIter.hasNext()) {
			Integer aKey = aIter.next();
			IContactMapAbstractAgent cMAA = abstractAgentMapOld.get(aKey);
			((CContactMapAbstractAgent) cMAA).print();
		}
		System.out
				.println("***************************************************************************");
	}
}
