package com.plectix.simulator.stories.weakcompression.util;

import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.plectix.simulator.component.stories.ActionOfAEvent;
import com.plectix.simulator.component.stories.MarkOfEvent;
import com.plectix.simulator.component.stories.TypeOfWire;
import com.plectix.simulator.component.stories.compressions.CompressionPassport;
import com.plectix.simulator.component.stories.storage.AtomicEvent;
import com.plectix.simulator.component.stories.storage.Event;
import com.plectix.simulator.component.stories.storage.EventIteratorInterface;
import com.plectix.simulator.component.stories.storage.StoryStorageException;
import com.plectix.simulator.component.stories.storage.WireHashKey;
import com.plectix.simulator.component.stories.storage.WireStorageInterface;
import com.plectix.simulator.stories.StoryCorrectness;

public class Correctness {

	private WireStorageInterface storage;

	public Correctness(WireStorageInterface storage) {
		this.storage = storage;
	}

	public void checkAnotherWay(Map<Long, MarkOfEvent> map)
			throws StoryStorageException {
		Set<Event> events = storage.getEvents();
		for (Event event : events) {
			MarkOfEvent mark = map.get(event.getStepId());
			if (!event.getMark().equals(mark))
				event.setMark(mark, storage.getInformationAboutWires());
		}

		CompressionPassport passport = storage.extractPassport();
		passport.removeEventWithMarkDelete();

		StoryCorrectness.testOfStates(passport.getStorage());

	}

	public void checkStorage(WireStorageInterface st)
			throws StoryStorageException {
		// printer.printResult(null, true);
		CompressionPassport passport = st.extractPassport();
		passport.removeEventWithMarkDelete();
		StoryCorrectness.testOfStates(passport.getStorage());
	}

	public boolean checkMap(Map<Long, MarkOfEvent> map)
			throws StoryStorageException {
		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire : storage
				.getStorageWires().entrySet()) {
			if (!wire.getValue().isEmpty())
				if (!checkTheCorrectnessOnWire(map, wire.getKey(), wire
						.getValue())) {
					return false;
				}
		}
		return true;
	}

	public boolean checkTheCorrectnessOnWire(Map<Long, MarkOfEvent> map,
			WireHashKey wKey, TreeMap<Long, AtomicEvent<?>> wire)
			throws StoryStorageException {
		AtomicEvent<?> aevent = null;
		AtomicEvent<?> prevaevent = null;

		long eventId = wire.firstKey();

		while (!wire.containsKey(eventId)) {
			eventId = wire.higherKey(eventId);
		}
		for (EventIteratorInterface iterator = storage.eventIterator(wKey,
				eventId, false); iterator.hasNext();) {
			eventId = iterator.next();

			while (map.get(eventId).equals(MarkOfEvent.DELETED)) {
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
			AtomicEvent<?> second, TypeOfWire typeOfWire) {
		if (first != null && second != null) {
			if (first.getContainer().getMark() != MarkOfEvent.KEPT)
				assertTrue("TEST IS WRONG1: " + first.getContainer().getMark(),
						false);
			if (second.getContainer().getMark() != MarkOfEvent.KEPT)
				assertTrue(
						"TEST IS WRONG2: " + second.getContainer().getMark(),
						false);

			if (first.getType().equals(ActionOfAEvent.TEST)) {
				return first.getState().getBeforeState().equals(
						second.getState().getBeforeState());
			} else {
				if (!second.getType().equals(ActionOfAEvent.MODIFICATION)) {
					return first.getState().getAfterState().equals(
							second.getState().getBeforeState());
				}
			}
		}

		return true;
	}

}
