package com.plectix.simulator.probability;

import java.util.*;

import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IRandom;
import com.plectix.simulator.parser.util.IdGenerator;

public class CInjectionsRandomizer {
	// id to index
	public final Map<Integer, List<Long>> injectionsToIndexes = new TreeMap<Integer, List<Long>>();
	// index to id
	private final Map<Long, Integer> indexesToInjections = new TreeMap<Long, Integer>();

	private IdGenerator indexGenerator = new IdGenerator();

	public final void addInjection(CInjection inj) {
		if (inj != null) {
			int id = inj.getId();
			if (injectionsToIndexes.get(id) != null) {
				removeInjection(inj);
			}
			List<Long> finderValue = new ArrayList<Long>();
			injectionsToIndexes.put(id, finderValue);
			for (long i = 0; i < inj.getPower(); i++) {
				long index = indexGenerator.generateNext();
				finderValue.add(index);
				indexesToInjections.put(index, id);
			}
		}
	}

	public final void decreaseInjection(CInjection inj) {
		if (inj != null) {
			int id = inj.getId();
			List<Long> finderValue = injectionsToIndexes.get(id);
			if (finderValue != null) {
				safelyRemoveFirst(finderValue);
				if (finderValue.isEmpty()) {
					injectionsToIndexes.remove(id);
				}
			}
		}
	}
	
	public final void increaseInjection(CInjection inj) {
		if (inj != null) {
			int id = inj.getId();
			List<Long> finderValue = injectionsToIndexes.get(id);
			if (finderValue == null) {
				finderValue = new ArrayList<Long>();
				injectionsToIndexes.put(id, finderValue);
			}
			long index = indexGenerator.generateNext();
			indexesToInjections.put(index, id);
			finderValue.add(index);
		}
	}
	
	private final void safelyRemoveFirst(List<Long> indexesToDelete) {
		long indexToDelete = indexesToDelete.get(0);
		indexGenerator.low();
		swap(indexToDelete, indexGenerator.check());
		indexesToDelete.remove(indexGenerator.check());
		indexesToInjections.remove(indexGenerator.check());
	}
	
	/**
	 * This method swaps to indexes in injectionsFinder, keeping in mind, that the first
	 * would be deleted
	 * @param index1
	 * @param index2
	 */
	private final void swap(long index1, long index2) {
		if (index1 != index2) {
			int id1 = indexesToInjections.get(index1);
			int id2 = indexesToInjections.get(index2);
			if (id1 == id2) {
				return;
			}
			indexesToInjections.put(index1, id2);
//			indexesToInjections.put(index2, id1);
			List<Long> firstIdsIndexes = injectionsToIndexes.get(id1);
			firstIdsIndexes.remove(index1);
			firstIdsIndexes.add(index2);
			List<Long> secondIdsIndexes = injectionsToIndexes.get(id2);
			secondIdsIndexes.remove(index2);
			secondIdsIndexes.add(index1);
		}
	}
	
	public final void removeInjection(CInjection inj) {
		if (inj != null) {
			int id = inj.getId();
			List<Long> finderValue = injectionsToIndexes.get(id);
			if (finderValue != null) {
				List<Long> indexesToDelete = injectionsToIndexes.get(id);
				while (!indexesToDelete.isEmpty()) {
					safelyRemoveFirst(indexesToDelete);
				}
				injectionsToIndexes.remove(id);
			}
		}
	}

	public int getRandomInjection(IRandom random) {
		long index = (long)random.getInteger(indexesToInjections.size());
		return indexesToInjections.get(index);
	}

	public int getCommonPower() {
		return indexesToInjections.size();
	}

	public void simplifyInjection(CInjection inj) {
		if (inj != null) {
			int id = inj.getId();
			List<Long> finderValue = injectionsToIndexes.get(id);
			if (finderValue != null) {
				while (finderValue.size() != 1) {
					safelyRemoveFirst(finderValue);
				}
			}
		}
	}

}
