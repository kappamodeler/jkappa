package com.plectix.simulator.components.solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.simulator.initialization.InjectionsBuilder;

public class SuperStorage implements IStorage {
	private Map<String, SuperSubstance> myStorage = new LinkedHashMap<String, SuperSubstance>();
	private final ISolution mySolution;
	private int agentsLimit = Integer.MAX_VALUE;
	// this one keeps max component's length
	private int maxComponentLength = 0;
	
	SuperStorage(ISolution solution) {
		mySolution = solution;
	}
	
	public boolean tryIncrement(IConnectedComponent component) {
		if (component.getAgentsQuantity() <= maxComponentLength) {
			String hash = component.getHash();
			SuperSubstance previousEntry = myStorage.get(hash);
			if (previousEntry != null) {
				previousEntry.add();
				// there could be some injections here even after the negative update!
				component.deleteIncomingInjections();
				previousEntry.getComponent().incrementIncomingInjections();
				return true;
			}
		}
		return false;
	}
	
	private void refreshMaxLength(IConnectedComponent component) {
		maxComponentLength = Math.max(maxComponentLength, component.getAgentsQuantity());
	}
	
	public void addOrEvenIncrement(long quant, IConnectedComponent component) {
		String hash = component.getHash();
		SuperSubstance substanceToAdd = new SuperSubstance(quant, component);
		SuperSubstance previousEntry = myStorage.get(hash);
		if (previousEntry == null) {
			myStorage.put(hash, substanceToAdd);
			refreshMaxLength(component);
		} else {
			previousEntry.add(substanceToAdd.getQuantity());
		}
	}
	
	public void addNewSuperSubstance(IConnectedComponent component) {
		SuperSubstance s = new SuperSubstance(1, component);
		refreshMaxLength(component);
		myStorage.put(s.getHash(), s);
		component.deleteIncomingInjections();
		setInjectionsForSuperSubstance(s);
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
			} else {
				// TODO do we really want to remove this component from collection?
				myStorage.remove(image.getHash());
				image.getComponent().deleteIncomingInjections();
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
	
	public final int getAgentsLimit() {
		return agentsLimit;
	}
	
	public final void setAgentsLimit(int limit) {
		agentsLimit = limit;
	}
}
