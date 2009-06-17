package com.plectix.simulator.probability.skiplist;

import java.util.Collection;
import java.util.Collections;
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

	public final void updatedItems(Collection<E> changedWeightedItemList) {
		for (E weightedItem : changedWeightedItemList) {
			updatedItem(weightedItem);
		}
	}

	public final void updatedItem(E weightedItem) {
		final SkipListItem<E> skipListItem = weightedItemToSkipListItemMap.get(weightedItem);
		if (skipListItem == null) {
			if (weightedItem.getWeight() > 0.0) {
				// this item is new, we need to add
				weightedItemToSkipListItemMap.put(weightedItem, addWeightedItem(weightedItem));
			}
		} else {
			// this item is old... what is the new weight?
			final double newWeight = weightedItem.getWeight();
			if (newWeight <= 0.0) {
				deleteItem(skipListItem);
				// let's clean this item to make sure that we'll have NullPointerException if there are any bugs
				skipListItem.clear();
				weightedItemToSkipListItemMap.remove(weightedItem);
			} else {
				updateWeight(skipListItem, newWeight);
			}
		}
	}
	
	private final SkipListItem<E> addWeightedItem(E weightedItem) {
		final double newWeight = weightedItem.getWeight();
		totalWeight += newWeight;
		
		final SkipListItem<E> newItem = new SkipListItem<E>();
		newItem.setWeightedItem(weightedItem);
		
		int level = getRandomLevel();
		for (int i = 0; i <= level; i++) {
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
		
		for (int i= level+1; i <= currentLevel ; i++) {
			tail.incrementSum(i, newWeight);
		}
		
		// update current level here, not above!
		if (level > currentLevel) {
			currentLevel = level;
		}
		
		return newItem;
	}
	
	public double getTotalWeight() {
		return totalWeight;
	}
	
	public Collection<E> asCollection() {
		return Collections.unmodifiableCollection(weightedItemToSkipListItemMap.keySet());
	}
	
	private final void deleteItem(SkipListItem<E> skipListItem) {
		final double weightDiff = -skipListItem.getSum(0);
		totalWeight += weightDiff;
		
		int lastUpdatedLevel = skipListItem.getLevel() - 1;

		// System.err.println(dumpLevels() + " --> deleting an item with level " + lastUpdatedLevel);
		
		for (int i= 0; i <= lastUpdatedLevel; i++) {
			final SkipListItem<E> previousItemAtThatLevel = skipListItem.getBackwardPointer(i);
			final SkipListItem<E> nextItemAtThatLevel = skipListItem.getForwardPointer(i);
			previousItemAtThatLevel.setForwardPointer(i, nextItemAtThatLevel);
			nextItemAtThatLevel.setBackwardPointer(i, previousItemAtThatLevel);
			if (i != 0) {
				nextItemAtThatLevel.incrementSum(i, skipListItem.getSum(i) + weightDiff);
			}
			
			if (previousItemAtThatLevel == head && nextItemAtThatLevel == tail) {
				// we have to delete that level and up			
				// System.err.println("========= DELETING ALL LEVELS AT OR ABOVE: " + i);
				head.removePointersAndSum(i);
				tail.removePointersAndSum(i);
				currentLevel = head.getLevel() - 1;
				// System.err.println(dumpLevels());
				return;
			} 
		}

		adjustWeightsForward(skipListItem, weightDiff, lastUpdatedLevel);
	}
	
	private final int getRandomLevel() {
		int ret = 0;
		while (random.getDouble() < P && ret <= currentLevel) {
			ret++;
		}
		return ret;
	}
	
	private final void updateWeight(SkipListItem<E> skipListItem, double newWeight) {
		final double weightDiff = newWeight - skipListItem.getSum(0);
		if (weightDiff == 0.0) {
			// there is no weight change!
			return;
		}
		totalWeight += weightDiff;
		
		int lastUpdatedLevel = skipListItem.getLevel() - 1;
		for (int i= 0; i <= lastUpdatedLevel; i++) {
			skipListItem.incrementSum(i, weightDiff);
		}
		
		adjustWeightsForward(skipListItem, weightDiff, lastUpdatedLevel);
	}
	
	private final void adjustWeightsForward(SkipListItem<E> skipListItem, final double weightDiff, int lastUpdatedLevel) {
		while (lastUpdatedLevel < currentLevel) {
			final SkipListItem<E> nextItemAtThatLevel = skipListItem.getForwardPointer(lastUpdatedLevel);
			for (int i= lastUpdatedLevel+1; i < nextItemAtThatLevel.getLevel(); i++) {
				nextItemAtThatLevel.incrementSum(i, weightDiff);
			}
			lastUpdatedLevel = nextItemAtThatLevel.getLevel()-1;
			skipListItem = nextItemAtThatLevel;
		}
	}

	private final SkipListItem<E> search(SkipListItem<E> skipListItem, int level, double randomValue) {
		// Originally I wrote this method with tail-recursion but then rewrote it with iteration to have it more efficient
		while (true) {
			if (randomValue == 0.0) {
				return skipListItem.getForwardPointer(0);
			}

			SkipListItem<E> nextItemAtThatLevel = skipListItem.getForwardPointer(level);
			while (nextItemAtThatLevel == tail) {
				if (level == 0) {
					// we can be here if there are some round-off errors!!!
					// let's return the last item then!!!
					return skipListItem;
				}
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
	
	public final String levelsToString() {
		StringBuffer stringBuffer = new StringBuffer();
		for (SkipListItem<E> skipListItem = head; skipListItem != null; skipListItem = skipListItem.getForwardPointer(0)) {
			if (skipListItem == head) {
				stringBuffer.append("H");
			}
			stringBuffer.append(skipListItem.getLevel());
			if (skipListItem == tail) {
				stringBuffer.append("T");
			}
			stringBuffer.append("-");
		}
		stringBuffer.append("(currentLevel=" + currentLevel + ") (totalWeight=" + totalWeight + ")");
		return stringBuffer.toString();
	}
	
	public final String weightsToString() {
		StringBuffer stringBuffer = new StringBuffer();
		for (SkipListItem<E> skipListItem = head; skipListItem != null; skipListItem = skipListItem.getForwardPointer(0)) {
			if (skipListItem == head) {
				stringBuffer.append("H");
			}
			stringBuffer.append(skipListItem.getSum(0));
			if (skipListItem == tail) {
				stringBuffer.append("T");
			}
			stringBuffer.append("-");
		}
		stringBuffer.append("(totalWeight=" + totalWeight + ")");
		return stringBuffer.toString();
	}
}
