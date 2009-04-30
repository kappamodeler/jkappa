package com.plectix.simulator.probability;

import java.util.Collection;


public interface WeightedItemSelector {

	public abstract void updatedItems(Collection<WeightedItem> changedWeightedItemList);

	public abstract WeightedItem select();

}