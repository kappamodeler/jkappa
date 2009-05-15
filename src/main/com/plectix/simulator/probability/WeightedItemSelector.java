package com.plectix.simulator.probability;

import java.util.Collection;

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
	 * 
	 * @param changedWeightedItemList a {@link Collection} of items
	 */
	public abstract void updatedItems(Collection<E> changedWeightedItemList);

	/**
	 * Randomly selects an item and returns it
	 * 
	 * @return the item selected
	 */
	public abstract E select();

}