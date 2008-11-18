/**
 * 
 */
package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.interfaces.IObservablesComponent;

public class ObservablesConnectedComponent extends CConnectedComponent
		implements IObservablesComponent {
	private String name;
	private String line;
	private int nameID;
	private List<Integer> automorphicObservables;
	public static final int NO_INDEX = -1;
	int mainAutomorphismNumber = NO_INDEX;
	private final List<Integer> countList = new ArrayList<Integer>();

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
		// String ccName = SimulationMain.getSimulationManager().printPartRule(
		// this, 0);
		// return ccName;
		return line;
	}

	public final List<Integer> getCountList() {
		return countList;
	}

	private int lastInjectionsQuantity = -1;

	@Override
	public void updateLastValue() {
		lastInjectionsQuantity = getInjectionsQuantity();
	}

	@Override
	public final void calculate(boolean replaceLast) {
		if (replaceLast)
			countList.set(countList.size() - 1, getInjectionsQuantity());
		else
			countList.add(lastInjectionsQuantity);
	}

	public ObservablesConnectedComponent(List<CAgent> connectedAgents,
			String name, String line, int nameID) {
		super(connectedAgents);
		this.name = name;
		this.line = SimulationMain.getSimulationManager()
				.printPartRule(this, 0);
		// this.line = line;
		this.automorphicObservables = new ArrayList<Integer>();
		this.nameID = nameID;
	}

	public final String getName() {
		return name;
	}

	@Override
	public byte getType() {
		return IObservablesComponent.TYPE_CONNECTED_COMPONENT;
	}

	@Override
	public double getSize() {
		return getInjectionsQuantity();
	}

	@Override
	public String getItem(int index, CObservables obs) {
		if (mainAutomorphismNumber == ObservablesConnectedComponent.NO_INDEX)
			return countList.get(index).toString();
		else
			return obs.getComponentList().get(mainAutomorphismNumber).getItem(
					index, obs);
	}
}