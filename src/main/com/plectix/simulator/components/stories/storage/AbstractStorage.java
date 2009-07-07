package com.plectix.simulator.components.stories.storage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public class AbstractStorage implements IWireStorage {
	// wireHashKey - wireId
	// Long - number of Event
	private HashMap<WireHashKey, TreeMap<Long, AtomicEvent<?>>> storageWires;
	private HashMap<CEvent, HashMap<WireHashKey, AtomicEvent<?>>> wiresByEvent;
	private HashMap<Integer,HashSet<Integer>> internalStatesBySiteId;

	// private LinkedList<CEventChanges> stackOfChanges;
	private CEvent observableEvent;

	public AbstractStorage(
			HashMap<WireHashKey, TreeMap<Long, AtomicEvent<?>>> storageWiresn,
			HashMap<CEvent, HashMap<WireHashKey, AtomicEvent<?>>> wiresByEventn,
			CEvent observableEventn, HashMap<Integer,HashSet<Integer>> internalStateBySiteId) {
		this.observableEvent = observableEventn;
		this.storageWires = storageWiresn;
		this.wiresByEvent = wiresByEventn;
		this.internalStatesBySiteId = internalStateBySiteId;

	}

	public IEventIterator eventIterator(WireHashKey wkey, Long first,
			boolean reverse) {

		return new CEventIteratorOnWire(storageWires.get(wkey), wkey, first,
				reverse);
	}

	public AtomicEvent<?> getAtomicEvent(WireHashKey wkey, Long event) {
		return storageWires.get(wkey).get(event);
	}

	public CEvent getEvent(WireHashKey wkey, Long event) {
		return storageWires.get(wkey).get(event).getContainer();
	}

	public int getUnresolvedCount(WireHashKey wkey) {
		return wkey.getNumberOfUnresolvedEventOnWire();
	}

	public void markAllUnresolved() {
		for (CEvent event : wiresByEvent.keySet()) {
			event.setMark(EMarkOfEvent.UNRESOLVED);
		}
	}

	// changes number of unresolved events on wires
	public void markEvent(CEvent event, EMarkOfEvent state) {
		event.setMark(state);
	}

	public CEvent observableEvent() {
		return observableEvent;
	}

	public IAtomicEventIterator wireEventIterator(CEvent event) {
		return new CIteratorAEventWithinEvent(wiresByEvent.get(event));
	}

	@Override
	public Iterator<Integer> wireInternalStateIterator(WireHashKey wkey) {

		if (wkey.getKeyOfState() == ETypeOfWire.INTERNAL_STATE) {
			return internalStatesBySiteId.get(wkey.getSiteId()).iterator();
		} else {
			System.out.println("nizzq!!");
			return null;
		}
	}

	@Override
	public IEventIterator eventIterator(WireHashKey wkey, boolean reverse) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long observableEventId() {
		// TODO Auto-generated method stub
		return null;
	}

}
