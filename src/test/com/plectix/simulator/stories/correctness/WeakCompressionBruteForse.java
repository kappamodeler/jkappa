package com.plectix.simulator.stories.correctness;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.junit.Test;

import com.plectix.simulator.components.stories.enums.EActionOfAEvent;
import com.plectix.simulator.components.stories.enums.EMarkOfEvent;
import com.plectix.simulator.components.stories.enums.ETypeOfWire;
import com.plectix.simulator.components.stories.storage.AtomicEvent;
import com.plectix.simulator.components.stories.storage.CEvent;
import com.plectix.simulator.components.stories.storage.IEventIterator;
import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.components.stories.storage.WireHashKey;

public class WeakCompressionBruteForse {
	private IWireStorage storage;
	private TreeMap<Long, EMarkOfEvent> first;

	public WeakCompressionBruteForse(IWireStorage storage) {
		this.storage = storage;
	}

	@Test
	public void bruteForse() throws StoryStorageException {
		// storage.markAllUnresolved();
		// Map<WireHashKey, V>
		System.out.println("_____________________________________________");
		Map<Long, EMarkOfEvent> eventMarks = new TreeMap<Long, EMarkOfEvent>();
		first = new TreeMap<Long, EMarkOfEvent>();
		/*
		 * /
		 * 
		 * only for testing without weak
		 */
		/*
		 * storage.markAllUnresolved();
		 * storage.observableEvent().setMark(EMarkOfEvent.KEPT, storage);
		 */

		for (Entry<CEvent, Map<WireHashKey, AtomicEvent<?>>> eventEntry : storage
				.getWiresByEvent().entrySet()) {

			if (eventEntry.getKey().getMark() != null) {
				assertFalse("mark can't be unresolved!!!!", eventEntry.getKey()
						.getMark().equals(EMarkOfEvent.UNRESOLVED));
				if (eventEntry.getKey().getMark().equals(EMarkOfEvent.DELETED)) {
					eventMarks.put(eventEntry.getKey().getStepId(),
							EMarkOfEvent.DELETED);
					first.put(eventEntry.getKey().getStepId(),
							EMarkOfEvent.DELETED);
				} else {
					eventEntry.getKey().setMark(EMarkOfEvent.UNRESOLVED,
							storage);
					first.put(eventEntry.getKey().getStepId(),
							EMarkOfEvent.DELETED);
				}
			} else {
				assertTrue(eventEntry.getKey().getStepId() == -1);
			}
		}

		// storage.observableEvent().setMark(EMarkOfEvent.KEPT, storage);
		// TODO mark -1 as KEPT!!!
		// storage.ge
		eventMarks
				.put(storage.observableEvent().getStepId(), EMarkOfEvent.KEPT);
		eventMarks.put(Long.valueOf(-1), EMarkOfEvent.KEPT);
		first.put(storage.observableEvent().getStepId(), EMarkOfEvent.KEPT);
		first.put(Long.valueOf(-1), EMarkOfEvent.KEPT);
		Iterator<Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>>> iterator = storage
				.getStorageWires().entrySet().iterator();
		if (iterator.hasNext())
			assertFalse(qwerty(eventMarks, iterator));
	}

	private boolean qwerty(Map<Long, EMarkOfEvent> map,
			Iterator<Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>>> iterator)
			throws StoryStorageException {
		Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire = iterator
				.next();
		TreeMap<Long, EMarkOfEvent> newMap = new TreeMap<Long, EMarkOfEvent>();
		for (Entry<Long, AtomicEvent<?>> aevent : wire.getValue().entrySet()) {
			if (!map.containsKey(aevent.getKey())) {
				newMap.put(aevent.getKey(), EMarkOfEvent.UNRESOLVED);
			}
		}
		if (!newMap.isEmpty()) {
			List<List<EMarkOfEvent>> list = generateLists(newMap.size());
			TreeMap<Long, EMarkOfEvent> tmpMap = null;
			TreeMap<Long, EMarkOfEvent> exMap = null;
			for (List<EMarkOfEvent> m : list) {
				tmpMap = new TreeMap<Long, EMarkOfEvent>();
				tmpMap.putAll(newMap);
				Long key = tmpMap.firstKey();
				for (EMarkOfEvent mark : m) {
					tmpMap.put(key, mark);
					// if (tmpMap.containsKey(tmpMap.higherKey(key)))
					key = tmpMap.higherKey(key);
					// else break;
				}

				exMap = new TreeMap<Long, EMarkOfEvent>();
				exMap.putAll(map);
				exMap.putAll(tmpMap);
				if (check(exMap, wire)) {
					if (!iterator.hasNext()) {
						if (compareMarksWithFirst(exMap)) {
							printResult(map);
							return false;
						}
						if (checkMap(exMap)) {
							printResult(exMap);
							return true;
						} else {
							return false;
						}
					}
					return qwerty(exMap, iterator);
				} else {
					return false;
				}
			}
		} else {
			if (compareMarksWithFirst(map)) {
				printResult(map);
				return false;
			}
			if (!iterator.hasNext()) {
				if (checkMap(map)) {
					printResult(map);
					return true;
				} else {
					return false;
				}
			}
			return qwerty(map, iterator);
		}
		System.err.println("ololololololololo");
		printResult(map);
		return true;
	}

	private boolean checkMap(Map<Long, EMarkOfEvent> map)
			throws StoryStorageException {
		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire : storage
				.getStorageWires().entrySet()) {
			check(map, wire);
		}
		return false;
	}

	private boolean compareMarksWithFirst(Map<Long, EMarkOfEvent> map) {
		if (map.size() != first.size())
			return false;
		for (Entry<Long, EMarkOfEvent> entry : map.entrySet()) {
			if (!first.get(entry.getKey()).equals(entry.getValue()))
				return false;
		}
		System.out.println("equals");
		return true;
	}

	private void printResult(Map<Long, EMarkOfEvent> exMap) {
		System.out.println("STORAGE WAS:");

		for (Entry<CEvent, Map<WireHashKey, AtomicEvent<?>>> entry : storage
				.getWiresByEvent().entrySet()) {
			System.out.println(entry.getKey().getStepId() + "\t"
					+ entry.getKey().getRuleId() + "\t"
					+ entry.getKey().getMark());
		}

		System.out.println("map WAS:");

		for (Entry<Long, EMarkOfEvent> entry : first.entrySet()) {
			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}

		System.out.println("map BECOME:");
		for (Entry<Long, EMarkOfEvent> entry : exMap.entrySet()) {
			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}

	}

	private boolean check(Map<Long, EMarkOfEvent> exMap,
			Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire)
			throws StoryStorageException {
		AtomicEvent<?> aevent = null;
		AtomicEvent<?> prevaevent = null;

		long eventId = wire.getValue().firstKey();
		for (IEventIterator iterator = storage.eventIterator(wire.getKey(),
				eventId, false); iterator.hasNext();) {
			eventId = iterator.next();
			while (exMap.get(eventId).equals(EMarkOfEvent.DELETED)) {
				if (iterator.hasNext())
					eventId = iterator.next();
				else
					break;
			}
			prevaevent = aevent;
			aevent = storage.getAtomicEvent(wire.getKey(), eventId);

			if (!checkNextState(prevaevent, aevent, wire.getKey()
					.getTypeOfWire())) {
				return false;
			}
		}
		prevaevent = null;
		aevent = null;
		return true;
	}

	private boolean checkNextState(AtomicEvent<?> first, AtomicEvent<?> second,
			ETypeOfWire typeOfWire) {
		if (first != null && second != null) {

			if (first.getType().equals(EActionOfAEvent.TEST))
				return first.getState().getBeforeState().equals(
						second.getState().getBeforeState());
			else {
				if (!second.getType().equals(EActionOfAEvent.MODIFICATION))
					return first.getState().getAfterState().equals(
							second.getState().getBeforeState());
			}
		}

		return true;
	}

	public List<List<EMarkOfEvent>> generateLists(int n) {
		List<List<EMarkOfEvent>> list = new ArrayList<List<EMarkOfEvent>>();
		for (int i = 0; i < Math.pow(2, n); i++) {
			List<EMarkOfEvent> tmp = new ArrayList<EMarkOfEvent>();
			list.add(tmp);
		}
		list = generate(n, list);
		// list.remove(list.size() - 1);
		list.remove(0);
		return list;
	}

	private List<List<EMarkOfEvent>> generate(int n,
			List<List<EMarkOfEvent>> list) {
		int i = 0;
		if (n == 1) {
			for (List<EMarkOfEvent> list2 : list) {
				list2.add((i % 2 == 0) ? EMarkOfEvent.KEPT
						: EMarkOfEvent.DELETED);
				i++;
			}
			return list;
		} else {
			// System.out.println("n = " + n + "  2^n = " + Math.pow(2, n - 1));
			for (List<EMarkOfEvent> list2 : list) {
				list2
						.add(((i % Math.pow(2, n)) < Math.pow(2, n - 1)) ? EMarkOfEvent.KEPT
								: EMarkOfEvent.DELETED);
				i++;
			}
			return generate(--n, list);
		}
	}

}
