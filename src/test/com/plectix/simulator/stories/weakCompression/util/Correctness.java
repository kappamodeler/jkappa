package com.plectix.simulator.stories.weakCompression.util;

import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.plectix.simulator.components.stories.compressions.CompressionPassport;
import com.plectix.simulator.components.stories.enums.EActionOfAEvent;
import com.plectix.simulator.components.stories.enums.EMarkOfEvent;
import com.plectix.simulator.components.stories.enums.ETypeOfWire;
import com.plectix.simulator.components.stories.storage.AtomicEvent;
import com.plectix.simulator.components.stories.storage.CEvent;
import com.plectix.simulator.components.stories.storage.IEventIterator;
import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.components.stories.storage.WireHashKey;
import com.plectix.simulator.stories.StoryCorrectness;

public class Correctness {

	private IWireStorage storage;

	public Correctness(IWireStorage storage) {
		this.storage = storage;
	}

	public void checkAnotherWay(Map<Long, EMarkOfEvent> map)
			throws StoryStorageException {
		Set<CEvent> events = storage.getEvents();
		for (CEvent event : events) {
			EMarkOfEvent mark = map.get(event.getStepId());
			if (!event.getMark().equals(mark))
				event.setMark(mark, storage);
		}

		CompressionPassport passport = storage.extractPassport();
		passport.removeEventWithMarkDelete();

		StoryCorrectness.testOfStates(passport.getStorage());

	}
	

	public void checkStorage(IWireStorage st) throws StoryStorageException {
//		printer.printResult(null, true);
		CompressionPassport passport = st.extractPassport();
		passport.removeEventWithMarkDelete();
		StoryCorrectness.testOfStates(passport.getStorage());
	}

	public boolean checkMap(Map<Long, EMarkOfEvent> map)
			throws StoryStorageException {
		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire : storage
				.getStorageWires().entrySet()) {
			if (!wire.getValue().isEmpty())
				if (!checkTheCorrectnessOnWire(map, wire.getKey(),
						wire.getValue())) {
					return false;
				}
		}
		return true;
	}

	public boolean checkTheCorrectnessOnWire(Map<Long, EMarkOfEvent> map,
			WireHashKey wKey, TreeMap<Long, AtomicEvent<?>> wire)
			throws StoryStorageException {
		AtomicEvent<?> aevent = null;
		AtomicEvent<?> prevaevent = null;

		long eventId = wire.firstKey();

		while (!wire.containsKey(eventId)) {
			eventId = wire.higherKey(eventId);
		}
		for (IEventIterator iterator = storage.eventIterator(wKey, eventId,
				false); iterator.hasNext();) {
			eventId = iterator.next();

			while (map.get(eventId).equals(EMarkOfEvent.DELETED)) {
				if (iterator.hasNext()) {
					eventId = iterator.next();
				} else
					return true;
			}

			prevaevent = aevent;
			aevent = storage.getAtomicEvent(wKey, eventId);

			if (prevaevent == null) {
				if (aevent.getState().getBeforeState() != null) {
					return false;
				}
			}

			if (!checkBetweenState(prevaevent, aevent, wKey.getTypeOfWire())) {
				return false;
			}
		}
		prevaevent = null;
		aevent = null;
		return true;
	}

	private boolean checkBetweenState(AtomicEvent<?> first,
			AtomicEvent<?> second, ETypeOfWire typeOfWire) {
		if (first != null && second != null) {
			if (first.getContainer().getMark() != EMarkOfEvent.KEPT)
				assertTrue("TEST IS WRONG1: " + first.getContainer().getMark(),
						false);
			if (second.getContainer().getMark() != EMarkOfEvent.KEPT)
				assertTrue(
						"TEST IS WRONG2: " + second.getContainer().getMark(),
						false);

			if (first.getType().equals(EActionOfAEvent.TEST)) {
				return first.getState().getBeforeState().equals(
						second.getState().getBeforeState());
			} else {
				if (!second.getType().equals(EActionOfAEvent.MODIFICATION)) {
					return first.getState().getAfterState().equals(
							second.getState().getBeforeState());
				}
			}
		}

		return true;
	}

}
