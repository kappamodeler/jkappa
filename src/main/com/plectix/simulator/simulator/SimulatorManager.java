package com.plectix.simulator.simulator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.components.NameDictionary;
import com.plectix.simulator.components.CObservables.ObservablesConnectedComponent;

public class SimulatorManager {

	private SimulationData simulationData = new SimulationData();

	private int agentIdGenerator = 0;

	private NameDictionary nameDictionary = new NameDictionary();

	public SimulatorManager() {
	}

	public final List<CConnectedComponent> buildConnectedComponents(
			List<CAgent> agents) {

		if (agents == null || agents.isEmpty())
			return null;

		List<CConnectedComponent> result = new ArrayList<CConnectedComponent>();

		while (!agents.isEmpty()) {
			int index = 0;

			List<CAgent> connectedAgents = new ArrayList<CAgent>();
			CAgent agent = agents.remove(0);
			connectedAgents.add(agent);

			agent.setIdInConnectedComponent(index);

			Collection<CSite> sites = agent.getSites();

			for (CSite site : sites) {
				if (site.getLinkIndex() != CSite.NO_INDEX) {
					CAgent linkedAgent = findLink(agents, site.getLinkIndex());
					if (linkedAgent != null) {
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

	private final CAgent findLink(List<CAgent> agents, int linkIndex) {
		for (CAgent tmp : agents) {
			for (CSite s : tmp.getSites()) {
				if (s.getLinkIndex() == linkIndex) {
					return tmp;
				}
			}
		}
		return null;
	}

	public final CRule buildRule(List<CAgent> left, List<CAgent> right,
			String name, Double activity) {
		return new CRule(buildConnectedComponents(left),
				buildConnectedComponents(right), name, activity);
	}

	public final void setRules(List<CRule> rules) {
		simulationData.setRules(rules);
	}

	public final List<CRule> getRules() {
		return simulationData.getRules();
	}

	public final SimulationData getSimulationData() {
		return simulationData;
	}

	public final synchronized long generateNextAgentId() {
		return agentIdGenerator++;
	}

	public final NameDictionary getNameDictionary() {
		return nameDictionary;
	}

	
	
	public void initialize() {
		CSolution solution = (CSolution) simulationData.getSolution();
		List<CRule> rules = simulationData.getRules();
		Iterator<List<CAgent>> iterator = solution.getAgentMap().values()
				.iterator();
		for (CRule rule : rules) {
			rule.createActivatedRulesList(rules);
		}
			

		while (iterator.hasNext()) {
			for (CAgent agent : iterator.next()) {
				for (CRule rule : rules) {
					for (CConnectedComponent cc : rule.getLeftHandSide()) {
						if (cc != null) {
							if (!agent.isAgentHaveLinkToConnectedComponent(cc)) {
								cc.setInjections(agent);
							}
						}
					}
				}

				for (ObservablesConnectedComponent oCC : simulationData
						.getObservables().getConnectedComponentList())
					if (oCC != null)
						if (!agent.isAgentHaveLinkToConnectedComponent(oCC)) {
							oCC.setInjections(agent);
						}

			}
		}

	}
}
