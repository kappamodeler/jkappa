package com.plectix.simulator.components.contactMap;

import java.util.*;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IContactMapAbstractAgent;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.IConnectedComponent;

public class CContactMapAbstractRule {

	private CContactMapAbstractSolution solution;
	private Map<Integer, IContactMapAbstractAgent> agentMapLeftHandSide;
	private Map<Integer, IContactMapAbstractAgent> agentMapRightHandSide;
	
	public CContactMapAbstractRule(CContactMapAbstractSolution solution, IRule rule){
		this.solution = solution;
		this.agentMapLeftHandSide = new TreeMap<Integer, IContactMapAbstractAgent>();
		this.agentMapRightHandSide = new TreeMap<Integer, IContactMapAbstractAgent>();
	}
	
	private void abstractCCList(List<IConnectedComponent> ccList, Map<Integer, IContactMapAbstractAgent> map){
		for(IConnectedComponent cc : ccList){
			for(IAgent agent : cc.getAgents()){
				Integer key = agent.getNameId(); 
				IContactMapAbstractAgent cMAA = map.get(key);
				if(cMAA==null){
					cMAA = new CContactMapAbstractAgent(agent);
					map.put(key,cMAA);
				}
				cMAA.addSites(agent);
			}
		}
	}
	
}
