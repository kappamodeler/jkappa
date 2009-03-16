package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.simulator.KappaSystem;

/*package*/ class SuperStorage implements IStorage {
	private Map<String, Set<SuperSubstance>> myStorage = 
		new TreeMap<String, Set<SuperSubstance>>();
	
	private final HashMap<Long, IAgent> agentMap = new HashMap<Long, IAgent>();;
	
	SuperStorage() {
	}

	@Override
	public void addConnectedComponent(IConnectedComponent component) {
		String hash = component.getHash();
		Set<SuperSubstance> set = myStorage.get(hash);
		if (set == null) {
			set = new HashSet<SuperSubstance>();
			set.add(new SuperSubstance(component));
			myStorage.put(hash, set);
		} else {
			boolean found = false;
			for (SuperSubstance substance : set) {
				if (substance.matches(component)) {
					substance.add();
					found = true;
					break;
				}
			}
			if (!found) {
				set.add(new SuperSubstance(component));
			}
		}
		for (IAgent agent : component.getAgents()) {
			agentMap.put(agent.getHash(), agent);
		}
	}

	@Override
	public void removeConnectedComponent(IConnectedComponent component) {
		String hash = component.getHash();
		Set<SuperSubstance> set = myStorage.get(hash);
		if (set != null) {
			for (SuperSubstance substance : set) {
				if (substance.matches(component)) {
					substance.extract();
					break;
				}
			}
		}
	}
	
	@Override
	public void clear() {
		myStorage.clear();
	}

	@Override
	public List<IConnectedComponent> split() {
		List<IConnectedComponent> list = new ArrayList<IConnectedComponent>();
		for (Set<SuperSubstance> set : myStorage.values()) {
			for (SuperSubstance substance : set) {
				list.add(substance.getConnectedComponent());
			}
		}
		return list;
	}

	@Override
	public StandardRuleApplicationPool prepareRuleApplicationPool(List<IInjection> injections) {
		return new StandardRuleApplicationPool(injections);
	}

	@Override
	public void applyRule(RuleApplicationPool pool) {
//		for (IConnectedComponent component : pool.getInitialComponents()) {
//			this.removeConnectedComponent(component);
//		}
//		for (IConnectedComponent component : pool.getCurrentComponents()) {
//			this.addConnectedComponent(component);
//		}
	}

	@Override
	public Collection<IAgent> getAgents() {
		return agentMap.values();
	}
}
