/**
 * 
 */
package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

public class ObservablesConnectedComponent extends CConnectedComponent {
	private String name;
	private String line;
	private int nameID;

	public int getNameID() {
		return nameID;
	}

	private List<Integer> automorphicObservables;
	public static final int NO_INDEX = -1;
	int mainAutomorphismNumber = NO_INDEX;

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

	private final List<Integer> countList = new ArrayList<Integer>();

	public final List<Integer> getCountList() {
		return countList;
	}

	public final void calculateInjection() {
		countList.add(getInjectionsList().size());
	}

	public ObservablesConnectedComponent(List<CAgent> connectedAgents,
			String name, String line, int nameID) {
		super(connectedAgents);
		this.name = name;
		this.line = line;
		this.automorphicObservables = new ArrayList<Integer>();
		this.nameID = nameID;
	}

	public final String getName() {
		return name;
	}
}