/**
 * 
 */
package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.interfaces.*;

public class ObservablesConnectedComponent extends CConnectedComponent
		implements IObservablesConnectedComponent {
	public static int COUNTER = 0;
	private String name;
	private String line;
	private int nameID;
	private List<Integer> automorphicObservables;
	public static final int NO_INDEX = -1;
	int mainAutomorphismNumber = NO_INDEX;
	private final List<Integer> countList = new ArrayList<Integer>();
	private boolean unique = true;

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
		return automorphicObservables;
	}

	public final void addAutomorphicObservables(int automorphicObservable) {
		this.automorphicObservables.add(automorphicObservable);
	}

	public final String getLine() {
		return line;
	}

	public final List<Integer> getCountList() {
		return countList;
	}

	private int lastInjectionsQuantity = -1;

	public void updateLastValue() {
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

	public double getSize() {
		return getInjectionsList().size();
	}

	public String getItem(int index, CObservables obs) {
		if (mainAutomorphismNumber == ObservablesConnectedComponent.NO_INDEX) {
			if (countList.get(index) == 0) {
				COUNTER++;
			}
			return countList.get(index).toString();
		} else
			return obs.getComponentList().get(mainAutomorphismNumber).getItem(
					index, obs);
	}

	@Override
	public boolean isUnique() {
		return unique;
	}

	@Override
	public long getValue(int index, CObservables obs) {
		if (mainAutomorphismNumber == ObservablesConnectedComponent.NO_INDEX) {
			if (countList.get(index) == 0) {
				COUNTER++;
			}
			return countList.get(index);
		} else
			return obs.getComponentList().get(mainAutomorphismNumber).getValue(
					index, obs);
	}
}