package com.plectix.simulator.simulator;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;

public class SimulatorManager {
	
	private static SimulatorManager instance;
	
	private SimulationData simulationData = new SimulationData();

	private int agentIdGenerator = 0;;
	
	private SimulatorManager() {
	}
	
	public static SimulatorManager getInstance() {
		if (instance == null) {
			instance = new SimulatorManager();
		}
		return instance;
	}
	
	public List<CConnectedComponent> buildConnectedComponents(List<CAgent> agents) {
		
		if(agents == null || agents.isEmpty())
			return null;
		
		List<CConnectedComponent> result = new ArrayList<CConnectedComponent>();
		
		while(!agents.isEmpty()) {
			int index = 0;
			
			List<CAgent> connectedAgents = new ArrayList<CAgent>();
			CAgent agent = agents.remove(0);
			connectedAgents.add(agent);
			
			agent.setIdInConnectedComponent(index);
			
			List<CSite> sites = agent.getSites();
			
			for(CSite site: sites) {
				if(site.getLinkIndex() != CSite.NO_INDEX) {
					CAgent linkedAgent = findLink(agents, site.getLinkIndex());
					if(linkedAgent != null) {
						connectedAgents.add(linkedAgent);
						agents.remove(linkedAgent);
						linkedAgent.setIdInConnectedComponent(++index);
					}
				}
			}
			
			result.add(new CConnectedComponent(connectedAgents));
		}
		
		return result;
	}
	
	

	private CAgent findLink(List<CAgent> agents, int linkIndex) {
		for(CAgent tmp: agents) {
			for (CSite s: tmp.getSites()) {
				if (s.getLinkIndex() == linkIndex) {
					return tmp;
				}
			}
		}
		return null;
	}

	public CRule buildRule(List<CAgent> left, List<CAgent> right,String name, Double activity) {
		return new CRule(buildConnectedComponents(left),buildConnectedComponents(right),name,activity);
	}
	

	public void setRules(List<CRule> rules) {
		simulationData.setRules(rules);
	}

	public List<CRule> getRules() {
		return simulationData.getRules();
	}

	public SimulationData getSimulationData() {
		return simulationData;
	}

	public synchronized long generateNextAgenId() {
		return agentIdGenerator  ++;
	}
}
