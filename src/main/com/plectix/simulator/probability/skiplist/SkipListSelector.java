package com.plectix.simulator.probability.skiplist;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.plectix.simulator.interfaces.IRandom;
import com.plectix.simulator.probability.WeightedItem;
import com.plectix.simulator.probability.WeightedItemSelector;


/**
 * This class select an item from a collection based on the item's weight. 
 * The data structure is inspired from a Skip List (see {@link http://en.wikipedia.org/wiki/Skip_list}).
 * Though the current implementation differs. 
 * 
 * @author ecemis
 */
public class SkipListSelector implements WeightedItemSelector {
	private static final double P = 0.5;
	
	private double totalWeight = 0.0;
	
	/** currentLevel is -1 if there is no item stored */
	private int currentLevel = -1;
	
	private final SkipListItem head = new SkipListItem();
	private final SkipListItem tail = new SkipListItem();

	private final Map<WeightedItem, SkipListItem> weightedItemToSkipListItemMap = new HashMap<WeightedItem, SkipListItem>();
	
	private IRandom random = null;
	
	public SkipListSelector(IRandom random) {
		super();
		this.random = random;
	}

	public final WeightedItem select() {
		if (currentLevel == -1) {
			return null;
		}
		
		return search(head, currentLevel, totalWeight * random.getDouble()).getWeightedItem();
	}

	public void updatedItems(Collection<WeightedItem> changedWeightedItemList) {
		for (WeightedItem weightedItem : changedWeightedItemList) {
			final SkipListItem skipListItem = weightedItemToSkipListItemMap.get(weightedItem);
			if (skipListItem == null) {
				// this item is new, we need to add
				weightedItemToSkipListItemMap.put(weightedItem, addWeightedItem(weightedItem));
			} else {
				// this item is old... what is the new weight?
				final double newWeight = weightedItem.getWeight();
				if (newWeight <= 0.0) {
					deleteItem(skipListItem);
					weightedItemToSkipListItemMap.remove(weightedItem);
				} else {
					updateWeight(skipListItem, newWeight);
				}
			}
		}
	}

	private final void deleteItem(SkipListItem skipListItem) {
		final double weightDiff = -skipListItem.getSum(0);
		final int level = skipListItem.getLevel();
		
		for (int i= 0; i < level; i++) {
			final SkipListItem previousItemAtThatLevel = skipListItem.getBackwardPointer(i);
			final SkipListItem nextItemAtThatItem = skipListItem.getForwardPointer(i);
			previousItemAtThatLevel.setForwardPointer(i, nextItemAtThatItem);
			nextItemAtThatItem.setBackwardPointer(i, previousItemAtThatLevel);
			if (i != 0) {
				nextItemAtThatItem.incrementSum(i, skipListItem.getSum(i) + weightDiff);
			}
		}
		
		// let's clean this item to make sure that we'll have NullPointerException if there are any bugs
		skipListItem.clean();
	}

	private final void updateWeight(SkipListItem skipListItem, double newWeight) {
		final double weightDiff = newWeight - skipListItem.getSum(0);
		
		int lastUpdatedLevel = skipListItem.getLevel() - 1;
		for (int i= 0; i <= lastUpdatedLevel; i++) {
			skipListItem.incrementSum(i, weightDiff);
		}
		
		while (lastUpdatedLevel < currentLevel) {
			final SkipListItem nextItemAtThatLevel = skipListItem.getForwardPointer(lastUpdatedLevel);
			for (int i= lastUpdatedLevel+1; i < nextItemAtThatLevel.getLevel(); i++) {
				nextItemAtThatLevel.incrementSum(i, weightDiff);
			}
			lastUpdatedLevel = nextItemAtThatLevel.getLevel()-1;
			skipListItem = nextItemAtThatLevel;
		}
	}

	private final int getRandomLevel() {
		int ret = 0;
		while (random.getDouble() < P && ret <= currentLevel) {
			ret++;
		}
		return ret;
	}
	
	private final SkipListItem addWeightedItem(WeightedItem weightedItem) {
		final SkipListItem newItem = new SkipListItem();
		final double newWeight = weightedItem.getWeight();

		totalWeight += newWeight;
		newItem.setWeightedItem(weightedItem);
		
		int level = getRandomLevel();
		for (int i = 0; i < level + 1; i++) {
			if (i <= currentLevel) {
				final SkipListItem oldLastItem = tail.getBackwardPointer(i);
				oldLastItem.setForwardPointer(i, newItem);
				tail.setBackwardPointer(i, newItem);
				newItem.addPointersAndSum(oldLastItem, tail, tail.getSum(i) + newWeight);
				tail.resetSum(i);
			} else {
				// creating a new level:
				head.addPointersAndSum(null, newItem, 0.0);
				tail.addPointersAndSum(newItem, null, 0.0);
				newItem.addPointersAndSum(head, tail, totalWeight);
			}
		}
		
		for (int i= level+1; i < currentLevel +1; i++) {
			tail.incrementSum(i, newWeight);
		}
		
		// update current level here, not above!
		if (level > currentLevel) {
			currentLevel = level;
		}
		
		return newItem;
	}
	
	private final SkipListItem search(SkipListItem skipListItem, int level, double randomValue) {
		// Originally I wrote this method with tail-recursion but then rewrote it with iteration to have it more efficient
		while (true) {
			if (randomValue == 0) {
				return skipListItem.getForwardPointer(0);
			}

			SkipListItem nextItemAtThatLevel = skipListItem.getForwardPointer(level);
			while (nextItemAtThatLevel == tail) {
				level--;
				nextItemAtThatLevel = skipListItem.getForwardPointer(level);
			}

			while (nextItemAtThatLevel.getSum(level) > randomValue) {
				if (level == 0) {
					return nextItemAtThatLevel;
				}
				level--;
				nextItemAtThatLevel = skipListItem.getForwardPointer(level);
			}

			randomValue -= nextItemAtThatLevel.getSum(level);
			skipListItem = nextItemAtThatLevel;
		}
	}

}
