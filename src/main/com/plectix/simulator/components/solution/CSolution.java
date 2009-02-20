package com.plectix.simulator.components.solution;

import java.io.Serializable;
import java.util.*;

import com.plectix.simulator.interfaces.*;

@SuppressWarnings("serial")
public final class CSolution extends PhysicalSolution implements Serializable {
	private final HashMap<Long, IAgent> agentMap = new HashMap<Long, IAgent>();;
	
	// we instantiate this type through UniversalSolution only
	CSolution() {
	}

	//---------------ADDERS---------------------------------
	
	// method for add-action only!
	public final void addAgent(IAgent agent) {
		if (agent != null) {
			long key = agent.getHash();
			agentMap.put(key, agent);
		}
	}
	
	public final void addConnectedComponent(IConnectedComponent component) {
		if (component == null)
			return;
		for (IAgent agent : component.getAgents()) {
			this.addAgent(agent);
		}
	}

	//----------------REMOVERS---------------------------------
	
	// method for delete-action only!
	public final void removeAgent(IAgent agent) {
		if (agent == null) {
			return;
		}
		agentMap.remove(agent.getHash());
	}

	//-----------------GETTERS----------------------------------
	
	public final Collection<IAgent> getAgents() {
		return Collections.unmodifiableCollection(agentMap.values());
	}

	public final List<IConnectedComponent> split() {
		BitSet bitset = new BitSet(1024);
		List<IConnectedComponent> ccList = new ArrayList<IConnectedComponent>();
		for (IAgent agent : agentMap.values()) {
			int index = (int) agent.getId();
			if (!bitset.get(index)) {
				IConnectedComponent cc = SolutionUtils.getConnectedComponent(agent);
				for (IAgent agentCC : cc.getAgents()) {
					bitset.set((int) agentCC.getId(), true);
				}
				ccList.add(cc);
			}
		}
		return ccList;
	}

	//---------------------CLEANING------------------------
	
	public final void clearAgents() {
		agentMap.clear();
	}
}
