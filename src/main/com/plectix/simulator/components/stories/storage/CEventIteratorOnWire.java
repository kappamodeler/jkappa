package com.plectix.simulator.components.stories.storage;

import java.util.TreeMap;

public class CEventIteratorOnWire implements IEventIterator {

	private TreeMap<Long, AtomicEvent<?>> wire;
	private Long currentKey;
	private boolean timeReverse;
	private boolean isFirst;

	public CEventIteratorOnWire(TreeMap<Long, AtomicEvent<?>> map,
			 Long first, boolean reverse)
			throws StoryStorageException {
		wire = map;
		currentKey = first;
		if (wire.get(first) == null) {
			throw new StoryStorageException("CEventIteratorOnWire hasn't this event", first);
		}
		timeReverse = reverse;
		isFirst = true;
	}

	public CEvent value() {
		return wire.get(currentKey).getContainer();
	}

	public boolean hasNext() {
		if(isFirst){
			return true;
		}
		if (timeReverse) {
			return (wire.lowerKey(currentKey) != null);
		} else {
			return (wire.higherKey(currentKey) != null);
		}
	}

	public Long next() {
		if(isFirst){
			isFirst = false;
			return currentKey;
		}
		if (timeReverse) {
			currentKey = wire.lowerKey(currentKey);
			return currentKey;
		} else {
			currentKey = wire.higherKey(currentKey);
			return currentKey;
		}
	}

	/**
	 * not implemented. Ask Nikita why.
	 */
	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

}
