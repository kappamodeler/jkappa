package com.plectix.simulator.probability;

import java.util.Collection;


public interface WeightedItemSelector<E extends WeightedItem> {

	public abstract void updatedItems(Collection<E> changedWeightedItemList);

	public abstract E select();

}