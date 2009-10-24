package com.plectix.simulator.staticanalysis.stories.storage;

import java.util.TreeMap;

public final class EventIteratorOnWire implements EventIteratorInterface {

	private final TreeMap<Long, AtomicEvent<?>> wire;
	private long currentKey;
	private final boolean timeReverse;
	private boolean isFirst;

	public EventIteratorOnWire(TreeMap<Long, AtomicEvent<?>> map,
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

	public final Event value() {
		return wire.get(currentKey).getContainer();
	}

	public final boolean hasNext() {
		if(isFirst){
			return true;
		}
		if (timeReverse) {
			return (wire.lowerKey(currentKey) != null);
		} else {
			return (wire.higherKey(currentKey) != null);
		}
	}

	@Override
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
	public final void remove() {
		// TODO Auto-generated method stub

	}
}
