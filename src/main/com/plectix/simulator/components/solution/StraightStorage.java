package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.action.*;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.util.Converter;

public class StraightStorage implements IStorage {
	private final HashMap<Long, IAgent> agentMap = new HashMap<Long, IAgent>();;
	
	// we instantiate this type through UniversalSolution only
	StraightStorage() {
	}

	//---------------ADDERS---------------------------------
	
	// FOR APPLICATION POOL USAGE ONLY!
	protected final void addAgent(IAgent agent) {
		if (agent != null) {
			long key = agent.getHash();
			if (agent != agentMap.get(key)) {
				agentMap.put(key, agent);
			}
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
	
	// FOR APPLICATION POOL USAGE ONLY!
	protected final void removeAgent(IAgent agent) {
		if (agent == null) {
			return;
		}
		agentMap.remove(agent.getHash());
	}
	
//	public final void removeConnectedComponent(IConnectedComponent component) {
//		if (component == null)
//			return;
//		for (IAgent agent : component.getAgents()) {
//			this.removeAgent(agent);
//		}
//	}

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

	//	--------------------------------------------------------------------
	
	public IConnectedComponent extractComponent(IInjection inj) {
		if (inj.isEmpty()) {
			return null;
		}
		SuperSubstance image = inj.getSuperSubstance();
		if (image == null) {
			IConnectedComponent component = SolutionUtils.getConnectedComponent(inj.getImageAgent());
			for (IAgent agent : component.getAgents()) {
				this.removeAgent(agent);
			}
			return component;
		} else {
			return null;
		}
	}
	
	public void applyRule(RuleApplicationPool pool) {
		// empty! we have "real-time" adds and removes here
	}
	
	//---------------------CLEANING------------------------
	
	public final void clear() {
		agentMap.clear();
	}
	
//	public String toString() {
//		TreeMap<String, Long> map = new TreeMap<String, Long>();
//		StringBuffer sb = new StringBuffer();
//		for (IConnectedComponent component : split()) {
//			sb.append("%init " + 1 + " * " + component + "\n");
//		}
//		return sb.toString();
//	}
}
