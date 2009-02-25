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
	private Map<Integer, List<Long>> agentNameIdToAgentId;
	private Map<Integer, IContactMapAbstractAgent> agentNameIdToAgent;
	private final SimulationData simulationData;

	public Map<Integer, IContactMapAbstractAgent> getAbstractAgentMapOld() {
		return abstractAgentMapOld;
	}

	public CContactMapAbstractSolution(SimulationData simulationData) {
		this.abstractAgentMapOld = new HashMap<Integer, IContactMapAbstractAgent>();
		this.abstractAgentMap = new HashMap<Long, IContactMapAbstractAgent>();
		this.agentNameIdToAgent = new HashMap<Integer, IContactMapAbstractAgent>();
		this.agentNameIdToAgentId = new HashMap<Integer, List<Long>>();
		this.simulationData = simulationData;
		fillModelMapOfAgents();
		// ==================
		fillAgentMap(simulationData.getKappaSystem().getSolution());
	}
	
	
	public boolean addNewData(List<IContactMapAbstractSite> listIn){
		Map<Long, IContactMapAbstractAgent> mapAgent = new HashMap<Long, IContactMapAbstractAgent>();
		//Map<Long, List<IContactMapAbstractSite>> mapAgentToSites = new HashMap<Long, List<IContactMapAbstractAgent>>();
		List<IContactMapAbstractAgent> listToAdd = new ArrayList<IContactMapAbstractAgent>();
		for(IContactMapAbstractSite s : listIn){
			IContactMapAbstractAgent newAgent = mapAgent.get(s.getAgentLink().getId());
			if(newAgent == null){
				newAgent = new CContactMapAbstractAgent(s.getAgentLink());
				listToAdd.add(newAgent);
			}
			newAgent.modify(s);
		}
		
		//clear listToAdd
		
		if(listToAdd.isEmpty())
			return false;
		return true;
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

	private boolean addAgentToAgentsMap(IContactMapAbstractAgent abstractAgent) {
		Integer agentNameId = abstractAgent.getNameId();
		List<Long> listAgentId = agentNameIdToAgentId.get(agentNameId);
		if (listAgentId == null)
			listAgentId = new ArrayList<Long>();

		boolean isAdd = true;
		for (Long l : listAgentId) {
			IContactMapAbstractAgent checkAgent = abstractAgentMap.get(l);
			if (checkAgent.equalz(abstractAgent)) {
				isAdd = false;
				break;
			}
		}

		if (isAdd) {
			long id = simulationData.getKappaSystem().generateNextAgentId();
			abstractAgent.setId(id);
			listAgentId.add(id);
			abstractAgentMap.put(id, abstractAgent);
			agentNameIdToAgentId.put(agentNameId, listAgentId);
			return true;
		}

		return false;
	}

	public final List<IContactMapAbstractAgent> getListOfAgentsByNameID(
			int nameID) {
		List<Long> listIDs = agentNameIdToAgentId.get(nameID);
		if (listIDs == null)
			return null;
		List<IContactMapAbstractAgent> agentsList = new ArrayList<IContactMapAbstractAgent>();

		for (long id : listIDs) {
			IContactMapAbstractAgent agent = abstractAgentMap.get(id);
			agentsList.add(agent);
		}

		return agentsList;
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
