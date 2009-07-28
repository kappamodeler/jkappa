package com.plectix.simulator.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.IObservablesConnectedComponent;

/**
 * This class implements observable connected component. In fact, this is connected component
 * from observables list. <br>
 * In general we have kappa file line like
 * <br><br>
 * <code>'observableName' connectedComponents</code>,
 * where : <br><code>observableName</code> - name of this observable
 * <br><code>connectedComponents</code> - list of substances
 * @see CObservables
 * @author avokhmin
 *
 */
public final class ObservablesConnectedComponent extends CConnectedComponent
		implements IObservablesConnectedComponent, Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * If we have several automorphic components in observables list, than
	 * we should fix the only canonical one. NO_INDEX has -1 value for these and the other
	 * value (number of automorphism) for others.  
	 */
	public static final int NO_INDEX = -1;
	
	private int mainAutomorphismNumber = NO_INDEX;
	private final String name;
	private final String line;
	private final int nameID;
	private final List<Integer> automorphicObservables;
	private final List<Long> countList = new ArrayList<Long>();
	private final boolean unique;
	private long lastInjectionsQuantity = -1;
	
	/**
	 * Constructor. Creates ObservablesConnectedComponent from the list of connected agents.<br>
	 * For example, we have kappa file line such as :<br> 
	 * <code>'name' A(x)</code> - This one means unique observable.<br>
	 * <code>'name' A(x),B(x)</code> - This one means observable group, 
	 * and we should create 2 "ObservablesConnectedComponent" with same "name", 
	 * "line", "nameId".
	 * @param connectedAgents list of agents to create component from 
	 * @param name name of current observable
	 * @param line kappa file line becoming this observable.
	 * @param nameID unique id of current observable.
	 * @param unique <tt>false</tt> if this observable connected component is already included in
	 * observables list, otherwise <tt>true</tt>
	 */
	public ObservablesConnectedComponent(List<CAgent> connectedAgents,
			String name, String line, int nameID, boolean unique) {
		super(connectedAgents);
		this.unique = unique;
		this.name = name;
		this.line = line;
		this.automorphicObservables = new ArrayList<Integer>();
		this.nameID = nameID;
	}

	public final int getId() {
		return nameID;
	}

	public final int getMainAutomorphismNumber() {
		return mainAutomorphismNumber;
	}

	public final void setMainAutomorphismNumber(int mainAutomorphismNumber) {
		this.mainAutomorphismNumber = mainAutomorphismNumber;
	}

	public final void addAutomorphicObservables(int automorphicObservable) {
		this.automorphicObservables.add(automorphicObservable);
	}

	public final String getLine() {
		return line;
	}

	public final void updateLastValue() {
		lastInjectionsQuantity = getInjectionsWeight();
	}

	public final void calculate(boolean replaceLast) {
		if (replaceLast)
			countList.set(countList.size() - 1, getInjectionsWeight());
		else
			countList.add(lastInjectionsQuantity);
	}

	public final String getName() {
		return name;
	}

	public final double getCurrentState(CObservables obs) {
		if (this.isUnique())
			return getInjectionsWeight();
		long value = 1;
		for (IObservablesConnectedComponent cc : obs
				.getConnectedComponentList())
			if (cc.getId() == this.getId())
				value *= cc.getInjectionsWeight();
		return value;
	}

	public final String getStringItem(int index, CObservables obs) {
		if (index >= countList.size())
			index = countList.size() - 1;
		if (mainAutomorphismNumber == ObservablesConnectedComponent.NO_INDEX) {
			return countList.get(index).toString();
		} else
			return obs.getComponentList().get(mainAutomorphismNumber).getStringItem(
					index, obs);
	}

	public final boolean isUnique() {
		return unique;
	}

	public final double getItem(int index, CObservables obs) {
		if (mainAutomorphismNumber == ObservablesConnectedComponent.NO_INDEX) {
			return countList.get(index);
		} else
			return obs.getComponentList().get(mainAutomorphismNumber).getItem(
					index, obs);
	}
}