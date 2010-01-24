package com.plectix.simulator.staticanalysis.stories.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class IteratorAtomicEventWithinEvent implements
		Iterator<WireHashKey> {

	private final List<AtomicEvent<?>> listOfEvent = new ArrayList<AtomicEvent<?>>();
	private final List<WireHashKey> listOfWires = new ArrayList<WireHashKey>();
	private int value;
	private int size = 0;

	public IteratorAtomicEventWithinEvent(Map<WireHashKey, AtomicEvent<?>> map) {
		value = -1;
		// should be some heuristic by size of wires
		for (Map.Entry<WireHashKey, AtomicEvent<?>> entry : map.entrySet()) {
			listOfEvent.add(entry.getValue());
			listOfWires.add(entry.getKey());

		}
		size = listOfEvent.size();
	}

	public final boolean hasNext() {
		return (value < size - 1);
	}

	/**
	 * 
	 */
	public final WireHashKey next() {
		value++;
		return listOfWires.get(value);
	}

	/**
	 * not implemented. Ask Nikita why.
	 */
	@Override
	public final void remove() {
		// TODO Auto-generated method stub
	}
}
