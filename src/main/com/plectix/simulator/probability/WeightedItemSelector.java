package com.plectix.simulator.probability;

import java.util.Collection;
import java.util.Set;

/**
 * Interface to select a {@link WeightedItem} from a {@link Collection}
 * of WeightedItems, where the probability of an item's selection depends
 * on that items weight. 
 * 
 * @author ecemis
 * 
 * @param <E> a class implementing items with weight
 */
public interface WeightedItemSelector<E extends WeightedItem> {

	/**
	 * maybe we don't need this one?
	 * @param changedWeightedItemList a {@link Collection} of items
	 */
	public abstract void updatedItems(Collection<E> changedWeightedItemList);

	/**
	 * Randomly selects an item and returns it
	 * 
	 * @return the item selected
	 */
	public abstract E select();

	/**
	 * 
	 * @return collection with all items
	 */
	public abstract Set<E> asSet();

	/**
	 * Check for changes in item
	 * @param changed item
	 */
	public abstract void updatedItem(E item);

	/**
	 * 
	 * @return total weight of all items
	 */
	public abstract double getTotalWeight();
}