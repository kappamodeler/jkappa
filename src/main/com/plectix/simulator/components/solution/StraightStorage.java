package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IConnectedComponent;

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
public final class StraightStorage implements IStorage {
	private final Set<CAgent> agentMap = new LinkedHashSet<CAgent>();
	
	StraightStorage() {
	}

	/**
	 * This method lets us add an agent to this storage. Notice that it's not public.
	 * @param agent agent to be added
	 */
	protected final void addAgent(CAgent agent) {
		if (agent != null) {
			agentMap.add(agent);
		}
	}
	
	/**
	 * This method lets us remove an agent from this storage. Notice that it's not public.
	 * @param agent agent to be deleted
	 */
	protected final void removeAgent(CAgent agent) {
		if (agent == null) {
			return;
		}
		agentMap.remove(agent);
	}
	
	@Override
	public final void addConnectedComponent(IConnectedComponent component) {
		if (component == null)
			return;
		for (CAgent agent : component.getAgents()) {
			this.addAgent(agent);
		}
	}

	/**
	 * This method returns all agents from this storage. Method specified for this type of storage only.
	 * @return collection of agents
	 */
	public final Collection<CAgent> getAgents() {
		return agentMap;
	}

	@Override
	public final Collection<IConnectedComponent> split() {
		BitSet bitset = new BitSet(1024);
		List<IConnectedComponent> ccList = new ArrayList<IConnectedComponent>();
		for (CAgent agent : agentMap) {
			int index = (int) agent.getId();
			if (!bitset.get(index)) {
				IConnectedComponent cc = SolutionUtils.getConnectedComponent(agent);
				for (CAgent agentCC : cc.getAgents()) {
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
	public final IConnectedComponent extractComponent(CInjection inj) {
		if (inj.isEmpty()) {
			return null;
		}
		SuperSubstance image = inj.getSuperSubstance();
		if (image == null) {
			IConnectedComponent component = SolutionUtils.getConnectedComponent(inj.getImageAgent());
//			IConnectedComponent component = image.getComponent();
//			for (CAgent agent : component.getAgents()) {
//				this.removeAgent(agent);
//			}
			return component;
		}
		return null;
	}
	
	@Override
	public final void clear() {
		agentMap.clear();
	}

	@Override
	public final boolean isEmpty() {
		return agentMap.isEmpty();
	}
}
