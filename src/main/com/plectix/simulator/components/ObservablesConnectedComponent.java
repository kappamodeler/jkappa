/**
 * 
 */
package com.plectix.simulator.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.plectix.simulator.interfaces.*;

public final class ObservablesConnectedComponent extends CConnectedComponent
		implements IObservablesConnectedComponent, Serializable {
	public static final int NO_INDEX = -1;
	
	private int mainAutomorphismNumber = NO_INDEX;
	private final String name;
	private final String line;
	private final int nameID;
	private final List<Integer> automorphicObservables;
	private final List<Integer> countList = new ArrayList<Integer>();
	private final boolean unique;
	private int lastInjectionsQuantity = -1;
	
	public ObservablesConnectedComponent(List<IAgent> connectedAgents,
			String name, String line, int nameID, boolean unique) {
		super(connectedAgents);
		this.unique = unique;
		this.name = name;
		this.line = line;
		this.automorphicObservables = new ArrayList<Integer>();
		this.nameID = nameID;
	}

	public final int getNameID() {
		return nameID;
	}

	public final int getMainAutomorphismNumber() {
		return mainAutomorphismNumber;
	}

	public final void setMainAutomorphismNumber(int mainAutomorphismNumber) {
		this.mainAutomorphismNumber = mainAutomorphismNumber;
	}

	public final List<Integer> getAutomorphicObservables() {
		return Collections.unmodifiableList(automorphicObservables);
	}

	public final void addAutomorphicObservables(int automorphicObservable) {
		this.automorphicObservables.add(automorphicObservable);
	}

	public final String getLine() {
		return line;
	}

	public final List<Integer> getCountList() {
		return Collections.unmodifiableList(countList);
	}

	public final void updateLastValue() {
		lastInjectionsQuantity = getInjectionsList().size();
	}

	public final void calculate(boolean replaceLast) {
		if (replaceLast)
			countList.set(countList.size() - 1, getInjectionsList().size());
		else
			countList.add(lastInjectionsQuantity);
	}

	public final String getName() {
		return name;
	}

	public final double getSize(CObservables obs) {
		if (this.isUnique())
			return getInjectionsList().size();
		long value = 1;
		for (IObservablesConnectedComponent cc : obs
				.getConnectedComponentList())
			if (cc.getNameID() == this.getNameID())
				value *= cc.getInjectionsList().size();
		return value;
	}

	public final String getItem(int index, CObservables obs) {
		if (index >= countList.size())
			index = countList.size() - 1;
		if (mainAutomorphismNumber == ObservablesConnectedComponent.NO_INDEX) {
			return countList.get(index).toString();
		} else
			return obs.getComponentList().get(mainAutomorphismNumber).getItem(
					index, obs);
	}

	public final boolean isUnique() {
		return unique;
	}

	public final long getValue(int index, CObservables obs) {
		if (mainAutomorphismNumber == ObservablesConnectedComponent.NO_INDEX) {
			return countList.get(index);
		} else
			return obs.getComponentList().get(mainAutomorphismNumber).getValue(
					index, obs);
	}
}