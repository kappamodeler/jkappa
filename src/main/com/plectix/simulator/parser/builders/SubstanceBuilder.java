package com.plectix.simulator.parser.builders;

import java.util.LinkedList;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.parser.abstractmodel.AbstractAgent;
import com.plectix.simulator.simulator.SimulationData;

public class SubstanceBuilder {
	private final SimulationData myData;
	
	public SubstanceBuilder(SimulationData data) {
		myData = data;
	}
	public IAgent buildAgent(AbstractAgent agent) {
		CAgent resultAgent = new CAgent(agent.getNameId(), myData.generateNextAgentId());
		for (ISite site : agent.getSites()) {
			resultAgent.addSite(site);
		}
		return resultAgent;
	}
	
	public List<IAgent> buildAgents(List<AbstractAgent> agents) {
		if (agents == null) {
			return null;
		}
		List<IAgent> result = new LinkedList<IAgent>();
		for (AbstractAgent agent : agents) {
			result.add(buildAgent(agent));
		}
		return result;
	}
}
