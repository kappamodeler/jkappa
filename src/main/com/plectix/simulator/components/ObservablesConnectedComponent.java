/**
 * 
 */
package com.plectix.simulator.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.plectix.simulator.interfaces.*;

/**
 * This class describes observables of ConnectedComponent storage.
 * In general we have kappa file line like
 * <br><br>
 * <code>'observableName' connectedComponents</code>,
 * where 
 * <br>
 * <li><code>observableName</code> - name of this observable</li>
 * <li><code>connectedComponents</code> - list of substances</li>
 * @author avokhmin
 *
 */
public final class ObservablesConnectedComponent extends CConnectedComponent
		implements IObservablesConnectedComponent, Serializable {
	private static final long serialVersionUID = 1L;

	public static final int NO_INDEX = -1;
	
	private int mainAutomorphismNumber = NO_INDEX;
	private final String name;
	private final String line;
	private final int nameID;
	private final List<Integer> automorphicObservables;
	private final List<Integer> countList = new ArrayList<Integer>();
	private final boolean unique;
	private int lastInjectionsQuantity = -1;
	
	/**
	 * Constructor ObservablesConnectedComponent with <b>connectedAgents</b> agents.<br>
	 * For example, we have kappa file line such as :<br> 
	 * <code>'name' A(x)</code> - This one means unique observable.<br>
	 * <code>'name' A(x),B(x)</code> - This one means observable group, 
	 * and we should create 2 "ObservablesConnectedComponent" with same "name", 
	 * "line", "nameId".
	 * @param connectedAgents agents we want to add
	 * @param name name of current observable
	 * @param line full string line from kappa file.
	 * @param nameID unique id of current observable.
	 * @param unique <tt>true</tt> if current observable connectedComponent not 
	 * include to group, otherwise <tt>false</tt>
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

	public final int getNameID() {
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

	public final double getCurrentState(CObservables obs) {
		if (this.isUnique())
			return getInjectionsList().size();
		long value = 1;
		for (IObservablesConnectedComponent cc : obs
				.getConnectedComponentList())
			if (cc.getNameID() == this.getNameID())
				value *= cc.getInjectionsList().size();
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

	public final long getLongItem(int index, CObservables obs) {
		if (mainAutomorphismNumber == ObservablesConnectedComponent.NO_INDEX) {
			return countList.get(index);
		} else
			return obs.getComponentList().get(mainAutomorphismNumber).getLongItem(
					index, obs);
	}
}