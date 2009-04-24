package com.plectix.simulator.probability;

import java.util.*;

import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IRandom;
import com.plectix.simulator.parser.util.IdGenerator;

public class CInjectionsRandomizer {
	// id to index
	private final Map<Integer, SortedSet<Long>> injectionsToIndexes = new HashMap<Integer, SortedSet<Long>>();
	// index to id
	private final Map<Long, Integer> indexesToInjections = new HashMap<Long, Integer>();

	private IdGenerator indexGenerator = new IdGenerator();

	public final void addInjection(CInjection inj) {
		if (inj != null) {
			int id = inj.getId();
			if (injectionsToIndexes.get(id) != null) {
				removeInjection(inj);
			}
			SortedSet<Long> finderValue = new TreeSet<Long>();
			injectionsToIndexes.put(id, finderValue);
			for (long i = 0; i < inj.getPower(); i++) {
				long index = indexGenerator.generateNext();
				finderValue.add(index);
				indexesToInjections.put(index, id);
			}
		}
	}

	public final void increaseInjection(CInjection inj) {
		if (inj != null) {
			int id = inj.getId();
			SortedSet<Long> finderValue = injectionsToIndexes.get(id);
			if (finderValue == null) {
				finderValue = new TreeSet<Long>();
				injectionsToIndexes.put(id, finderValue);
			}
			long index = indexGenerator.generateNext();
			indexesToInjections.put(index, id);
			finderValue.add(index);
		}
	}

	private final void removeAnyIndex(SortedSet<Long> indexesToDelete) {
		if (!indexesToDelete.isEmpty()) {
			long indexToDelete = indexesToDelete.first();
			indexGenerator.low();
			long lastIndex = indexGenerator.check();
			int idOfThisIndex = indexesToInjections.get(indexToDelete);
			int idOfLastIndex = indexesToInjections.get(lastIndex);
			if (idOfThisIndex != idOfLastIndex) {
				swap(indexToDelete, lastIndex);
			}
			indexesToDelete.remove(lastIndex);
			indexesToInjections.remove(lastIndex);
		}
	}

	/**
	 * This method swaps to indexes in injectionsFinder, keeping in mind, that
	 * the first would be deleted
	 * 
	 * @param index1
	 * @param last
	 */
	private final void swap(long index1, long index2) {
		if (index1 != index2) {
			int idOfIndex1 = indexesToInjections.get(index1);
			int idOfIndex2 = indexesToInjections.get(index2);
			indexesToInjections.put(index1, idOfIndex2);
			SortedSet<Long> firstIdsIndexes = injectionsToIndexes.get(idOfIndex1);
			firstIdsIndexes.remove(index1);
//			firstIdsIndexes.add(index2);
			SortedSet<Long> secondIdsIndexes = injectionsToIndexes.get(idOfIndex2);
			secondIdsIndexes.remove(index2);
			secondIdsIndexes.add(index1);
		}
	}

	public final void removeInjection(CInjection inj) {
		if (inj != null) {
			int id = inj.getId();
			SortedSet<Long> indexesToDelete = injectionsToIndexes.get(id);
			if (indexesToDelete != null) {
				while (!indexesToDelete.isEmpty()) {
					removeAnyIndex(indexesToDelete);
				}
				injectionsToIndexes.remove(id);
			}
		}
	}

	public int getRandomInjection(IRandom random) {
		long index = (long) random.getInteger(indexesToInjections.size());
		return indexesToInjections.get(index);
	}

	public int getCommonPower() {
		return indexesToInjections.size();
	}

	public void simplifyInjection(CInjection inj) {
		if (inj != null) {
			int id = inj.getId();
			SortedSet<Long> finderValue = injectionsToIndexes.get(id);
			if (finderValue != null) {
				while (finderValue.size() > 1) {
					removeAnyIndex(finderValue);
				}
			}
		}
	}

}
