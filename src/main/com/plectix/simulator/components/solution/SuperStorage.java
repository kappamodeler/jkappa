package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.interfaces.ISolution;

public class SuperStorage implements IStorage {
	private Map<String, Set<SuperSubstance>> myStorage = new TreeMap<String, Set<SuperSubstance>>();

	private List<SuperSubstance> myComponents = new ArrayList<SuperSubstance>();

	// private final HashMap<Long, CAgent> agentMap = new HashMap<Long,
	// CAgent>();;
	private final ISolution mySolution;

	SuperStorage(ISolution solution) {
		mySolution = solution;
	}

	public void addSuperSubstance(SuperSubstance substanceToAdd) {
		myComponents.add(substanceToAdd);
		String hash = substanceToAdd.getComponent().getHash();
		Set<SuperSubstance> set = myStorage.get(hash);
		if (set == null) {
			set = new HashSet<SuperSubstance>();
			set.add(substanceToAdd);
			myComponents.add(substanceToAdd);
			myStorage.put(hash, set);
		} else {
			boolean found = false;
			for (SuperSubstance substance : set) {
				if (substance.matches(substanceToAdd.getComponent())) {
					substance.add();
					found = true;
					break;
				}
			}
			if (!found) {
				set.add(substanceToAdd);
				myComponents.add(substanceToAdd);
			}
		}
	}
	
	public void addConnectedComponent(IConnectedComponent component) {
		addSuperSubstance(new SuperSubstance(component));
	}

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

	public void clear() {
		myStorage.clear();
	}

	public List<IConnectedComponent> split() {
		List<IConnectedComponent> list = new ArrayList<IConnectedComponent>();
		for (Set<SuperSubstance> set : myStorage.values()) {
			for (SuperSubstance substance : set) {
				list.add(substance.getComponent());
			}
		}
		return list;
	}

	public IConnectedComponent extractComponent(CInjection inj) {
		SuperSubstance image = inj.getSuperSubstance();
		if (image != null) {
			IConnectedComponent component = this.extract(image);
			return component;
		} else {
			return null;
		}
	}

	private IConnectedComponent extract(SuperSubstance image) {
		if (!image.isEmpty()) {
			IConnectedComponent component = image.extract();
			image.setComponent(mySolution.cloneConnectedComponent(component));
			return component;
		} else {
			return null;
		}
	}

	public void applyRule(RuleApplicationPool pool) {
	}

	public List<SuperSubstance> getComponents() {
		return Collections.unmodifiableList(myComponents);
	}
}
