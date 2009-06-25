package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.simulator.initialization.InjectionsBuilder;

public class SuperStorage implements IStorage {
	private Map<String, SuperSubstance> myStorage = new HashMap<String, SuperSubstance>();
	private final ISolution mySolution;

	SuperStorage(ISolution solution) {
		mySolution = solution;
	}
	
	public boolean tryIncrement(IConnectedComponent component) {
		String hash = component.getHash();
		SuperSubstance previousEntry = myStorage.get(hash);
		if (previousEntry != null) {
			previousEntry.add();
			component.deleteIncomingInjections();
			previousEntry.getComponent().deleteIncomingInjections();
			setInjectionsForSuperSubstance(previousEntry);
			return true;
		}
		return false;
	}
	
	public void addOrEvenIncrement(long quant, IConnectedComponent component) {
		String hash = component.getHash();
		SuperSubstance substanceToAdd = new SuperSubstance(quant, component);
		SuperSubstance previousEntry = myStorage.get(hash);
		if (previousEntry == null) {
			myStorage.put(hash, substanceToAdd);
		} else {
			previousEntry.add(substanceToAdd.getQuantity());
		}
	}
	
	public void addNewSuperSubstance(IConnectedComponent component) {
		myStorage.put(component.getHash(), new SuperSubstance(1, component));
	}
	
	public void clear() {
		myStorage.clear();
	}

	public List<IConnectedComponent> split() {
		List<IConnectedComponent> list = new ArrayList<IConnectedComponent>();
		for (SuperSubstance substance : myStorage.values()) {
			for (int i = 0; i < substance.getQuantity(); i++) {
				list.add(substance.getComponent());
			}
		}
		return list;
	}

	// TODO add a chance that we can take the same component twice!
	public IConnectedComponent extractComponent(CInjection inj) {
		SuperSubstance image = inj.getSuperSubstance();
		if (image != null && !image.isEmpty()) {
			IConnectedComponent component = this.extract(image);
			component.burnIncomingInjections();
			return component;
		} else {
			return null;
		}
	}

	private IConnectedComponent extract(SuperSubstance image) {
		if (!image.isEmpty()) {
			IConnectedComponent component = image.extract();
			image.setComponent(mySolution.cloneConnectedComponent(component));
			// we don't want to choose an injection built to empty SS
			if (!image.isEmpty()) {
				setInjectionsForSuperSubstance(image);
			}
			return component;
		} else {
			// not reachable
			return null;
		}
	}

	private void setInjectionsForSuperSubstance(SuperSubstance substance) {
		(new InjectionsBuilder(mySolution.getKappaSystem())).build(substance);
	}
	
	public void applyRule(RuleApplicationPool pool) {
	}

	public Collection<SuperSubstance> getComponents() {
		return Collections.unmodifiableCollection(myStorage.values());
	}
}
