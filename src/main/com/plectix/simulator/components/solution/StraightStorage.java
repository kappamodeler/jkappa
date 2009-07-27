package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.*;

public class StraightStorage implements IStorage {
	private final Set<CAgent> agentMap = new LinkedHashSet<CAgent>();;
	
	// we instantiate this type through UniversalSolution only
	StraightStorage() {
	}

	//---------------ADDERS---------------------------------
	
	// FOR APPLICATION POOL USAGE ONLY!
	protected final void addAgent(CAgent agent) {
		if (agent != null) {
			agentMap.add(agent);
		}
	}
	
	public final void addConnectedComponent(IConnectedComponent component) {
		if (component == null)
			return;
		for (CAgent agent : component.getAgents()) {
			this.addAgent(agent);
		}
	}

	//----------------REMOVERS---------------------------------
	
	// FOR APPLICATION POOL USAGE ONLY!
	protected final void removeAgent(CAgent agent) {
		if (agent == null) {
			return;
		}
		agentMap.remove(agent);
	}

	//-----------------GETTERS----------------------------------
	
	public final Collection<CAgent> getAgents() {
		return Collections.unmodifiableCollection(agentMap);
	}

	public final List<IConnectedComponent> split() {
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

	public static final List<IConnectedComponent> split(List<CAgent> list) {
		BitSet bitset = new BitSet(1024);
		List<IConnectedComponent> ccList = new ArrayList<IConnectedComponent>();
		for (CAgent agent : list) {
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

	//	--------------------------------------------------------------------
	
	/**
	 * This feature used in operation modes 2-3
	 */
	public IConnectedComponent extractComponent(CInjection inj) {
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
	
	public void applyRule(RuleApplicationPool pool) {
		// empty! we have "real-time" adds and removes here
	}
	
	//---------------------CLEANING------------------------
	
	public final void clear() {
		agentMap.clear();
	}
}
