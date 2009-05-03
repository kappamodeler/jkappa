package com.plectix.simulator.probability.avl.processors;

import com.plectix.simulator.probability.WeightedItem;
import com.plectix.simulator.probability.avl.WeightedNode;
import com.plectix.simulator.probability.avl.Orientation;

public interface RotatePerformer {
	public <E extends WeightedItem> WeightedNode<E> perform(WeightedNode<E> root, 
			Orientation o);
}
