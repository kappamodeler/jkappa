package com.plectix.simulator.staticanalysis.speciesenumeration;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;

public final class SpeciesEnumeration {
	private final Map<String, List<AbstractAgent>> localViews;
	private final Map<String, Species> species = new LinkedHashMap<String, Species>();
	private boolean isUnbounded = false;

	public SpeciesEnumeration(Map<String, List<AbstractAgent>> localViews) {
		this.localViews = localViews;
	}

	public final Map<String, Species> getSpecies() {
		return species;
	}

	public final void enumerate() {
		Map<String, List<AbstractAgent>> availableListOfView = new LinkedHashMap<String, List<AbstractAgent>>();
		for (String i : localViews.keySet()) {
			List<AbstractAgent> newlist = new LinkedList<AbstractAgent>();
			newlist = localViews.get(i);
			availableListOfView.put(i, newlist);
			for (AbstractAgent view : newlist) {
				buildSpeciesFromRoot(availableListOfView, view);
			}
		}
	}

	private final void buildSpeciesFromRoot(
			Map<String, List<AbstractAgent>> availableListOfViews,
			AbstractAgent root) {
		Species first = new Species(availableListOfViews, root);
		Stack<Species> children = new Stack<Species>();
		children.add(first);

		// TODO stack -> map<String,Species>
		while (!children.isEmpty()) {
			Species initiative = children.pop();
			if (initiative.isComplete()) {
				if (species.get(initiative.getHashCode()) == null) {
					species.put(initiative.getHashCode(), initiative);
				}
			} else {
				List<Species> list = initiative.propagate();
				if (list != null)
					children.addAll(list);
			}

		}
	}

	public final void unbound() {
		isUnbounded = true;
	}

	public boolean isUnbounded() {
		return isUnbounded;
	}
}
