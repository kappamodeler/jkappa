/**
 * 
 */
package com.plectix.simulator.probability.skiplist;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.probability.WeightedItem;


public final class SkipListItem {
	private WeightedItem weightedItem = null;
	private List<Double> sums = new ArrayList<Double>();
	private List<SkipListItem> forwardpointers = new ArrayList<SkipListItem>();
	private List<SkipListItem> backPointers = new ArrayList<SkipListItem>();
	
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
	
	public final boolean addPointersAndSum(SkipListItem backwardPointer, SkipListItem forwardPointer, double sum) {
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
	
	public final SkipListItem getForwardPointer(int index) {
		return forwardpointers.get(index);
	}

	public final SkipListItem getBackwardPointer(int index) {
		return backPointers.get(index);
	}
	
	public final void setForwardPointer(int index, SkipListItem skipListItem) {
		forwardpointers.set(index, skipListItem);
	}

	public final void setBackwardPointer(int index, SkipListItem skipListItem) {
		backPointers.set(index, skipListItem);
	}
	
	public final WeightedItem getWeightedItem() {
		return weightedItem;
	}
	
	public final void setWeightedItem(WeightedItem weightedItem) {
		this.weightedItem = weightedItem;
	}

}