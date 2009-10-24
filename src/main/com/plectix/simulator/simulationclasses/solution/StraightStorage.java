package com.plectix.simulator.simulationclasses.solution;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.staticanalysis.Agent;

/**
 * <p>This one is the StraightStorage. This kind of storage keeps substances
 * as collection of agents and there's no faster way to get quantity of any substance,
 * then simply count it.</p>
 * <p>We use it with two purposes in fact:
 * <br>1) As a part of the solution alternative to SuperStorage. For example, the solution
 * in the first operation mode consist of StraightStorage only.
 * <br>2) As a temporary storage for the StandardRuleApplicationPool.
 * </p>
 */
public final class StraightStorage implements StorageInterface {
	private final Set<Agent> agentMap = new LinkedHashSet<Agent>();
	
	StraightStorage() {
	}

	/**
	 * This method lets us add an agent to this storage. Notice that it's not public.
	 * @param agent agent to be added
	 */
	protected final void addAgent(Agent agent) {
		if (agent != null) {
			agentMap.add(agent);
		}
	}
	
	/**
	 * This method lets us remove an agent from this storage. Notice that it's not public.
	 * @param agent agent to be deleted
	 */
	protected final void removeAgent(Agent agent) {
		if (agent == null) {
			return;
		}
		agentMap.remove(agent);
	}
	
	@Override
	public final void addConnectedComponent(ConnectedComponentInterface component) {
		if (component == null)
			return;
		for (Agent agent : component.getAgents()) {
			this.addAgent(agent);
		}
	}

	/**
	 * This method returns all agents from this storage. Method specified for this type of storage only.
	 * @return collection of agents
	 */
	public final Collection<Agent> getAgents() {
		return agentMap;
	}

	@Override
	public final Collection<ConnectedComponentInterface> split() {
		BitSet bitset = new BitSet(1024);
		List<ConnectedComponentInterface> ccList = new ArrayList<ConnectedComponentInterface>();
		for (Agent agent : agentMap) {
			int index = (int) agent.getId();
			if (!bitset.get(index)) {
				ConnectedComponentInterface cc = SolutionUtils.getConnectedComponent(agent);
				for (Agent agentCC : cc.getAgents()) {
					bitset.set((int) agentCC.getId(), true);
				}
				ccList.add(cc);
			}
		}
		return ccList;
	}

	/**
	 * This feature used in operation modes 2-3
	 */
	@Override
	public final ConnectedComponentInterface extractComponent(Injection injection) {
		if (injection.isEmpty()) {
			return null;
		}
		SuperSubstance image = injection.getSuperSubstance();
		if (image == null) {
			ConnectedComponentInterface component = SolutionUtils.getConnectedComponent(injection.getImageAgent());
			return component;
		}
		return null;
	}
	
	@Override
	public final void clear() {
		agentMap.clear();
	}
}
