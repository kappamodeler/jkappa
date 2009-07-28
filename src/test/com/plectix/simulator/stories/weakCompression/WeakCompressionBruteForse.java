package com.plectix.simulator.stories.weakCompression;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
		Map<Long, EMarkOfEvent> checkingMap = new TreeMap<Long, EMarkOfEvent>();
		first = new TreeMap<Long, EMarkOfEvent>();

		printResult(null);
		for (CEvent event : storage
				.getEvents()) {

			if (event.getMark() != null) {
				// assertFalse("mark can't be unresolved!!!!",
				// eventEntry.getKey()
				// .getMark().equals(EMarkOfEvent.UNRESOLVED));
				if (event.getMark().equals(EMarkOfEvent.DELETED)) {
					eventMarks.put(event.getStepId(),
							EMarkOfEvent.DELETED);
					checkingMap.put(event.getStepId(),
							EMarkOfEvent.DELETED);
					first.put(event.getStepId(),
							EMarkOfEvent.DELETED);
				} else {
					if (!(event.getStepId() == storage
							.observableEvent().getStepId())) {
						if (!event.getMark().equals(EMarkOfEvent.UNRESOLVED))
						event.setMark(EMarkOfEvent.UNRESOLVED,
								storage);
						checkingMap.put(event.getStepId(),
								EMarkOfEvent.KEPT);
						first.put(event.getStepId(),
								EMarkOfEvent.UNRESOLVED);
					} else {
						checkingMap.put(event.getStepId(),
								EMarkOfEvent.KEPT);
						first.put(event.getStepId(),
								EMarkOfEvent.KEPT);
					}
				}
			} else {
				assertTrue(event.getStepId() == -1);
				event.setMark(EMarkOfEvent.UNRESOLVED, storage);
				checkingMap.put(event.getStepId(),
						EMarkOfEvent.UNRESOLVED);
				first.put(event.getStepId(),
						EMarkOfEvent.UNRESOLVED);

			}
		}

		assertTrue(checkMap(checkingMap));
		// storage.observableEvent().setMark(EMarkOfEvent.KEPT, storage);
		// TODO mark -1 as KEPT!!!
		// storage.ge
		eventMarks
				.put(storage.observableEvent().getStepId(), EMarkOfEvent.KEPT);
		// eventMarks.put(Long.valueOf(-1), EMarkOfEvent.KEPT);

		first.put(storage.observableEvent().getStepId(), EMarkOfEvent.KEPT);
		// first.put(Long.valueOf(-1), EMarkOfEvent.KEPT);

		printResult(null);
		// System.out.println("first gotten marks:");
		// for (Entry<Long, EMarkOfEvent> entry : eventMarks.entrySet()) {
		// System.out.println(entry.getKey() + "\t" + entry.getValue());
		// }
		Iterator<Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>>> iterator = storage
				.getStorageWires().entrySet().iterator();
		if (iterator.hasNext())
			assertFalse(walkOnWires(eventMarks, iterator));
	}

	private boolean walkOnWires(Map<Long, EMarkOfEvent> map,
			Iterator<Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>>> iterator)
			throws StoryStorageException {
		Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire = iterator
				.next();
		TreeMap<Long, EMarkOfEvent> newMap = new TreeMap<Long, EMarkOfEvent>();
		// mark all new atomic events as unresolved and put them to newMap
		for (Entry<Long, AtomicEvent<?>> aevent : wire.getValue().entrySet()) {
			if (!map.containsKey(aevent.getKey())) {
				newMap.put(aevent.getKey(), EMarkOfEvent.UNRESOLVED);
			}
		}
		if (!newMap.isEmpty()) {
			// if there are new atomic events generate all variants of marking
			// them
			List<List<EMarkOfEvent>> list = generateLists(newMap.size());
			TreeMap<Long, EMarkOfEvent> tmpMap = null;
			TreeMap<Long, EMarkOfEvent> exMap = null;
			for (List<EMarkOfEvent> marksVariant : list) {
				tmpMap = new TreeMap<Long, EMarkOfEvent>();
				tmpMap.putAll(newMap);
				// mark new events as next variant
				Long key = tmpMap.firstKey();
				for (EMarkOfEvent mark : marksVariant) {
					tmpMap.put(key, mark);
					key = tmpMap.higherKey(key);
				}
				exMap = new TreeMap<Long, EMarkOfEvent>();
				exMap.putAll(map);
				exMap.putAll(tmpMap);
				// check the correctness of wire
				if (checkTheCorrectnessOnWire(exMap, wire)) {
					// if it is correct, check if it is the last
					if (!iterator.hasNext()) {
						// if it is the last, check if it is the same as the
						// given marks
						if (compareMarksWithFirst(exMap)) {
							// do nothing
							// printResult(map);
							// return false;
						} else if (checkMap(exMap)) {
							// new map is correct and it is the last wire, so we
							// found the block for delete
							printResult(exMap);
							return true;
						}
					} else {
						// if the map is correct but it is not the end, walk
						// beyond
						if (walkOnWires(exMap, iterator)) {
							return true;
						}
					}
				}
				// if it is not correct, do nothing
			}
		} else {
			// if there are no new atomic events on the wire there are 2 ways:
			// if it is the end:
			if (!iterator.hasNext()) {
				if (checkMap(map)) {
					// oh yes, it is right
					if (compareMarksWithFirst(map)) {
						// /OH NO, THEY ARE THE SAME
						System.out
								.println("oh no, the are no new blocks for deleting.....");
						printResult(map);
						return false;
					}
					System.out.println("oh yes, the map is correct!!!.....");
					printResult(map);
					return true;
				} else {
					System.out.println("oh no, the map is not correct.....");
					printResult(map);
					return false;
				}
			} else
				// if it is not the end, we should go beyond
				return (walkOnWires(map, iterator));
		}
		System.out.println("ololololololololo");
		printResult(map);
		return false;
	}

	private boolean checkMap(Map<Long, EMarkOfEvent> map)
			throws StoryStorageException {
		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire : storage
				.getStorageWires().entrySet()) {
			if (!checkTheCorrectnessOnWire(map, wire)){
				
				return false;
			}
		}
		return true;
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
		if (exMap == null)
			System.out.println("BEGIN");
		System.out.println("STORAGE WAS:");

		for (CEvent event : storage
				.getEvents()) {
			System.out.println(event.getStepId() + "\t"
					+ event.getRuleId() + "\t"
					+ event.getMark());
		}

		System.out.println("map WAS:");

		for (Entry<Long, EMarkOfEvent> entry : first.entrySet()) {
			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}

		if (exMap != null) {
			System.out.println("map BECOME:");
			for (Entry<Long, EMarkOfEvent> entry : exMap.entrySet()) {
				System.out.println(entry.getKey() + "\t" + entry.getValue());
			}
		}

	}

	private boolean checkTheCorrectnessOnWire(Map<Long, EMarkOfEvent> exMap,
			Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire)
			throws StoryStorageException {
		AtomicEvent<?> aevent = null;
		AtomicEvent<?> tmp = null;
		AtomicEvent<?> prevaevent = null;

		long eventId = wire.getValue().firstKey();

		for (IEventIterator iterator = storage.eventIterator(wire.getKey(),
				eventId, false); iterator.hasNext();) {
			eventId = iterator.next();
			while (exMap.get(eventId).equals(EMarkOfEvent.DELETED)) {
				if (iterator.hasNext()) {
					eventId = iterator.next();
					if (wire.getKey().getTypeOfWire().equals(
							ETypeOfWire.INTERNAL_STATE)) {
						tmp = storage
								.getAtomicEvent(wire.getKey(), eventId);
						System.out.println("deleted: eventId=" + eventId
								+ "\tbefore = "
								+ tmp.getState().getBeforeState()
								+ "\tafter: "
								+ tmp.getState().getAfterState());
					}
				} else
					break;
			}
			prevaevent = aevent;
			aevent = storage.getAtomicEvent(wire.getKey(), eventId);
			if (prevaevent == null)
				if (aevent.getState().getBeforeState() != null){
					System.err.println("first event's before state != null");
					System.err.println("" + aevent.getContainer().getStepId() + "(" + aevent.getContainer().getRuleId() + ") before =" + aevent.getState().getBeforeState());
					return false;
				}
			if (wire.getKey().getTypeOfWire()
					.equals(ETypeOfWire.INTERNAL_STATE)) {
				if (prevaevent != null)
					System.out.println("1st event: "
							+ prevaevent.getContainer().getStepId() + "\t"
							+ prevaevent.getContainer().getRuleId());
				else
					System.out.println("1st event: null");
				if (aevent != null)
					System.out.println("2nd event: " + eventId + "\t"
							+ aevent.getContainer().getRuleId());
				else
					System.out.println("2st event: null");

			}
			if (!checkNextState(prevaevent, aevent, wire.getKey()
					.getTypeOfWire())) {
				if (prevaevent != null && aevent != null) {
					System.err.println("first: " + prevaevent.getState().getAfterState());
					System.err.println("second: " + aevent.getState().getBeforeState());
				}
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
			if (typeOfWire.equals(ETypeOfWire.INTERNAL_STATE)) {
				System.out.println("first type = " + first.getType());
				System.out.println("second type = " + second.getType());
				System.out.println("1st = " + first.getState().getAfterState());
				System.out.println("2nd = "
						+ second.getState().getBeforeState());
			}
			if (first.getType().equals(EActionOfAEvent.TEST)) {
				if (typeOfWire.equals(ETypeOfWire.INTERNAL_STATE)) {
					System.out.println("1st = "
							+ first.getState().getAfterState());
					System.out.println("2nd = "
							+ second.getState().getBeforeState());
				}
				return first.getState().getBeforeState().equals(
						second.getState().getBeforeState());
			} else {
				if (!second.getType().equals(EActionOfAEvent.MODIFICATION)) {
					// if (typeOfWire.equals(ETypeOfWire.INTERNAL_STATE)) {
					// System.out.println("1st = "
					// + first.getState().getAfterState());
					// System.out.println("2nd = "
					// + second.getState().getBeforeState());
					// }
					return first.getState().getAfterState().equals(
							second.getState().getBeforeState());
				}
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
