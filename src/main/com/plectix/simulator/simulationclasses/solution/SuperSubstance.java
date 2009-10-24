package com.plectix.simulator.simulationclasses.solution;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.util.string.ConnectedComponentToSmilesString;

/**
 * SuperSubstance is a wrap for connected component with counter.
 * We use this object to represents groups of complexes in higher operation modes.
 * @see OperationMode
 */
public final class SuperSubstance {
	private long quantity = 0;
	private ConnectedComponentInterface component;
	private final String stringHash;
	
	public SuperSubstance(long quant, ConnectedComponentInterface component) {
		this.component = component;
		this.component.setSuperSubstance(this);
		quantity = quant;
		stringHash = ConnectedComponentToSmilesString.getInstance().toUniqueString(this.component);
	}
	
	/**
	 * This method just decreases counter of this SuperSubstance and returns component
	 * of this SuperSubstance. If counter is already zero, then we return null.
	 * @return connected component of this substance
	 */
	public final ConnectedComponentInterface extract() {
		if (isEmpty()) { 
			return null;
		}
		quantity--;
		return component;
	}
	
	/**
	 * We use this method to switch component's pointer to other connected component
	 * (different physical object) with the same hash, i.e. the same structure. 
	 * @param component component to switch pointer to
	 */
	public final void setComponent(ConnectedComponentInterface component) {
		this.component = component;
	}
	
	/**
	 * @return <tt>true</tt> if the counter of this SuperSubstance is zero, otherwise <tt>false</tt>
	 */
	public final boolean isEmpty() {
		return quantity == 0;
	}
	
	/**
	 * This method just increases counter of this SuperSubstance
	 */
	public final void add() {
		quantity++;
	}
	
	/**
	 * This method just increases counter of this SuperSubstance on a given value
	 * @param quantity value to add to counter
	 */
	public final void add(long quantity) {
		this.quantity += quantity;
	}
	
	/**
	 * @return connected component which this SuperSubstance stores
	 */
	public final ConnectedComponentInterface getComponent() {
		return this.component;
	}

	/**
	 * @return counter of this SuperSubstance
	 */
	public final long getQuantity() {
		return quantity;
	}
	
	/**
	 * @return string representation of connected component stored in this SuperSubstance.
	 */
	public final String getHash() {
		return stringHash;
	}
	
	@Override
	public final String toString() {
		return "ss:" + this.component + " * " + quantity;
	}
}
