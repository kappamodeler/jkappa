package com.plectix.simulator.components.contactMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.action.CAddAction;
import com.plectix.simulator.components.CInjection;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.interfaces.IAction;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IContactMapAbstractAgent;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISolution;

public class CContactMapAbstractSolution {

	private Map<Integer, IContactMapAbstractAgent> abstractAgentMap;

	public Map<Integer, IContactMapAbstractAgent> getAbstractAgentMap() {
		return abstractAgentMap;
	}

	public CContactMapAbstractSolution(ISolution solution) {
		this.abstractAgentMap = new HashMap<Integer, IContactMapAbstractAgent>();
		fillMapCCList(solution);
	}

	private boolean fillMapCCList(ISolution solution) {
		Map<Long, IAgent> agentMap = solution.getAgents();
		Iterator<Long> iterator = agentMap.keySet().iterator();
		boolean wasAdded = false;

		while (iterator.hasNext()) {
			Long key = iterator.next();
			IAgent agent = agentMap.get(key);
			if (addAgentToMap(agent))
				wasAdded = true;
		}
		return wasAdded;
	}

	private boolean addAgentToMap(IAgent agent) {
		Integer keyToPut = agent.getNameId();
		IContactMapAbstractAgent cMAA = abstractAgentMap.get(keyToPut);
		if (cMAA == null) {
			cMAA = new CContactMapAbstractAgent(agent);
			abstractAgentMap.put(keyToPut, cMAA);
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
		Iterator<Integer> aIter = abstractAgentMap.keySet().iterator();
		while (aIter.hasNext()) {
			Integer aKey = aIter.next();
			IContactMapAbstractAgent cMAA = abstractAgentMap.get(aKey);
			((CContactMapAbstractAgent)cMAA).print();
		}
		System.out.println("***************************************************************************");
	}

	public boolean addNewData(List<IContactMapAbstractSite> newData) {
		if(newData.isEmpty())
			return false;
		
		return false;
	}
}
