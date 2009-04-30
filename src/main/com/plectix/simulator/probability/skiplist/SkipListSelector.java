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
public class SkipListSelector<E extends WeightedItem> implements WeightedItemSelector<E> {
	private static final double P = 0.5;
	
	private double totalWeight = 0.0;
	
	/** currentLevel is -1 if there is no item stored */
	private int currentLevel = -1;
	
	private final SkipListItem<E> head = new SkipListItem<E>();
	private final SkipListItem<E> tail = new SkipListItem<E>();

	private final Map<E, SkipListItem<E>> weightedItemToSkipListItemMap = new HashMap<E, SkipListItem<E>>();
	
	private IRandom random = null;
	
	public SkipListSelector(IRandom random) {
		super();
		this.random = random;
	}

	public final E select() {
		if (currentLevel == -1) {
			return null;
		}
		
		return search(head, currentLevel, totalWeight * random.getDouble()).getWeightedItem();
	}

	public void updatedItems(Collection<E> changedWeightedItemList) {
		for (E weightedItem : changedWeightedItemList) {
			final SkipListItem<E> skipListItem = weightedItemToSkipListItemMap.get(weightedItem);
			if (skipListItem == null) {
				// this item is new, we need to add
				SkipListItem<E> newItem = addWeightedItem(weightedItem);
				if (newItem != null) {
					weightedItemToSkipListItemMap.put(weightedItem, newItem);
				}
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

	private final void deleteItem(SkipListItem<E> skipListItem) {
		final double weightDiff = -skipListItem.getSum(0);
		final int level = skipListItem.getLevel();
		
		for (int i= 0; i < level; i++) {
			final SkipListItem<E> previousItemAtThatLevel = skipListItem.getBackwardPointer(i);
			final SkipListItem<E> nextItemAtThatItem = skipListItem.getForwardPointer(i);
			previousItemAtThatLevel.setForwardPointer(i, nextItemAtThatItem);
			nextItemAtThatItem.setBackwardPointer(i, previousItemAtThatLevel);
			if (i != 0) {
				nextItemAtThatItem.incrementSum(i, skipListItem.getSum(i) + weightDiff);
			}
		}
		
		// let's clean this item to make sure that we'll have NullPointerException if there are any bugs
		skipListItem.clean();
	}

	private final void updateWeight(SkipListItem<E> skipListItem, double newWeight) {
		final double weightDiff = newWeight - skipListItem.getSum(0);
		
		int lastUpdatedLevel = skipListItem.getLevel() - 1;
		for (int i= 0; i <= lastUpdatedLevel; i++) {
			skipListItem.incrementSum(i, weightDiff);
		}
		
		while (lastUpdatedLevel < currentLevel) {
			final SkipListItem<E> nextItemAtThatLevel = skipListItem.getForwardPointer(lastUpdatedLevel);
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
	
	private final SkipListItem<E> addWeightedItem(E weightedItem) {
		final double newWeight = weightedItem.getWeight();
		if (newWeight <= 0) {
			return null;
		}

		final SkipListItem<E> newItem = new SkipListItem<E>();
		totalWeight += newWeight;
		newItem.setWeightedItem(weightedItem);
		
		int level = getRandomLevel();
		for (int i = 0; i < level + 1; i++) {
			if (i <= currentLevel) {
				final SkipListItem<E> oldLastItem = tail.getBackwardPointer(i);
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
	
	private final SkipListItem<E> search(SkipListItem<E> skipListItem, int level, double randomValue) {
		// Originally I wrote this method with tail-recursion but then rewrote it with iteration to have it more efficient
		while (true) {
			if (randomValue == 0) {
				return skipListItem.getForwardPointer(0);
			}

			SkipListItem<E> nextItemAtThatLevel = skipListItem.getForwardPointer(level);
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
