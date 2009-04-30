/**
 * 
 */
package com.plectix.simulator.probability.skiplist;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.probability.WeightedItem;


public final class SkipListItem<E extends WeightedItem> {
	private E weightedItem = null;
	private List<Double> sums = new ArrayList<Double>();
	private List<SkipListItem<E>> forwardpointers = new ArrayList<SkipListItem<E>>();
	private List<SkipListItem<E>> backPointers = new ArrayList<SkipListItem<E>>();
	
	public SkipListItem() {
		super();
	}
	
	public final int getLevel() {
		return forwardpointers.size();
	}

	public void clean() {
		weightedItem = null;
		sums.clear();
		forwardpointers.clear();
		backPointers.clear();
	}
	
	public final boolean addPointersAndSum(SkipListItem<E> backwardPointer, SkipListItem<E> forwardPointer, double sum) {
		backPointers.add(backwardPointer);
		forwardpointers.add(forwardPointer);
		return sums.add(sum);
	}
	
	public final double getSum(int index) {
		return sums.get(index);
	}
	
	public final void incrementSum(int index, double increment) {
		sums.set(index, sums.get(index) + increment);
	}
	
	public final void resetSum(int index) {
		sums.set(index, 0.0);
	}
	
	public final SkipListItem<E> getForwardPointer(int index) {
		return forwardpointers.get(index);
	}

	public final SkipListItem<E> getBackwardPointer(int index) {
		return backPointers.get(index);
	}
	
	public final void setForwardPointer(int index, SkipListItem<E> skipListItem) {
		forwardpointers.set(index, skipListItem);
	}

	public final void setBackwardPointer(int index, SkipListItem<E> skipListItem) {
		backPointers.set(index, skipListItem);
	}
	
	public final E getWeightedItem() {
		return weightedItem;
	}
	
	public final void setWeightedItem(E weightedItem) {
		this.weightedItem = weightedItem;
	}

}