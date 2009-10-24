/**
 * 
 */
package com.plectix.simulator.simulationclasses.probability;

/**
 * Interface to be used with {@link WeightedItemSelector} class.
 * 
 * @author ecemis
 * @see WeightedItemSelector
 */
public interface WeightedItem {
	
	/**
	 * Returns the weight of this item. An Item with  
	 * negative or zero weight should not be selected by the
	 * {@link com.plectix.simulator.simulationclasses.probability.WeightedItemSelector}.
	 * 
	 * @return the weight of this item
	 */
	public double getWeight();
	
}