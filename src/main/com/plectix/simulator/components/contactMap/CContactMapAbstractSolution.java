package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.action.CAddAction;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IAction;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IContactMapAbstractAgent;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.simulator.SimulationData;

public class CContactMapAbstractSolution {

	private Map<Integer, IContactMapAbstractAgent> abstractAgentMapOld;
	private Map<Long, IContactMapAbstractAgent> abstractAgentMap;
	private Map<Integer,List<Long>> agentNameIdToAgentId;
	private final SimulationData simulationData;
	
	public Map<Integer, IContactMapAbstractAgent> getAbstractAgentMapOld() {
		return abstractAgentMapOld;
	}

	public CContactMapAbstractSolution(SimulationData simulationData) {
		this.abstractAgentMapOld = new HashMap<Integer, IContactMapAbstractAgent>();
		this.abstractAgentMap = new HashMap<Long, IContactMapAbstractAgent>();
		this.agentNameIdToAgentId = new HashMap<Integer, List<Long>>();
		this.simulationData = simulationData;
		fillMapCCList(simulationData.getKappaSystem().getSolution());
		fillAgentMap(simulationData.getKappaSystem().getSolution());
	}

	private void fillAgentMap(ISolution solution) {
		
		Collection<IAgent> agentMap = solution.getAgents();
		for (IAgent a : agentMap) {
			IContactMapAbstractAgent abstractAgent = new CContactMapAbstractAgent(a);
			abstractAgent.addSites(a);
			addAgentToAgentsMap(abstractAgent);
		}
	}
	
	private boolean addAgentToAgentsMap(IContactMapAbstractAgent abstractAgent) {
		Integer agentNameId = abstractAgent.getNameId();
		List<Long> listAgentId = agentNameIdToAgentId.get(agentNameId);
		if(listAgentId==null)
			listAgentId = new ArrayList<Long>();
			
		boolean isAdd = true;
		for(Long l : listAgentId){
			IContactMapAbstractAgent checkAgent = abstractAgentMap.get(l);
			if(checkAgent.equalz(abstractAgent)){
				isAdd = false;
				break;
			}
		}
		
		if(isAdd){
			long id = simulationData.getKappaSystem().generateNextAgentId();
			abstractAgent.setId(id);
			listAgentId.add(id);
			abstractAgentMap.put(id, abstractAgent);
			agentNameIdToAgentId.put(agentNameId, listAgentId);
			return true;
		}

		return false;
	}
	
	public IContactMapAbstractSite findSite(Integer agentId,Integer siteId, int internalStateId, int agentLinkId, int siteLinkId, int internalStateLinkId){
//		IContactMapAbstractAgent agent = abstractAgentMap.get(agentId);
//		if(agent == null)
//			return null;
//		List<IContactMapAbstractSite> list = agent.getSiteMap().get(siteId);
//		if(list == null)
//			return null;
//		for(IContactMapAbstractSite s : list){
//			if(s.getInternalState().getNameId() != internalStateId)
//				continue;
//			CContactMapLinkState linkState = s.getLinkState();
//			if(linkState.getAgentNameID() != agentLinkId)
//				continue;
//			if(linkState.getInternalStateNameID() != internalStateLinkId)
//				continue;
//			if(linkState.getLinkSiteNameID() != siteLinkId)
//				continue;
//			return s;
//		}
		return null;
	}
	
	private boolean fillMapCCList(ISolution solution) {
		Collection<IAgent> agentMap = solution.getAgents();
		boolean wasAdded = false;

		for (IAgent agent : agentMap) {
			if (addAgentToMap(agent))
				wasAdded = true;
		}
		return wasAdded;
	}

	private boolean addAgentToMap(IAgent agent) {
		Integer keyToPut = agent.getNameId();
		IContactMapAbstractAgent cMAA = abstractAgentMapOld.get(keyToPut);
		if (cMAA == null) {
			cMAA = new CContactMapAbstractAgent(agent);
			abstractAgentMapOld.put(keyToPut, cMAA);
		}
		if (cMAA.addSites(agent))
			return true;

		return false;
	}

	public boolean addNewAgentsToAbstractMap(ISolution solution,
			List<IInjection> injList, IRule rule) {
		boolean wasAdded = false;
		if(rule.getRightHandSide()==null)
			return wasAdded;
		
		if(rule.getLeftHandSide().get(0) != CRule.EMPTY_LHS_CC)
		for (IInjection inj : injList) {
			IAgent agentToCheck = inj.getAgentLinkList().get(0).getAgentTo();
			IConnectedComponent ccToCheck = solution
					.getConnectedComponent(agentToCheck);
			for (IAgent agent : ccToCheck.getAgents()) {
				if (addAgentToMap(agent))
					wasAdded = true;
			}
		}
		
		for (IAction action : rule.getActionList()) {
			if (action.getTypeId() == CActionType.ADD.getId()) {
				if(addAgentToMap(((CAddAction) action).getAgentTo()))
					wasAdded = true;
			}
		}
		return wasAdded;
	}

	public void print(){
		Iterator<Integer> aIter = abstractAgentMapOld.keySet().iterator();
		while (aIter.hasNext()) {
			Integer aKey = aIter.next();
			IContactMapAbstractAgent cMAA = abstractAgentMapOld.get(aKey);
			((CContactMapAbstractAgent)cMAA).print();
		}
		System.out.println("***************************************************************************");
	}

	public boolean addNewData(List<IContactMapAbstractSite> newData) {
		if(newData.isEmpty())
			return false;
		boolean isAdd = false;
		for(IContactMapAbstractSite s : newData){
			if(addSite(s))
				isAdd = true;
		}
		return isAdd;
	}

	private boolean addSite(IContactMapAbstractSite s) {
		IContactMapAbstractAgent agent = abstractAgentMapOld.get(s.getAgentLink().getNameId());
		if(agent == null)
			agent = new CContactMapAbstractAgent(s.getAgentLink().getNameId());
		return agent.addSite(s);
	}
}
