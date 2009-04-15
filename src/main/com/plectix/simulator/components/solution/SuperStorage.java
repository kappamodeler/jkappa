package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.string.ConnectedComponentToSmilesString;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.simulator.initialization.InjectionsBuilder;

public class SuperStorage implements IStorage {
	private Map<String, SuperSubstance> myStorage = new TreeMap<String, SuperSubstance>();
	
//	private List<SuperSubstance> myComponents = new ArrayList<SuperSubstance>();

	// private final HashMap<Long, CAgent> agentMap = new HashMap<Long,
	// CAgent>();;
	private final ISolution mySolution;

	SuperStorage(ISolution solution) {
		mySolution = solution;
	}

	public void tryAdd(SuperSubstance substanceToAdd) {
		String hash = ConnectedComponentToSmilesString.getInstance()
			.toUniqueString(substanceToAdd.getComponent());
		if (myStorage.get(hash) == null) {
			myStorage.put(hash, substanceToAdd);
		}
	}
	
	public void addAndReplace(SuperSubstance substanceToAdd) {
		String hash = ConnectedComponentToSmilesString.getInstance()
			.toUniqueString(substanceToAdd.getComponent());
		SuperSubstance previousEntry = myStorage.get(hash);
		if (previousEntry == null) {
			myStorage.put(hash, substanceToAdd);
		} else {
			previousEntry.add(substanceToAdd.getQuantity());
		}
	}
	
	public void addConnectedComponent(IConnectedComponent component) {
		tryAdd(new SuperSubstance(component));
	}

	public void removeConnectedComponent(IConnectedComponent component) {
		String hash = ConnectedComponentToSmilesString.getInstance().toUniqueString(component);
		SuperSubstance substance = myStorage.remove(hash);
		substance.extract();
	}

	public void clear() {
		myStorage.clear();
	}

	public List<IConnectedComponent> split() {
		List<IConnectedComponent> list = new ArrayList<IConnectedComponent>();
		for (SuperSubstance substance : myStorage.values()) {
			list.add(substance.getComponent());
		}
		return list;
	}

	public IConnectedComponent extractComponent(CInjection inj) {
		SuperSubstance image = inj.getSuperSubstance();
		if (image != null && !image.isEmpty()) {
			IConnectedComponent component = this.extract(image);
			component.burnInjections();
			return component;
		} else {
			return null;
		}
	}

	private IConnectedComponent extract(SuperSubstance image) {
		if (!image.isEmpty()) {
			IConnectedComponent component = image.extract();
			image.setComponent(mySolution.cloneConnectedComponent(component));
			setInjectionsForTheRestOfSubstance(image);
			return component;
		} else {
			return null;
		}
	}

	private void setInjectionsForTheRestOfSubstance(SuperSubstance substance) {
		(new InjectionsBuilder(mySolution.getKappaSystem())).build(substance);
	}
	
	public void applyRule(RuleApplicationPool pool) {
	}

	public Collection<SuperSubstance> getComponents() {
//		return Collections.unmodifiableList(myComponents);
		return Collections.unmodifiableCollection(myStorage.values());
	}
}
